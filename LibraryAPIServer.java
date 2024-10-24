
//An example of a simple REST API for a library system.
//The API allows students to request books from the library. 
//On Opolot's side there is two different clients: one for requesting a book and another one to view the available books in the library.


import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

// Simple Library REST API Server
public class LibraryAPIServer {
    // List to store available books
    private static List<String> availableBooks = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        // Adding some books to the library
        availableBooks.add("Basic studies of Java by A. Alex");
        availableBooks.add("Advanced Java by O. Martin");
        availableBooks.add("The Beginning of Java by M. Gedion");

        // Create a new HttpServer instance on port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // Define contexts for handling requests
        server.createContext("/api/books", new BooksHandler());
        server.createContext("/api/request", new RequestHandler());
        
        // Start the server
        server.start();
        System.out.println("Library Server started on port 8080");
    }

    // Handler for viewing available books
    static class BooksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Check if request method is GET
            if ("GET".equals(exchange.getRequestMethod())) {
                StringBuilder response = new StringBuilder("Available Books:\n");
                for (String book : availableBooks) {
                    response.append(book).append("\n"); // Building the response
                }
                exchange.sendResponseHeaders(200, response.length()); // Send response headers
                OutputStream os = exchange.getResponseBody(); // Get output stream
                os.write(response.toString().getBytes()); // Write the response
                os.close(); // Close output stream
            } else {
                // Handle unsupported methods
                String response = "Method not supported"; 
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // Handler for book requests
    static class RequestHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Check if the request method is POST
            if ("POST".equals(exchange.getRequestMethod())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()));
                String bookRequest = reader.readLine(); // Reading the requested book
                
                // Check if the requested book is available
                if (availableBooks.contains(bookRequest)) {
                    String response = "You have successfully requested: " + bookRequest;
                    exchange.sendResponseHeaders(200, response.length()); // Send success response headers
                    OutputStream os = exchange.getResponseBody(); 
                    os.write(response.getBytes()); // Write the response
                    os.close(); // Close output stream
                } else {
                    String response = "Book not available: " + bookRequest;
                    exchange.sendResponseHeaders(404, response.length()); // Send not found response headers
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes()); // Write the response
                    os.close(); // Close output stream
                }
            } else {
                // Handle unsupported methods
                String response = "Method not supported"; 
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }
}
