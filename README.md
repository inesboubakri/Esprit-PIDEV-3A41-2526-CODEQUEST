# CodeQuest Desktop - Application de Bureau pour l'Apprentissage de la Programmation

## Table des Matières

- [Description du Projet](#description-du-projet)
- [Fonctionnalités Principales](#fonctionnalités-principales)
- [Architecture](#architecture)
- [Installation](#installation)
- [Utilisation](#utilisation)
- [Contribution](#contribution)
- [Licence](#licence)

---

## Description du Projet

**CodeQuest Desktop** est une application de bureau complète d'apprentissage et de gestion pédagogique en programmation. Elle permet aux formateurs de créer et gérer des **cours**, des **projets** et des **problèmes de code**, tandis que les étudiants peuvent suivre des cours, soumettre des solutions de code qui sont **automatiquement validées**, accumuler des points et consulter leur progression.

### Objectif

Offrir une solution complète et autonome pour l'**enseignement de la programmation** avec :
- Validation automatique des solutions de code
- Gestion centralisée des ressources pédagogiques
- Gamification pour motiver les apprenants
- Suivi complet de la progression
- Interface graphique intuitive et responsive

### Problème résolu

- ❌ **Avant** : Validation manuelle laborieuse, ressources dispersées, dépendance internet
- ✅ **Après** : Validation instantanée, plateforme centralisée, application autonome sans dépendance web

### Technologies utilisées

- **Frontend** : JavaFX (Java UI Framework)
- **Backend** : Java 17+ avec architecture MVC
- **Base de données** : SQLite / MySQL (configurable)
- **Build Tool** : Maven
- **Validation de code** : Compilateurs locaux (Java, Python, etc.)
- **Gestion de dépendances** : Maven Central Repository
- **Interfaces** : FXML (XML-based UI markup)

---

## Architecture

### Structure du projet

```
CodeQuest-Java/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/codequest/
│   │   │       ├── controller/          # Contrôleurs JavaFX
│   │   │       ├── model/               # Modèles de données (User, Course, Problem, etc.)
│   │   │       ├── service/             # Logique métier
│   │   │       ├── repository/          # Accès aux données
│   │   │       ├── validator/           # Validation de code
│   │   │       ├── util/                # Utilitaires
│   │   │       └── Main.java            # Point d'entrée
│   │   ├── resources/
│   │   │   ├── fxml/                    # Interfaces FXML
│   │   │   │   ├── main-window.fxml
│   │   │   │   ├── problem-list.fxml
│   │   │   │   ├── course-view.fxml
│   │   │   │   ├── admin-panel.fxml
│   │   │   │   └── ...
│   │   │   ├── css/                     # Feuilles de style
│   │   │   │   └── application.css
│   │   │   ├── images/                  # Ressources graphiques
│   │   │   └── config.properties        # Configuration
│   │   └── sql/
│   │       ├── schema.sql               # Schéma de base de données
│   │       └── initial-data.sql         # Données initiales
│   └── test/
│       ├── java/                        # Tests unitaires
│       └── resources/                   # Ressources de test
├── pom.xml                              # Configuration Maven
├── README.md                            # Ce fichier
└── .gitignore
```

### Dépendances principales (pom.xml)

```xml
<!-- JavaFX - Interface graphique -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
</dependency>

<!-- JDBC - Accès aux données -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>

<!-- Logging -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
</dependency>
```

---

## Fonctionnalités Principales

### 1. Gestion des Utilisateurs et Authentification

- **Inscription et connexion** avec email/mot de passe
- **Profils utilisateur** : Photo, bio, statistiques personnelles
- **Rôles et permissions** : Admin, Formateur, Étudiant
- **Session persistante** : Mémorisation de la dernière session

### 2. Gestion des Cours

Les formateurs peuvent créer des cours structurés :

- **Structure de cours** : Titre, description, objectifs d'apprentissage
- **Chapitres** : Division du contenu en sections logiques
- **Gestion d'accès** : Publics ou réservés aux inscrits
- **Suivi de progression** : Voir le % de complétion par étudiant
- **Ressources associées** : Associer des problèmes, projets
- **Certificats** : Générer des certificats PDF à la fin du cours
- **Points de compétence** : Attribuer des points par chapitre complété

### 3. Gestion des Projets

Créer des projets pratiques complexes :

- **Énoncés détaillés** : Description, objectifs, contraintes
- **Listes de tâches** : Checklist des étapes à accomplir
- **Gestion des délais** : Dates limites de soumission
- **Documentation** : Fichiers, ressources
- **Soumissions** : Les étudiants soumettent leur code
- **Évaluation** : Feedback détaillé des formateurs
- **Critères d'évaluation** : Rubriques de notation personnalisées

### 4. Gestion des Problèmes (Défis de Code)

Créer et gérer des défis de programmation :

- **Énoncés clairs** : Description, entrée, sortie attendue
- **Niveaux de difficulté** : Facile, moyen, difficile
- **Système de points** : Points configurables par problème
- **Solution de référence** : Fournir une solution pour validation
- **Validation automatique locale** : Compilation et test instantanés
- **Support multi-langage** : Python, Java, C++, JavaScript, etc.
- **Historique** : Tracer toutes les tentatives des étudiants
- **Statuts** : ACCEPTED (accepté), REJECTED (rejeté)
- **Compétences associées** : Lier à des compétences (Python, Java, SQL, etc.)

### 5. Système de Points et Gamification

- **Points globaux** : Accumulation par soumissions
- **Classements** : Leaderboard global et par compétence
- **Badges** : Récompenses visuelles pour jalons atteints
- **Compétences** : Niveaux de maîtrise par domaine
- **Historique** : Tracer l'évolution des points
- **Analytics** : Statistiques détaillées de progression

### 6. Tableau de Bord et Statistiques

- **Vue d'ensemble** : Résumé personnel de la progression
- **Statistiques** : Problèmes résolus, taux de réussite, points gagnés
- **Cours actifs** : Liste des cours en cours de suivi
- **Projets** : État des projets en cours
- **Classement** : Position parmi les autres utilisateurs
- **Graphiques** : Visualisation de la progression

### 7. Gestion Administrateur

Interface d'administration complète :

- **Gestion des utilisateurs** : Créer, modifier, supprimer, bannir
- **Gestion des cours** : CRUD complet
- **Gestion des problèmes** : Création, édition, validation des solutions
- **Gestion des projets** : Configuration, suivi des soumissions
- **Rapports** : Statistiques globales, activité des utilisateurs
- **Permissions** : Attribution des rôles (Admin, Formateur, Étudiant)

---

## Installation

### Prérequis

- **Java** >= 17 (JDK)
- **Maven** >= 3.6
- **MySQL** >= 5.7 (ou SQLite pour version légère)
- **Git** : Système de contrôle de version

### Étapes d'installation

#### 1. Cloner le repository

```bash
git clone https://github.com/inesboubakri/Esprit-PIDEV-3A41-2526-CODEQUEST.git
cd CodeQuest-Java
```

#### 2. Configurer la base de données

Créer un fichier `config.properties` dans `src/main/resources/` :

```properties
# ===== BASE DE DONNÉES =====
# Pour MySQL
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/codequest_java
db.user=root
db.password=votre_mot_de_passe

# OU pour SQLite (fichier local)
# db.driver=org.sqlite.JDBC
# db.url=jdbc:sqlite:codequest.db

# ===== APPLICATION =====
app.name=CodeQuest Desktop
app.version=1.0.0

# ===== VALIDATION DE CODE =====
java.compiler.path=javac
python.interpreter.path=python3
cpp.compiler.path=g++
```

#### 3. Créer la base de données

```bash
# Pour MySQL, créer la base de données
mysql -u root -p -e "CREATE DATABASE codequest_java;"

# Importer le schéma
mysql -u root -p codequest_java < src/main/sql/schema.sql

# (Optionnel) Importer les données initiales
mysql -u root -p codequest_java < src/main/sql/initial-data.sql
```

#### 4. Installer les dépendances Maven

```bash
mvn clean install
```

Cette commande télécharge toutes les dépendances listées dans `pom.xml`.

#### 5. Compiler le projet

```bash
mvn compile
```

#### 6. Lancer l'application

**Option 1 : Avec Maven**
```bash
mvn javafx:run
```

**Option 2 : Créer un JAR exécutable**
```bash
mvn package -DskipTests
java -jar target/codequest-desktop-1.0.0.jar
```

**Option 3 : Depuis l'IDE (IntelliJ, Eclipse, NetBeans)**
- Ouvrir le projet
- Cliquer sur `Main.java`
- Exécuter avec Shift+F10 (IntelliJ) ou Ctrl+F11 (Eclipse)

---

## Utilisation

### Pour les Étudiants

#### Créer un compte et se connecter

1. Lancer l'application
2. Cliquer sur **"S'inscrire"** depuis l'écran de connexion
3. Remplir vos informations (email, nom, prénom, mot de passe)
4. Cliquer sur **"Créer un compte"**
5. Se connecter avec vos identifiants

#### Consulter les problèmes et résoudre un défi

1. Depuis le menu, aller à **"Problèmes"**
2. Filtrer par difficulté, compétence ou langage
3. Sélectionner un problème
4. Lire attentivement l'énoncé et les exemples
5. Écrire votre solution dans l'éditeur de code
6. Cliquer sur **"Soumettre et Valider"**
7. Voir instantanément le résultat :
   - ✅ **Accepté** : Recevoir les points
   - ❌ **Rejeté** : Voir les erreurs de compilation/test
8. Réessayer autant de fois que nécessaire

**Exemple d'interface** :
```
┌─────────────────────────────────────────────┐
│ Énoncé: Calculer le Nième nombre Fibonacci │
│                                             │
│ Difficulté: ⭐⭐ (Moyen)                    │
│ Points: 50                                  │
│ Langage: Python                             │
│                                             │
│ ┌─────────────────────────────────────────┐ │
│ │ def fibonacci(n):                       │ │
│ │     if n <= 1:                          │ │
│ │         return n                        │ │
│ │     return fibonacci(n-1) + ..          │ │
│ │                                         │ │
│ └─────────────────────────────────────────┘ │
│                                             │
│         [Valider] [Aide] [Historique]      │
└─────────────────────────────────────────────┘
```

#### Suivre un cours

1. Aller à **"Mes Cours"**
2. Chercher et s'inscrire à un cours
3. Lire les chapitres dans l'ordre
4. Compléter les problèmes associés
5. Voir votre progression (0-100%)
6. À 100% : Télécharger votre certificat

#### Participer à un projet

1. Aller à **"Projets"**
2. Sélectionner un projet
3. Lire l'énoncé et les critères d'évaluation
4. Voir la checklist des tâches à accomplir
5. Développer votre solution dans l'éditeur intégré
6. Soumettre votre code
7. Attendre l'évaluation du formateur
8. Recevoir le feedback et les points

#### Consulter vos statistiques

1. Cliquer sur votre profil (coin haut droit)
2. Voir :
   - Points totaux et par compétence
   - Problèmes résolus
   - Classement global
   - Badges débloqués
   - Progression dans les cours
   - Graphiques de progression dans le temps

### Pour les Formateurs

#### Créer un cours

1. Aller à **"Administration"** → **"Mes Cours"** → **"Nouveau Cours"**
2. Remplir le formulaire :
   - Titre du cours
   - Description détaillée
   - Niveau (Débutant, Intermédiaire, Avancé)
   - Objectifs d'apprentissage
3. Cliquer **"Créer le cours"**
4. Le cours est créé et vous pouvez ajouter du contenu

#### Ajouter des chapitres et du contenu

1. Dans votre cours → **"Ajouter un chapitre"**
2. Remplir :
   - Titre du chapitre
   - Description et contenu
   - Ressources (fichiers, documentations)
3. Ajouter des problèmes au chapitre
4. Configurer l'ordre d'apparition
5. Sauvegarder

#### Créer des problèmes de code

1. Aller à **"Administration"** → **"Problèmes"** → **"Nouveau Problème"**
2. Remplir l'énoncé :
   - Titre du problème
   - Description claire
   - Exemples d'entrée/sortie (tester manuellement)
3. Écrire la solution de référence :

**Exemple** :
```
Titre : Vérifier si un nombre est premier
Énoncé : Écrire une fonction qui vérifie si un nombre donné est premier.
         Retourner True si premier, False sinon.

Langage : Python
Solution :
def is_prime(n):
    if n < 2:
        return False
    for i in range(2, int(n**0.5) + 1):
        if n % i == 0:
            return False
    return True

Test 1 :
Entrée : 7
Sortie attendue : True

Test 2 :
Entrée : 10
Sortie attendue : False

Difficulté : Moyen (3/5)
Points : 40
Compétence : Python
```

4. Configurer :
   - Langage(s) supporté(s)
   - Difficulté (1-5)
   - Points à attribuer
   - Compétence associée
   - Temps limite (optionnel)
5. Sauvegarder

#### Créer un projet

1. Aller à **"Administration"** → **"Projets"** → **"Nouveau Projet"**
2. Remplir :
   - Titre du projet
   - Description détaillée
   - Niveau de difficulté
   - Date limite de soumission
   - Fichiers de ressources
3. Ajouter les critères d'évaluation :
   - Critère 1 : Fonctionnalité (0-30 points)
   - Critère 2 : Code Quality (0-20 points)
   - Critère 3 : Documentation (0-20 points)
4. Créer une checklist des tâches
5. Sauvegarder

#### Évaluer les soumissions

1. Aller à **"Évaluations"** ou **"Mes Projets"**
2. Voir les soumissions en attente
3. Ouvrir une soumission d'étudiant
4. Voir le code soumis dans l'éditeur
5. Ajouter des commentaires ligne par ligne
6. Attribuer une note pour chaque critère
7. Valider l'évaluation
8. L'étudiant reçoit automatiquement les points et le feedback

### Pour les Administrateurs

#### Gérer les utilisateurs

1. Aller à **"Administration"** → **"Utilisateurs"**
2. Voir la liste de tous les utilisateurs
3. Pour chaque utilisateur :
   - Modifier les informations
   - Changer le rôle (Admin, Formateur, Étudiant)
   - Bannir si nécessaire
   - Consulter les statistiques d'activité

#### Gérer les ressources

1. **Cours** : `Administration → Cours`
   - Voir tous les cours
   - Modifier, dupliquer, archiver
   - Voir les inscriptions

2. **Problèmes** : `Administration → Problèmes`
   - CRUD complet
   - Importer/Exporter des problèmes
   - Gérer les langages supportés

3. **Projets** : `Administration → Projets`
   - CRUD complet
   - Configurer les critères d'évaluation

#### Consulter les rapports et statistiques

1. Aller à **"Administration"** → **"Rapports"**
2. Voir :
   - Nombre d'utilisateurs actifs
   - Taux de complétion des cours
   - Problèmes les plus résolus
   - Utilisateurs les plus actifs
   - Performances globales
   - Exportation des données (CSV, PDF)

#### Configurer l'application

1. Aller à **"Administration"** → **"Paramètres"**
2. Configurer :
   - Langages de programmation supportés
   - Compilateurs/Interpréteurs disponibles
   - Limites de ressources (mémoire, timeout)
   - Thème de l'application

---

## Structure de la Base de Données

### Principales tables

```sql
-- Utilisateurs
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    avatar_path VARCHAR(255),
    role ENUM('ADMIN', 'TEACHER', 'STUDENT'),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Cours
CREATE TABLE courses (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    teacher_id INT,
    difficulty VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES users(id)
);

-- Problèmes
CREATE TABLE problems (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    difficulty INT,
    points INT,
    reference_solution LONGTEXT,
    language VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Soumissions
CREATE TABLE submissions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    problem_id INT,
    code LONGTEXT,
    status ENUM('ACCEPTED', 'REJECTED', 'PENDING'),
    feedback TEXT,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (problem_id) REFERENCES problems(id)
);

-- Points
CREATE TABLE points (
    id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    problem_id INT,
    points_earned INT,
    earned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES users(id),
    FOREIGN KEY (problem_id) REFERENCES problems(id)
);
```

---

## Compilation et Distribution

### Créer une distribution exécutable

```bash
# Créer un JAR exécutable avec dépendances
mvn clean package -DskipTests

# Le JAR sera disponible dans target/codequest-desktop-1.0.0-jar-with-dependencies.jar
```

### Créer un installateur (Windows)

```bash
mvn clean package
# Utiliser des outils comme NSIS ou jpackage (JDK 16+)
jpackage --input target \
         --name CodeQuest \
         --main-jar codequest-desktop-1.0.0.jar \
         --main-class com.codequest.Main \
         --type exe
```

### Empaqueter pour macOS

```bash
jpackage --input target \
         --name CodeQuest \
         --main-jar codequest-desktop-1.0.0.jar \
         --main-class com.codequest.Main \
         --type dmg
```

---

## Tests

### Lancer les tests unitaires

```bash
mvn test
```

### Lancer les tests d'intégration

```bash
mvn verify
```

### Tests avec couverture de code

```bash
mvn clean test jacoco:report
# Rapport dans : target/site/jacoco/index.html
```

---

## Dépannage

### Problème : "Erreur de connexion à la base de données"

**Solution** :
1. Vérifier que MySQL est en cours d'exécution
2. Vérifier les paramètres dans `config.properties`
3. Vérifier que la base de données `codequest_java` existe
4. Vérifier les permissions de l'utilisateur MySQL

### Problème : "JavaFX non trouvé"

**Solution** :
```bash
# Mettre à jour les dépendances
mvn clean install

# Ou spécifier le chemin du SDK JavaFX dans pom.xml
<properties>
    <javafx.version>21.0.1</javafx.version>
    <javafx.maven.plugin.version>0.0.8</javafx.maven.plugin.version>
</properties>
```

### Problème : "Le compilateur Python/Java n'est pas trouvé"

**Solution** :
1. Vérifier que Python/Java est installé
2. Vérifier que les chemins dans `config.properties` sont corrects
3. Ajouter les répertoires au PATH de l'OS

```bash
# Windows
set PATH=%PATH%;C:\Python39;C:\Program Files\Java\jdk17\bin

# Linux/macOS
export PATH=$PATH:/usr/bin/python3:/usr/local/java/bin
```

---

## Contribution

Nous accueillons les contributions ! Voici comment contribuer au projet :

### Avant de commencer

1. **Forker le repository** sur GitHub
2. **Cloner votre fork**
   ```bash
   git clone https://github.com/votre-username/CodeQuest-Java.git
   ```
3. **Créer une branche** pour votre feature
   ```bash
   git checkout -b feature/ma-nouvelle-fonctionnalite
   ```

### Pendant le développement

1. Effectuer vos modifications
2. Ajouter des tests pour les nouvelles fonctionnalités
3. Tester localement
   ```bash
   mvn clean test
   mvn javafx:run
   ```
4. Commit avec un message clair
   ```bash
   git add .
   git commit -m "Ajouter nouvelle fonctionnalité X"
   ```

### Après le développement

1. Push votre branche
   ```bash
   git push origin feature/ma-nouvelle-fonctionnalite
   ```
2. Créer une Pull Request sur GitHub
3. Attendre la review et répondre aux commentaires

### Bonnes pratiques

- Suivre la convention de nommage : `feature/`, `bugfix/`, `hotfix/`
- Rédiger des messages de commit descriptifs
- Ajouter des tests pour les nouvelles fonctionnalités
- Documenter les changements majeurs
- Respecter la structure du code (architecture MVC)
- Utiliser des noms significatifs pour les variables et méthodes
- Commenter le code complexe

### Types de contributions bienvenues

- **Corrections de bugs** : Signaler les problèmes dans les Issues
- **Nouvelles fonctionnalités** : Proposer des améliorations
- **Documentation** : Améliorer le README, les commentaires
- **Design/UX** : Améliorations de l'interface FXML
- **Performance** : Optimisations de code
- **Support de langages** : Ajouter des compilateurs supplémentaires

---

## Licence

Ce projet est sous **licence propriétaire**.

### Conditions d'utilisation

Tous les droits sont réservés. L'utilisation, la modification ou la distribution de ce code source est strictement interdite sans permission écrite préalable.

Pour toute question concernant la licence, veuillez contacter les mainteneurs du projet.

---

## Crédits

- **Développement** : Équipe de programmation
- **Architecture** : Design MVC avec JavaFX
- **Tester** : Communauté des contributeurs

---

## Ressources Utiles

### Documentation

- [JavaFX Documentation](https://gluonhq.com/products/javafx/)
- [Maven Guide](https://maven.apache.org/)
- [JDBC Tutorial](https://docs.oracle.com/javase/tutorial/jdbc/)

### Outils

- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - IDE recommandé
- [Scene Builder](https://gluonhq.com/products/scene-builder/) - Éditeur FXML
- [MySQL Workbench](https://www.mysql.com/products/workbench/) - Gestion BD

### Communautés

- GitHub Issues pour signaler les bugs
- Discussions GitHub pour les questions

---

## Contact

Pour toute question ou suggestion :
- 📧 Email : support@codequest.local
- 💬 Issues GitHub : [CodeQuest-Java/issues](https://github.com/inesboubakri/Esprit-PIDEV-3A41-2526-CODEQUEST.git/issues)

---

**Dernier mise à jour** : Mai 2026
**Version** : 1.0.0
