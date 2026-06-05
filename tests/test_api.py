import requests
import json
import sys

BASE_URL = "http://localhost:8080"
AUTH = ("mario", "password")
AUTH_ANNA = ("anna", "password")

def test_auth():
    print("=== Test Autenticazione ===")
    # Senza credenziali
    r = requests.get(f"{BASE_URL}/api/tasks")
    assert r.status_code == 401, f"Atteso 401, ottenuto {r.status_code}"
    print("✓ 401 senza credenziali OK")

    # Con credenziali sbagliate
    r = requests.get(f"{BASE_URL}/api/tasks", auth=("pippo", "pluto"))
    assert r.status_code == 401, f"Atteso 401, ottenuto {r.status_code}"
    print("✓ 401 con credenziali errate OK")

    # Con credenziali corrette
    r = requests.get(f"{BASE_URL}/api/tasks", auth=AUTH)
    assert r.status_code == 200, f"Atteso 200, ottenuto {r.status_code}"
    print("✓ Autenticazione Mario OK")

    r = requests.get(f"{BASE_URL}/api/tasks", auth=AUTH_ANNA)
    assert r.status_code == 200, f"Atteso 200, ottenuto {r.status_code}"
    print("✓ Autenticazione Anna OK\n")

def test_get_tasks():
    print("=== Test GET Tasks ===")
    # Mario: attesi task 1 (BACKLOG) e task 2 (IN_PROGRESS) dal data.sql
    r = requests.get(f"{BASE_URL}/api/tasks", auth=AUTH)
    assert r.status_code == 200
    tasks = r.json()
    assert len(tasks) == 2, f"Mario dovrebbe vedere 2 task, ma ne ha {len(tasks)}"
    print(f"✓ GET all: {len(tasks)} task")

    # Filtro per BACKLOG
    r = requests.get(f"{BASE_URL}/api/tasks?status=BACKLOG", auth=AUTH)
    tasks = r.json()
    assert len(tasks) == 1
    assert tasks[0]["status"] == "BACKLOG"
    print(f"✓ BACKLOG: {len(tasks)} task")

    # Filtro per IN_PROGRESS
    r = requests.get(f"{BASE_URL}/api/tasks?status=IN_PROGRESS", auth=AUTH)
    tasks = r.json()
    assert len(tasks) == 1
    assert tasks[0]["status"] == "IN_PROGRESS"
    print(f"✓ IN_PROGRESS: {len(tasks)} task")

    # COMPLETED per Mario (nessuno)
    r = requests.get(f"{BASE_URL}/api/tasks?status=COMPLETED", auth=AUTH)
    tasks = r.json()
    assert len(tasks) == 0
    print("✓ COMPLETED: 0 task (come atteso)\n")

def test_get_task_detail():
    print("=== Test Task Detail ===")
    r = requests.get(f"{BASE_URL}/api/tasks/2", auth=AUTH)
    assert r.status_code == 200
    data = r.json()
    assert data["id"] == 2
    assert data["title"] == "Task 2"
    assert data["status"] == "IN_PROGRESS"
    assert data["totalHours"] == 2.5  # dal data.sql
    assert "comments" in data
    assert len(data["comments"]) == 1
    assert data["comments"][0]["authorUsername"] == "mario"
    print("✓ Dettaglio task 2 OK")
    print(f"  - Totale ore: {data['totalHours']}")
    print(f"  - Commenti: {len(data['comments'])}\n")

def test_create_task():
    print("=== Test Creazione Task ===")
    payload = {
        "title": "Task creato da test",
        "description": "Test Python"
    }
    r = requests.post(f"{BASE_URL}/api/tasks", json=payload, auth=AUTH)
    assert r.status_code == 201
    new_task = r.json()
    assert new_task["title"] == payload["title"]
    assert new_task["status"] == "BACKLOG"
    assert new_task["createdByUsername"] == "mario"
    # Verifica che sia stato assegnato a Mario
    assert "Mario Rossi" in new_task["assigneeNames"]
    print(f"✓ Task creato con ID {new_task['id']}")
    global created_task_id
    created_task_id = new_task["id"]
    print()

