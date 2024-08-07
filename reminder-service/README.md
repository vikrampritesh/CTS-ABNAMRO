## Solution Design and Development
Solutioned Architecture as Event-Driven Microservices using SpringBoot and Apache Kafka.
With the following microservices.

1)	Customer Service 
To create Bank customer, accounts and bank transaction etc.
2)	Email Service (Kafka Consumer)
To receive email-reminder-event and send to intended customer which already subscribed to respective Event-Publisher.
3)	Interest Reminder Service (Kafka Producer)
Find maturity deposits ahead of one months from the bank accounts and post the email-reminder event to the respective Event-Subscriber. 

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

1) Run Kafka (the folder where installed) <br>
  $ bin/zookeeper-server-start.sh config/zookeeper.properties
2) Run Zookeeper (the folder where Kafka installed)  <br>
  $ bin/kafka-server-start.sh config/server.properties
3) To check DB stored entries (Open Browser to view)  <br>
   http://localhost:8081/h2-console/login.jsp
4) To check implemented api from Swagger (Open Browser to view)  <br>
   http://localhost:8081/v3/api-docs
5) To view implemented API document specs of Swagger from UI (Open Browser to view)  <br>
   http://localhost:8081/swagger-ui/index.html   

   
   
