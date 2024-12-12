import numpy as np
import cv2

# 위험 감지 함수
def detect_risk(pose_keypoints, bed_mask, image_shape, original_shape):
    risk_messages = []  # 위험 메시지를 누적할 리스트

    if pose_keypoints is None or len(pose_keypoints) == 0 or len(pose_keypoints[0]) < 7:
        risk_messages.append("위험을 평가할 수 없습니다. 키포인트가 누락되었거나 불완전합니다.")
    
    if bed_mask is None:
        risk_messages.append("위험을 평가할 수 없습니다. 침대 마스크가 누락되었습니다.")
    
    # 침대 가장자리 계산
    bed_mask_resized = cv2.resize(bed_mask, (image_shape[1], image_shape[0]))
    bed_edges = np.argwhere(bed_mask_resized > 0)
    if bed_edges.size == 0:
        risk_messages.append("위험을 평가할 수 없습니다. 침대 마스크가 비어있습니다.")
    
    if len(bed_edges) > 0:
        bed_min_y, bed_max_y = np.min(bed_edges[:, 0]), np.max(bed_edges[:, 0])
        bed_min_x, bed_max_x = np.min(bed_edges[:, 1]), np.max(bed_edges[:, 1])

        # 코, 가슴, 배, 눈, 다리 Keypoints 추출
        nose, left_eye, right_eye = pose_keypoints[0][0], pose_keypoints[0][1], pose_keypoints[0][2]
        chest, stomach = pose_keypoints[0][5], pose_keypoints[0][6]
        left_knee, right_knee = pose_keypoints[0][7], pose_keypoints[0][8]
        left_ankle, right_ankle = pose_keypoints[0][9], pose_keypoints[0][10]

        # 1. 낙상 위험 판단
        # 아기의 무릎이나 발목이 침대 범위를 벗어나면 낙상 위험
        if not (bed_min_y <= left_knee[1] <= bed_max_y) or not (bed_min_y <= right_knee[1] <= bed_max_y):
            return "경고: 아기가 침대에서 떨어졌습니다!"  # 낙상 시 다른 메시지 출력 안함
        
        # 얼굴이나 몸통이 침대 밖으로 나가면 낙상 위험
        face_keypoints = pose_keypoints[0][:5]  # 얼굴 Keypoints
        for idx, keypoint in enumerate(face_keypoints):
            x, y = keypoint[:2]
            x_original = x * (image_shape[1] / original_shape[1])
            y_original = y * (image_shape[0] / original_shape[0])

            if not (bed_min_x <= x_original <= bed_max_x and bed_min_y <= y_original <= bed_max_y):
                return "경고: 아기가 침대 밖에 나가 있습니다! 낙상 위험!"

        # 2. 질식 위험 판단
        # 아기의 얼굴이 침대에 눕혀져서 뒤통수가 보이는 경우
        if nose[1] > chest[1] and nose[1] > stomach[1]:
            return "경고: 아기가 질식 위험에 처해 있습니다!"  # 눈과 코가 가려져 뒤통수가 보일 때
        
        # 4. 정상 자세 판단
        # 아기가 눕고 있으면 "아기가 눕고 있습니다."
        if abs(chest[1] - stomach[1]) < image_shape[0] * 0.1:
            risk_messages.append("위험 없음: 아기가 눕고 있습니다.")
        
        # 아기가 서 있으면 "아기가 서 있습니다."
        if (bed_min_y <= left_knee[1] <= bed_max_y) and (bed_min_y <= right_knee[1] <= bed_max_y):
            risk_messages.append("위험 없음: 아기가 서 있습니다.")
        
    if not risk_messages:
        risk_messages.append("위험 없음: 아기의 자세는 정상입니다.")  # 위험 메시지가 하나도 없으면 기본 메시지 추가

    return "\n".join(risk_messages)  # 메시지를 줄바꿈으로 구분해서 반환
