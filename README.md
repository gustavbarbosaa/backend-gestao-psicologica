# Backend Gestao Psicologica

## Subindo com Docker Compose

1. Copie o arquivo de ambiente:

```bash
cp .env.example .env
```

2. Ajuste pelo menos estas variaveis no `.env`:

- `APP_SECRET`
- `ALLOWED_ORIGINS`
- `DB_PASSWORD`
- `POSTGRES_PASSWORD`
- `ADMIN_BOOTSTRAP_ENABLED`, `ADMIN_NAME`, `ADMIN_EMAIL`, `ADMIN_PASSWORD` se quiser criar o admin inicial

3. Suba os containers:

```bash
docker compose up -d --build
```

4. Verifique a aplicacao:

```bash
docker compose ps
docker compose logs -f backend
```

O backend responde em `http://localhost:8080/health` por padrao.

## Estrutura recomendada na EC2

- `backend`: este repositorio, subido com `docker compose`
- `frontend`: outro container, no mesmo host
- `nginx`: recomendado para publicar frontend e backend com dominio/HTTPS

Configuracao minima do backend em producao:

- `SPRING_PROFILES_ACTIVE=prod`
- `ALLOWED_ORIGINS=https://seu-frontend.com`
- `SWAGGER_ENABLED=false`
- `COOKIE_SECURE=true`
- `COOKIE_SAMESITE=None`

## Exemplo de compose com frontend

Se o frontend estiver em outro repositorio no mesmo servidor, voce pode criar um compose raiz assim:

```yaml
services:
  frontend:
    build:
      context: ../frontend-gestao-psicologica
    container_name: frontend_gestao_psicologica
    restart: unless-stopped
    ports:
      - "4200:80"

  backend:
    extends:
      file: ./docker-compose.yml
      service: backend

  db:
    extends:
      file: ./docker-compose.yml
      service: db
```

Nesse cenario, ajuste `ALLOWED_ORIGINS` para o dominio real do frontend ou para a porta publicada na VPS durante os testes.
