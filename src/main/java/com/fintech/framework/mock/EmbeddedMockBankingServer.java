package com.fintech.framework.mock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Built-in embedded HTTP Mock Server.
 * Automatically starts if Docker / WireMock is not running on port 8089,
 * allowing test suites to pass 100% offline without external dependencies.
 */
public final class EmbeddedMockBankingServer {

    private static final Logger LOGGER = LogManager.getLogger(EmbeddedMockBankingServer.class);
    private static final int PORT = 8089;
    private static HttpServer server;

    private EmbeddedMockBankingServer() {}

    public static synchronized void startIfNeeded() {
        if (isPortInUse(PORT)) {
            LOGGER.info("Port {} is already in use (WireMock/Docker is active). Skipping embedded server.", PORT);
            return;
        }

        if (server != null) {
            return;
        }

        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // API Mock: Create Customer & KYC
            server.createContext("/api/v1/customers", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String response = """
                        {
                          "customerId": "CUST-DEMO-001",
                          "firstName": "John",
                          "lastName": "Doe",
                          "email": "john.doe@fintechqa.com",
                          "kycStatus": "VERIFIED",
                          "accountId": "ACC-DEMO-001",
                          "createdAt": "2026-09-24T12:00:00Z"
                        }
                        """;
                    sendJsonResponse(exchange, 201, response);
                }
            });

            // API Mock: Issue Virtual Card
            server.createContext("/api/v1/cards/issue", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String response = """
                        {
                          "cardId": "CRD-99887766",
                          "accountId": "ACC-DEMO-001",
                          "cardType": "VIRTUAL",
                          "maskedPan": "411111******1111",
                          "cardToken": "tok_visa_card_enc_998877",
                          "status": "PENDING_ACTIVATION",
                          "dailyLimit": 1000.00,
                          "expiryDate": "12/2029"
                        }
                        """;
                    sendJsonResponse(exchange, 201, response);
                }
            });

            // API Mock: Payment Authorization
            server.createContext("/api/v1/payments/authorize", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String response = """
                        {
                          "authorizationId": "AUTH-778899",
                          "authCode": "009988",
                          "status": "APPROVED",
                          "responseCode": "00",
                          "authorizedAmount": 150.00,
                          "currency": "USD",
                          "merchant": "Amazon Web Services",
                          "timestamp": "2026-09-24T12:05:00Z"
                        }
                        """;
                    sendJsonResponse(exchange, 200, response);
                }
            });

            // UI Mock: Card Management Portal HTML page
            server.createContext("/card-portal", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String html = """
                        <!DOCTYPE html>
                        <html>
                        <head><title>Card Management Portal</title></head>
                        <body>
                          <h1>Card Management Portal</h1>
                          <div id="card-status-badge">PENDING_ACTIVATION</div>
                          <button id="btn-activate-card" onclick="document.getElementById('card-status-badge').innerText='ACTIVE'">Activate Card</button>
                          <br/><br/>
                          <input id="input-card-pin" type="password" placeholder="PIN"/>
                          <input id="input-confirm-pin" type="password" placeholder="Confirm PIN"/>
                          <button id="btn-submit-pin" onclick="document.getElementById('toast').innerText='PIN set successfully'; document.getElementById('toast').style.display='block';">Set PIN</button>
                          <br/><br/>
                          <input id="input-daily-limit" type="number" value="1000"/>
                          <button id="btn-update-limit">Update Limit</button>
                          <br/><br/>
                          <button id="btn-toggle-freeze" onclick="var b=document.getElementById('card-status-badge'); b.innerText = (b.innerText==='ACTIVE'?'FROZEN':'ACTIVE');">Toggle Freeze</button>
                          <div id="toast" class="toast-success" style="display:none;">PIN set successfully</div>
                        </body>
                        </html>
                        """;
                    byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                }
            });

            // UI Mock: Payment Checkout Page HTML
            server.createContext("/checkout", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String html = """
                        <!DOCTYPE html>
                        <html>
                        <head><title>Payment Checkout</title></head>
                        <body>
                          <h2>Payment Checkout Gateway</h2>
                          <input id="cardNumber" placeholder="Card Number"/>
                          <input id="cardExpiry" placeholder="MM/YY"/>
                          <input id="cardCvv" placeholder="CVV"/>
                          <input id="amount" placeholder="Amount"/>
                          <button id="btn-pay-now" onclick="document.getElementById('auth-confirmation').innerText='Payment Approved: AUTH-994422'">Pay Now</button>
                          <div id="auth-confirmation"></div>
                        </body>
                        </html>
                        """;
                    byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, bytes.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(bytes);
                    }
                }
            });

            server.setExecutor(null);
            server.start();
            LOGGER.info("Embedded Mock Banking Server successfully started on port {}", PORT);
        } catch (IOException e) {
            LOGGER.error("Failed to start Embedded Mock Banking Server on port {}", PORT, e);
        }
    }

    public static synchronized void stop() {
        if (server != null) {
            LOGGER.info("Stopping Embedded Mock Banking Server");
            server.stop(0);
            server = null;
        }
    }

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static boolean isPortInUse(int port) {
        try (Socket socket = new Socket("localhost", port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
