import cv2


def draw_keypoints(image, keypoints, image_shape, original_shape):
    for idx, keypoint in enumerate(keypoints):
        x, y, conf = keypoint
        if conf > 0.5:
            cv2.circle(image, (int(x), int(y)), 5, (0, 255, 0), -1)
    return image


def draw_pose_skeleton(image, keypoints, connections, image_shape, original_shape):
    for connection in connections:
        x1, y1, _ = keypoints[connection[0]]
        x2, y2, _ = keypoints[connection[1]]
        cv2.line(image, (int(x1), int(y1)), (int(x2), int(y2)), (255, 0, 0), 2)
    return image
