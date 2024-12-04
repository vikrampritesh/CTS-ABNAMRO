## Solution Design and Development
Solutioned Architecture as Event-Driven Microservices using SpringBoot and Apache Kafka with the following microservices.

1)	<b>Customer Service </b> </br>
To create Bank customer, accounts and bank transaction etc.
2)	<b>Email Service (Kafka Consumer)</b> </br>
To receive email-reminder-event and send to intended customer which already subscribed to respective Event-Publisher.
3)	<b>Interest Reminder Service (Kafka Producer) </b> </br>
Find maturity deposits ahead of one months from the bank accounts and post the email-reminder event to the respective Event-Subscriber. 

## Implemented Technologies
1. Java-18
2. Spring Boot 3.2.8
3. Spring Rest (Cloud Native Microservices)
4. Kafka 3.6.2
5. H2 Database
6. JPA
7. Spring Actuator
8. Lombok
9. Openapi/Swagger
10. Spring Junit/Integration Testing(Mockito/Jupitor)

## Run Steps To Test Local

1) Run Kafka (the folder where installed) <br>
```
  $ bin/zookeeper-server-start.sh config/zookeeper.properties
```
2) Run Zookeeper (the folder where Kafka installed)  <br>
```
  $ bin/kafka-server-start.sh config/server.properties
```
3) Run customer-service to create customer account with fixed deposit details
   Go to Project Home directory then
   ```
   ..\reminder-service\customer-service>  $ mvn spring-boot:run
   ```
3) Run interest-remainder-service to post email-events for mature fixed deposits after one month 
   Go to Project Home directory then
   ```
   ..\reminder-service\interest-remainder-service>  $ mvn spring-boot:run
   ```
5) Run email-service to conssume email-events for mature fixed deposits after one month 
 Go to Project Home directory then
```
 ..reminder-service\email-service>  $ mvn spring-boot:run
```
6) Go to Postman collection and run endpoints <br>

7) To check DB stored entries (Open Browser to view)  <br>
```
   http://localhost:8081/h2-console/login.jsp
```
8) To check implemented api from Swagger (Open Browser to view)  <br>
```
   http://localhost:8081/v3/api-docs
```
9) To view implemented API document specs of Swagger from UI (Open Browser to view)  <br>
```
   http://localhost:8081/swagger-ui/index.html
```

   
   
