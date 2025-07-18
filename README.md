# TankstellenManager

## 1. Projektübersicht

Der **TankstellenManager** ist eine JavaFX-Anwendung zur Simulation und Verwaltung einer Tankstelle. Ziel ist es, den Betrieb einer Tankstelle zu steuern, Preise festzulegen, Kraftstoffe zu bestellen und auf Kundenströme zu reagieren. Die Anwendung ist als Lernprojekt für objektorientierte Programmierung (OOP) konzipiert. 
Das Projekt ist nachwievor WIP.

---

## 2. Features

- Übersichtliche grafische Benutzeroberfläche (JavaFX)
- Verwaltung von vier Kraftstoffarten (Super 95, Super 95 E10, Super Plus, Diesel)
- Dynamische Preisgestaltung und Preisvalidierung
- Bestellsystem für Kraftstoffe mit Kapazitätsprüfung
- Simulation von Kunden und Umsatz
- Statistikanzeige (Umsatz, Gewinn, Verkaufsvolumen, Kundenzufriedenheit)
- Erweiterbar und modular durch OOP-Prinzipien
- Upgradesystem

---

## 3. Installation & Start

### Voraussetzungen
- Java 17 oder neuer (inkl. JavaFX)
- Maven

### Build & Start

```bash
mvn clean package
mvn javafx:run
```

Alternativ kann die ausführbare JAR im Verzeichnis `TankstellenManager/app/` genutzt werden:

```bash
java -jar TankstellenManager/app/TankstellenManager-1.0-SNAPSHOT.jar
```

Das Programm kann auch ohne jede Installation getestet werden. 
Unter www.adrianschultz.de/Tankstellenmanager.zip können sie das Programm herunterladen.
Entpacken sie es vollständig und führen sie die Anwendung aus. Lassen sie Java ein Moment Zeit.

---

## 4. Bedienung

- **Preise setzen:** Im Hauptfenster können die Preise für alle Kraftstoffarten angepasst werden. Die Eingabe wird validiert.
- **Kraftstoff bestellen:** Über den Button „Kraftstoff bestellen“ kann für jede Sorte eine Bestellung ausgelöst werden. Das Fenster bleibt offen, um mehrere Bestellungen nacheinander zu ermöglichen.
- **Simulation starten/stoppen:** Mit „Tankstelle öffnen/schließen“ wird die Kundensimulation gesteuert.
- **Statistik:** Über das Menü kann eine Statistikansicht geöffnet werden.
- **Guthaben:** Das aktuelle Bargeld wird immer oben rechts angezeigt.

---

## 5. Objektorientierte Struktur & OOP-Erklärung

### Was ist OOP?
Objektorientierte Programmierung (OOP) ist ein Paradigma, bei dem Software in Objekte unterteilt wird. Ein Objekt ist eine Instanz einer Klasse und vereint Daten (Attribute) und Verhalten (Methoden). Die vier Grundprinzipien sind:

1. **Kapselung**: Daten und Methoden werden in Klassen gebündelt. Zugriff auf interne Daten erfolgt meist über Methoden (Getter/Setter).
2. **Vererbung**: Klassen können von anderen Klassen erben und deren Eigenschaften/Verhalten übernehmen.
3. **Polymorphie**: Objekte können über gemeinsame Schnittstellen/Klassen unterschiedlich implementiert werden.
4. **Abstraktion**: Komplexe Sachverhalte werden durch Abstraktion vereinfacht, indem nur relevante Eigenschaften/Verhalten modelliert werden.

### Anwendung im TankstellenManager

#### Kapselung
- Beispiel: Die Klasse `FuelTank` kapselt die Eigenschaften eines Tanks (Füllstand, Kapazität, Kraftstofftyp) und stellt Methoden wie `refill()` bereit.

#### Vererbung
- Die abstrakte Klasse `FuelType` (bzw. das Enum) wird von konkreten Kraftstoffarten wie `Super95`, `Super95E10`, `SuperPlus`, `Diesel` genutzt. (Falls als Klassen umgesetzt: z.B. `Super95 extends FuelType`)
- Kundenarten wie `LoyalCustomer`, `RegularCustomer`, `PriceConsciousCustomer` erben von einer gemeinsamen Basisklasse `Customer`.

#### Polymorphie
- Methoden können mit Parametern vom Typ `FuelType` oder `Customer` arbeiten und erhalten zur Laufzeit konkrete Unterklassen.
- Beispiel: Die Simulation behandelt alle Kunden gleich, obwohl sie sich im Verhalten unterscheiden (z.B. Preisbewusstsein).

#### Abstraktion
- Die Klasse `GameState` abstrahiert den gesamten Zustand der Tankstelle (Guthaben, Tanks, Preise, Statistiken) und bietet eine zentrale Schnittstelle für die Spiellogik.
- Das Interface `GameStateObserver` abstrahiert die Beobachterrolle für UI-Updates.

#### Beispielstruktur (vereinfacht):

```java
// Abstrakte Basisklasse für Kunden
define abstract class Customer {
    abstract void buyFuel(...);
}

class LoyalCustomer extends Customer { ... }
class PriceConsciousCustomer extends Customer { ... }

// Tankmodell
class FuelTank {
    private double capacity;
    private double currentLevel;
    private FuelType fuelType;
    // Methoden: refill(), getFillPercentage(), ...
}

// Spiellogik
class GameState {
    private double cash;
    private Map<FuelType, FuelTank> tanks;
    // ...
}
```

---

## 6. Erweiterbarkeit

Dank OOP ist das Projekt leicht erweiterbar:
- Neue Kraftstoffarten können als neue Klassen/Enums hinzugefügt werden.
- Weitere Kundentypen lassen sich durch Vererbung von `Customer` ergänzen.
- Zusätzliche UI-Komponenten können als eigene Klassen entwickelt werden.
- Die Spiellogik ist durch zentrale Klassen wie `GameState` und Observer-Pattern modular aufgebaut.

---

## 7. Kontakt

Für Fragen, Feedback oder Beiträge:
- E-Mail: Entwicklung@adrianschultz.de
- GitHub-Issues nutzen


---

**Viel Spaß beim Ausprobieren und Lernen mit dem TankstellenManager!** 
