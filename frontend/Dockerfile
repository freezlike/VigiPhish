FROM node:22-bookworm-slim AS dependencies
WORKDIR /workspace
COPY package.json package-lock.json* ./
RUN npm install

FROM dependencies AS build
COPY angular.json tsconfig*.json proxy.conf.json ./
COPY public ./public
COPY src ./src
RUN npm run build

FROM dependencies AS test
RUN apt-get update \
    && apt-get install -y --no-install-recommends chromium \
    && rm -rf /var/lib/apt/lists/*
ENV CHROME_BIN=/usr/bin/chromium
COPY angular.json tsconfig*.json proxy.conf.json ./
COPY public ./public
COPY src ./src

FROM nginx:1.29-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /workspace/dist/dssi-phishing-awareness/browser /usr/share/nginx/html
EXPOSE 80
