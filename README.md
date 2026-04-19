# Paste King

Small Spring backend with PostgreSQL, JWT authorization, users, text posts, a static frontend, and nginx

## Run

```bash
docker compose up --build
```

Frontend is available at:

```text
http://localhost
```

Swagger UI is disabled by default for safety. To enable it for local development, set:

```text
APP_SWAGGER_ENABLED=true
```

Then open:

```text
http://localhost/swagger
```

The initial user is created from `.env`:

```text
APP_INITIAL_LOGIN=admin
APP_INITIAL_PASSWORD=change_me
```

## API

All `/api/**` endpoints except login require `Authorization: Bearer <jwt>`

```text
POST   /api/auth/login
GET    /api/auth/me
POST   /api/posts
GET    /api/posts?page=0&limit=20
PUT    /api/posts/{id}
DELETE /api/posts/{id}
```
