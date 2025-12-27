import cv2


class FaceCropper:
    def __init__(self, camera_index=0):
        self.cap = cv2.VideoCapture(camera_index)
        self.cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)
        self.cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)

        self.face_cascade = cv2.CascadeClassifier(
            "haarcascade_frontalface_default.xml"
        )

    def read(self):
        return self.cap.read()

    def detect_and_crop(self, frame):
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        faces = self.face_cascade.detectMultiScale(
            gray,
            scaleFactor=1.3,
            minNeighbors=5,
            minSize=(100, 100)
        )

        for (x, y, w, h) in faces:
            pad = int(0.2 * w)

            x1 = max(0, x - pad)
            y1 = max(0, y - pad)
            x2 = min(frame.shape[1], x + w + pad)
            y2 = min(frame.shape[0], y + h + pad)

            face_img = frame[y1:y2, x1:x2]

            return frame, face_img, (x1, y1, x2, y2)

        return frame, None, None

    def release(self):
        self.cap.release()

