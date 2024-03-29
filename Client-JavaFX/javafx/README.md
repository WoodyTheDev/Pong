# PongClient JavaFX

Dieses Projekt wurde mit JavaFX für die Entwicklung eines Pong-Spiel-Clients erstellt.

## Entwicklungsumgebung/Notwendige Installationen

Für die Entwicklung mit diesem JavaFX-Client sind folgende Voraussetzungen nötig:

- Mindestenss ein JDK (Java Development Kit) 11+ installiert
- Eine IDE (z.B. IntelliJ IDEA, Eclipse) die JavaFX unterstützt - nur erforderlich beim Starten des Clients mit Maven
- Maven für die Verwaltung von Abhängigkeiten und Builds - nur erforderlich beim Starten des Clients mit Maven

## Installation und Setup mit Maven

Bevor du den PongClient starten kannst, stelle sicher, dass Java und Maven korrekt auf deinem System installiert sind.

1. Öffne ein Terminal und navigiere zum Hauptverzeichnis des PongClient-Projekts.

2. Führe den folgenden Befehl aus, um die erforderlichen Abhängigkeiten zu installieren:
   `mvn clean install`

## Client mit Maven starten

1. Starte den PongClient, indem du den folgenden Befehl in deinem Terminal ausführst:
   `mvn exec:java`
   
2. Das Spiel sollte nun starten. Sollten Fehler auftreten, überprüfe die Konfiguration deines JDK und des Maven-Setups.

## Client ohne Maven und IDE starten mit der pong.jar

1. Kopiere die Datei pong.jar in ein Verzeichnis deiner Wahl oder belasse sie im ursprünglichen Verzeichnis.

2. Öffne ein Terminal und navigiere zum Verzeichnis, in dem pong.jar liegt.

3. Führe den folgenden Befehl aus, um den Pong Client zu starten:
   `java -jar pong.jar`

# Pong-Spiel JavaFX

Dies ist eine Anleitung zur Installation und Nutzung des Pong-Spiels mit dem JavaFX-Client.

## User Registrieren/Einloggen/Ausloggen

Nachdem du den Client wie oben beschrieben gestartet hast, folge diesen Schritten, um einen User anzulegen oder einzuloggen:

1. Das Spiel startet mit dem Startbildschirm und dem Hauptmenü.

2. Zu Beginn gibt es die Optionen 'Login' und 'Registrieren'.

3. Wenn du dich noch nicht registriert hast, tue dies über den Button 'Registrieren' und gebe deinen Daten in dem Dialog ein.

4. Dein User wird bei erfolgreicher Anlage auch automatisch eingeloggt.

5. Bist du bereits registriert, kannst du dich über den 'Login' Button im folgenden Dialog mit deiner E-Mail und deinem Passwort einloggen.

## Spielerhistorie

Ein eingeloggter User kann auf seine/ihre Spielhistorie zugreifen:

1. Wähle 'Spielerhistorie' im Hauptmenü.

2. Eine Tabelle mit all deinen bisherigen Ergebnissen wird angezeigt - chronologisch abwärts sortiert.

## User Ausloggen

Ein eingeloggter User kann sich wieder ausloggen:

1. Wenn du eingeloggt bist, kannst du dich über den Button 'Logout' im Hauptmenü wieder ausloggen.

## User Ändern

Ein eingeloggter User kann seine Daten ändern:

1. Wenn du eingeloggt bist, kannst du über den Button 'Ändern' im Hauptmenü dein Passwort und/oder dein Bild ändern.

## Ein Spiel starten

Sobald du eingeloggt bist, kannst du ein Spiel starten:

1. Wähle 'Spiel starten' im Hauptmenü.

2. Du gelangst zunächst in den Wartebereich, wo du auf einen zweiten Spieler wartest.

3. Sobald ein zweiter Spieler gefunden wurde, beginnt das Spiel.

4. Der Wartebereich kann jederzeit über den Button 'Zurück zum Hauptmenü' verlassen werden.

5. Das 'X' auf der Seite Punktestandsanzeige gibt an, welches Paddel dein Paddel ist (also links oder rechts).

6. Gesteuert wird das Paddel wie folgt: Aufwärts -> 'Pfeil rauf'- oder 'W'-Taste der Tastatur / Abwärts -> 'Pfeil rauf'- oder 'S'-Taste der Tastatur

7. Das Spiel kann jederzeit über den Button 'Zurück zum Menü' verlassen werden.

8. Wenn das Spiel vorbei ist, wird der Gewinner angezeigt und es gibt die Optionen 'Zurück zum Menü' und 'Neues Spiel'.

## Pong Client beenden

1. Wenn du im Hauptmenü auf den Button 'Exit' klickst, wird das Pong Client Fenster geschlossen.

2. Zu jeder Zeit lässt sich der Client durch einen Klick auf den 'X'-Button beenden.


	