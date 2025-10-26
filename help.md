# 🧭 Minecraft Server – Command Übersicht

Eine Übersicht aller verfügbaren Befehle mit Beschreibung und Parametern.

---

## ⚙️ **Server Moderation**

| Command | Parameter / Subcommand | Beschreibung |
|----------|------------------------|---------------|
| `/admin` | `aua (playername)` | Fügt einem Spieler Schaden hinzu |
| | `debug` | Debug-Modus aktivieren oder ausgeben |
| | `filecombine` | Kombiniert alte mit neuen `.yml`-Dateien |
| | `help` | Zeigt eine Liste der verfügbaren Subcommands |
| | `lootChest open` | Zeigt den theoretischen Inhalt der Lootchests |
| | `lootChest generierte` | Generiert eine Lootchest an der aktuellen Position |
| | `lootChest menü` | Öffnet das Lootchest-Itemmenü |
| | `resetRandomtp (playername)` | Setzt den täglichen Random-TP des Spielers zurück |
| | `restoreinv (playername)` | Stellt das Inventar des Spielers von vor 10 Minuten wieder her |
| | `skull (playername)` | Generiert den Kopf des angegebenen Spielers |
| | `sudo (playername) (command)` | Führt einen Command im Namen eines anderen Spielers aus |
| | `trader` | Generiert einen Trader |
| | `reloadConfig` | Lädt die Konfiguration neu (Kommentar: „Bernd ist zu faul…“) |

---

## 🔁 **Teleportation & Reisen**

| Command | Parameter / Subcommand | Beschreibung |
|----------|------------------------|---------------|
| `/back` | – | Teleportiert dich an die letzte Position zurück (nach einem Teleport) |
| `/randomTP` | – | Teleportiert dich zufällig in der Welt (Radius & Center in `config.yml`) |

### `/reise` – Reisesystem

| Subcommand | Parameter | Beschreibung |
|-------------|------------|---------------|
| `list` | – | Listet alle eingetragenen Reise-IDs |
| `remove` | `(ID)` | Entfernt die angegebene ID aus der Liste |
| `reset` | – | Leert alle gesetzten IDs |
| `setup setup` | `(ID)` | Platziert eine neue ID an der aktuellen Position |
| `setup` | `(playername) (ID)` | Schreibt den Spieler in die angegebene ID |
| `/rbsuche` | `(playername)` | Markiert den Spieler-Teleport im Reisebüro |

---

## 🧰 **Spieler-Management**

| Command | Parameter / Subcommand | Beschreibung |
|----------|------------------------|---------------|
| `/mute` | `(playername) (grund)` | Mutet den Spieler permanent (nur Default-Gruppe) |
| `/tempban` | `(playername) (zeit) (grund)` | Sperrt den Spieler temporär. Beispiel:<br>`/tempban MisterGoody 1h besoffen` <br>(Zeitformate: `1s`, `1h`, `1d`, `1w`, `1m`, `1y`) |
| `/unban` | `(playername)` | Entbannt einen Spieler |
| `/request accept` | `(playername)` | Schaltet einen neuen Spieler frei |
| `/request deny` | `(playername) (grund)` | Lehnt den Spieler ab und entzieht Rechte |
| `/request kick` | – | Kickt Spieler im Notfall |
| `/playerinfo` | `(playername)` | Zeigt detaillierte Spielerinformationen im Chat |
| `/prefix` | – | Setzt einen Prefix für den ausführenden Spieler |

---

## 🎁 **Loot & Items**

| Command | Parameter / Subcommand | Beschreibung |
|----------|------------------------|---------------|
| `/loot give all` | – | Zeigt (vermutlich) alle verfügbaren Loot-Gegenstände |
| *(siehe auch `/admin lootChest`)* | | |

---
