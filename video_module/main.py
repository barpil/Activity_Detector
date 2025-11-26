from config import Config
from sources import RTSPSource, FileSource
import cv2

def main():
    print(f"MODE: {Config.MODE}")

    if Config.MODE == 'RTSP':
        url = Config.get_rtsp_url()
        source = RTSPSource(url)
    elif Config.MODE == 'FILE':
        source = FileSource(Config.FILE_PATH)
    else:
        print('Invalid mode')
        return
    
    try:
        while True:
            ret, frame = source.get_frame()
            if not ret:
                if Config.MODE == 'FILE':
                    print('End of file')
                    break
                continue
            cv2.imshow('Video', frame)
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
    except KeyboardInterrupt:
        print("Stopped by user")
    finally:
        source.release()
        cv2.destroyAllWindows()

if __name__ == "__main__":
    main()