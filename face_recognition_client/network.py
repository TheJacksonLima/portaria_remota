import requests
import base64
from crypto_utils import encrypt_bytes
from config import load_config

_config = load_config()
HOST_FACE_EVALUATOR = _config["HOST_FACE_EVALUATOR"]

def send_face_image(image_bytes: bytes) -> bool:
    print(f"Sending requisition to the server")
    try:
        encrypted = encrypt_bytes(image_bytes)
        payload = base64.b64encode(encrypted).decode()

        resp = requests.post(
            HOST_FACE_EVALUATOR,
            json={"image": payload},
            timeout=5
        )
        print(f"Answer: {resp}")

    except Exception as e:
        print(f"Error : {e}")

    finally:
        return resp.ok and resp.json().get("status") == "ok"
