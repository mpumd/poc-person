# poc-person

This project is just a poc to show :
- DDD approch on a functional entity named person with best practices,
- Context approch, aggregat, repository, event, command, query (CQRS vocabulary),
- BDD testing on the domain by the application layer,
- hexagonal architecture,
- spring, quarkus, others ? 
  - in : 
    - rest, soap, grpc, graphql and reactive/stream with reader model, 
    - kafka/jms consumer,
  - out : 
    - jpa(postgre, oracle, sqlserver), 
    - nosql (mongodb, elasticsearch) with a reader model
    - client (rest, soap), 
    - kafka/jms producer
  - security OIDC, SAML, bearer.
- java native compilation with graalvm,

# spring boot
```bash
mvn -T 1C clean install -DskipTests && mvn spring-boot:run -pl infra-spring/sb-bootstrap/
```

## url acces
- http://localhost:8090/spring/api-docs
- http://localhost:8090/spring/swagger-ui
- http://localhost:8090/spring/actuator
- http://localhost:8090/spring/admin

## Hot reload

[Spring Boot Auto Restart and Live Reload in IntelliJ IDEA Ultimate](https://www.codejava.net/frameworks/spring-boot/spring-boot-auto-restart-and-live-reload-in-intellij-idea)

Ctrl+f9 is enough for intellij community