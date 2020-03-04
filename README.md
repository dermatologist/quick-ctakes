# :rocket: QTakes - Apache cTAKES™ on Quarkus

Apache cTAKES™ is a natural language processing system for the extraction of information from electronic medical record clinical free-text. A web application providing REST API for cTAKES is available from the original repository. This is an extension of the REST API using Quarkus, (the Supersonic Subatomic Java Framework) designed for containers (docker).

## You need

* docker
* java 8
* maven

## How to use

### STEP 1: Create the database

You can use a local or remote MySQL database. The SQL data scripts are available in [this repo](https://github.com/GoTeamEpsilon/ctakes-rest-service). Refer to the section titled 'SQL Data Scripts.' You can also spin up a docker container with these scripts as below (Yes, it has to be MySQL 5):

```
FROM mysql:5

COPY ./sno_rx_16ab_db/ /docker-entrypoint-initdb.d/
```

### STEP 2: Check out this repo and add the details

```
git clone https://github.com/dermatologist/quick-ctakes.git
```
Add the database details in customDictionary.xml in the resources folder. Please note that you have to add them in two places. You can add your UMLS credentials also here. Make changes to the Default.piper if needed

### STEP 3: Install cTakes 4.0.1-SNAPSHOT locally

The application has cTakes 4.0.1 as a dependency, which is currently not available in maven central. Hence, you have to build and install it locally from the source as shown below.

```
git clone https://github.com/apache/ctakes.git
cd ctakes && mvn compile -DskipTests && mvn install -pl '!ctakes-distribution'  -DskipTests

```

### STEP 4: Build the java application, test it and package it
```
cd quick-ctakes
./mvnw clean compile quarkus:dev
mvn clean package -DskipTests

```

### STEP 5: Dockarize it
See the docker folder for details
A sample docker-compose is also provided.


## Author

[Bell Eapen](https://nuchange.ca)

## More about Quarkus


This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application is packageable using `./mvnw package`.
It produces the executable `quick-ctakes-1.0.0-SNAPSHOT-runner.jar` file in `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/quick-ctakes-1.0.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or you can use Docker to build the native executable using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/quick-ctakes-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .
