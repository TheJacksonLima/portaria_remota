# 📸 Portaria Remota — Face Recognition

Sistema de **controle de acesso por reconhecimento facial**, utilizando um **cliente embarcado (Raspberry Pi)** e um **servidor Spring Boot**, com **armazenamento de imagens no Firebase Storage**.

O objetivo do projeto é capturar imagens faciais no cliente, enviá-las de forma segura ao servidor e manter um fluxo de autorização centralizado.

---

## 🧱 Arquitetura Geral

```mermaid
flowchart LR;
    c[client (raspberry-pi)]
    s[server]
    f[firebase]

    c --> s --> f
```

---

## 📦 Componentes do Sistema

### 1️⃣ Cliente — Raspberry Pi (Python)

Responsável por:
- Capturar vídeo da câmera
- Detectar e recortar o rosto
- Criptografar a imagem com AES-256-GCM
- Enviar a imagem criptografada ao servidor via HTTP REST

Tecnologias:
- Python 3
- OpenCV
- PySide6
- Requests
- AES-GCM

---

### 2️⃣ Servidor — Spring Boot (Java)

Responsável por:
- Receber imagens criptografadas
- Descriptografar os dados
- Armazenar imagens no Firebase Storage
- Orquestrar o fluxo de autorização

---

## 🔐 Segurança

- Criptografia ponta-a-ponta (AES-256-GCM)
- Nenhuma imagem trafega em texto puro
- Chaves via variáveis de ambiente
- Firebase Service Account fora do repositório

---

## ☁️ Firebase

Utilizado para:
- Armazenamento das imagens
- Integração futura com aprovação humana

---

## ⚙️ Variáveis de Ambiente

### Cliente
```
HOST_FACE_EVALUATOR=http://<IP_SERVIDOR>:9191/api/face/evaluate
AES_KEY=<CHAVE_AES_BASE64>
```

### Servidor
```
FACE_AES_KEY=<CHAVE_AES_BASE64>
FIREBASE_CREDENTIALS=/path/firebase-service-account.json
```

---

## 🚀 Execução

### Servidor
```
./mvnw spring-boot:run
```

### Cliente
```
python app.py
```

---

## 👨‍💻 Autor

Jackson Lima  
Projeto acadêmico — Pós-graduação em IoT
