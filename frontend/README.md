# Short Link Frontend

A modern Angular 20 web application for shortening URLs, managing links, and viewing statistics. This project is designed to work with a Spring Boot backend and supports local development as well as Dockerized deployment.

## UI Screenshots

Screenshots of the user interface are available in the `ui-screenshots` folder.

## Table of Contents

- [Features](#features)
- [Tech Stack & Versions](#tech-stack--versions)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Running Locally](#running-locally)
- [Running with Docker](#running-with-docker)
- [Environment Configuration](#environment-configuration)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)

---

## Features

- User registration and login
- Shorten URLs
- Dashboard with statistics
- Responsive UI with Angular Material
- Global error handling
- Refresh token

## Tech Stack & Versions

- **Angular:** ^20.1.0
- **Node.js:** 20.x (for build)
- **NPM:** 10.x
- **TypeScript:** ~5.8.2
- **RxJS:** ~7.8.0
- **Angular Material:** ^20.1.4

## Setup & Installation

## Running Locally

1. **Clone the repository:**
   ```sh
   git clone https://github.com/Panic7/url-shortener.git
   cd frontend
   ```
2. **Install dependencies:**
   ```sh
   npm install
   ```

3. **Start the Angular development server:**
   ```sh
   npm start
   ```

   The app will be available at [http://localhost:4200](http://localhost:4200).

4. **Backend API:**
   - Ensure the backend Spring Boot server is running at `http://127.0.0.1:8080`.
