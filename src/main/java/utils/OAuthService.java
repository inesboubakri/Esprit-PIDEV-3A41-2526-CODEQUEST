package utils;

import models.SocialUserInfo;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * OAuth2 Service for handling social login with Google, GitHub, and Discord
 * 
 * This service manages the complete OAuth2 flow:
 * 1. Opens browser to provider's consent screen
 * 2. Starts local HTTP server on port 8080 to receive callback
 * 3. Exchanges authorization code for access token
 * 4. Fetches user information from provider
 * 5. Returns SocialUserInfo with email, name, and provider
 * 
 * Each OAuth flow runs in a separate thread to avoid blocking the UI.
 */
public class OAuthService {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static HttpServer callbackServer = null;
    private static SocialUserInfo oauthResult = null;
    private static String oauthError = null;
    private static final Object resultLock = new Object();

    /**
     * Start the OAuth2 flow with Google
     * 
     * @return SocialUserInfo if successful, null otherwise
     */
    public static SocialUserInfo startGoogleOAuth() {
        return startOAuthFlow("google", 
            OAuthConfig.GOOGLE_AUTH_URL,
            OAuthConfig.GOOGLE_CLIENT_ID,
            OAuthConfig.GOOGLE_REDIRECT_URI,
            OAuthConfig.GOOGLE_SCOPE);
    }

    /**
     * Start the OAuth2 flow with GitHub
     * 
     * @return SocialUserInfo if successful, null otherwise
     */
    public static SocialUserInfo startGithubOAuth() {
        return startOAuthFlow("github",
            OAuthConfig.GITHUB_AUTH_URL,
            OAuthConfig.GITHUB_CLIENT_ID,
            OAuthConfig.GITHUB_REDIRECT_URI,
            OAuthConfig.GITHUB_SCOPE);
    }

    /**
     * Start the OAuth2 flow with Discord
     * 
     * @return SocialUserInfo if successful, null otherwise
     */
    public static SocialUserInfo startDiscordOAuth() {
        return startOAuthFlow("discord",
            OAuthConfig.DISCORD_AUTH_URL,
            OAuthConfig.DISCORD_CLIENT_ID,
            OAuthConfig.DISCORD_REDIRECT_URI,
            OAuthConfig.DISCORD_SCOPE);
    }

    /**
     * Internal method to handle OAuth2 flow for any provider
     * 
     * @param provider The provider name ("google", "github", "discord")
     * @param authUrl The provider's authorization URL
     * @param clientId The OAuth client ID
     * @param redirectUri The redirect URI (callback URL)
     * @param scope The requested OAuth scope
     * @return SocialUserInfo if successful, null otherwise
     */
    private static SocialUserInfo startOAuthFlow(String provider, String authUrl, 
                                                 String clientId, String redirectUri, String scope) {
        System.out.println("\n🔐 Starting OAuth2 flow for " + provider.toUpperCase());
        
        // Reset state
        oauthResult = null;
        oauthError = null;

        try {
            // Step 1: Start local callback server
            System.out.println("📡 Starting local callback server on port " + OAuthConfig.CALLBACK_SERVER_PORT);
            startCallbackServer();

            // Step 2: Generate random state (CSRF protection)
            String state = generateRandomState();

            // Step 3: Build authorization URL
            String authorizeUrl = buildAuthorizationUrl(authUrl, clientId, redirectUri, scope, state);
            System.out.println("🌐 Opening browser to authorization URL");

            // Step 4: Open browser
            openBrowser(authorizeUrl);

            // Step 5: Wait for callback (with timeout)
            System.out.println("⏳ Waiting for user authorization (timeout: " + OAuthConfig.OAUTH_TIMEOUT_SECONDS + " seconds)");
            boolean authorized = waitForCallback(OAuthConfig.OAUTH_TIMEOUT_SECONDS);

            // Step 6: Stop callback server
            stopCallbackServer();

            if (!authorized) {
                if (oauthError != null) {
                    System.out.println("❌ OAuth Error: " + oauthError);
                    return null;
                }
                System.out.println("❌ OAuth timeout - user took too long to authorize");
                return null;
            }

            System.out.println("✅ OAuth authorization successful!");
            System.out.println("🔐 User: " + oauthResult);
            return oauthResult;

        } catch (Exception e) {
            System.err.println("❌ Error during OAuth flow: " + e.getMessage());
            e.printStackTrace();
            stopCallbackServer();
            return null;
        }
    }

