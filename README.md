Spring-Boot REST-API with swagger-ui
=====================================
A step-by-step introduction.

System Requirements:
--------------------
- OpenJDK for Java 1.8
- Git
- Maven 3.3.9 or higher
- Project Lombok https://projectlombok.org

Building the example project:
-----------------------------

To build the fat JAR and run tests:

    mvn clean install

To run:

    java -jar target/lendico-plangenerator-0.0.1-SNAPSHOT.jar


Swagger UI:

    http://localhost:8080/swagger-ui.html

Assumptions:
-------------------------
- I kept the start date as 1st day of the month irrespective of any date supplied (i.e 2020-02-15T00:00:0Z bill be 2020-02-01T00:00:0Z)
- No database, all calculation happen in memory, I kept it simple for now. I feel no need of database.
- As per the assignment document it look like 2 decimal place formatting is good to have, hence applied 2 decimal place formatting.
- JUnit coverage using jacoco-maven-plugin.

References:
-----------

