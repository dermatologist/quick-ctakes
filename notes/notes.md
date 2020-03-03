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