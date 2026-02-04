# Bus Tracking – Frontend Integration Guide

This document describes how the bus tracking process works and how frontends (Driver, Parent, Admin) should integrate with the backend APIs.

---

## 1. Overview

### 1.1 Tracking Lifecycle

```
Driver taps "Start Trip"
        ↓
POST /api/driver/tracking/start  →  Backend creates ACTIVE tracking, sets bus status = ON_ROUTE
        ↓
Driver app sends GPS repeatedly (every 5–10 sec or every 20–50 m)
        ↓
POST /api/driver/tracking/location  →  Backend stores latest position
        ↓
Parents / Admin can read live location via GET endpoints
        ↓
Driver taps "Stop Trip"
        ↓
POST /api/driver/tracking/stop  →  Backend marks tracking STOPPED, sets bus status = ACTIVE
```

### 1.2 Roles and Capabilities

| Role   | Can do |
|--------|--------|
| **Driver** | Start tracking, send GPS updates, stop tracking (only for their assigned bus). |
| **Parent** | View live location of buses that have at least one of their children assigned. |
| **Admin**  | View tracking status and GPS history for any bus (with optional date filters). |

### 1.3 Important Rules

- **One active trip per bus** – A bus cannot have two ACTIVE tracking sessions at once. The backend rejects a second “start” if one is already active.
- **Driver-bound** – Location updates are accepted only when the driver has an active tracking session and the bus is the one assigned to that driver.
- **Parent visibility** – A parent can request location only for buses that have at least one of their children assigned (matched by parent phone/name and school).
- **Dates/times** – All timestamps are in ISO 8601 (e.g. `2025-01-12T08:30:00`). Use the same format when sending `from`/`to` query params.

---

## 2. Authentication

All endpoints below require a valid **JWT** in the `Authorization` header:

```http
Authorization: Bearer <access_token>
```

- Drivers use the token obtained at login (role `DRIVER`).
- Parents use the token obtained at login (role `PARENT`).
- Admins use the token obtained at login (role `ADMIN`).

Replace `<access_token>` with the actual token string.  
Base URL is your backend root (e.g. `https://api.yourschool.com` or `http://localhost:8081`).

---

## 3. Driver Integration

### 3.1 Start Tracking

**When:** User (driver) taps “Start Trip” or equivalent.

| Item        | Value |
|------------|--------|
| **Method** | `POST` |
| **URL**    | `/api/driver/tracking/start` |
| **Headers**| `Authorization: Bearer <token>` |
| **Body**   | None |

**Success (200):**

```json
{
  "message": "Tracking started successfully",
  "success": true
}
```

**Possible errors (4xx):**

- Driver not found / user is not a driver.
- Driver has no assigned bus → “Driver does not have an assigned bus”.
- Bus already has an active session → “Bus already has an active tracking session”.

**Backend side-effects:**

- Creates a new `BusTracking` record with status `ACTIVE` and `startedAt = now`.
- Sets the bus’s status to `ON_ROUTE`.

---

### 3.2 Send GPS Location

**When:** Repeatedly while the trip is active (e.g. every 5–10 seconds or every 20–50 metres).

| Item        | Value |
|------------|--------|
| **Method** | `POST` |
| **URL**    | `/api/driver/tracking/location` |
| **Headers**| `Authorization: Bearer <token>`, `Content-Type: application/json` |
| **Body**   | JSON (see below) |

**Request body:**

```json
{
  "latitude": -1.9441,
  "longitude": 30.0619,
  "speed": 42.5,
  "heading": 180
}
```

| Field      | Type   | Required | Description |
|-----------|--------|----------|-------------|
| `latitude`  | number | Yes      | Latitude  |
| `longitude` | number | Yes      | Longitude |
| `speed`     | number | No       | Speed (e.g. km/h) |
| `heading`   | number | No       | Direction (e.g. 0–360 degrees) |

**Success (200):**

```json
{
  "message": "Location updated successfully",
  "success": true
}
```

**Possible errors:**

- No active tracking for this driver → “No active tracking session found. Please start tracking first.”
- Tracking does not match driver’s bus → “Tracking session does not match driver's assigned bus”.

**Recommendation:** If the request fails with “No active tracking session”, prompt the driver to start tracking again.

---

### 3.3 Stop Tracking

**When:** User (driver) taps “Stop Trip” or equivalent.

| Item        | Value |
|------------|--------|
| **Method** | `POST` |
| **URL**    | `/api/driver/tracking/stop` |
| **Headers**| `Authorization: Bearer <token>` |
| **Body**   | None |

**Success (200):**

```json
{
  "message": "Tracking stopped successfully",
  "success": true
}
```

**Possible errors:**

- No active tracking → “No active tracking session found”.

**Backend side-effects:**

- Current tracking record is set to `STOPPED` and `stoppedAt = now`.
- Bus status is set back to `ACTIVE` (no longer on route).

---

## 4. Parent Integration

### 4.1 Get Live Bus Location

**When:** Parent opens “Track bus” or similar (e.g. for a bus their child is on).

| Item        | Value |
|------------|--------|
| **Method** | `GET` |
| **URL**    | `/api/parent/buses/{busId}/location` |
| **Headers**| `Authorization: Bearer <token>` |

Replace `{busId}` with the bus ID (e.g. from the list of buses their children use).

**Success (200):**

```json
{
  "latitude": -1.9441,
  "longitude": 30.0619,
  "speed": 42.5,
  "heading": 180,
  "lastUpdated": "2025-01-12T08:30:00"
}
```

