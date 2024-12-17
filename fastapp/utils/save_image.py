import cv2
import os
import numpy as np

def save_image(image, path):
    """
    Save an image to the specified path. If saving as PNG fails, retry with JPEG format.

    Args:
        image (numpy.ndarray): The image to save.
        path (str): The path to save the image.

    Raises:
        ValueError: If the image is invalid or None.
        RuntimeError: If the image cannot be saved to the specified path.
    """
    # 이미지가 유효한지 확인
    if image is None or not isinstance(image, np.ndarray):
        raise ValueError("Invalid image provided. Ensure the image is a valid numpy array.")

    # 디렉토리 경로가 존재하지 않으면 생성
    directory = os.path.dirname(path)
    if directory and not os.path.exists(directory):
        os.makedirs(directory, exist_ok=True)

    # 이미지 저장 시도 (PNG)
    success = cv2.imwrite(path, image)
    if not success:
        print(f"Failed to save image as PNG at {path}. Retrying with JPEG format...")
        
        # 경로 확장자를 .jpg로 변경
        jpg_path = os.path.splitext(path)[0] + ".jpg"
        success = cv2.imwrite(jpg_path, image)
        
        if not success:
            raise RuntimeError(f"Failed to save image at {jpg_path} as well.")
        
        print(f"Image successfully saved as JPEG at: {jpg_path}")
    else:
        print(f"Image successfully saved at: {path}")
