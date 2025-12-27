import sys
import cv2
import threading
import time

from PySide6.QtWidgets import (
    QApplication, QWidget, QLabel,
    QPushButton, QVBoxLayout
)
from PySide6.QtCore import QTimer
from PySide6.QtGui import QImage, QPixmap

from face_crop import FaceCropper
from network import send_face_image
from face_status import FaceStatus


class CameraApp(QWidget):
    def __init__(self):
        super().__init__()

        self.face_status = FaceStatus.IDLE
        self.face_img = None
        self.face_rect = None

        self.request_in_flight = False
        self.last_send_time = 0
        self.cooldown_seconds = 3

        self.authorized_until = 0
        self.auth_valid_seconds = 60

        self.setWindowTitle("Controle de Acesso")
        self.resize(800, 650)

        self.video_label = QLabel()
        self.video_label.setFixedSize(640, 480)
        self.video_label.setStyleSheet("background-color: black")

        self.btn_portaria = QPushButton("Acionar Portaria")
        self.btn_abrir = QPushButton("Abrir Porta")
        self.btn_abrir.setEnabled(False)
        self.btn_portaria.setVisible(False)

        layout = QVBoxLayout()
        layout.addWidget(self.video_label)
        layout.addWidget(self.btn_portaria)
        layout.addWidget(self.btn_abrir)
        self.setLayout(layout)

        self.face_cropper = FaceCropper()

        self.timer = QTimer()
        self.timer.timeout.connect(self.update_frame)
        self.timer.start(30)

        self.btn_portaria.clicked.connect(self.acionar_portaria)
        self.btn_abrir.clicked.connect(self.abrir_porta)

    def is_authorized(self):
        return time.time() < self.authorized_until

    def authorized_seconds_left(self):
        remaining = int(self.authorized_until - time.time())
        return max(0, remaining)

    def update_frame(self):
        ret, frame = self.face_cropper.read()
        if not ret:
            return

        frame, self.face_img, self.face_rect = self.face_cropper.detect_and_crop(frame)
        now = time.time()

        if not self.is_authorized() and self.face_status == FaceStatus.OK:
            self.face_status = FaceStatus.IDLE

        if (
            self.face_img is not None and
            not self.is_authorized() and
            not self.request_in_flight and
            now - self.last_send_time > self.cooldown_seconds
        ):
            self.enviar_para_servidor()

        self.btn_abrir.setEnabled(self.is_authorized())

        if self.face_img is None and self.face_status != FaceStatus.SENDING:
            self.face_status = FaceStatus.IDLE

        if self.face_rect is not None:
            x1, y1, x2, y2 = self.face_rect
            color = self.get_box_color()
            cv2.rectangle(frame, (x1, y1), (x2, y2), color, 2)

            if self.is_authorized():
                seconds = self.authorized_seconds_left()
                cv2.putText(
                    frame,
                    f"AUTORIZADO ({seconds}s)",
                    (x1, y1 - 10),
                    cv2.FONT_HERSHEY_SIMPLEX,
                    0.8,
                    (0, 255, 0),
                    2
                )

        frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        h, w, ch = frame_rgb.shape
        image = QImage(frame_rgb.data, w, h, ch * w, QImage.Format_RGB888)
        self.video_label.setPixmap(QPixmap.fromImage(image))

    def acionar_portaria(self):
        print("➡️ Acionar portaria (manual)")

    def enviar_para_servidor(self):
        if self.face_img is None:
            return

        self.face_status = FaceStatus.SENDING
        self.request_in_flight = True
        self.last_send_time = time.time()

        def task():
            try:
                jpeg = self.face_to_jpeg(self.face_img)
                ok = send_face_image(jpeg)

                if ok:
                    self.face_status = FaceStatus.OK
                    self.authorized_until = time.time() + self.auth_valid_seconds
                else:
                    self.face_status = FaceStatus.DENIED

            except Exception as e:
                print("Erro ao enviar face:", e)
                self.face_status = FaceStatus.DENIED
            finally:
                self.request_in_flight = False

        threading.Thread(target=task, daemon=True).start()

    def abrir_porta(self):
       if not self.is_authorized():
            return

       print("🚪 Porta aberta")

       self.authorized_until = 0
       self.face_status = FaceStatus.IDLE
       self.btn_abrir.setEnabled(False)
       time.sleep(3) 
       print("🚪 Porta fechada")



    def face_to_jpeg(self, face_img):
        ret, buf = cv2.imencode(
            ".jpg",
            face_img,
            [cv2.IMWRITE_JPEG_QUALITY, 85]
        )
        return buf.tobytes()

    def get_box_color(self):
        if self.is_authorized():
            return (0, 255, 0) 

        return {
            FaceStatus.IDLE: (0, 255, 255),      # 🟨
            FaceStatus.SENDING: (255, 0, 0),     # 🔵
            FaceStatus.OK: (0, 255, 0),          # 🟩
            FaceStatus.DENIED: (0, 0, 255)       # 🟥
        }[self.face_status]

    def closeEvent(self, event):
        self.face_cropper.release()
        event.accept()


if __name__ == "__main__":
    app = QApplication(sys.argv)
    window = CameraApp()
    window.show()
    sys.exit(app.exec())
