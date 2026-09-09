# Scoreboard-app
# Scoreboard 

A game-time companion that tracks who's losing, tallies what they owe, and settles the bill.

Play anything with a "loser pays" rule — video games, board games, card games, sports, whatever your group plays. Set a price per loss for each game, log losses as you go, and let the app work out exactly who owes who.

---

##  Quick Links

*  **Live Web Demo:** [Try Scoreboard Online](https://okmcacu.github.io/Scoreboard-app/)
*  **Download App:** [Latest Release](https://github.com/okmcacu/Scoreboard-app/releases/latest)

---
## 📸 Preview

| Board | Settle Up |
| :---: | :---: |
| <img width="100%" alt="frontpage" src="https://github.com/user-attachments/assets/ff572338-650c-4a32-bd33-70f1616ab49f" /> | <img width="100%" alt="settle" src="https://github.com/user-attachments/assets/237ef622-ceca-4648-b653-be668eb548fe" /> |
| **History** | **Stats** |
| <img width="100%" alt="history" src="https://github.com/user-attachments/assets/2bfb6b63-0a1a-4de6-ad46-7f06efbb5b25" /> | <img width="100%" alt="stats" src="https://github.com/user-attachments/assets/78471636-1d87-4106-a3fd-8a4b0badddb2" /> |
##  Features

* **Board** — Log a loss for any player in any game with one tap; pick the winner and both tallies update instantly. Undo the last entry if you tap the wrong button.
* **Stats** — All-time win/loss record and win % per player, per game. Export your stats to CSV, or import a CSV to restore/merge history.
* **Settle** — Automatically calculates what each player owes, broken down by game (losses × price per loss), plus a grand total. Mark it "Paid" to close out the round.
* **History** — Every past settlement is kept with its date, total, and per-player breakdown, so you can look back at who paid what.
* **Games & Players** — Add any game you want to track, set a custom price-per-loss for each one, and manage your player roster, all from one screen.

---

##  Built With

* **App:** Kotlin (Android)
* **Web Preview / Demo:** HTML, CSS, JavaScript
* **Hosting:** GitHub Pages
* **Version Control:** Git & GitHub

---

##  Getting Started

**Try it in the browser** — no install needed, just open the [live demo](https://okmcacu.github.io/Scoreboard-app/).

**Install the app:**
1. Go to the [Releases page](https://github.com/okmcacu/Scoreboard-app/releases/latest).
2. Download the latest build.
3. Install it on your device (you may need to allow installs from unknown sources for a sideloaded APK).

**Run the source locally:**
```bash
git clone https://github.com/okmcacu/Scoreboard-app.git
cd Scoreboard-app
```
Open the project in Android Studio to build and run it yourself.

---

##  Contributing

Pull requests are welcome. A few ideas if you're looking for a place to start:
* Persistent storage across sessions
* Editable/deletable individual loss entries
* Multi-currency support

---

##  License

This project is open-source and available under the [MIT License](LICENSE).
