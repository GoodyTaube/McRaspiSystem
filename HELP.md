
## 📘 McRaspiSystem – Befehlsübersicht

---

### 🔧 `/admin`

| Befehl | Beschreibung |
|--------|-------------|
| `/admin aua (playername)` | Fügt dem Spieler Schaden hinzu |
| `/admin debug` | Debug-Modus |
| `/admin filecombine` | Kombiniert alte mit neuen YML-Dateien |
| `/admin help` | Zeigt Liste der Subsysteme |
| `/admin lootChest open` | Zeigt theoretischen Inhalt der Lootchests |
| `/admin lootChest genererte` | Generiert die Lootchest an der aktuellen Position |
| `/admin lootChest menü` | Öffnet das Lootchest-Itemmenü |
| `/admin resetRandomtp (playername)` | Setzt täglichen Random-TP zurück |
| `/admin restoreinv (playername)` | Stellt Inventar von vor 10 Minuten wieder her |
| `/admin skull (playername)` | Generiert Kopf des angegebenen Spielers |
| `/admin sudo (playername) (command)` | Führt einen Befehl über den Spieler aus |
| `/admin trader` | Generiert einen Trader |
| `/admin reloadConfig` | (Noch nicht implementiert) |

---

### ⏪ `/back`

| Befehl | Beschreibung |
|--------|-------------|
| `/back` | Teleportiert dich zur letzten Position nach einem Teleport |

---

### 📦 `/loot`

| Befehl | Beschreibung |
|--------|-------------|
| `/loot give all` | Gibt Loot an alle Spieler (unvollständig dokumentiert) |

---

### 🧭 `/reise`

| Befehl | Beschreibung |
|--------|-------------|
| `/reise list` | Listet alle gespeicherten IDs |
| `/reise remove (ID)` | Entfernt die angegebene ID |
| `/reise reset` | Leert alle IDs |
| `/reise setup (ID)` | Setzt ID auf aktuelle Position |
| `/reise setup (playername) (ID)` | Fügt Spieler zur ID hinzu |

---

### 🧳 `/rbsuche`

| Befehl | Beschreibung |
|--------|-------------|
| `/rbsuche (playername)` | Markiert Spieler-Teleport im Reisebüro |

---

### 🔇 `/mute`

| Befehl | Beschreibung |
|--------|-------------|
| `/mute (playername) (grund)` | Mutet den Spieler dauerhaft (nur Default-Gruppe) |

---

### ⏱ `/tempban` / `/unban`

| Befehl | Beschreibung |
|--------|-------------|
| `/tempban (playername) (zeit) (grund)` | Temporärer Bann (z. B. `/tempban Mistergoody 1h besoffen`) <br> Zeitangaben: `1s`, `1h`, `1d`, `1w`, `1m`, `1y` |
| `/unban (playername)` | Entbannt den Spieler |

---

### 📨 `/request`

| Befehl | Beschreibung |
|--------|-------------|
| `/request accept (playername)` | Gibt dem Spieler Rechte/Freischaltung |
| `/request deny (playername) (grund)` | Lehne Anfrage ab & entferne Rechte |
| `/request kick` | Kickt Spieler im Notfall |

---

### 👤 Weitere Befehle

| Befehl | Beschreibung |
|--------|-------------|
| `/playerinfo (playername)` | Zeigt Informationen über den Spieler |
| `/prefix` | Setzt Prefix für den ausführenden Spieler |
| `/randomTP` | Teleportiert Spieler an zufällige Position <br> (Radius & Zentrum in `config.yml`) |

---

## 🧩 Hinweise

- Diese Liste deckt alle verfügbaren Systeme & Subsystem-Kommandos ab.
- Manche Befehle benötigen Admin-Rechte.
- Weitere Systeme werden modular über die Konfiguration geladen.

---

## 📎 Verwendung

Du kannst diese Datei speichern als:  
📄 `HELP.md`  
und in deiner `README.md` so verlinken:

```markdown
👉 Befehlsübersicht: [HELP.md](HELP.md)
```
