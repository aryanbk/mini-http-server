# Minimal Java HTTP Server

A lightweight, custom-built HTTP server implemented in Java without relying on built-in tools.

## Features

-   Supports GET and POST requests
-   GZip compression
-   File transfers
-   Multiple concurrent connections
-   Custom request dispatcher for routing

## Getting Started

### Prerequisites

-   Java JDK 21 or higher
-   Maven

### Installation

1. Clone the repository:
    ```
    git clone https://github.com/aryanbk/mini-http-server
    ```
2. Navigate to the project directory:
    ```
    cd mini-http-server
    ```
3. Build the project:
    ```
    mvn package
    ```
4. Run the server:
    ```
    ./mini_http_server.sh
    ```
    or
    ```
    java -jar target/mini-http-server-1.0-SNAPSHOT.jar
    ```

### Running the Server

Run the server using the following command:

```
mvn exec:java -Dexec.mainClass="com.aryanbk.minihttp.Server"
```

Options:

-   `--directory <path>`: Specify the directory for file operations

## Usage

The server handles various types of requests:

1. GET /: Returns a 200 OK response
2. GET /echo/<message>: Echoes the message back
3. GET /user-agent: Returns the User-Agent header
4. GET /files/<filename>: Retrieves a file from the specified directory
5. POST /files/<filename>: Uploads a file to the specified directory

## Project Structure

-   `Main.java`: Entry point of the application
-   `ClientHandler.java`: Handles individual client connections
-   `HttpRequest.java`: Parses and represents HTTP requests
-   `HttpResponse.java`: Builds and represents HTTP responses
-   `RequestHandler.java`: Routes requests to appropriate handlers
