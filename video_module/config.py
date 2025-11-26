import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    
    MODE = os.getenv('APP_MODE').upper()
    
    CAM_USER = os.getenv('CAMERA_USER')
    CAM_PASS = os.getenv('CAMERA_PASSWORD')
    CAM_IP = os.getenv('CAMERA_IP')
    CAM_PORT = os.getenv('CAMERA_PORT')
    CAM_PATH = os.getenv('RTSP_PATH')

    SHOW_VIDEO = bool(os.getenv('SHOW_VIDEO'))
    VIDEO_HEIGHT = int(os.getenv('VIDEO_HEIGHT'))
    VIDEO_WIDTH = int(os.getenv('VIDEO_WIDTH'))

    MODEL_PATH = os.getenv('MODEL_PATH')
    FILE_PATH = os.getenv('TEST_FILE_PATH')

    @staticmethod
    def get_rtsp_url():
             
        return f"rtsp://{Config.CAM_USER}:{Config.CAM_PASS}@{Config.CAM_IP}:{Config.CAM_PORT}/{Config.CAM_PATH}"