    /**
     * Start the local HTTP server on localhost:8080 to receive OAuth callbacks
     * The server listens for:
     * - /callback/google?code=...&state=...
     * - /callback/github?code=...&state=...
     * - /callback/discord?code=...&state=...
     */
    private static void startCallbackServer() throws IOException {
        // Stop any existing server first
        stopCallbackServer();
        
        // Give the OS time to release the port
        try {
            Thread.sleep(1000); // Increased to 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            callbackServer = HttpServer.create(
                new InetSocketAddress("localhost", OAuthConfig.CALLBACK_SERVER_PORT), 0);
            
            // Enable SO_REUSEADDR to allow quick port reuse
            callbackServer.setExecutor(Executors.newSingleThreadExecutor());
        } catch (IOException e) {
            System.err.println("❌ Failed to create HTTP server on port " + OAuthConfig.CALLBACK_SERVER_PORT);
            System.err.println("⚠️  Another application may be using this port");
            System.err.println("💡 Try killing the process: netstat -ano | findstr :8080");
            throw e;
        }

        // Create context for Google callback
        callbackServer.createContext("/callback/google", exchange -> {
            handleCallbackRequest(exchange, "google");
        });

        // Create context for GitHub callback
        callbackServer.createContext("/callback/github", exchange -> {
            handleCallbackRequest(exchange, "github");
        });

        // Create context for Discord callback
        callbackServer.createContext("/callback/discord", exchange -> {
            handleCallbackRequest(exchange, "discord");
        });

        // Create context for root (catch-all)
        callbackServer.createContext("/", exchange -> {
            String response = "CodeQuest OAuth Callback Server - Ready";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.close();
        });

