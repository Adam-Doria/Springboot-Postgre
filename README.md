# Spring Boot PostgreSQL Docker Demo

Ce projet est une application Spring Boot avec PostgreSQL, conteneurisée via Docker.
Sprint Initializer a été  utilisé pour initiliaser le projet.

## Prérequis

- Docker
- Docker Compose

## Structure du projet

```
├── src/                     # Code source du projet
│   ├── main/                # Code source principal de l'application
│   │   ├── java/            # Classes Java de l'application
│   │   └── resources/       # Ressources nécessaires à l'application
│   └── test/                # Code source des tests
├── .gitignore               # Liste des fichiers à ignorer par Git
├── docker-compose.yml       # Configuration Docker Compose
├── Dockerfile               # Instructions pour la création de l'image Docker
├── HELP.md                  # Aide générée par Spring Initializer
├── mvnw                     # Wrapper Maven pour Linux/Mac
├── mvnw.cmd                 # Wrapper Maven pour Windows
├── pom.xml                  # Configuration du projet Maven
└── README.md                # Documentation du projet

```

## Démarrage rapide

1. Clonez le repository
2. Exécutez la commande suivante pour démarrer l'application :

```bash
docker-compose up -d
```

L'application sera accessible sur http://localhost:8080/swagger-ui/index.html

## API Endpoints

La documentation de l'application  sera accessible sur http://localhost:8080


## Exemple d'utilisation avec cURL

### Créer un 
```bash
curl -X POST http://localhost:8080/api/**** \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop", "description":"Un ordinateur portable puissant", "price":1299.99}'
```

### Récupérer tous les utilisateurs en ligne
```bash
curl -X GET http://localhost:8080/api/users/online
```

## Arrêter l'application

Pour arrêter l'application et supprimer les conteneurs, exécutez :

```bash
docker-compose down
```

Pour arrêter l'application et conserver les données, exécutez :

```bash
docker-compose stop
``` 