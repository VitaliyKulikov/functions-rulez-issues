# two issues then spring-cloud-stream-binder-kafka is enabled

## (1) swagger is not loading. show error. [seems fixed](https://github.com/springdoc/springdoc-openapi/commit/5257353a938948bc893f648d137c9bb5bb942585) 

- let's start.
let's say we don't have alive kafka broker, but with live one result is same.

```
$ ./gradlew bR --info

```

check `api-docs`
```
$ http :8080/v3/api-docs

http: error: ConnectionError: HTTPConnectionPool(host='localhost', port=8080): Max retries exceeded with url: /v3/api-docs (Caused by NewConnectionError('<urllib3.connection.HTTPConnection object at 0x101d989a0>: Failed to establish a new connection: [Errno 61] Connection refused')) while doing a GET request to URL: http://localhost:8080/v3/api-docs

```
such error will be during 1st minute for all endpoints (/ping) till binder will give up with msg:
```
[AdminClient clientId=adminclient-1] Metadata update failed

org.apache.kafka.common.errors.TimeoutException: The AdminClient thread has exited. Call: fetchMetadata
```
after that
```
$ http :8080/v3/api-docs

{
    "error": "Internal Server Error",
    "message": "No bean named 'streamBridge' available",
    "path": "/v3/api-docs",
    "requestId": "9716228b-3",
    "status": 500,
    "timestamp": "2022-08-03T15:49:26.783+00:00",
    "trace": "org.springframework.beans.factory.NoSuchBeanDefinitionException: No bean named 'streamBridge' available\n\tat org.springframework.beans.factory.support.DefaultListableBeanFactory.getBeanDefinition(DefaultListableBeanFactory.java:874)\n\tSuppressed: The stacktrace has been enhanced by Reactor, refer to additional information below: \nError has been observed at the following site(....
```
notice. if broker alive then such message will be shown from the begging.

if you comment `spring-cloud-starter-stream-kafka` as a dependency, everything is working as expected. 
```
$ http :8080/v3/api-docs

HTTP/1.1 200 OK
Content-Length: 1441
Content-Type: application/json;charset=UTF-8

{
    "components": {},
    "info": {
        "title": "OpenAPI definition",
        "version": "v0"
    },...
```

## (2) /actuator/health is not responding if kafka broker out of reach
this one is also funny

let's enable kafka-binder in build file. and make sure that kafka-broker is down.
first minute this endpoint is out of reach as others

```
http :8080/actuator/health

http: error: ConnectionError: HTTPConnectionPool(host='localhost', port=8080): Max retries exceeded with url: /actuator/health (Caused by NewConnectionError('<urllib3.connection.HTTPConnection object at 0x1077bc7f0>: Failed to establish a new connection: [Errno 61] Connection refused')) while doing a GET request to URL: http://localhost:8080/actuator/health

```
after one minute, then binder give up, we can have a call `http :8080/actuator/health` but with no response and waiting. after some time (next minute) with message 
```
Metadata update failed

org.apache.kafka.common.errors.TimeoutException: Timed out waiting for a node assignment. Call: fetchMetadata
```
we can see response for /health
```
❯ http :8080/actuator/health
HTTP/1.1 503 Service Unavailable
Content-Length: 311
Content-Type: application/vnd.spring-boot.actuator.v3+json

{
    "components": {
        "binders": {
            "components": {
                "kafka": {
                    "details": {
                        "Failed to retrieve partition information in": "60 seconds"
                    },
                    "status": "DOWN"
                }
            },
            "status": "DOWN"
        },
```
in few seconds, then broker start to reconnect (re-try) `/health` is dead (waiting, no response) again

## summary
seems, those issues are critical. springdoc (swagger) and just /api-docs are useful to show function endpoints. also, `/actuator/health` should be responsive then kafka-broker is down.

thanks ;)


### tools
- https://httpie.io/cli
