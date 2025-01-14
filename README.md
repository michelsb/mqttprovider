# MQTTProvider: An MQTT traffic generator aimed at FIWARE Middleware

**MQTTProvider** is an MQTT synthetic traffic generator. It allows the modeling of time-based sensors, event-based sensors (with data generated from the dataset generated by the SmartSPEC tool) and mobile sensors (with data generated from the SUMO tool)

# Architecture

This application will only make use of one Postgres Database component. Since all interactions between the two elements are initiated by HTTP requests, the entities can be containerized and
run from exposed ports.

The necessary configuration information can be seen in the services section of the associated `docker-compose.yml` file:

```yaml
  postgres:
    image: postgres
    hostname: postgres
    container_name: postgres
    volumes:
      - ./sql/mqttproviderdb.sql:/docker-entrypoint-initdb.d/mqttproviderdb.sql
    environment:
      POSTGRES_PASSWORD: "mysecretpassword"
      POSTGRES_USER: "postgres"
      POSTGRES_DB: "mqttproviderdb"
    networks:
      - mqtt-net
    ports:
      - "5432:5432"
```

```yaml
  mqttprovider:
    build: ./app
    image: mqttprovider:v1.0
    hostname: mqttprovider
    container_name: mqttprovider
    volumes:
      - ./app/upload-dir:/app/target/upload-dir
    environment:
      DB_HOSTNAME: "postgres"
      DB_PORT: 5432
      DB_DATABASE: "mqttproviderdb"
      DB_USER: "postgres"
      DB_PASS: "mysecretpassword"
    networks:
      - mqtt-net
    ports:
      - "8080:8080"
    depends_on:
      - postgres
```

# Prerequisites

## Minimal hardware requirements

- 2-core CPU (additional are strongly recommended, especially if you intend to emulate more than 1000 devices);
- 4 GB RAM (additional memory is strongly recommended, especially if you intend to emulate more than 1000 devices);
- Minimum 30 GB hard disk space.

## Docker and Docker Compose

To keep things simple both components will be run using [Docker](https://www.docker.com). **Docker** is a container
technology which allows to different components isolated into their respective environments.

-   To install Docker on Windows follow the instructions [here](https://docs.docker.com/docker-for-windows/)
-   To install Docker on Mac follow the instructions [here](https://docs.docker.com/docker-for-mac/)
-   To install Docker on Linux follow the instructions [here](https://docs.docker.com/install/)

**Docker Compose** is a tool for defining and running multi-container Docker applications. A
[YAML file](https://raw.githubusercontent.com/Fiware/tutorials.Entity-Relationships/master/docker-compose.yml) is used
configure the required services for the application. This means all container services can be brought up in a single
command. Docker Compose is installed by default as part of Docker for Windows and Docker for Mac, however Linux users
will need to follow the instructions found [here](https://docs.docker.com/compose/install/)

You can check your current **Docker** and **Docker Compose** versions using the following commands:

```console
docker-compose -v
docker version
```

Please ensure that you are using Docker version 20.10 or higher and Docker Compose 1.29 or higher and upgrade if
necessary.

# Start Up

To run MQTTProvider, follow the steps below.

1) Open a terminal

2) Clone the MQTTProvider repository.

```console
$ git clone -b latest https://github.com/SmartCampus-UFC/SmartUFC-MQTTProvider mqttprovider
```

3) Access the mqttprovider directory.

4) Start up the container stack!

```console
$ docker compose up -d
```

5) If you have made changes to the code and want to build the image again and recreate the container:

```console
$ docker compose up -d --build --force-recreate
```

# Finishing the service

1) Stops running containers without removing them.

```
$ docker compose stop
```

2) Stops containers and removes containers, networks and volumes created

```
$ docker compose down -v --remove-orphans
```
