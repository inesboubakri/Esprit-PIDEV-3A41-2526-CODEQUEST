package utils;

/**
 * OAuth2 Configuration for Social Login Providers
 * 
 * Store your OAuth2 credentials here for Google, GitHub, and Discord.
 * Replace the YOUR_* placeholders with your actual client IDs and secrets.
 */
public class OAuthConfig {

    // ═══════════════════════════════════════════════════════════════════
    // GOOGLE OAUTH2 CONFIGURATION
    // ═══════════════════════════════════════════════════════════════════
    public static final String GOOGLE_CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
    public static final String GOOGLE_CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");
    public static final String GOOGLE_REDIRECT_URI = "http://localhost:8081/callback/google";
    public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
    public static final String GOOGLE_SCOPE = "email profile";

    // ═══════════════════════════════════════════════════════════════════
    // GITHUB OAUTH2 CONFIGURATION
    // ═══════════════════════════════════════════════════════════════════
    public static final String GITHUB_CLIENT_ID = System.getenv("GITHUB_CLIENT_ID");
    public static final String GITHUB_CLIENT_SECRET = System.getenv("GITHUB_CLIENT_SECRET");
    public static final String GITHUB_REDIRECT_URI = "http://localhost:8081/callback/github";
    public static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    public static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";
    public static final String GITHUB_USERINFO_URL = "https://api.github.com/user";
    public static final String GITHUB_USER_EMAILS_URL = "https://api.github.com/user/emails";
    public static final String GITHUB_SCOPE = "user:email";

    // ═══════════════════════════════════════════════════════════════════
    // DISCORD OAUTH2 CONFIGURATION
    // ═══════════════════════════════════════════════════════════════════
    public static final String DISCORD_CLIENT_ID = System.getenv("DISCORD_CLIENT_ID");
    public static final String DISCORD_CLIENT_SECRET = System.getenv("DISCORD_CLIENT_SECRET");
    public static final String DISCORD_REDIRECT_URI = "http://localhost:8081/callback/discord";
    public static final String DISCORD_AUTH_URL = "https://discord.com/api/oauth2/authorize";
    public static final String DISCORD_TOKEN_URL = "https://discord.com/api/oauth2/token";
    public static final String DISCORD_USERINFO_URL = "https://discord.com/api/users/@me";
    public static final String DISCORD_SCOPE = "identify email";

    // ═══════════════════════════════════════════════════════════════════
    // LOCAL CALLBACK SERVER CONFIGURATION
    // ═══════════════════════════════════════════════════════════════════
    public static final int CALLBACK_SERVER_PORT = 8081;
    public static final int OAUTH_TIMEOUT_SECONDS = 120; // 2-minute timeout
    public static final String CALLBACK_SUCCESS_HTML = 
        "<html>" +
        "<head><title>Authorization Successful</title></head>" +
        "<body style='font-family: Arial, sans-serif; text-align: center; padding: 50px;'>" +
        "<h1 style='color: #FF6B4A;'>✅ Authorization Successful!</h1>" +
        "<p>You can now close this window and return to CodeQuest.</p>" +
        "<script>setTimeout(() => window.close(), 2000);</script>" +
        "</body>" +
        "</html>";
    
    public static final String CALLBACK_ERROR_HTML = 
        "<html>" +
        "<head><title>Authorization Error</title></head>" +
        "<body style='font-family: Arial, sans-serif; text-align: center; padding: 50px;'>" +
        "<h1 style='color: #d32f2f;'>❌ Authorization Failed</h1>" +
        "<p>%s</p>" +
        "<p>You can close this window and try again.</p>" +
        "</body>" +
        "</html>";
}
