# Schabaschka – Backend (Java 17, Spring Boot 3)

> Hinweis  
> Dieses Repository ist **noch nicht vollständig fertig**.  
> Aus Zeitgründen sind **Authentifizierung/Autorisierung** und **automatisierte Tests** (Unit/Integration) noch nicht implementiert.  
> Der Fokus dieser Version liegt auf der **Backend-Architektur, Datenmodellierung und REST-API**.

---

## 🇩🇪 Projektbeschreibung (Deutsch)

**Schabaschka** ist eine kleine Plattform für lokale Jobs und Miniaufträge – ähnlich wie Kleinanzeigen/OLX:  
Arbeitgeber erstellt einen **Job**, Worker bewirbt sich mit einem **Offer**, danach gibt es einen einfachen **Chat pro Offer**.

### Tech-Stack

- Java 17
- Spring Boot 3.x
- Maven Wrapper (`./mvnw`)
- PostgreSQL (z.B. via Docker)
- Spring Data JPA, Hibernate
- Flyway für Datenbank-Migrationen

### Implementierte Features (Stand dieser Version)

**Users & Profiles**

- Benutzer werden in der Tabelle `users` gespeichert, Profilinformationen in `profiles`.
- Ein Job ist über `employerId` mit dem User/Profil verknüpft.
- In den Job-DTOs werden zusätzliche Informationen (z.B. Name/Telefon des Arbeitgebers) über das Profil aufgelöst.

**Jobs**

- Erstellen, Aktualisieren und Löschen von Jobs.
- Job-Status (z.B. `OPEN`, `IN_PROGRESS`, `DONE`).
- Suche mit Filtern (Stadt, Kategorie, Text) und Pagination.
- REST-API unter `/api/jobs`.

**Offers**

- Worker können sich auf einen Job bewerben (Offer pro Job/Worker-Kombination).
- Schutz vor doppelten Offers für denselben Job und Worker.
- Statusverwaltung (`PENDING`, `ACCEPTED`, `REJECTED`, `CANCELED`).
- Wenn ein Job auf `DONE` gesetzt wird, werden automatisch alle `PENDING`-Offers zu `REJECTED`.

**Chat pro Offer**

- Tabelle `offer_messages` und Entity `OfferMessage`.
- REST-Endpunkte:
    - `GET  /api/offers/{offerId}/messages` – Chatverlauf.
    - `POST /api/offers/{offerId}/messages` – neue Nachricht.
- `senderId` wird geprüft:
    - Nachricht darf nur von **Worker** des Offers oder **Arbeitgeber** des zugehörigen Jobs gesendet werden.
- Antwortformat über DTOs (`OfferMessageDto`, `NewOfferMessageDto`).

### Noch nicht umgesetzt (bewusst offen gelassen)

Diese Version ist als **Lern- und Beispielprojekt** gedacht:

- Keine Authentifizierung/Autorisierung (kein Login, keine Rollenprüfung im HTTP-Layer).
- Keine automatisierten Tests (Unit- und Integrationstests fehlen noch).
- Eingabedaten werden nur begrenzt validiert (kein Bean Validation Setup).

### Projektstruktur (Backend)

- `schabaschka.user.*` – Benutzer
- `schabaschka.profile.*` – Profile
- `schabaschka.job.*` – Jobs
- `schabaschka.offer.*` – Offers (Bewerbungen)
- `schabaschka.chat.*` – Chat / OfferMessages

Pro Feature:

- `model` – JPA-Entities
- `dao` – Repositories (Spring Data JPA)
- `dto` – Datenobjekte für das Frontend
- `service` – Geschäftslogik, Mapping Entity ↔ DTO
- `controller` – dünne REST-Controller (keine Business-Logik)
