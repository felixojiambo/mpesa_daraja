# B2C Transaction Handling

## Project Description
This project handles Daraja B2C (Business to Customer) transactions using Java Spring Boot, Kafka, and MongoDB.

## Setup Instructions
1. Clone the repository.
2. Set up MongoDB and Kafka locally.
3. Update the `application.yml` file with your MongoDB and Kafka configurations.
4. Run the application using `./mvnw spring-boot:run`.

## API Endpoints
### Receive B2C Request
- **Endpoint**: `/api/b2c/request`
- **Method**: POST
- **Request Body**: `InternalB2CTransactionRequest`
- **Response**: String message indicating request received.

### Fetch Payment Status
- **Endpoint**: `/api/b2c/status/{transactionId}`
- **Method**: GET
- **Response**: `TransactionStatusSyncResponse`

## Testing Instructions
- Run unit tests: `./mvnw test`
- Run integration tests: `./mvnw verify`

## Dependencies
- Spring Boot
- Spring Data MongoDB
- Spring for Apache Kafka
- Lombok
- OkHttp
- JUnit
- Mockito

## Contributing
- Fork the repository.
- Create a new branch for your feature or bugfix.
- Submit a pull request with a detailed description of your changes.
