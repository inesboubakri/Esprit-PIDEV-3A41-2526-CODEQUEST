import json
import os
import sys
from typing import List, Optional, Sequence, Tuple

sys.dont_write_bytecode = True

USER_SITE_PACKAGES = r"C:\Users\msi\AppData\Local\Programs\Python\Python311\Lib\site-packages"
if USER_SITE_PACKAGES not in sys.path:
    sys.path.insert(0, USER_SITE_PACKAGES)

try:
    import cv2
    import face_recognition
    import mysql.connector
    import numpy as np
except ImportError as import_err:
    print(json.dumps({
        "success": False,
        "message": f"Import error: {import_err}. User site-packages: {USER_SITE_PACKAGES}. sys.path: {sys.path}",
        "userId": -1,
    }, ensure_ascii=False))
    sys.exit(1)


DB_CONFIG = {
    "host": "localhost",
    "database": "codyquest",
    "user": "root",
    "password": "",
}

FACE_DISTANCE_THRESHOLD = 0.5
WINDOW_NAME = "CodeQuest Face ID"


def emit(success: bool, message: str, user_id: int = -1) -> int:
    payload = {
        "success": bool(success),
        "message": message,
        "userId": int(user_id),
    }
    print(json.dumps(payload, ensure_ascii=False))
    return 0 if success else 1


def connect_db():
    return mysql.connector.connect(**DB_CONFIG)


def fetch_user_embedding(cursor, user_id: int) -> Optional[np.ndarray]:
    cursor.execute(
        "SELECT face_embedding FROM users WHERE id = %s LIMIT 1",
        (user_id,),
    )
    row = cursor.fetchone()
    if not row or not row[0]:
        return None

    try:
        values = json.loads(row[0])
        if not isinstance(values, list) or len(values) != 128:
            return None
        return np.asarray(values, dtype=np.float64)
    except Exception:
        return None


def fetch_known_embeddings(cursor) -> List[Tuple[int, np.ndarray]]:
    cursor.execute(
        "SELECT id, face_embedding FROM users "
        "WHERE face_embedding IS NOT NULL AND face_embedding <> '' AND is_face_verified = 1"
    )
    known = []
    for user_id, raw_embedding in cursor.fetchall():
        try:
            values = json.loads(raw_embedding)
            if isinstance(values, list) and len(values) == 128:
                known.append((int(user_id), np.asarray(values, dtype=np.float64)))
        except Exception:
            continue
    return known


def store_embedding(cursor, user_id: int, encoding: np.ndarray) -> bool:
    payload = json.dumps([float(value) for value in encoding.tolist()])
    cursor.execute(
        "UPDATE users SET face_embedding = %s, is_face_verified = 1 WHERE id = %s",
        (payload, user_id),
    )
    return True


def draw_overlay(frame, title: str, subtitle: str) -> None:
    cv2.rectangle(frame, (0, 0), (frame.shape[1], 90), (0, 0, 0), -1)
    cv2.putText(frame, title, (20, 34), cv2.FONT_HERSHEY_SIMPLEX, 0.9, (255, 255, 255), 2, cv2.LINE_AA)
    cv2.putText(frame, subtitle, (20, 68), cv2.FONT_HERSHEY_SIMPLEX, 0.65, (180, 255, 180), 2, cv2.LINE_AA)


def capture_face(mode_label: str, help_text: str) -> Optional[np.ndarray]:
    cap = cv2.VideoCapture(0)
    if not cap.isOpened():
        return None

    try:
        while True:
            ok, frame = cap.read()
            if not ok:
                continue

            small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
            rgb_small = cv2.cvtColor(small_frame, cv2.COLOR_BGR2RGB)
            face_locations = face_recognition.face_locations(rgb_small, model="hog")
            face_encodings = face_recognition.face_encodings(rgb_small, face_locations)

            for (top, right, bottom, left) in face_locations:
                top *= 4
                right *= 4
                bottom *= 4
                left *= 4
                cv2.rectangle(frame, (left, top), (right, bottom), (0, 255, 0), 2)

            status_text = help_text if not face_encodings else "Face detected. Hold still..."
            draw_overlay(frame, mode_label, status_text)
            cv2.imshow(WINDOW_NAME, frame)

            if face_encodings:
                cv2.waitKey(700)
                return face_encodings[0]

            key = cv2.waitKey(1) & 0xFF
            if key in (27, ord('q')):
                return None
    finally:
        cap.release()
        cv2.destroyAllWindows()


