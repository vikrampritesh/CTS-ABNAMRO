# CTS-ABNAMRO
Coding Round

This assignment is to understand your hands on level working on Java 8 preferrably 17 using Spring boot. 
Use this repo to create your own branches from the main branch(empty)

## Problem Statement:
Mr Alex has a fixed deposit account started with Principal sum of $1000 which is maturing on 15th August 2024 at the Simple Interest Rate of 10% p.a. for 5 years.
The Bank now needs to remind him that his account is maturing with the maturity amount and the date of maturity.


## Solution Design and Development
1. Design and Develop Rest Microservices that use Kafka or any broker of your choice to communicate between them.
2. The code should follow SOLID priciples.
3. Code should be Junit tested and if possible create Integration Test.
4. Please note that this solution should be cloud deployment ready with proper swagger contract and good to have POSTMAN collections to test the APIs.


## Implemented Technologies
1. Java-18
2. Spring Boot 3.2.8
3. Spring Rest (Cloud Native Microservices)
4. Kafka
5. H2 Database
6. JPA
7. Spring Actuator
8. Lombok
9. Openapi/Swagger

## Run Steps To Test Local

1) Run Kafka (the folder where installed)
  $ bin/zookeeper-server-start.sh config/zookeeper.properties
2) Run Zookeeper (the folder where Kafka installed)
  $ bin/kafka-server-start.sh config/server.properties
3) To check DB stored entries (Open Browser to view)
   http://localhost:8081/h2-console/login.jsp
4) To check implemented api from Swagger (Open Browser to view)
   http://localhost:8081/v3/api-docs
5) To view implemented API document specs of Swagger from UI (Open Browser to view)
   http://localhost:8081/swagger-ui/index.html   

   
   
