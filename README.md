# 🛍️ Products App

A Spring Boot application for managing products and exchange rates, powered by PostgreSQL and Docker Compose.

---

## 📦 Features

- CRUD operations for products
- Fetch & store exchange rate data from [HNB API](https://api.hnb.hr/)
- Scheduled exchange rate updates
- Dockerized setup with [PostgreSQL](https://www.postgresql.org/)

---

## 🚀 Getting Started

### 🔧 Prerequisites

- [Docker](https://www.docker.com/) & Docker Compose installed
- Java 21+ (only if running locally without Docker)
- Maven 3.9.9
- [Intellij IDE](https://www.jetbrains.com/idea/) installed
- [Postman](https://www.postman.com/) installed

---

### 🐳 Run with Docker Compose

- You can run the app in root folder by running next command:
```bash
docker-compose up --build
```

## 🔗 App & Database Info

- **App**: [http://localhost:8080](http://localhost:8080)

### 🗄️ PostgreSQL

- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `productdb`
- **User**: `myuser`
- **Password**: `mypassword`

```
├── assignment/               # Spring Boot application
│   ├── src/
│   └── Dockerfile
├── docker-compose.yml        # Docker orchestration
├── README.md
```
## 🛠️ Useful Endpoints

| Method | Endpoint                        | Description                  |
|--------|---------------------------------|------------------------------|
| GET    | `api/products`                  | List all products            |
| GET    | `api/products/{id}`             | Get product by id            |
| POST   | `api/products`                  | Create a new product         |
| PUT    | `api/products/{id}`             | Update an existing product   |
| DELETE | `api/products/{id}`             | Delete a product             |
| GET    | `api/data/`                     | Get latest USD exchange rate |

- Postman collection can be reached and imported from this [link](https://orange-firefly-351030.postman.co/workspace/Posao~6f36db3a-2d27-4ae1-acf4-bd3d97b2e4ee/collection/11488437-a0e206b9-4a5c-4892-ba91-a856b9aa565b?action=share&creator=11488437)
- Also there is one Postman collection in the root file ready for import
- Swagger ui can be seen once running the application on next [link](http://localhost:8080/swagger-ui/index.html
)

## 💻 Local development setup

- When opening in Intellij if you are using Community Edition you will need to edit running configuration to use local profile 
you need to set next Environment variable
```bash
spring.profiles.active=local
```
- After profile is setup you can run the app and start developing
- H2 database is on the next [link](http://localhost:8080/h2-console) (runs in memory and only when app is running)
- You can use links from above regarding Postman collection to use app and Swagger-ui for help about app endpoints