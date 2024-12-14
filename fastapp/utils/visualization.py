import cv2

def draw_keypoints(image, keypoints, image_shape, original_shape):
    for keypoint in keypoints:
        x, y, conf = keypoint
        if conf > 0.5:
            x = int(x * image_shape[1] / original_shape[1])
            y = int(y * image_shape[0] / original_shape[0])
            cv2.circle(image, (x, y), 10, (0, 255, 0), -1)
    return image


def draw_pose_skeleton(image, keypoints, connections, image_shape, original_shape):
    for connection in connections:
        x1, y1, _ = keypoints[connection[0]]
        x2, y2, _ = keypoints[connection[1]]
        x1 = int(x1 * image_shape[1] / original_shape[1])
        y1 = int(y1 * image_shape[0] / original_shape[0])
        x2 = int(x2 * image_shape[1] / original_shape[1])
        y2 = int(y2 * image_shape[0] / original_shape[0])
        cv2.line(image, (x1, y1), (x2, y2), (255, 0, 0), 2)
    return image
