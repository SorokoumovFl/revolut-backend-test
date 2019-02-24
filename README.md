# Transfer Service

A primitive RESTful implementation of a money transfering service.


## Getting started
To build and run the project, execute the following from a project root directory

```
mvn clean package
java -jar transfer-service/target/transfer-service-1.0-SNAPSHOT.jar
```

## Configuration
As-is, service uses TCP port 8080.
Project doesn't support external configuration for the sake of simplicity, although default values could be changed via modifying .properties files

```
src/main/resources/web.properties
src/test/resources/web.properties
```

for basic configuration and test configuration accordingly.

By default, service should be available at `http://localhost:8080/`

## About
A project includes the following modules:
* **core**: CQRS/ES-based in-memory persistence model
* **transfer-service**: RESTful wrapping

Side note: keep in mind that with all the benefits event sourcing provides us, here comes a serious disadvantage: *eventual consistency*. Although an entire service is in-memory based, thus synchronization delay is insignificant, there is a delay.

## REST

**Response model**
```
{
    "code": 0,
    "message": "Response message",
    "payload": {
      // Response data
    }
}
```

**Possible response codes**

| Response code | Description                     | HTTP code |
|---------------|---------------------------------|-----------|
| 0             | OK                              | 200       |
| 1             | No such account                 | 404       |
| 2             | Insufficient funds              | 400       |
| 3             | Opreation currently unavailable | 500       |
| -1            | Unknown error                   | 500       |

> Side note: code **3** occurs due to optimistic transaction locking

**Available methods**

| Method                   | Description            |
|--------------------------|------------------------|
| GET /account/{accountId} | Get account by its ID  |
| POST /account/           | Create a new account   |
| PUT /account/{accountId} | Change account         |
| POST /transfer/          | Transfer funds         |
| GET /transfer/history    | Show transfers history |


## Request/response models

>GET /account/{accountId}

**Request body**

N/A

**Expected response**
```
{
    "code": 0,
    "message": "ok",
    "payload": {
        "account-id": "98821a1f-aec8-4891-9726-e821f766c05e",
        "owner-name": "name"
    }
}
```

>POST /account/

**Request body**

```
{
    "owner-name":  "name" // Account owner's name
}
```

**Expected response**
```
{
    "code": 0,
    "message": "ok",
    "payload": {
      "98821a1f-aec8-4891-9726-e821f766c05e" // Auto-generated account ID 
    }
}
```

>PUT /account/{accountId}

**Request body**

```
{
    "owner-name":  "name" // Account owner's name
}
```

**Expected response**
```
{
    "code": 0,
    "message": "ok"
}
```

> POST /transfer/

**Request body**

```
{
    "payer-id":  "98821a1f-aec8-4891-9726-e821f766c05e", // Payer's account ID (may be null)
    "beneficiary-id":  "98821a1f-aec8-4891-9726-e821f766c05e", // Beneficiary's account ID
    "amount": 0 // Amount
}
```

**Expected response**
```
{
    "code": 0,
    "message": "ok"
}
```

> POST /transfer/history/

**Request body**

N/A

**Expected response**
```
{
    "code": 0,
    "message": "ok",
    "payload": [{
      "payer-id": "98821a1f-aec8-4891-9726-e821f766c05e",
      "beneficiary-id": "98821a1f-aec8-4891-9726-e821f766c05e",
      "amount": 0
    }, ...]
}
```