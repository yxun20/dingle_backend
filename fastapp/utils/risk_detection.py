import numpy as np
import cv2

def extract_bed_edges(bed_mask, image_shape):
    if bed_mask is None:
        return [], None, None, None, None

    bed_mask_resized = cv2.resize(bed_mask, (image_shape[1], image_shape[0]))
    mask_bool = bed_mask_resized > 0.5
    bed_edges = np.argwhere(mask_bool)  # 침대 가장자리 좌표 추출
    bed_min_x, bed_max_x = np.min(bed_edges[:, 1]), np.max(bed_edges[:, 1])
    bed_min_y, bed_max_y = np.min(bed_edges[:, 0]), np.max(bed_edges[:, 0])

    return bed_edges, bed_min_x, bed_max_x, bed_min_y, bed_max_y


def detect_risk_with_edges(pose_keypoints, bed_edges, bed_min_x, bed_max_x, bed_min_y, bed_max_y):
    if pose_keypoints is None or len(pose_keypoints) == 0:
        return "위험 평가 불가능: 키포인트 정보가 누락되었거나 불완전합니다."

    if bed_edges is None or len(bed_edges) == 0:
        return "위험 평가 불가능: 침대 가장자리 정보가 없습니다."

    keypoints_on_edge = 0
    keypoints_outside_bed = 0

    for keypoint in pose_keypoints[0]:
        x, y, conf = keypoint
        if conf < 0.5:
            continue

        x_original = int(x)
        y_original = int(y)

        if [x_original, y_original] in bed_edges:
            keypoints_on_edge += 1

        if not (bed_min_x <= x_original <= bed_max_x and bed_min_y <= y_original <= bed_max_y):
            keypoints_outside_bed += 1

    if keypoints_outside_bed >= 2:
        return f"위험 수준 2: {keypoints_outside_bed}개의 키포인트가 침대 밖에 있습니다."
    if keypoints_on_edge > 0:
        return f"위험 수준 1: {keypoints_on_edge}개의 키포인트가 침대 가장자리에 있습니다."

    return "위험 없음."


def detect_prone_via_contour(person_mask):
    if person_mask is None:
        return False, "윤곽 분석을 위한 사람 마스크가 없습니다."

    contours, _ = cv2.findContours((person_mask > 0).astype(np.uint8), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    if len(contours) > 0:
        contour = max(contours, key=cv2.contourArea)
        convexity = cv2.isContourConvex(contour)
        if convexity:
            return True, "경고: 머리 윤곽이 둥근 것으로 감지됨 (엎드린 자세)."
    return False, "머리 윤곽이 둥글지 않음 (엎드린 자세 아님)."


def detect_prone_via_keypoints(pose_keypoints):
    if pose_keypoints is None or len(pose_keypoints) == 0:
        return False, "포즈 키포인트가 누락되었거나 불완전합니다."

    try:
        nose = pose_keypoints[0][0]
        left_eye = pose_keypoints[0][1]
        right_eye = pose_keypoints[0][2]
    except IndexError:
        return False, "키포인트 배열에 충분한 데이터가 없습니다."

    if nose[2] < 0.5 and left_eye[2] < 0.5 and right_eye[2] < 0.5:
        return True, "경고: 엎드린 자세로 감지되었습니다!"

    return False, "누운 자세"
