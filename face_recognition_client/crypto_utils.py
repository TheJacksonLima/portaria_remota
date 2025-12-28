import os
import base64
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
from config import load_config

_config = load_config()
AES_KEY = _config["AES_KEY"]


def encrypt_bytes(data: bytes) -> bytes:
    aesgcm = AESGCM(AES_KEY)
    nonce = os.urandom(12)
    encrypted = aesgcm.encrypt(nonce, data, None)
    return nonce + encrypted
