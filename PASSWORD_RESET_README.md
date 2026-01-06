# Password Reset Functionality

This document describes the password reset functionality implemented for the School Bus Tracker application.

## Overview

The password reset functionality allows users (Admin, Driver, Parent) to reset their passwords via email verification. The process involves two steps:

1. **Request Password Reset**: User provides their email address
2. **Confirm Password Reset**: User provides the reset token and new password

## API Endpoints

### 1. Request Password Reset
- **URL**: `POST /api/auth/password-reset/request`
- **Description**: Initiates password reset process by sending a reset token to user's email
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

### 2. Confirm Password Reset
- **URL**: `POST /api/auth/password-reset/confirm`
- **Description**: Completes password reset using the token and new password
- **Request Body**:
```json
{
  "token": "uuid-token-from-email",
  "newPassword": "newSecurePassword123"
}
```
- **Response**:
```json
{
  "message": "Password reset successfully",
  "success": true
}
```

## Configuration

### Email Configuration
Add the following environment variables or update `application.properties`:

```properties
# Email configuration
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password

# Frontend URL for reset links
FRONTEND_URL=http://localhost:3000
```

### Database Schema
The implementation adds a new table `password_reset_tokens`:

```sql
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expiry_date TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE
);
```

## Security Features

1. **Token Expiration**: Reset tokens expire after 1 hour
2. **Single Use**: Each token can only be used once
3. **Unique Tokens**: UUID-based tokens prevent guessing
4. **User Validation**: Only existing users can request password reset
5. **Token Cleanup**: Old tokens are automatically deleted when new ones are generated

## Implementation Details

### New Components Added:

1. **Models**:
   - `PasswordResetToken`: Entity for storing reset tokens

2. **DTOs**:
   - `PasswordResetRequest`: Request DTO for initiating reset
   - `PasswordResetConfirmRequest`: Request DTO for confirming reset
   - `SimpleApiResponse`: Consistent response format

3. **Services**:
   - `EmailService`: Interface for email operations
   - `EmailServiceImpl`: Implementation for sending reset emails

4. **Repository**:
   - `PasswordResetTokenRepository`: Data access for reset tokens

5. **Controller Updates**:
   - Added password reset endpoints to `AuthController`

## Usage Example

### Frontend Integration
```javascript
// Request password reset
const requestReset = async (email) => {
  const response = await fetch('/api/auth/password-reset/request', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ email })
  });
  return response.json();
};

// Confirm password reset
const confirmReset = async (token, newPassword) => {
  const response = await fetch('/api/auth/password-reset/confirm', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ token, newPassword })
  });
  return response.json();
};
```

## Error Handling

The implementation handles various error scenarios:
- User not found
- Invalid or expired tokens
- Already used tokens
- Email sending failures

All errors are returned as appropriate HTTP status codes with descriptive messages.