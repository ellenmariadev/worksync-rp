# Build stage
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

# Run stage
FROM node:20-alpine
WORKDIR /app
COPY --from=build /app/dist /app/dist
COPY --from=build /app/package*.json ./
RUN npm ci --only=production && npm cache clean --force

EXPOSE 10000
CMD ["npm", "run", "serve:ssr:frontend"]
