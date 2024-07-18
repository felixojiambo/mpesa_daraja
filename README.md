# M-PesaPay B2C Transaction Service

## Overview

The M-PesaPay B2C Transaction Service is a microservice designed to handle Business-to-Consumer (B2C) transactions through Safaricom's M-Pesa API. This service provides endpoints for initiating B2C transactions, checking transaction status, and handling callbacks from M-Pesa.

## Features

- **B2C Transaction Requests**: Submit and process B2C transaction requests.
- **Transaction Status**: Retrieve the status of a given transaction.
- **Access Token Management**: Fetch and manage OAuth access tokens for secure API communication.
- **URL Registration**: Register URLs for M-Pesa validation and confirmation callbacks.

## Prerequisites

- Docker
- Docker Compose
- Java 11+
- Maven

## Getting Started

### Clone the Repository

```sh
git clone https://github.com/felixojiambo/mpesa_daraja.git
cd mpesapay
```

### Setup Configuration

Update the `application.yml` file in the `src/main/resources` directory with your M-Pesa API credentials and URLs:

```yaml
mpesa:
  daraja:
    consumer-key: your_consumer_key
    consumer-secret: your_consumer_secret
    grant-type: client_credentials
    oauth-endpoint: https://sandbox.safaricom.co.ke/oauth/v1/generate
    register-url-endpoint: https://sandbox.safaricom.co.ke/mpesa/b2c/v1/registerurl
    b2c-transaction-endpoint: https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest
    transaction-status-endpoint: https://sandbox.safaricom.co.ke/mpesa/transactionstatus/v1/query
    shortCode: your_shortcode
    confirmationURL: https://yourdomain.com/confirmation
    validationURL: https://yourdomain.com/validation
    b2c-result-url: https://yourdomain.com/b2c/result
    b2c-queue-timeout-url: https://yourdomain.com/b2c/queue-timeout
    initiator-name: your_initiator_name
    initiator-password: your_initiator_password
```

### Build and Run the Application

#### Using Docker Compose

Build and start the services:

```sh
docker-compose up --build
```

#### Running Locally

Ensure MongoDB, Zookeeper, and Kafka are running. You can use Docker Compose to start them:

```sh
docker-compose up zookeeper kafka mongodb
```

Then, build and run the Spring Boot application:

```sh
./mvnw clean install
./mvnw spring-boot:run
```

## API Endpoints

### B2C Transaction Request

**Endpoint:** `/api/b2c/request`

**Method:** `POST`

**Request Body:**

```json
{
    "InitiatorName": "your_initiator_name",
    "SecurityCredential": "your_security_credential",
    "CommandID": "BusinessPayment",
    "Amount": "1000",
    "PartyA": "600000",
    "PartyB": "254708374149",
    "Remarks": "Payment for services",
    "QueueTimeOutURL": "https://yourdomain.com/queue-timeout",
    "ResultURL": "https://yourdomain.com/result",
    "Occasion": "Birthday"
}
```

**Response:**

```json
{
    "status": "success",
    "message": "B2C request received and processing"
}
```

### Fetch Transaction Status

**Endpoint:** `/api/b2c/status/{transactionId}`

**Method:** `GET`

**Response:**

```json
{
    "ResultType": 0,
    "ResultCode": 0,
    "ResultDesc": "The service request has been accepted successfully.",
    "OriginatorConversationID": "AG_20191212_00004be9c3e1c64538bb",
    "ConversationID": "AG_20191212_000043645d4e7a9c3d5d",
    "TransactionID": "LGR519G3SZ",
    "ResultParameters": {
        "ResultParameter": [
            {
                "Key": "TransactionAmount",
                "Value": "100.00"
            },
            {
                "Key": "TransactionReceipt",
                "Value": "LGR519G3SZ"
            },
            {
                "Key": "B2CWorkingAccountAvailableFunds",
                "Value": "100000.00"
            },
            {
                "Key": "B2CUtilityAccountAvailableFunds",
                "Value": "100000.00"
            },
            {
                "Key": "TransactionCompletedDateTime",
                "Value": "12.12.2019 12:45:00"
            },
            {
                "Key": "ReceiverPartyPublicName",
                "Value": "John Doe"
            },
            {
                "Key": "B2CChargesPaidAccountAvailableFunds",
                "Value": "1000.00"
            },
            {
                "Key": "B2CRecipientIsRegisteredCustomer",
                "Value": "Y"
            }
        ]
    }
}
```

### Retrieve Access Token

**Endpoint:** `/tandapay/token`

**Method:** `GET`

**Response:**

```json
{
    "accessToken": "some-access-token",
    "expiresIn": "3599"
}
```

### Register URL

**Endpoint:** `/tandapay/register-url`

**Method:** `GET`

**Response:**

```json
{
    "conversationID": "AG_20191212_00004be9c3e1c64538bb",
    "originatorConversationID": "AG_20191212_000043645d4e7a9c3d5d",
    "responseDescription": "Success"
}
```

## Running Tests

### Unit Tests

To run the unit tests, execute:

```sh
./mvnw test
```

### Integration Tests

To run the integration tests, execute:

```sh
./mvnw verify
```

## Contributing

We welcome contributions to improve this project. Please submit a pull request with your changes and ensure that all tests pass before submitting.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
