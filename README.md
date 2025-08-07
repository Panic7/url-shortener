## Table of Contents

- [Description](#description)
- [Technologies](#technologies)
- [Setup](#setup)
  - [Docker](#docker)
  - [Local](#local)
- [Usage](#usage)
  - [AuthenticationController](#authenticationcontroller)
    - [Login](#1-login)
    - [Refresh Token](#2-refresh-token)
    - [Logout](#3-logout)
    - [Current User](#4-current-user)
  - [ShortLinkController](#shortlinkcontroller)
    - [Create Short Link](#1-create-short-link)
    - [Get Short Links with Metrics](#2-get-short-links-with-metrics)
    - [Redirect to Original URL](#3-redirect-to-original-url)
  - [UserController](#usercontroller)
    - [Signup](#1-signup)
- [Testing](#testing)

## Description
A link shortening service that lets users submit long URLs and receive shortened links for easy sharing.
It supports user authentication, metrics, and allows custom short links.

## Technologies

[Java](https://www.oracle.com/java/): **21**

[Spring Boot](https://github.com/spring-projects/spring-boot): **3.5.4**

[Tomcat](https://github.com/apache/tomcat): **10.1.43**

[Spring Framework](https://github.com/spring-projects/spring-framework): **6.2.9**

[Maven](https://maven.apache.org/): **3.9.11**

## Setup
You can run this project using docker or manually on your local machine.

### Docker
**Note:**  
Docker must be installed on your machine.

1. Clone the repository
    ```bash
    git clone https://github.com/panic7/url-shortener.git
    cd url-shortener
    ```
2. Build the Docker Image
    ```bash
    docker build -t url-shortener .
    ```
3. Run the Docker Container
    ```bash
    docker run -p 8080:8080 url-shortener
    ```
    The application will be available at [http://localhost:8080](http://localhost:8080).


### Local
**Note:**  
JDK 21 and Maven must be installed on your machine.

1. Clone the repository
    ```bash
    git clone https://github.com/panic7/url-shortener.git
    cd url-shortener
    ```

2. Build the project with Maven
    ```bash
    mvn clean package
    ```

3. Run the application
    ```bash
    java -jar target/link-shortener-0.0.1-SNAPSHOT.jar
    ```
   The application will be available at [http://localhost:8080](http://localhost:8080).

## Usage

In the root of the project, you will find the `link-shortener.har` file. This file contains a collection of HTTP requests for all 7 endpoints of the project. You can import it into tools like Postman or Insomnia to test the API.

### AuthenticationController

#### 1. Login
- **Endpoint:** `POST /auth/login`
- **Input:**
    ```json
    {
      "email": "user@example.com",
      "password": "StrongPassword123"
    }
    ```
- **Output:** HTTP 200 OK with Set-Cookie headers for `accessToken` and `refreshToken`.
    ```json
    {
      "email": "user@example.com"
    }
    ```

#### 2. Refresh Token
- **Endpoint:** `POST /auth/refresh`
- **Input:**
    ```json
    {
      "refreshToken": "refresh-token-string"
    }
    ```
- **Output:** HTTP 200 OK with Set-Cookie headers for `accessToken` and `refreshToken`.

#### 3. Logout
- **Endpoint:** `POST /auth/logout`
- **Output:** HTTP 200 OK with Set-Cookie headers to clear `accessToken` and `refreshToken`.

#### 4. Current User
- **Endpoint:** `GET /auth/current-user`
- **Output:** HTTP 200 OK
    ```json
    {
      "email": "user@example.com";
    }
    ```

### ShortLinkController

#### 1. Create Short Link
- **Endpoint:** `POST /shorten`
- **Input:**
    ```json
    {
      "url": "https://example.com"
    }
    ```
- **Output:** HTTP 200 OK
    ```json
    {
      "shortenedUrl": "http://localhost:8080/abc123"
    }
    ```

#### 2. Get Short Links with Metrics
- **Endpoint:** `GET /shortlinks/users/me`
- **Output:** HTTP 200 OK
    ```json
    {
      "content": [
        {
          "shortUrl": "http://localhost:8080/abc123",
          "originalUrl": "https://example.com",
          "shortCode": "abc123",
          "clickCount": 5
        }
      ],
      "totalPages": 1,
      "totalElements": 1,
      "size": 20,
      "number": 0
    }
    ```

#### 3. Redirect to Original URL
- **Endpoint:** `GET /{shortCode}`
- **Output:** 200 OK with a redirect to the original URL.

### UserController

#### 1. Signup
- **Endpoint:** `POST /users/signup`
- **Input:**
    ```json
    {
      "email": "user@example.com",
      "password": "StrongPassword123"
    }
    ```
- **Output:** HTTP 200 OK


## Testing

### Automated Tests
To run the tests locally, use the following Maven command:
```bash
mvn test
```
Tests are also executed automatically during the Docker build process.

> Unit tests are **not implemnted** yet because that's time-consuming.

Planned improvements:
- Achieve at least **80% code coverage** using unit tests.
- Integrate **JaCoCo** for code coverage analysis.

### Manual Testing
You can manually test all available endpoints using [Postman](https://www.postman.com/) or [Insomnia](https://insomnia.rest/).  

> `link-shortener.har` - file located at the project root containing a pre-configured request collection for all endpoints.

Simply import this `.har` file into your API client of choice to get started.
