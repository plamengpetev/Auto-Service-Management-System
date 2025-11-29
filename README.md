Auto Service Management System - Main Application
Overview
The Auto Service Management System is the main application in a two-component architecture
designed for managing customers, vehicles, and service requests. It provides the UI, business
logic, security, and integration with a separate REST microservice responsible for mechanic
management. This project is developed for SoftUni – Spring Advanced (October 2025).
Architecture Overview
The system consists of:- Main Application (this project): MVC + Thymeleaf UI, business services, security, caching,
scheduling.- MechanicAssignmentService: REST microservice providing mechanic assignment and availability
APIs.
Technologies- Java 17, Spring Boot 3.4- Spring MVC, Thymeleaf, MySQL- Spring Security (BCrypt hashing)- 
OpenFeign for inter-service communication- JUnit 5, Mockito, MockMvc, Jacoco- Maven
Security & Roles- ROLE_USER: managing own profile, vehicles, and requests- ROLE_ADMIN: admin panel, managing users,
roles, and mechanic assignment- Passwords hashed with BCrypt- Public routes: /, /login, /register, 
/contact- Protected routes: /my_vehicles, /requests, /profile- Admin-only routes: /admin/**
Main Functionalities
User:- Register / Login / Logout
- Edit profile- Add vehicles- View vehicles- Create service request- View or cancel requests
  Admin:- View all users, vehicles, and requests- Manual mechanic assignment- Automatic mechanic assignment 
- via Feign → microservice- Release mechanic- Promote/demote user roles
  Integration with REST Microservice
  Main app uses Feign Client to call:- GET /api/mechanics- POST /api/mechanics/assign- PUT /api/mechanics/{id}/availability
  Database Model
  Entities using UUID:- Customer- Vehicle- ServiceRequest- Role
  Relationships:- Customer 1–M Vehicle- Vehicle 1–M ServiceRequest- Customer M–N Role
  Scheduling & Caching- Daily cron: auto-cancel old pending requests- Fixed-rate: log active requests 
- count- Cacheable: customers, requests, vehicles- CacheEvict on modifications
  Testing- Unit, Integration, and API tests- Jacoco with 80%+ enforced coverage- MockMvc for controller tests
  Running the Application
1. Start microservice:
   mvn spring-boot:run (port 8081)
2. Start main app:
   mvn spring-boot:run (port 8080)
3. Database:
   MySQL URL: jdbc:mysql://localhost:3306/car_service
   User: root
   Pass: 121314