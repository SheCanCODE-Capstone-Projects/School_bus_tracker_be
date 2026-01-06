# Environment Setup Instructions

## Quick Setup (Choose one method):

### Method 1: Using .env file (Recommended)
1. Edit the `.env` file in the project root
2. Replace the placeholder values with your actual credentials:
   - `your_db_username` → your PostgreSQL username
   - `your_db_password` → your PostgreSQL password  
   - `your-email@gmail.com` → your Gmail address
   - `your-app-password` → your Gmail App Password (see below)

### Method 2: Using run.bat (Windows)
1. Edit `run.bat` file
2. Replace placeholder values
3. Double-click `run.bat` to start the application

### Method 3: Manual Windows Environment Variables
```cmd
set DB_URL=jdbc:postgresql://localhost:5432/school_bus_tracker
set DB_USERNAME=your_db_username
set DB_PASSWORD=your_db_password
set MAIL_USERNAME=your-email@gmail.com
set MAIL_PASSWORD=your-app-password
set FRONTEND_URL=http://localhost:3000
```

## Gmail App Password Setup:
1. Go to Google Account settings
2. Enable 2-Factor Authentication
3. Generate App Password for "Mail"
4. Use this App Password (not your regular password)

## Test the Setup:
Run the application and check logs for "Password reset email sent successfully"