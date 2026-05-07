package utils;

/**
 * Configuration for Mistral AI API
 * 
 * Get your free API key at: https://console.mistral.ai
 * Then replace the placeholder below with your actual key.
 */
public class MistralConfig {
    
    // ⚠️ IMPORTANT: Replace this with your actual Mistral API key
    // Get it free at: https://console.mistral.ai
    public static final String API_KEY = "CExDXGLCnvDRpUL0ycLK1SF9Jlv49SUK";
    
    // Mistral API endpoints
    public static final String API_URL = "https://api.mistral.ai/v1/chat/completions";
    public static final String MODEL = "mistral-small-latest";
    
    // Request parameters
    public static final int MAX_TOKENS = 1024;
    public static final double TEMPERATURE = 0.7;
    
    /**
     * Check if API key is configured
     */
    public static boolean isConfigured() {
        return API_KEY != null && !API_KEY.equals("YOUR_MISTRAL_API_KEY") && !API_KEY.isEmpty();
    }
}
