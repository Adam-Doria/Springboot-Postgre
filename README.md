# Spring Boot PostgreSQL Docker Demo

Ce projet est une application Spring Boot avec PostgreSQL, conteneurisée via Docker.

## Prérequis

- Docker
- Docker Compose

## Structure du projet

```
spring-postgres-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── demo/
│   │   │               ├── controller/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── DemoApplication.java
│   │   └── resources/
│   │       └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Démarrage rapide

1. Clonez le repository
2. Exécutez la commande suivante pour démarrer l'application :

```bash
docker-compose up -d
```

L'application sera accessible sur http://localhost:8080

## API Endpoints

L'application expose les endpoints REST suivants pour gérer les produits :

- `GET /api/products` - Récupérer tous les produits
- `GET /api/products/{id}` - Récupérer un produit par son ID
- `POST /api/products` - Créer un nouveau produit
- `PUT /api/products/{id}` - Mettre à jour un produit existant
- `DELETE /api/products/{id}` - Supprimer un produit

## Exemple d'utilisation avec cURL

### Créer un produit
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop", "description":"Un ordinateur portable puissant", "price":1299.99}'
```

### Récupérer tous les produits
```bash
curl -X GET http://localhost:8080/api/products
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