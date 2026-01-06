# School Bus Tracker API - Complete CRUD Endpoints

## All Issues Fixed ✅

### Fixed Validation Errors
- Removed deprecated `required()` method from `@Schema` annotations
- Added proper `@NotBlank`, `@NotNull`, `@Email`, and `@Min` validation annotations
- Validation messages now appear correctly in API responses

### Fixed Null Safety Issues
- Added `@SuppressWarnings("null")` annotations where needed
- Removed invalid `setCreatedAt()` calls (now handled by `@PrePersist`)
- Fixed method signatures in response converters

---

## Student API Endpoints

### 1. Get All Students
- **Method:** `GET`
- **URL:** `/api/students`
- **Response:** List of all students with their bus and school info

### 2. Get Student by ID
- **Method:** `GET`
- **URL:** `/api/students/{id}`
- **Path Parameter:** `id` - Student ID
- **Response:** Single student object

### 3. Create Student
- **Method:** `POST`
- **URL:** `/api/students?schoolId={schoolId}`
- **Query Parameter:** `schoolId` - School ID (required)
- **Request Body:**
```json
{
  "studentName": "Alice Johnson",
  "age": 12,
  "parentName": "Robert Johnson",
  "parentPhone": "+1234567890",
  "address": "123 Main St, City",
  "busStopId": 1,
  "assignedBusId": 1
}
```
- **Validation:** All fields except `busStopId` and `assignedBusId` are required

### 4. Update Student
- **Method:** `PUT`
- **URL:** `/api/students/{id}`
- **Path Parameter:** `id` - Student ID
- **Request Body:** Same as Create Student
- **Validation:** All required fields must be provided

### 5. Delete Student
- **Method:** `DELETE`
- **URL:** `/api/students/{id}`
- **Path Parameter:** `id` - Student ID
- **Response:** Success message

---

## Driver API Endpoints

### 1. Get All Drivers
- **Method:** `GET`
- **URL:** `/api/drivers`
- **Response:** List of all drivers with their school and bus info

### 2. Get Driver by ID
- **Method:** `GET`
- **URL:** `/api/drivers/{id}`
- **Path Parameter:** `id` - Driver ID
- **Response:** Single driver object

### 3. Create Driver
- **Method:** `POST`
- **URL:** `/api/drivers?schoolId={schoolId}`
- **Query Parameter:** `schoolId` - School ID (required)
- **Request Body:**
```json
{
  "fullName": "John Smith",
  "email": "john.smith@example.com",
  "phoneNumber": "+1234567890",
  "licenseNumber": "DL123456789",
  "assignedBusId": 1
}
```
- **Validation:** 
  - All fields except `assignedBusId` are required
  - Email must be valid format
  - Email, phone number, and license number must be unique
  - Will return error if any field already exists

### 4. Update Driver
- **Method:** `PUT`
- **URL:** `/api/drivers/{id}`
- **Path Parameter:** `id` - Driver ID
- **Request Body:** Same as Create Driver
- **Validation:** Same as Create, but allows updating existing values for the same driver

### 5. Delete Driver
- **Method:** `DELETE`
- **URL:** `/api/drivers/{id}`
- **Path Parameter:** `id` - Driver ID
- **Response:** Success message

---

## Bus API Endpoints

### 1. Get All Buses
- **Method:** `GET`
- **URL:** `/api/buses`
- **Response:** List of all buses with their driver and school info

### 2. Get Bus by ID
- **Method:** `GET`
- **URL:** `/api/buses/{id}`
- **Path Parameter:** `id` - Bus ID
- **Response:** Single bus object

### 3. Create Bus
- **Method:** `POST`
- **URL:** `/api/buses?schoolId={schoolId}`
- **Query Parameter:** `schoolId` - School ID (required)
- **Request Body:**
```json
{
  "busName": "School Bus A",
  "busNumber": "SB001",
  "capacity": 50,
  "route": "Route 1: Downtown - School",
  "assignedDriverId": 1,
  "status": "ACTIVE"
}
```
- **Validation:** 
  - `busName`, `busNumber`, and `capacity` are required
  - Capacity must be at least 1
  - Status must be one of: `ACTIVE`, `INACTIVE`, `MAINTENANCE`
  - Default status is `ACTIVE` if not provided

### 4. Update Bus
- **Method:** `PUT`
- **URL:** `/api/buses/{id}`
- **Path Parameter:** `id` - Bus ID
- **Request Body:** Same as Create Bus
- **Validation:** Same as Create

### 5. Delete Bus
- **Method:** `DELETE`
- **URL:** `/api/buses/{id}`
- **Path Parameter:** `id` - Bus ID
- **Response:** Success message

---

## Response Format

All endpoints return responses in the following format:

### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data here
  }
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

### Validation Error Response
```json
{
  "success": false,
  "message": "Field validation error message",
  "data": null
}
```

---

## Notes

1. **Required Query Parameter:** Both Student and Driver creation endpoints require `schoolId` as a query parameter
2. **Validation:** All `@NotBlank` and `@NotNull` fields are required in request bodies
3. **Unique Constraints:** Driver email, phone number, and license number must be globally unique
4. **Bus Status:** Valid values are `ACTIVE`, `INACTIVE`, or `MAINTENANCE`
5. **Relationships:** When updating relationships (bus, driver assignments), pass the ID of the related entity
6. **Error Handling:** All endpoints include comprehensive error handling with descriptive messages

---

## Testing Tips

### Create a Student
```
POST /api/students?schoolId=1
Content-Type: application/json

{
  "studentName": "Alice Johnson",
  "age": 12,
  "parentName": "Robert Johnson",
  "parentPhone": "+1234567890",
  "address": "123 Main St, City"
}
```

### Create a Driver
```
POST /api/drivers?schoolId=1
Content-Type: application/json

{
  "fullName": "John Smith",
  "email": "john.smith@example.com",
  "phoneNumber": "+1234567890",
  "licenseNumber": "DL123456789"
}
```

### Create a Bus
```
POST /api/buses?schoolId=1
Content-Type: application/json

{
  "busName": "School Bus A",
  "busNumber": "SB001",
  "capacity": 50,
  "route": "Route 1: Downtown - School",
  "status": "ACTIVE"
}
```

### Update a Bus
```
PUT /api/buses/1
Content-Type: application/json

{
  "busName": "School Bus B",
  "busNumber": "SB001",
  "capacity": 50,
  "route": "Route 1: Downtown - School",
  "assignedDriverId": 1,
  "status": "ACTIVE"
}
```