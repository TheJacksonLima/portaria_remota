from enum import Enum

class FaceStatus(Enum):
    IDLE = 0        # amarelo
    SENDING = 1     # azul
    OK = 2          # verde
    DENIED = 3      # vermelho
