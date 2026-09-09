#  Java MVC Vulnerability Scanner

Un scanner de vulnérabilités web léger, modulaire et extensible, entièrement développé en Java natif. Conçu selon l'architecture **Modèle-Vue-Contrôleur (MVC)**, cet outil permet d'auditer la sécurité des applications web en identifiant des failles critiques telles que les Injections SQL et le Cross-Site Scripting (XSS).

Ce projet a été pensé pour s'intégrer dans des workflows de cybersécurité modernes : les résultats sont générés au format **JSON structuré**, facilitant leur ingestion par des plateformes d'analyse Blue Team, des SIEM, ou des solutions d'orchestration de type SOAR.

##  Fonctionnalités

*   **Architecture MVC stricte :** Séparation claire entre les payloads (Modèle), la logique d'attaque (Contrôleur) et l'affichage (Vue).
*   **Détection d'Injections SQL (SQLi) :** Analyse des réponses HTTP pour identifier les erreurs de syntaxe de base de données (ex: MySQL/MariaDB) et les fuites de données.
*   **Détection de Cross-Site Scripting (Reflected XSS) :** Vérification du reflet des payloads JavaScript dans le code source de la page.
*   **Gestion de Sessions Authentifiées :** Support complet des cookies de session (ex: `PHPSESSID`) pour auditer les espaces restreints.
*   **Génération de Rapports JSON :** Export horodaté (ISO 8601) incluant le type de vulnérabilité, la sévérité, l'URL affectée et le paramètre vulnérable.

##  Structure du Projet

```text
📁 Java-Web-Scanner/
├── 📁 src/
│   ├── 📁 model/
│   │   ├── ScannerModel.java       # Stockage des payloads et gestion des vulnérabilités
│   │   └── Vulnerability.java      # Objet représentant une faille (avec méthode toJson)
│   ├── 📁 view/
│   │   └── ScannerView.java        # Interface console et formatage JSON
│   ├── 📁 controller/
│   │   └── ScannerController.java  # Moteur de requêtes HTTP et logique de détection
│   └── Main.java                   # Point d'entrée et configuration de la cible
├── 📁 bin/                         # Fichiers compilés (.class) - ignorés par git
├── .gitignore
└── README.md
```

##  Prérequis

*   **Java Development Kit (JDK) 11** ou supérieur (utilise `java.net.http.HttpClient`).
*   **Docker** (recommandé pour déployer une application cible vulnérable comme DVWA).

##  Installation et Déploiement

### 1. Cloner le dépôt
```bash
git clone https://github.com/Basile-Dufrene/java-web-scanner.git
cd java-web-scanner
```

### 2. Configurer une cible de test (DVWA)
Il est illégal de scanner des applications sans autorisation. Utilisez l'application Damn Vulnerable Web App (DVWA) en local :
```bash
docker run --rm -it -p 80:80 vulnerables/web-dvwa
```
Accédez à `http://localhost`, connectez-vous (`admin` / `password`), allez dans *Setup / Reset DB* et initialisez la base de données. Réglez la sécurité sur **Low**.

### 3. Configurer l'attaque dans le scanner
Ouvrez le fichier `src/Main.java`.
1. Récupérez votre cookie de session (`PHPSESSID`) depuis votre navigateur (F12 > Stockage > Cookies).
2. Insérez-le dans le code :
```java
String myCookies = "PHPSESSID=votre_valeur_ici; security=low";
model.setSessionCookie(myCookies);
```
3. Définissez l'URL cible et le paramètre à tester.

##  Compilation et Utilisation

Compilez l'ensemble du projet en respectant la structure des packages :
```bash
javac -d bin src/model/*.java src/view/*.java src/controller/*.java src/Main.java
```

Lancez le scanner :
```bash
java -cp bin Main
```

### Exemple de sortie :
```json
[
  {
    "timestamp": "2026-09-09T07:07:42.837Z",
    "type": "Injection SQL",
    "severity": "HIGH",
    "url": "http://localhost/vulnerabilities/sqli/",
    "parameter": "Submit=Submit&id",
    "payload": "' OR 1=1--"
  }
]
```

##  Auteur
**Basile DUFRENE**  
*Projet développé dans le cadre d'études et de recherches en Cybersécurité.*

## ⚠️ Avertissement Légal
Cet outil a été développé à des fins **strictement éducatives et défensives**. L'auteur décline toute responsabilité quant à l'utilisation de ce programme sur des systèmes d'information sans le consentement explicite de leurs propriétaires.
