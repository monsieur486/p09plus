# Prérequis

- docker compose v 2.40.0 ou supérieur
- docker v 24.0.5 ou supérieur

# Démarrage du projet

```bash
docker compose --profile fullstack up -d --build
```

NB : le profil "fullstack" démarre tous les services (application, gateway, eureka, base de données)

Si vous rencontrez un conflit de ports, vous pouvez créer une copie du fichier `dist.env` en `.env` et modifier les variables de ports selon vos besoins.

Sans le fichier `.env`, les ports par défaut sont les suivants :

# Accès à l'application
http://localhost:8080

user: 
```app_user```

password:
```app_password```

# Accès à la gateway
http://localhost:9000

# Accès à Eureka
http://localhost:8761

# Accès à la documentation Swagger
http://localhost:9000/swagger-ui/index.html