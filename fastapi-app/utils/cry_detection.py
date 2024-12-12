import numpy as np
import tensorflow as tf
import soundfile as sf
import scipy.signal
import scipy.ndimage

# CNN 모델 로드
cnn_model = tf.keras.models.load_model("models/cnn_model.h5")

# 클래스 이름 정의
class_names = ['belly_pain', 'burping', 'discomfort', 'hungry', 'tired']

def compute_fft_spectrogram(audio, sample_rate, window_size=20, step_size=10, target_shape=(128, 128)):
    """
    FFT 스펙트로그램 생성 함수.
    :param audio: 오디오 데이터 (numpy array)
    :param sample_rate: 오디오 샘플링 속도
    :param window_size: 윈도우 크기 (ms)
    :param step_size: 윈도우 이동 간격 (ms)
    :param target_shape: 출력 스펙트로그램 크기 (128, 128)
    :return: 크기 조정된 스펙트로그램
    """
    nperseg = int(window_size * sample_rate / 1000)
    noverlap = nperseg - int(step_size * sample_rate / 1000)

    frequencies, times, spectrogram = scipy.signal.spectrogram(
        audio, fs=sample_rate, nperseg=nperseg, noverlap=noverlap, mode='magnitude'
    )

    # 크기 조정 및 정규화
    spectrogram_resized = scipy.ndimage.zoom(spectrogram, (target_shape[0] / spectrogram.shape[0], target_shape[1] / spectrogram.shape[1]))
    spectrogram_normalized = spectrogram_resized / np.max(spectrogram_resized)
    return spectrogram_normalized

def preprocess_wav(file_path, target_shape=(128, 128)):
    """
    `.wav` 파일을 CNN 모델에 입력 가능한 형태로 전처리.
    :param file_path: 오디오 파일 경로
    :param target_shape: 출력 스펙트로그램 크기 (128, 128)
    :return: 전처리된 데이터
    """
    audio, sample_rate = sf.read(file_path)
    if len(audio.shape) > 1:
        audio = np.mean(audio, axis=1)  # 스테레오 -> 모노 변환
    spectrogram = compute_fft_spectrogram(audio, sample_rate, target_shape=target_shape)
    return spectrogram[..., np.newaxis]  # 채널 추가 (128, 128) -> (128, 128, 1)

def predict_baby_cry_condition(file_path):
    """
    아기 울음소리를 분석하여 상태를 예측.
    :param file_path: 오디오 파일 경로
    :return: 예측된 클래스 이름 및 확률
    """
    input_data = preprocess_wav(file_path)
    input_data = np.expand_dims(input_data, axis=0)  # 배치 차원 추가

    # 예측 수행
    predictions = cnn_model.predict(input_data)
    probabilities = predictions[0]

    # 가장 높은 확률의 클래스 예측
    predicted_class = class_names[np.argmax(probabilities)]
    return predicted_class, {class_names[i]: probabilities[i] * 100 for i in range(len(class_names))}
