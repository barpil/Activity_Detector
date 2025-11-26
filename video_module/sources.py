import cv2
import threading
import time
from abc import ABC, abstractmethod

class VideoSource(ABC):
    @abstractmethod
    def get_frame(self):
        pass

    @abstractmethod
    def release(self):
        pass

    def get_frame_rate(self):
        if self.cap.isOpened():
            return self.cap.get(cv2.CAP_PROP_FPS)
        return 0.0

class RTSPSource(VideoSource):
    def __init__(self, url):
        self.cap = cv2.VideoCapture(url)
    
    def get_frame(self):
        return self.cap.read()

    def release(self):
        self.cap.release()

class FileSource(VideoSource):
    def __init__(self, file_path, loop=False):
        self.file_path = file_path
        self.loop = loop
        self.cap = cv2.VideoCapture(file_path)

    def get_frame(self):
        ret, frame = self.cap.read()
        
        if not ret and self.loop:
            self.cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
            ret, frame = self.cap.read()
            
        return ret, frame

    def release(self):
        self.cap.release()