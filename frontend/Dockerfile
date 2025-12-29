FROM node:24-alpine
WORKDIR /app

ENV PNPM_HOME="/root/.local/share/pnpm"
ENV PNPM_STORE_DIR="/root/.local/share/pnpm/store"
ENV PATH="$PNPM_HOME:$PATH"

RUN corepack enable

COPY frontend/ . 
EXPOSE 5173

CMD ["pnpm", "dev"]
