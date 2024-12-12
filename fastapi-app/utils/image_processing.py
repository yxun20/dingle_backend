import numpy as np
import cv2
from ultralytics import YOLO
from utils.risk_detection import detect_risk

# 모델 경로
MODEL_DIR = Path(__file__).parent.parent / "models"
SEG_MODEL_PATH = MODEL_DIR / "seg_best.pt"
POSE_MODEL_PATH = MODEL_DIR / "yolov8n-pose.pt"

# 모델 로드
seg_model = YOLO(str(SEG_MODEL_PATH))
pose_model = YOLO(str(POSE_MODEL_PATH))


def process_image(image: np.ndarray):
    """이미지를 처리하고 위험을 감지하는 함수"""
    # 모델 추론
    seg_results = seg_model(image, conf=0.5, iou=0.4)  # YOLO Segmentation
    pose_results = pose_model(image)  # Pose 추론

    # Segmentation Masks 및 Keypoints 추출
    masks = seg_results[0].masks.data.cpu().numpy() if seg_results[0].masks is not None else []
    pose_keypoints = pose_results[0].keypoints.data.cpu().numpy() if pose_results[0].keypoints is not None else None

    # 침대 마스크 필터링
    bed_class_id = 1  # 침대 클래스 ID
    bed_mask = None
    if seg_results[0].boxes is not None:
        classes = seg_results[0].boxes.cls.cpu().numpy()
        bed_indices = np.where(classes == bed_class_id)[0]
        if len(bed_indices) > 0:
            bed_mask = masks[bed_indices[0]]

    # Segmentation 마스크 시각화
    mask_overlay = np.zeros_like(image, dtype=np.uint8)
    if bed_mask is not None:
        bed_mask_resized = cv2.resize(bed_mask, (image.shape[1], image.shape[0]))
        mask_bool = bed_mask_resized > 0.5
        mask_overlay[mask_bool] = [255, 0, 0]  # 빨간색으로 표시

    # Keypoints 시각화
    blended_image = cv2.addWeighted(image, 0.7, mask_overlay, 0.3, 0)
    if pose_keypoints is not None and len(pose_keypoints[0]) > 0:
        for keypoint in pose_keypoints[0]:
            x, y = keypoint[:2]
            cv2.circle(blended_image, (int(x), int(y)), 5, (0, 255, 0), -1)

    # 위험 감지
    risk_message = detect_risk(pose_keypoints, bed_mask, image.shape, pose_results[0].orig_shape)

    return blended_image, risk_message


def save_image(image: np.ndarray, image_filename: str):
    """이미지를 저장하고 파일 경로 반환"""
    image_path = os.path.join("static", image_filename)
    cv2.imwrite(image_path, image)
    return image_path
