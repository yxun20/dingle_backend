# FastAPI Dockerfile
FROM python:3.9-slim
WORKDIR /app
COPY ./src /app
RUN pip install --no-cache-dir fastapi uvicorn pillow opencv-python-headless ultralytics numpy
CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]