        callbackServer.start();
        System.out.println("✅ Local callback server started on http://localhost:" + OAuthConfig.CALLBACK_SERVER_PORT);
    }

    /**
     * Handle incoming OAuth callback request
     * Extracts authorization code, exchanges it for access token, and fetches user info
     */
    private static void handleCallbackRequest(HttpExchange exchange, String provider) throws IOException {
        try {
            // Parse query parameters
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendErrorResponse(exchange, "Missing parameters");
                return;
            }

            // Extract authorization code and error (if any)
            String authCode = null;
            String error = null;
            String[] params = query.split("&");
            
            for (String param : params) {
                String[] kv = param.split("=", 2);
                if (kv.length == 2) {
                    String key = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
                    String value = URLDecoder.decode(kv[1], StandardCharsets.UTF_8);
                    
                    if ("code".equals(key)) {
                        authCode = value;
                    } else if ("error".equals(key)) {
                        error = value;
                    }
                }
            }

            // Check for user denial
            if (error != null) {
                System.out.println("❌ User denied authorization: " + error);
                synchronized (resultLock) {
                    oauthError = error;
                    resultLock.notifyAll();
                }
                sendErrorResponse(exchange, "Authorization denied by user");
                return;
            }

            if (authCode == null) {
                sendErrorResponse(exchange, "Authorization code not received");
                return;
            }

            System.out.println("📦 Received authorization code for " + provider);

            // Capture final references for lambda
            final String finalAuthCode = authCode;
            final String finalProvider = provider;

            // Exchange code for access token in a separate thread to avoid blocking
            new Thread(() -> {
                try {
                    exchangeCodeForToken(finalAuthCode, finalProvider);
                } catch (Exception e) {
                    System.err.println("❌ Error exchanging code for token: " + e.getMessage());
                    e.printStackTrace();
                    synchronized (resultLock) {
                        oauthError = e.getMessage();
                        resultLock.notifyAll();
                    }
                }
            }).start();

            // Send success response
            sendSuccessResponse(exchange);

        } catch (Exception e) {
            System.err.println("❌ Error handling callback: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(exchange, e.getMessage());
        }
    }

    /**
     * Exchange authorization code for access token
     * Then fetch user information from the provider
     */
    private static void exchangeCodeForToken(String authCode, String provider) throws IOException, InterruptedException {
        System.out.println("🔄 Exchanging authorization code for access token (" + provider + ")");

        String tokenUrl;
        String clientId;
        String clientSecret;
        String redirectUri;

        switch (provider) {
            case "google" -> {
                tokenUrl = OAuthConfig.GOOGLE_TOKEN_URL;
                clientId = OAuthConfig.GOOGLE_CLIENT_ID;
                clientSecret = OAuthConfig.GOOGLE_CLIENT_SECRET;
                redirectUri = OAuthConfig.GOOGLE_REDIRECT_URI;
            }
            case "github" -> {
                tokenUrl = OAuthConfig.GITHUB_TOKEN_URL;
                clientId = OAuthConfig.GITHUB_CLIENT_ID;
                clientSecret = OAuthConfig.GITHUB_CLIENT_SECRET;
                redirectUri = OAuthConfig.GITHUB_REDIRECT_URI;
            }
            case "discord" -> {
                tokenUrl = OAuthConfig.DISCORD_TOKEN_URL;
                clientId = OAuthConfig.DISCORD_CLIENT_ID;
                clientSecret = OAuthConfig.DISCORD_CLIENT_SECRET;
                redirectUri = OAuthConfig.DISCORD_REDIRECT_URI;
            }
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        }

        // Build token request body
        String tokenBody = "grant_type=authorization_code" +
                "&code=" + URLEncoder.encode(authCode, StandardCharsets.UTF_8) +
                "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        // For GitHub and Discord, we need to add Accept header
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(tokenUrl))
                .POST(HttpRequest.BodyPublishers.ofString(tokenBody))
                .header("Content-Type", "application/x-www-form-urlencoded");

        if ("github".equals(provider) || "discord".equals(provider)) {
            requestBuilder.header("Accept", "application/json");
        }

        HttpRequest tokenRequest = requestBuilder.build();
        HttpResponse<String> tokenResponse = HTTP_CLIENT.send(tokenRequest, HttpResponse.BodyHandlers.ofString());

        if (tokenResponse.statusCode() != 200) {
            throw new IOException("Token exchange failed with status " + tokenResponse.statusCode() + 
                    ": " + tokenResponse.body());
        }

        // Parse response to get access token
        JsonObject tokenJson = JsonParser.parseString(tokenResponse.body()).getAsJsonObject();
        String accessToken = tokenJson.get("access_token").getAsString();

        System.out.println("✅ Access token received");

        // Fetch user info with access token
        fetchUserInfo(accessToken, provider);
    }

    /**
     * Fetch user information using the access token
     */
    private static void fetchUserInfo(String accessToken, String provider) throws IOException, InterruptedException {
        System.out.println("👤 Fetching user information for " + provider);

        String userInfoUrl;
        switch (provider) {
            case "google" -> userInfoUrl = OAuthConfig.GOOGLE_USERINFO_URL;
            case "github" -> userInfoUrl = OAuthConfig.GITHUB_USERINFO_URL;
            case "discord" -> userInfoUrl = OAuthConfig.DISCORD_USERINFO_URL;
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        }

        // Build request with Authorization header
        HttpRequest userRequest = HttpRequest.newBuilder()
                .uri(URI.create(userInfoUrl))
                .GET()
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> userResponse = HTTP_CLIENT.send(userRequest, HttpResponse.BodyHandlers.ofString());

        if (userResponse.statusCode() != 200) {
            throw new IOException("Failed to fetch user info: " + userResponse.statusCode());
        }

        // Parse user info based on provider
        JsonObject userJson = JsonParser.parseString(userResponse.body()).getAsJsonObject();
        SocialUserInfo userInfo = parseUserInfo(userJson, provider, accessToken);

        if (userInfo != null) {
            System.out.println("✅ User info retrieved: " + userInfo);
            synchronized (resultLock) {
                oauthResult = userInfo;
                resultLock.notifyAll();
            }
        } else {
            System.out.println("❌ Failed to parse user info");
            throw new IOException("Could not parse user information from " + provider);
        }
    }

    /**
     * Parse user information from provider's JSON response
     * Each provider has a different response format
     */
    private static SocialUserInfo parseUserInfo(JsonObject json, String provider, String accessToken) throws IOException, InterruptedException {
        System.out.println("📊 Parsing user info from " + provider + " response");

        String email = null;
        String name = null;

        if ("google".equals(provider)) {
            // Google response format
            email = json.has("email") && !json.get("email").isJsonNull() ? json.get("email").getAsString() : null;
            name = json.has("name") && !json.get("name").isJsonNull() ? json.get("name").getAsString() : null;

        } else if ("github".equals(provider)) {
            // GitHub response format
            email = json.has("email") && !json.get("email").isJsonNull() ? json.get("email").getAsString() : null;
            name = json.has("name") && !json.get("name").isJsonNull() ? json.get("name").getAsString() : null;
            
            // GitHub's email might be null in the main response
            // If so, fetch from the /user/emails endpoint
            if (email == null) {
                email = fetchGitHubPrimaryEmail(accessToken);
            }

        } else if ("discord".equals(provider)) {
            // Discord response format
            email = json.has("email") && !json.get("email").isJsonNull() ? json.get("email").getAsString() : null;
            String username = json.has("username") && !json.get("username").isJsonNull() ? json.get("username").getAsString() : null;
            String globalName = json.has("global_name") && !json.get("global_name").isJsonNull() 
                    ? json.get("global_name").getAsString() : username;
            name = globalName != null ? globalName : username;
        }

        if (email == null || email.isEmpty()) {
            System.out.println("❌ Email not found in " + provider + " response");
            return null;
        }

        if (name == null || name.isEmpty()) {
            System.out.println("⚠️  Name not found in " + provider + " response, using email prefix");
            name = email.split("@")[0]; // Use email prefix as name fallback
        }

        return new SocialUserInfo(email, name, provider);
    }

    /**
     * Fetch GitHub user's primary email if not included in main response
     * This requires making an additional API call
     */
    private static String fetchGitHubPrimaryEmail(String accessToken) throws IOException, InterruptedException {
        System.out.println("📧 Fetching primary email from GitHub emails endpoint");
        
        try {
            HttpRequest emailRequest = HttpRequest.newBuilder()
                    .uri(URI.create(OAuthConfig.GITHUB_USER_EMAILS_URL))
                    .GET()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/json")
                    .build();
            
            HttpResponse<String> emailResponse = HTTP_CLIENT.send(emailRequest, HttpResponse.BodyHandlers.ofString());
            
            if (emailResponse.statusCode() == 200) {
                // Parse emails array and find primary email
                com.google.gson.JsonArray emails = JsonParser.parseString(emailResponse.body()).getAsJsonArray();
                for (var emailObj : emails) {
                    if (emailObj.getAsJsonObject().has("primary") && emailObj.getAsJsonObject().get("primary").getAsBoolean()) {
                        String primaryEmail = emailObj.getAsJsonObject().get("email").getAsString();
                        System.out.println("✅ Found primary GitHub email: " + primaryEmail);
                        return primaryEmail;
                    }
                }
                // If no primary found, use first verified email
                if (emails.size() > 0) {
                    String firstEmail = emails.get(0).getAsJsonObject().get("email").getAsString();
                    System.out.println("✅ Using first GitHub email: " + firstEmail);
                    return firstEmail;
                }
            } else {
                System.out.println("❌ Failed to fetch GitHub emails: " + emailResponse.statusCode());
            }
        } catch (Exception e) {
            System.out.println("❌ Error fetching GitHub emails: " + e.getMessage());
        }
        return null;
    }

    /**
     * Build the OAuth authorization URL
     */
    private static String buildAuthorizationUrl(String baseUrl, String clientId, String redirectUri, 
                                                String scope, String state) {
        return baseUrl +
                "?client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&response_type=code" +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);
    }

    /**
     * Open browser to OAuth authorization URL
     */
    private static void openBrowser(String url) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI.create(url));
        } else {
            System.out.println("⚠️  Desktop not supported - please manually visit: " + url);
        }
    }

    /**
     * Generate random state for CSRF protection
     */
    private static String generateRandomState() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * Wait for OAuth callback with timeout
     */
    private static boolean waitForCallback(int timeoutSeconds) throws InterruptedException {
        synchronized (resultLock) {
            long deadline = System.currentTimeMillis() + (timeoutSeconds * 1000L);
            while (oauthResult == null && oauthError == null) {
                long remaining = deadline - System.currentTimeMillis();
                if (remaining <= 0) {
                    return false; // Timeout occurred
                }
                resultLock.wait(remaining);
            }
            return true; // Result or error received
        }
    }

    /**
     * Send success response HTML to browser
     */
    private static void sendSuccessResponse(HttpExchange exchange) throws IOException {
        String response = OAuthConfig.CALLBACK_SUCCESS_HTML;
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    /**
     * Send error response HTML to browser
     */
    private static void sendErrorResponse(HttpExchange exchange, String errorMessage) throws IOException {
        String response = String.format(OAuthConfig.CALLBACK_ERROR_HTML, errorMessage);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(400, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    /**
     * Stop the local callback server
     */
    private static void stopCallbackServer() {
        if (callbackServer != null) {
            System.out.println("🛑 Stopping callback server");
            try {
                callbackServer.stop(5); // Give 5 seconds to shutdown gracefully
            } catch (Exception e) {
                System.err.println("⚠️  Error stopping server: " + e.getMessage());
            }
            callbackServer = null;
        }
    }
}
