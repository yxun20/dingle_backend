import numpy as np
import soundfile as sf
import scipy.signal
import scipy.ndimage

def compute_fft_spectrogram(audio, sample_rate, window_size=20, step_size=10, target_shape=(128, 128)):
    nperseg = int(window_size * sample_rate / 1000)
    noverlap = nperseg - int(step_size * sample_rate / 1000)
    frequencies, times, spectrogram = scipy.signal.spectrogram(
        audio, fs=sample_rate, nperseg=nperseg, noverlap=noverlap, mode='magnitude'
    )
    spectrogram_resized = scipy.ndimage.zoom(spectrogram, (target_shape[0] / spectrogram.shape[0], target_shape[1] / spectrogram.shape[1]))
    spectrogram_normalized = spectrogram_resized / np.max(spectrogram_resized)
    return spectrogram_normalized

def preprocess_wav(audio_file, target_shape=(128, 128)):
    audio, sample_rate = sf.read(audio_file)
    if len(audio.shape) > 1:
        audio = np.mean(audio, axis=1)  # 스테레오 -> 모노 변환
    spectrogram = compute_fft_spectrogram(audio, sample_rate, target_shape=target_shape)
    return spectrogram[..., np.newaxis]  # 채널 추가
