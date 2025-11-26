import cv2
import numpy as np
from ultralytics.engine.results import Results

class Anonymizer:
    
    def anonymize(self, result: Results):
        frame = result.orig_img.copy() 

        if result.keypoints is None or result.keypoints.data.numel() == 0:
            return frame

        keypoints_data = result.keypoints.data.cpu().numpy()

        RADIUS_MULTIPLIER = 2.0 
        BLUR_KERNEL_SIZE = (25, 25) 
        LEFT_EYE_IDX = 1
        RIGHT_EYE_IDX = 2

        blurred_frame = cv2.blur(frame, BLUR_KERNEL_SIZE)

        for person_kps in keypoints_data:
            
            if len(person_kps) > max(LEFT_EYE_IDX, RIGHT_EYE_IDX):
                left_eye = person_kps[LEFT_EYE_IDX]
                right_eye = person_kps[RIGHT_EYE_IDX]

                if left_eye[2] > 0.2 and right_eye[2] > 0.2:

                    eye_distance = np.linalg.norm(left_eye[:2] - right_eye[:2])
                    
                    radius = int(eye_distance * RADIUS_MULTIPLIER)

                    center_x = int((left_eye[0] + right_eye[0]) / 2)
                    center_y = int((left_eye[1] + right_eye[1]) / 2)
                    center = (center_x, center_y)

                    mask = np.zeros(frame.shape[:2], dtype=np.uint8)
                    cv2.circle(mask, center, radius, 255, -1)
                    

                    blurred_segment = cv2.bitwise_and(blurred_frame, blurred_frame, mask=mask)

                    inverse_mask = cv2.bitwise_not(mask)
                    non_blurred_segment = cv2.bitwise_and(frame, frame, mask=inverse_mask)

                    frame = cv2.add(blurred_segment, non_blurred_segment)
                    
        return frame

    def anonymize_list(self, result_list):
        anonymized = []
        for result in result_list:
            anonymized.append(self.anonymize(result))
        return anonymized