@echo off
echo Setting environment variables for School Bus Tracker...

set DB_URL=jdbc:postgresql://localhost:5432/school_bus_tracker
set DB_USERNAME=your_db_username
set DB_PASSWORD=your_db_password
set JWT_SECRET=your-256-bit-secret-key-for-jwt-signing-hs256-algorithm-needs-at-least-32-bytes
set JWT_EXPIRATION=3600000
set MAIL_HOST=smtp.gmail.com
set MAIL_PORT=587
set MAIL_USERNAME=your-email@gmail.com
set MAIL_PASSWORD=your-app-password
set FRONTEND_URL=http://localhost:3000

echo Environment variables set successfully!
echo Starting Spring Boot application...
mvn spring-boot:run