# AMQ Devourer

Devours messages from a specified Active MQ queue.

### Configuration
Environment variables:

``` properties
activemq_broker_url
activemq_broker_user (optional)
activemq_broker_password (optional)
activemq_concurrency (default is 8-32)
activemq_connection_factory_max_connections (default is 50)
activemq_target_queue
```

### Build
Command line:

``` bash
./gradlew [clean] [cleanTest] [build] bootJar
```

### Start
Command line:

``` bash
java -jar build/libs/devourer-1.0.0.jar
```

### Stop
Press ctrl+c.

