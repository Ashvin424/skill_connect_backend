# Skill Connect Backend

## Introduction
Skill Connect Backend is a **Spring Boot REST API** for a **Skill Exchange Platform**.  
It provides user authentication, service listings, bookings, and ratings.  
The backend uses **Spring Boot**, **Spring Security (JWT)**, **Spring Data JPA**, and **PostgreSQL** for data storage.

---

## Features
- User authentication & authorization with **JWT**
- User profile management (update, upload image, FCM token)
- Service creation, update, search & image upload
- Booking system (create, cancel, update status)
- Rating system (add, update, fetch average rating)
- PostgreSQL persistence with JPA/Hibernate
- File upload support (up to 10MB per file, 20MB per request)
- Detailed API documentation via **Swagger**

---

## Tech Stack
- **Java 17+**  
- **Spring Boot 3.x**  
- **Spring Security + JWT**  
- **Spring Data JPA (Hibernate)**  
- **PostgreSQL**  
- **Maven**  

---

## Installation

### 1️⃣ Clone the repository
- git clone https://github.com/skill_connect_backend.git

- cd skillconnect-backend


### 2️⃣ Build with Maven


- mvn clean install


### 3️⃣ Run the application


- mvn spring-boot:run

Or use the provided wrapper:


- ./mvnw spring-boot:run


---

## Configuration
Application configuration is in `src/main/resources/application.properties`.

| Property | Default Value | Description |
|----------|---------------|-------------|
| `server.port` | `8080` | Server port |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/skillconnectdb` | PostgreSQL DB URL |
| `spring.datasource.username` | `postgres` | DB Username |
| `spring.datasource.password` | `****` | DB Password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Schema generation |
| `spring.jpa.show-sql` | `true` | Show SQL queries |
| `spring.servlet.multipart.max-file-size` | `10MB` | File upload limit |
| `spring.servlet.multipart.max-request-size` | `20MB` | Total request size |

---

## Usage
Once the application is running, visit:

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- OpenAPI Spec: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)  

---

## API Endpoints

### Authentication
- `POST /auth/register` → Register new user  
- `POST /auth/login` → Login with email & password  
- `POST /auth/logout` → Logout current user  
- `POST /auth/refresh-token` → Refresh JWT token  
- `GET /auth/me` → Get current user details  

### Users
- `GET /users/{id}` → Get user by ID  
- `GET /users/username/{username}` → Get user by username  
- `GET /users/profile/me` → Get current user profile  
- `PUT /users/{id}` → Update user by ID  
- `PUT /users/profile/update` → Update profile  
- `PUT /users/change-password` → Change password  
- `PUT /users/update-fcm-token` → Update FCM token  
- `POST /users/profile/upload-image` → Upload profile image  

### Services
- `GET /services` → Get all services (with pagination)  
- `GET /services/{id}` → Get service by ID  
- `POST /services` → Create new service  
- `PUT /services/{id}` → Update service  
- `DELETE /services/{id}` → Delete service  
- `POST /services/{id}/upload-images` → Upload service images  
- `GET /services/search` → Search services  

### Bookings
- `POST /bookings` → Create booking  
- `GET /bookings/{id}` → Get booking by ID  
- `PUT /bookings/{id}` → Update booking status  
- `PUT /bookings/{id}/cancel` → Cancel booking  
- `GET /bookings/requested-by/{userId}` → Get bookings by requester  
- `GET /bookings/service-provider/{providerId}` → Get bookings for a provider  

### Ratings
- `POST /ratings` → Add rating  
- `PUT /ratings/{ratingId}` → Update rating  
- `DELETE /ratings/{ratingId}` → Delete rating  
- `GET /ratings/user/{userId}` → Get ratings for a user  
- `GET /ratings/average/{userId}` → Get average rating for a user  
- `GET /ratings/all/ratings` → Get all ratings  

---

## Troubleshooting
- **DB Connection error:** Ensure PostgreSQL is running and credentials match `application.properties`.  
- **Port already in use:** Change `server.port` in `application.properties`.  
- **JWT expired/invalid:** Use `/auth/refresh-token` to get a new token.  

---

## Contributors
- Ashvin Prajapati (Backend Developer) 

---

## License
This project is licensed under the **MIT License** – feel free to use, modify, and distribute.

