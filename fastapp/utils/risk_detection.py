import numpy as np
import cv2

def extract_bed_edges(bed_mask, image_shape):
    """
    침대 마스크에서 가장자리를 추출하고 최소 경계 박스를 계산합니다.

    :param bed_mask: 침대 마스크 (numpy 배열)
    :param image_shape: 원본 이미지의 크기
    :return: 침대 가장자리 좌표, 최소 x, 최대 x, 최소 y, 최대 y
    """
    if bed_mask is None:
        return [], None, None, None, None

    # 마스크를 원본 이미지 크기로 리사이즈
    bed_mask_resized = cv2.resize(bed_mask, (image_shape[1], image_shape[0]))

    # 마스크를 이진화
    _, binary_mask = cv2.threshold(bed_mask_resized, 0.5, 1, cv2.THRESH_BINARY)

    # 가장자리 추출
    contours, _ = cv2.findContours((binary_mask > 0).astype(np.uint8),
                cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    if not contours:
        return [], None, None, None, None

    # 가장 큰 윤곽선 선택 (여러 개의 윤곽선이 있을 경우)
    largest_contour = max(contours, key=cv2.contourArea)

    # 윤곽선을 단순화하여 다각형으로 변환
    epsilon = 0.01 * cv2.arcLength(largest_contour, True)  # 윤곽선을 단순화
    approx_polygon = cv2.approxPolyDP(largest_contour, epsilon, True)

    # 최소 경계 박스 계산
    x_coordinates = [point[0][0] for point in approx_polygon]
    y_coordinates = [point[0][1] for point in approx_polygon]
    min_x, max_x = min(x_coordinates), max(x_coordinates)
    min_y, max_y = min(y_coordinates), max(y_coordinates)

    # 가장자리 좌표 반환
    bed_edges = np.array(approx_polygon).squeeze(axis=1)

    return bed_edges, min_x, max_x, min_y, max_y


def detect_risk_with_edges(pose_keypoints, bed_edges, bed_min_x, bed_max_x, bed_min_y, bed_max_y):
    if pose_keypoints is None or len(pose_keypoints) == 0:
        return "위험 평가 불가능: 키포인트 정보 없음."

    if bed_edges is None or len(bed_edges) == 0:
        return "위험 평가 불가능: 침대 가장자리 정보 없음."

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
        return f"위험 수준 2: {keypoints_outside_bed}개의 키포인트가 침대 밖에 있음."
    if keypoints_on_edge > 0:
        return f"위험 수준 1: {keypoints_on_edge}개의 키포인트가 침대 가장자리에 있음."

    return "위험 없음."



def detect_prone_via_keypoints(pose_keypoints):
    if pose_keypoints is None or len(pose_keypoints) == 0:
        return False, "키포인트 없음."

    try:
        nose = pose_keypoints[0][0]
        left_eye = pose_keypoints[0][1]
        right_eye = pose_keypoints[0][2]
    except IndexError:
        return False, "키포인트 배열에 충분한 데이터가 없음."

    if nose[2] < 0.5 and left_eye[2] < 0.5 and right_eye[2] < 0.5:
        return True, "경고: 엎드린 자세로 감지됨!"

    return False, "누운 자세.(머리가 위로, 질식사 아님)"
