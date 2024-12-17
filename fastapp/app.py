from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
from utils.preprocess import preprocess_wav

import base64
import cv2
import numpy as np
import os
import io
import uuid
from utils.model_loader import load_models
from utils.save_image import save_image
from utils.risk_detection import (
    extract_bed_edges,
    detect_risk_with_edges,
    detect_prone_via_keypoints,
)
from utils.visualization import draw_keypoints, draw_pose_skeleton

app = FastAPI()

# YOLOv8 Segmentation 모델 및 Pose 모델 로드
seg_model, pose_model, cnn_model = load_models()

# 포즈 연결 정의
pose_connections = [
    (0, 1), (0, 2), (1, 3), (2, 4),  # 머리 연결
    (5, 6), (5, 7), (7, 9), (6, 8), (8, 10),  # 팔 연결
    (11, 12), (11, 13), (13, 15), (12, 14), (14, 16)  # 다리 연결
]

# 이미지 저장 디렉토리 설정
STATIC_DIR = "static"
os.makedirs(STATIC_DIR, exist_ok=True)

# 전역 상태 변수
previous_keypoints = None
no_motion_time = 0
frame_interval = 1 / 30  # 30fps 기준


@app.post("/pose-detection")
async def upload_image(file: UploadFile = File(...)):
    global previous_keypoints, no_motion_time

    try:
        # 이미지 로드
        image_bytes = await file.read()
        np_arr = np.frombuffer(image_bytes, np.uint8)
        image = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
        if image is None:
            return JSONResponse(content={"error": "이미지 디코딩 실패"}, status_code=400)

        blended_image = image.copy()  # 기본값으로 초기화

        # Segmentation 및 Pose 모델 실행
        seg_results = seg_model(image, conf=0.5, iou=0.4)
        pose_results = pose_model(image)

        if not seg_results[0].masks:
            return JSONResponse(content={"error": "Segmentation 결과 없음"}, status_code=400)
        if not pose_results[0].keypoints:
            return JSONResponse(content={"error": "Pose 결과 없음"}, status_code=400)

        # Mask 및 Keypoints 추출
        masks = seg_results[0].masks.data.cpu().numpy() if seg_results[0].masks else []
        pose_keypoints = pose_results[0].keypoints.data.cpu().numpy() if pose_results[0].keypoints else None

        bed_class_id, person_class_id = 1, 0
        bed_mask, person_mask = None, None
        if seg_results[0].boxes:
            classes = seg_results[0].boxes.cls.cpu().numpy()
            for i, cls in enumerate(classes):
                if cls == bed_class_id:
                    bed_mask = masks[i]
                elif cls == person_class_id:
                    person_mask = masks[i]

        # 침대 가장자리 추출
        bed_edges, bed_min_x, bed_max_x, bed_min_y, bed_max_y = extract_bed_edges(bed_mask, image.shape)

        # --- 낙상 감지 ---
        risk_message = detect_risk_with_edges(
            pose_keypoints, bed_edges, bed_min_x, bed_max_x, bed_min_y, bed_max_y
        )

        if "위험 수준" in risk_message:
            # 시각화
            mask_overlay = np.zeros_like(image, dtype=np.uint8)
            if bed_mask is not None:
                bed_mask_resized = cv2.resize(bed_mask, (image.shape[1], image.shape[0]))
                mask_bool = bed_mask_resized > 0.5
                mask_overlay[mask_bool] = [255, 0, 0]  # 빨간색
                blended_image = cv2.addWeighted(image, 0.8, mask_overlay, 0.2, 0)

            # Keypoint 시각화
            if pose_keypoints is not None:
                blended_image = draw_keypoints(
                    blended_image, pose_keypoints[0], image.shape, pose_results[0].orig_shape
                )
                blended_image = draw_pose_skeleton(
                    blended_image,
                    pose_keypoints[0],
                    pose_connections,
                    image.shape,
                    pose_results[0].orig_shape,
                )

            # 결과 저장
            unique_id = str(uuid.uuid4())
            image_filename = f"{unique_id}.png"
            image_path = os.path.join(STATIC_DIR, image_filename)
            save_image(blended_image, image_path)

            return JSONResponse(content={
                "risk": risk_message,
                "image_url": f"/static/{image_filename}",
            })

        # --- 질식 자세 감지 ---
        prone_keypoints_detected, keypoints_message = detect_prone_via_keypoints(pose_keypoints)

        # 움직임 감지
        motion_detected = False
        if pose_keypoints is not None and len(pose_keypoints) > 0:
            if previous_keypoints is not None and len(previous_keypoints) > 0:
                if previous_keypoints[0].shape == pose_keypoints[0].shape:
                    differences = np.linalg.norm(
                        previous_keypoints[0][:, :2] - pose_keypoints[0][:, :2], axis=1
                    )
                    if np.any(differences > 5):  # 움직임이 있으면
                        motion_detected = True
                else:
                    print("Keypoints shape mismatch, skipping motion detection.")
            else:
                motion_detected = True  # 이전 키포인트가 없으면 움직임 있다고 가정

        # 움직임 상태 업데이트
        if motion_detected:
            no_motion_time = 0  # 움직임이 있으면 시간 초기화
        else:
            no_motion_time += frame_interval

        choking_status = "안전 - 움직임 감지됨"
        if prone_keypoints_detected:  # 질식 자세 판단
            if no_motion_time >= 90:  # 90초 이상 움직임이 없으면
                choking_status = "질식 위험 감지됨!"
            else:
                choking_status = f"움직임 없음: {int(no_motion_time)}초"

        # 이전 키포인트 업데이트
        previous_keypoints = pose_keypoints

        # --- 시각화 ---
        mask_overlay = np.zeros_like(image, dtype=np.uint8)
        if bed_mask is not None:
            bed_mask_resized = cv2.resize(bed_mask, (image.shape[1], image.shape[0]))
            mask_bool = bed_mask_resized > 0.5
            mask_overlay[mask_bool] = [255, 0, 0]  # 빨간색
            blended_image = cv2.addWeighted(image, 0.8, mask_overlay, 0.2, 0)

        # Keypoint 시각화
        if pose_keypoints is not None:
            blended_image = draw_keypoints(
                blended_image, pose_keypoints[0], image.shape, pose_results[0].orig_shape
            )
            blended_image = draw_pose_skeleton(
                blended_image,
                pose_keypoints[0],
                pose_connections,
                image.shape,
                pose_results[0].orig_shape,
            )

        # 결과 저장
        unique_id = str(uuid.uuid4())
        image_filename = f"{unique_id}.png"
        image_path = os.path.join(STATIC_DIR, image_filename)
        save_image(blended_image, image_path)

        # 결과 반환
        response_data = {
            "prone_status": "Detected" if prone_keypoints_detected else "Not Detected",
            "messages": [ keypoints_message],
            "choking_status": choking_status,
            "image_url": f"/static/{image_filename}",
        }
        return JSONResponse(content=response_data)

    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)


@app.post("/cry-detection")
async def predict(file: UploadFile = File(...)):
    try:
        # 오디오 데이터 처리
        audio_data = await file.read()
        audio_file = io.BytesIO(audio_data)
        spectrogram = preprocess_wav(audio_file)
        spectrogram = np.expand_dims(spectrogram, axis=0)

        # CNN 모델 예측
        predictions = cnn_model.predict(spectrogram)
        probabilities = predictions[0]
        result = {
            class_name: f"{probabilities[i] * 100:.2f}%"
            for i, class_name in enumerate(
                ["복통", "트림", "불편함", "배고픔", "피로"]
            )
        }
        result["predicted_class"] = [
            "복통",
            "트림",
            "불편함",
            "배고픔",
            "피로",
        ][np.argmax(probabilities)]

        return JSONResponse(content=result)

    except Exception as e:
        return JSONResponse(content={"error": str(e)}, status_code=500)
