## Create db container

```
FROM mysql:5

COPY ./sno_rx_16ab_db/ /docker-entrypoint-initdb.d/
```

## Build 4.0.1-SNAPSHOT

```
git clone https://github.com/apache/ctakes.git
cd ctakes && mvn compile -DskipTests && mvn install -pl '!ctakes-distribution'  -DskipTests

```

## Deploy database

```
docker run -d -p 3306:3306 --name ctakes-mysql -e MYSQL_ROOT_PASSWORD=pass beapen/ctdict

```

## Run It

```
./mvnw clean compile quarkus:dev

```

## Package it

```
mvn clean package -DskipTests
```

## Build container

```
docker build -f src/main/docker/Dockerfile.jvm -t beapen/qtakes .
```

## Deploy docker-compose

```
docker-compose -f src/main/docker/docker-compose.yml up

```