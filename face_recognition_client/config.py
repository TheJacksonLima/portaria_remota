# config.py
import os
import base64
from dotenv import load_dotenv

def load_config():
    # Carrega .env se existir (não sobrescreve variáveis do sistema)
    load_dotenv()

    host = os.getenv("HOST_FACE_EVALUATOR")
    aes_key_b64 = os.getenv("FACE_AES_KEY")

    if not host:
        raise RuntimeError(
            "HOST_FACE_EVALUATOR não definido. "
            "Configure no .env ou como variável de ambiente."
        )

    if not aes_key_b64:
        raise RuntimeError(
            "FACE_AES_KEY não definida. "
            "Configure no .env ou como variável de ambiente."
        )

    try:
        aes_key = base64.b64decode(aes_key_b64)
    except Exception as e:
        raise RuntimeError("FACE_AES_KEY não é Base64 válida") from e

    if len(aes_key) not in (16, 24, 32):
        raise RuntimeError(
            f"FACE_AES_KEY inválida: {len(aes_key)} bytes "
            "(esperado 16, 24 ou 32)"
        )

    return {
        "HOST_FACE_EVALUATOR": host,
        "AES_KEY": aes_key
    }
