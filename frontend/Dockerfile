FROM node:22.18-alpine3.21 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build --docker

FROM nginx:alpine
COPY --from=build /app/dist/short-link-frontend/browser /usr/share/nginx/html/
COPY /nginx.conf /etc/nginx/conf.d/default.conf
CMD ["nginx", "-g", "daemon off;"]
