 # 모델 로드 관련 로직from pathlib import Path
from ultralytics import YOLO
from tensorflow.keras.models import load_model
from pathlib import Path

# 모델 로드 함수
def load_models():
    MODEL_DIR = Path(__file__).parent.parent / "models"
    seg_model = YOLO(str(MODEL_DIR / "seg_best.pt"))
    pose_model = YOLO(str(MODEL_DIR / "yolov8n-pose.pt"))
    cnn_model = load_model(str(MODEL_DIR / "cnn_model.h5"))
    return seg_model, pose_model, cnn_model
