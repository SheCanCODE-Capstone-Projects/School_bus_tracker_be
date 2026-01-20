# Password Reset Functionality

## Overview
The password reset flow uses a 6-digit verification code sent via email. The process is:
1. User requests password reset by entering their email
2. System sends a 6-digit verification code to the user's email
3. User enters the code and new password (with confirmation)
4. System validates and updates the password

## API Endpoints

### 1. Request Password Reset
- **URL**: `POST /api/auth/password-reset/request`
- **Request Body**:
```json
{
  "email": "user@example.com"
}
```
- **Response**:
```json
{
  "message": "Password reset email sent successfully",
  "success": true
}
```
- **Backend Action**: 
  - Validates user exists
  - Generates 6-digit verification code
  - Sends code via email (Brevo SMTP)
  - Code expires in 10 minutes

### 2. Confirm Password Reset
- **URL**: `POST /api/auth/password-reset/confirm`
- **Request Body**:
```json
{
  "code": "123456",
  "newPassword": "newSecurePassword123",
  "confirmPassword": "newSecurePassword123"
}
```
- **Response**:
```json
{
  "message": "Password reset successfully",
  "success": true
}
```
- **Validation**:
  - Code must be valid and not expired
  - Code must not be already used
  - New password and confirm password must match
  - Password must be at least 6 characters

## Security Features
- ✅ 6-digit verification codes (more user-friendly than UUID tokens)
- ✅ Codes expire after 10 minutes
- ✅ Single-use codes (cannot be reused)
- ✅ User validation before sending emails
- ✅ Automatic cleanup of old tokens
- ✅ Password confirmation required

## Configuration Required
Set these environment variables in `.env` file:
```
# Brevo Email Configuration
BREVO_EMAIL=your-email@example.com
BREVO_SMTP_KEY=your-brevo-smtp-key

# Frontend URL (optional, defaults to http://localhost:3000)
FRONTEND_URL=http://localhost:3000
```

## Brevo SMTP Setup
1. Sign up for a Brevo account at https://www.brevo.com
2. Go to SMTP & API section
3. Generate an SMTP key
4. Use your Brevo account email and SMTP key in the `.env` file