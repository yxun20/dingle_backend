FROM python:3.9-slim

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 파일 복사
COPY ./fastapp/requirements.txt /app/requirements.txt

# 시스템 라이브러리 설치 (OpenCV용 libGL.so.1)
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    && apt-get install -y --no-install-recommends \
    libglib2.0-0 \
    libsndfile1 \
    && rm -rf /var/lib/apt/lists/*
# 의존성 설치
RUN pip install --no-cache-dir -r /app/requirements.txt
RUN pip install python-multipart
# FastAPI 애플리케이션 파일 복사
COPY . /app

# Python 경로 설정
ENV PYTHONPATH="/app"

# FastAPI 애플리케이션 실행
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]