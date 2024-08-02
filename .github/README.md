# Medfast Backend

Backend repository for MedFast project

<!-- TOC -->
* [Medfast Backend](#medfast-backend)
  * [Technological Stack](#technological-stack)
  * [Getting Started Locally](#getting-started-locally)
    * [Installation](#installation)
    * [Running the project](#running-the-project)
      * [Via Docker](#via-docker)
        * [Prerequisites](#prerequisites)
        * [Steps](#steps)
      * [Without Docker](#without-docker)
        * [Prerequisites](#prerequisites-1)
        * [Steps](#steps-1)
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

### Installation

Clone the repository

```bash
git clone https://github.com/medfastdev/medfast-be.git
```

### Running the project

We can run either of `prod` (will pull the latest `main` image from docker hub) or `dev` (will build
image from the local version) docker compose files.

#### Via Docker

##### Prerequisites

- Docker / Docker Compose

##### Steps

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

#### Without Docker

##### Prerequisites

- Java 17
- Make sure you have PostgreSQL installed and running on your machine on port `5432` with the
  following credentials:
  - username: `user`
  - password: `secret`
- Environment variable:
    - `MAIL_PASSWORD=medfast_email_password`

> [!IMPORTANT]
> The `medfast_email_password` can be found
> in [the Medfast docs](https://docs.google.com/document/d/16I_MUle7IBE3wN9GDAVzZhlh00B0DSXia3hpzNTSyT8/edit#heading=h.9l37u1xea78s)

> [!IMPORTANT]
> How to set environment
> variables: [Windows](https://www.architectryan.com/2018/08/31/how-to-change-environment-variables-on-windows-10/),
> [macOS and Linux](https://phoenixnap.com/kb/set-environment-variable-mac)

##### Steps

Run the following command in the root directory of the project

```bash
./gradlew bootRun
```
