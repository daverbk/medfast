# Medfast Backend

Backend repository for MedFast project

<!-- TOC -->
* [Medfast Backend](#medfast-backend)
  * [Technological Stack](#technological-stack)
  * [Getting Started Locally](#getting-started-locally)
    * [Prerequisites](#prerequisites)
    * [Installation](#installation)
    * [Running the project](#running-the-project)
<!-- TOC -->

## Technological Stack

- Java 17
- Gradle
- PostgreSQL
- Spring Boot
- Spring Security (JWT)
- Spring Data JPA
- Spring Cloud Stream for RabbitMQ
- Spring Testing
- Liquibase
- Docker / Docker Compose
- Swagger
- Thymeleaf

## Getting Started Locally

### Prerequisites

- Docker / Docker Compose

### Installation

Clone the repository

```bash
git clone https://github.com/medfastdev/medfast-be.git
```

### Running the project

We can run either of `prod` (will pull the latest `main` image from docker hub) or `dev` (will build
image from the local version) docker compose files.

- `prod`

```bash
docker compose up
```

- `dev`

```bash
docker compose up --build
```

> [!IMPORTANT]
> Execute these commands in the root directory of the project

> [!IMPORTANT]
> If you are running the `prod` version, please make sure that you have logged in to the docker hub
> beforehand
> ```bash
> docker login docker.io -u healflowdev
> ```
> Shortly after executing the command above, you will be prompted to enter the password, it can be
> found
> in [the Medfast docs](https://docs.google.com/document/d/16I_MUle7IBE3wN9GDAVzZhlh00B0DSXia3hpzNTSyT8/edit#heading=h.9l37u1xea78s)
