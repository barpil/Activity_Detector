from config import Config
import processor
from sources import RTSPSource, FileSource
from collections import defaultdict, deque
from ultralytics import YOLO
from anonymizer import Anonymizer
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
    
    video_buffer = processor.Buffer(300)
    skeletons_history = defaultdict(lambda: deque(maxlen=30))
    yolo = YOLO(Config.MODEL_PATH)
    anonymizer = Anonymizer()

    frame_count = 0
    try:
        while True:
            ret, frame = source.get_frame()
            if not ret:
                if Config.MODE == 'FILE':
                    print('End of file')
                    break
                continue

            results = yolo.track(frame, verbose=False)
            video_buffer.add(results[0])
            if results[0].boxes.id is not None:
                boxes = results[0].boxes.xyxy.cpu().numpy()
                track_ids = results[0].boxes.id.int().cpu().numpy()
                keypoints = results[0].keypoints.data.cpu().numpy()

                for box, track_id, keypoint in zip(boxes, track_ids, keypoints):
                    skeletons_history[track_id].append(keypoint)

                    # if frame_count % 5 == 0 and len(skeletons_history[track_id]) == 30:
                        
                    #     pred = our_model.predict(skeletons_history[track_id])
                        
                    #     if pred.is_suspicious_activity:
                    #         print(f"ALARM! Osoba {track_id} wykonuje: {pred.label}")
                    #         anonymized_frames = anonymizer.anonymize_list(video_buffer.get_all())
                    #         send_alert(anonymized_frames, track_id)
                            
                    #         skeletons_history[track_id].clear()
            frame_count += 1
            
            if Config.SHOW_VIDEO:
                resized = cv2.resize(anonymizer.anonymize(results[0]), (Config.VIDEO_WIDTH, Config.VIDEO_HEIGHT))
                cv2.imshow('video', resized)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break

            if frame_count % 1000 == 0:
                skeletons_history = defaultdict(lambda: deque(maxlen=30))
    except KeyboardInterrupt:
        print("Stopped by user")
    finally:
        source.release()
        cv2.destroyAllWindows()

if __name__ == "__main__":
    main()