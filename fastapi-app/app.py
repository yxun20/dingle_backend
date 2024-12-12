import os
import uuid
import logging
from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
import requests
import numpy as np
import cv2
from PIL import Image
from io import BytesIO

from utils.risk_detection import detect_risk
from utils.image_processing import process_image, save_image
from utils.cry_detection import predict_baby_cry_condition

# FastAPI 앱 생성
app = FastAPI()

# 로그 설정
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 이미지 및 오디오 저장을 위한 디렉토리 설정
STATIC_DIR = "static"
os.makedirs(STATIC_DIR, exist_ok=True)

# Spring 서버 URL 설정
SPRING_SERVER_URL = "https://bbang.justsloth.com/api/ai_response"

@app.post("/predict/")
async def predict(file: UploadFile = File(...)):
    """
    업로드된 이미지 파일을 처리하고 위험 메시지를 예측.
    """
    try:
        # 이미지 파일 로드 및 전처리
        image_data = await file.read()
        image = np.array(Image.open(BytesIO(image_data)))
        image = cv2.cvtColor(image, cv2.COLOR_RGB2BGR)

        # 이미지 처리
        blended_image, risk_message = process_image(image)

        # 고유한 파일명 생성 및 이미지 저장
        unique_id = str(uuid.uuid4())
        image_filename = f"{unique_id}.png"
        image_path = save_image(blended_image, image_filename)

        # 위험 메시지와 이미지 URL을 포함한 응답 데이터
        response_data = {
            "risk_message": risk_message,
            "image_url": f"/static/{image_filename}"
        }

        # Spring 서버로 AI 분석 결과 전송
        await send_to_spring_server(response_data)

        return JSONResponse(content=response_data)
    except Exception as e:
        logger.error(f"Error processing image: {e}")
        return JSONResponse(content={"error": str(e)}, status_code=500)

@app.post("/analyze-cry/")
async def analyze_cry(file: UploadFile = File(...)):
    """
    업로드된 울음소리 파일을 분석하여 아기의 상태를 예측.
    """
    try:
        # 오디오 파일 저장
        file_path = f"{STATIC_DIR}/{file.filename}"
        with open(file_path, "wb") as audio_file:
            audio_file.write(await file.read())

        # 울음소리 분석
        predicted_class, probabilities = predict_baby_cry_condition(file_path)

        # 결과 반환
        return JSONResponse(content={
            "predicted_class": predicted_class,
            "probabilities": probabilities
        })
    except Exception as e:
        logger.error(f"Error analyzing cry sound: {e}")
        return JSONResponse(content={"error": str(e)}, status_code=500)

async def send_to_spring_server(ai_response: dict):
    """
    Spring 서버로 AI 응답 데이터를 전송.
    """
    try:
        response = requests.post(SPRING_SERVER_URL, json=ai_response)
        if response.status_code == 200:
            logger.info("AI response successfully sent to Spring server")
        else:
            logger.error(f"Failed to send AI response to Spring server, status: {response.status_code}")
    except Exception as e:
        logger.error(f"Error sending AI response to Spring server: {e}")