- `lastUpdated` is ISO 8601. Use it to show “last updated X minutes ago” or to decide when to poll again.
- If the bus has never sent a location during an active trip, the API may return 4xx (e.g. “No location data available for this bus”). Handle this in the UI (e.g. “Location not available yet”).

**Possible errors:**

- Parent has no child on this bus → “Access denied: You do not have a child assigned to this bus”.
- Bus not found.
- No location data yet → “No location data available for this bus”.

**Polling:** To show “live” movement, call this endpoint periodically (e.g. every 5–10 seconds) while the parent is viewing the map. You can use `lastUpdated` to avoid refreshing the map if nothing changed.

---

## 5. Admin Integration

### 5.1 Get Tracking Status

**When:** Admin opens bus details or a “tracking status” view for a bus.

| Item        | Value |
|------------|--------|
| **Method** | `GET` |
| **URL**    | `/api/admin/buses/{busId}/tracking-status` |
| **Headers**| `Authorization: Bearer <token>` |

**Success (200):**

```json
{
  "status": "ACTIVE",
  "startedAt": "2025-01-12T07:00:00",
  "stoppedAt": null,
  "driverName": "John Driver",
  "busNumber": "SB001"
}
```

- `status`: `"ACTIVE"` (trip in progress) or `"STOPPED"` (no current trip).
- `startedAt` / `stoppedAt`: ISO 8601; `stoppedAt` is `null` when status is `ACTIVE`.
- When there is no tracking at all, backend may return `status: "STOPPED"` with `startedAt` and `stoppedAt` null.

---

### 5.2 Get GPS History (Route / Replay)

**When:** Admin wants to see past route or replay a trip (e.g. on a map).

| Item        | Value |
|------------|--------|
| **Method** | `GET` |
| **URL**    | `/api/admin/buses/{busId}/locations` |
| **Headers**| `Authorization: Bearer <token>` |
| **Query**  | Optional: `from`, `to` (ISO 8601 date-time) |

**Examples:**

- All stored points for the bus:  
  `GET /api/admin/buses/1/locations`
- Points in a time window:  
  `GET /api/admin/buses/1/locations?from=2025-01-12T07:00:00&to=2025-01-12T09:00:00`

**Success (200):** Array of location objects (newest first when no filters; filtered by date when `from`/`to` are used):

```json
[
  {
    "latitude": -1.9441,
    "longitude": 30.0619,
    "speed": 42.5,
    "heading": 180,
    "lastUpdated": "2025-01-12T08:35:00"
  },
  {
    "latitude": -1.9450,
    "longitude": 30.0620,
    "speed": 40.0,
    "heading": 175,
    "lastUpdated": "2025-01-12T08:34:50"
  }
]
```

Use this list to draw a path on a map or to replay a trip.

---

## 6. Quick Reference Table

| Role   | Action           | Method | Endpoint                                  |
|--------|------------------|--------|-------------------------------------------|
| Driver | Start tracking   | POST   | `/api/driver/tracking/start`             |
| Driver | Send GPS         | POST   | `/api/driver/tracking/location`           |
| Driver | Stop tracking    | POST   | `/api/driver/tracking/stop`               |
| Parent | Live bus location| GET    | `/api/parent/buses/{busId}/location`      |
| Admin  | Tracking status  | GET    | `/api/admin/buses/{busId}/tracking-status`|
| Admin  | GPS history      | GET    | `/api/admin/buses/{busId}/locations`      |

---

## 7. Suggested Frontend Flows

### Driver app

1. On “Start Trip”: call `POST /api/driver/tracking/start`. On success, enable the “Send location” loop and show “Trip active”.
2. In a timer or on position change: call `POST /api/driver/tracking/location` with current GPS. Recommended: every 5–10 s or every 20–50 m.
3. On “Stop Trip”: call `POST /api/driver/tracking/stop`. On success, stop the location loop and show “Trip ended”.
4. If any location request returns “No active tracking session”, stop sending locations and ask the driver to start again.

### Parent app

1. Get the list of buses the parent’s children use (from your existing parents/students or buses API).
2. For the selected bus, call `GET /api/parent/buses/{busId}/location` in a loop (e.g. every 5–10 s) while the “track bus” screen is open.
3. Plot `latitude`/`longitude` on a map and show `lastUpdated` (e.g. “Updated 30 s ago”).
4. If the API returns 4xx (e.g. no location data), show a message like “Location not available” instead of a map point.

### Admin app

1. For each bus (or selected bus), call `GET /api/admin/buses/{busId}/tracking-status` to show whether it is ACTIVE or STOPPED and driver/bus info.
2. For “View route” or “Replay trip”, call `GET /api/admin/buses/{busId}/locations` with optional `from` and `to` (e.g. today’s trip window).
3. Draw the returned array of locations as a path on a map; optionally animate using `lastUpdated` order.

---

## 8. Error Handling

- All listed endpoints use the same JWT and return standard HTTP status codes (e.g. 200 success, 400 bad request, 403 forbidden, 404 not found).
- Error response bodies may be plain text or JSON; check your backend’s global error format.
- Typical causes of failures:
  - **403:** Invalid or expired token, or wrong role.
  - **400:** Validation error (e.g. missing `latitude`/`longitude`), or business rule (e.g. “Bus already has an active tracking session”, “No active tracking session found”).
  - **404:** Bus or resource not found.

Handle these in the UI (toast, inline message, or disable “Start” when a session is already active) so users know what to do next.

---

If your backend base URL or path prefix differs (e.g. `/api/v1`), replace the paths above accordingly.  
For exact request/response shapes and error formats, you can also use your Swagger/OpenAPI UI (e.g. `/swagger-ui.html`) if it is enabled.