def test_change_status():
    print("=== Test Cambio Stato ===")
    # Porta il task appena creato a IN_PROGRESS
    r = requests.put(
        f"{BASE_URL}/api/tasks/{created_task_id}/status",
        json={"status": "IN_PROGRESS"},
        auth=AUTH
    )
    assert r.status_code == 200
    print("✓ Creato -> IN_PROGRESS OK")

    # Portalo a COMPLETED
    r = requests.put(
        f"{BASE_URL}/api/tasks/{created_task_id}/status",
        json={"status": "COMPLETED"},
        auth=AUTH
    )
    assert r.status_code == 200
    print("✓ IN_PROGRESS -> COMPLETED OK")

    # Prova transizione non consentita: COMPLETED -> BACKLOG
    r = requests.put(
        f"{BASE_URL}/api/tasks/{created_task_id}/status",
        json={"status": "BACKLOG"},
        auth=AUTH
    )
    assert r.status_code == 500  # IllegalStateException
    print("✓ COMPLETED -> BACKLOG bloccato (errore 500)")
    
    # Prova BACKLOG -> COMPLETED (su task 1, che è rimasto BACKLOG)
    r = requests.put(
        f"{BASE_URL}/api/tasks/1/status",
        json={"status": "COMPLETED"},
        auth=AUTH
    )
    assert r.status_code == 500
    print("✓ BACKLOG -> COMPLETED bloccato (errore 500)\n")

def test_assign_user():
    print("=== Test Assegnazione Utente ===")
    # Assegna Anna al task 1
    r = requests.post(
        f"{BASE_URL}/api/tasks/1/assignees",
        json={"userId": 2},
        auth=AUTH
    )
    assert r.status_code == 200
    print("✓ Anna assegnata al task 1")
    # Verifica che appaia nella lista degli assegnatari
    r = requests.get(f"{BASE_URL}/api/tasks/1", auth=AUTH)
    data = r.json()
    assert "Anna Bianchi" in data["assigneeNames"]
    print("✓ Nome di Anna presente negli assegnatari\n")

def test_log_work():
    print("=== Test Registrazione Ore ===")
    # Mario registra 1.5 ore sul task 2
    r = requests.post(
        f"{BASE_URL}/api/tasks/2/worklogs",
        json={"hours": 1.5},
        auth=AUTH
    )
    assert r.status_code == 200
    print("✓ Ore registrate (1.5) sul task 2")

    # Verifica nuovo totale (2.5 + 1.5 = 4.0)
    r = requests.get(f"{BASE_URL}/api/tasks/2", auth=AUTH)
    data = r.json()
    assert data["totalHours"] == 4.0, f"Atteso 4.0, ottenuto {data['totalHours']}"
    print(f"✓ Nuovo totale ore: {data['totalHours']}\n")

def test_add_comment():
    print("=== Test Aggiunta Commento ===")
    # Aggiunge un commento sul task 2
    r = requests.post(
        f"{BASE_URL}/api/tasks/2/comments",
        json={"text": "Secondo commento via Python"},
        auth=AUTH
    )
    assert r.status_code == 200
    print("✓ Commento aggiunto sul task 2")

    # Verifica che compaia (ordine: più recente prima)
    r = requests.get(f"{BASE_URL}/api/tasks/2", auth=AUTH)
    comments = r.json()["comments"]
    assert len(comments) == 2
    assert comments[0]["text"] == "Secondo commento via Python"  # più recente in cima
    print(f"✓ Ora ci sono {len(comments)} commenti, l'ultimo è in cima\n")

def test_anna_view():
    print("=== Test Visualizzazione di Anna ===")
    # Anna deve vedere i task assegnati: task 2 (IN_PROGRESS), task 3 (COMPLETED) e ora anche task 1 (dopo assegnazione)
    r = requests.get(f"{BASE_URL}/api/tasks", auth=AUTH_ANNA)
    tasks = r.json()
    assert len(tasks) >= 2  # almeno 2 e 1
    task_ids = [t["id"] for t in tasks]
    assert 1 in task_ids, "Anna dovrebbe vedere il task 1"
    assert 2 in task_ids, "Anna dovrebbe vedere il task 2"
    assert 3 in task_ids, "Anna dovrebbe vedere il task 3"
    print(f"✓ Anna vede {len(tasks)} task (ID: {task_ids})")
    print("✓ Test completati con successo!")

if __name__ == "__main__":
    print("🚀 Avvio test API Activity Tracker\n")
    try:
        test_auth()
        test_get_tasks()
        test_get_task_detail()
        test_create_task()   # popola created_task_id
        test_change_status()
        test_assign_user()
        test_log_work()
        test_add_comment()
        test_anna_view()
        print("\n✅ Tutti i test sono passati!")
    except AssertionError as e:
        print(f"\n❌ Test fallito: {e}")
        sys.exit(1)
    except Exception as e:
        print(f"\n❌ Errore imprevisto: {e}")
        sys.exit(1)
