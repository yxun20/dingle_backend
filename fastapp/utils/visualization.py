import cv2

def draw_keypoints(image, keypoints, image_shape, original_shape):
    for keypoint in keypoints:
        x, y, conf = keypoint
        if conf > 0.5:
            x = int(x * image_shape[1] / original_shape[1])
            y = int(y * image_shape[0] / original_shape[0])
            cv2.circle(image, (x, y), 10, (0, 255, 0), -1)
    return image



# 뼈대 시각화 함수
def draw_pose_skeleton(image, keypoints, connections, img_shape, orig_shape):
    for connection in connections:
        start_idx, end_idx = connection
        if start_idx >= len(keypoints) or end_idx >= len(keypoints):
            continue

        start_x, start_y, start_conf = keypoints[start_idx]
        end_x, end_y, end_conf = keypoints[end_idx]
        if start_conf < 0.5 or end_conf < 0.5:  # 신뢰도가 낮으면 생략
            continue

        start_x = int(start_x * (img_shape[1] / orig_shape[1]))
        start_y = int(start_y * (img_shape[0] / orig_shape[0]))
        end_x = int(end_x * (img_shape[1] / orig_shape[1]))
        end_y = int(end_y * (img_shape[0] / orig_shape[0]))

        cv2.line(image, (start_x, start_y), (end_x, end_y), (0, 255, 255), 2)  # 노란색 선

    return image
