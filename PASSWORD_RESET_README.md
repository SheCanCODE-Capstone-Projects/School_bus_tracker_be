# Password Reset Functionality

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

### 2. Confirm Password Reset
- **URL**: `POST /api/auth/password-reset/confirm`
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

## Security Features
- ✅ Tokens expire after 1 hour
- ✅ Single-use tokens
- ✅ UUID-based tokens for security
- ✅ User validation before sending emails
- ✅ Automatic cleanup of old tokens

## Configuration Required
Set these environment variables in `.env` file:
```
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
FRONTEND_URL=http://localhost:3000
```