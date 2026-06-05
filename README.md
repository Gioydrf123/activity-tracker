# Activity Tracker - Backend

Backend RESTful per la gestione delle attività di un team di sviluppo.

## 🛠 Stack

Spring Boot 3 • Spring Security • Spring Data JPA • H2 Database • Maven

## ✨ Funzionalità principali

- Autenticazione HTTP Basic (stateless)
- Task organizzati per stato: **Backlog**, **In Progress**, **Completati**
- Ogni stato ha un colore associato
- Registrazione ore lavorate su ogni task
- Commenti ordinati dal più recente
- Assegnazione multipla dei task ai membri del team
- Transizioni di stato controllate

## 🚀 Avvio rapido

```bash
mvn clean spring-boot:run