def register(user_id: int) -> int:
    try:
        conn = connect_db()
    except mysql.connector.Error as exc:
        return emit(False, f"Database connection failed: {exc}", user_id)

    try:
        cursor = conn.cursor()
        cursor.execute("SELECT id FROM users WHERE id = %s", (user_id,))
        if cursor.fetchone() is None:
            return emit(False, f"User {user_id} was not found.", user_id)

        encoding = capture_face("Face ID Registration", "Look at the camera to register your face. Press Q or Esc to cancel.")
        if encoding is None:
            return emit(False, "Face capture was cancelled or no face was detected.", user_id)

        if not store_embedding(cursor, user_id, encoding):
            conn.rollback()
            return emit(False, "Could not store the Face ID data in the database.", user_id)

        conn.commit()
        return emit(True, "Face ID registered successfully.", user_id)
    except mysql.connector.Error as exc:
        conn.rollback()
        return emit(False, f"Database error: {exc}", user_id)
    except Exception as exc:
        conn.rollback()
        return emit(False, f"Face ID registration failed: {exc}", user_id)
    finally:
        try:
            conn.close()
        except Exception:
            pass


def check(user_id: int) -> int:
    try:
        conn = connect_db()
    except mysql.connector.Error as exc:
        return emit(False, f"Database connection failed: {exc}", user_id)

    try:
        cursor = conn.cursor()
        stored_embedding = fetch_user_embedding(cursor, user_id)
        if stored_embedding is None:
            return emit(False, "No stored Face ID was found for this user.", user_id)

        captured_encoding = capture_face("Face ID Verification", "Align your face with the camera. Press Q or Esc to cancel.")
        if captured_encoding is None:
            return emit(False, "Face verification was cancelled or no face was detected.", user_id)

        distance = float(face_recognition.face_distance([stored_embedding], captured_encoding)[0])
        if distance <= FACE_DISTANCE_THRESHOLD:
            return emit(True, f"Face matched successfully (distance={distance:.3f}).", user_id)

        return emit(False, f"Face did not match the stored profile (distance={distance:.3f}).", user_id)
    except mysql.connector.Error as exc:
        return emit(False, f"Database error: {exc}", user_id)
    except Exception as exc:
        return emit(False, f"Face verification failed: {exc}", user_id)
    finally:
        try:
            conn.close()
        except Exception:
            pass


def login() -> int:
    try:
        conn = connect_db()
    except mysql.connector.Error as exc:
        return emit(False, f"Database connection failed: {exc}", -1)

    try:
        cursor = conn.cursor()
        known_faces = fetch_known_embeddings(cursor)
        if not known_faces:
            return emit(False, "No verified Face ID records were found.", -1)

        captured_encoding = capture_face("Face ID Login", "Look at the camera to sign in. Press Q or Esc to cancel.")
        if captured_encoding is None:
            return emit(False, "Face login was cancelled or no face was detected.", -1)

        best_user_id = -1
        best_distance = None

        for user_id, known_encoding in known_faces:
            distance = float(face_recognition.face_distance([known_encoding], captured_encoding)[0])
            if best_distance is None or distance < best_distance:
                best_distance = distance
                best_user_id = user_id

        if best_distance is not None and best_distance <= FACE_DISTANCE_THRESHOLD:
            return emit(True, f"Face recognized successfully (distance={best_distance:.3f}).", best_user_id)

        return emit(False, "No matching Face ID was found.", -1)
    except mysql.connector.Error as exc:
        return emit(False, f"Database error: {exc}", -1)
    except Exception as exc:
        return emit(False, f"Face login failed: {exc}", -1)
    finally:
        try:
            conn.close()
        except Exception:
            pass


def main(argv: Sequence[str]) -> int:
    if not argv:
        return emit(False, "Usage: python face_auth.py <register|check|login> [user_id]", -1)

    mode = argv[0].strip().lower()
    if mode == "login":
        if len(argv) != 1:
            return emit(False, "Login mode does not accept a user ID.", -1)
        return login()

    if mode in {"register", "check"}:
        if len(argv) != 2:
            return emit(False, f"Usage: python face_auth.py {mode} <user_id>", -1)
        try:
            user_id = int(argv[1])
        except ValueError:
            return emit(False, "User ID must be an integer.", -1)
        if mode == "register":
            return register(user_id)
        return check(user_id)

    return emit(False, f"Unknown mode: {argv[0]}", -1)


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))