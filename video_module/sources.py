import cv2
import threading
import time
from abc import ABC, abstractmethod

class VideoSource(ABC):
    @abstractmethod
    def get_frame(self):
        """Zwraca (ret, frame)"""
        pass

    @abstractmethod
    def release(self):
        """Zwalnia zasoby"""
        pass

class RTSPSource(VideoSource):
    def __init__(self, url):
        self.cap = cv2.VideoCapture(url)
        self.lock = threading.Lock()
        self.running = True
        self.ret, self.frame = False, None

        self.thread = threading.Thread(target=self._update, daemon=True)
        self.thread.start()

    def _update(self):
        while self.running:
            if self.cap.isOpened():
                ret, frame = self.cap.read()
                with self.lock:
                    self.ret, self.frame = ret, frame
            else:
                time.sleep(0.1)

    def get_frame(self):
        with self.lock:
            return self.ret, self.frame

    def release(self):
        self.running = False
        self.thread.join()
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