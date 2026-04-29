package utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * BioGeneratorService — AI-powered professional bio generation using Mistral AI
 * 
 * This service generates engaging professional bios for CodeQuest users
 * based on their profile information (role, level, experience, education, etc.)
 * 
 * Uses Mistral AI API with async/non-blocking HTTP calls via CompletableFuture
 */
public class BioGeneratorService {
    
    private static final Gson gson = new Gson();
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    
    // Temperature: 0.3 (professional) to 1.2 (creative)
    private static final double TEMPERATURE = 0.8;
    private static final int MAX_TOKENS = 150;
    
    /**
     * Generate a professional bio for a user using Mistral AI
     * Returns a CompletableFuture for non-blocking async execution
     * 
     * @param user The user object with profile info (name, role, level, education, etc.)
     * @return CompletableFuture<String> — the generated bio text or error message
     */
    public static CompletableFuture<String> generateBio(User user) {
        // Run the API call in a background thread pool
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check if API key is configured
                if (!MistralConfig.isConfigured()) {
                    System.err.println("❌ Mistral API key not configured");
                    return "Mistral API key not configured. Please set it in utils/MistralConfig.java";
                }
                
                // Build the prompt dynamically from user data
                String prompt = buildBioPrompt(user);
                System.out.println("🤖 Building bio prompt for user: " + user.getNomComplet());
                
                // Build request JSON
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", MistralConfig.MODEL);
                requestBody.addProperty("temperature", TEMPERATURE);
                requestBody.addProperty("max_tokens", MAX_TOKENS);
                
                // Messages array with user prompt
                JsonArray messages = new JsonArray();
                JsonObject message = new JsonObject();
                message.addProperty("role", "user");
                message.addProperty("content", prompt);
                messages.add(message);
                requestBody.add("messages", messages);
                
                // Create HTTP POST request
                String requestJson = gson.toJson(requestBody);
                System.out.println("📤 Sending request to Mistral AI...");
                
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MistralConfig.API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + MistralConfig.API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();
                
                // Send request and get response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                
                // Check for HTTP errors
                if (response.statusCode() != 200) {
                    System.err.println("❌ Mistral API error: " + response.statusCode());
                    System.err.println("Response: " + response.body());
                    return "Failed to generate bio. API returned status: " + response.statusCode();
                }
                
                // Parse response JSON
                String responseBody = response.body();
                System.out.println("✅ Received response from Mistral AI");
                
                JsonObject responseJson = gson.fromJson(responseBody, JsonObject.class);
                
                // Extract the generated bio from choices[0].message.content
                JsonArray choices = responseJson.getAsJsonArray("choices");
                if (choices == null || choices.size() == 0) {
                    System.err.println("❌ No choices in response");
                    return "Failed to generate bio. Invalid API response.";
                }
                
                JsonObject firstChoice = choices.get(0).getAsJsonObject();
                JsonObject messageObj = firstChoice.getAsJsonObject("message");
                String generatedBio = messageObj.get("content").getAsString().trim();
                
                System.out.println("✅ Generated bio: " + generatedBio);
                return generatedBio;
                
            } catch (Exception e) {
                System.err.println("❌ Error generating bio: " + e.getMessage());
                e.printStackTrace();
                return "Failed to generate bio. Please try again. Error: " + e.getMessage();
            }
        });
    }
    
    /**
     * Build a detailed prompt from user profile data
     * Dynamically constructs the prompt based on available user information
     * 
     * @param user The user object to extract info from
     * @return The formatted prompt string for Mistral AI
     */
    private static String buildBioPrompt(User user) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a professional bio writer for a coding learning platform called CodeQuest. ");
        prompt.append("Generate a short, engaging, first-person professional bio (2-3 sentences max, under 200 characters) ");
        prompt.append("for a user based on their profile information below.\n\n");
        
        prompt.append("Profile:\n");
        
        // Add user info if available
        if (user.getNomComplet() != null && !user.getNomComplet().isBlank()) {
            prompt.append("- Name: ").append(user.getNomComplet()).append("\n");
        }
        if (user.getRole() != null && !user.getRole().isBlank()) {
            prompt.append("- Role: ").append(user.getRole()).append("\n");
        }
        if (user.getAge() > 0) {
            prompt.append("- Age: ").append(user.getAge()).append("\n");
        }
        if (user.getNiveauInfo() != null && !user.getNiveauInfo().isBlank()) {
            prompt.append("- Level: ").append(user.getNiveauInfo()).append("\n");
        }
        if (user.getEducation() != null && !user.getEducation().isBlank()) {
            prompt.append("- Education: ").append(user.getEducation()).append("\n");
        }
        if (user.getExperience() != null && !user.getExperience().isBlank()) {
            prompt.append("- Experience: ").append(user.getExperience()).append("\n");
        }
        if (user.getFormation() != null && !user.getFormation().isBlank()) {
            prompt.append("- Certifications/Training: ").append(user.getFormation()).append("\n");
        }
        if (user.getXp() > 0) {
            prompt.append("- XP Points: ").append(user.getXp()).append("\n");
        }
        
        prompt.append("\nRules:\n");
        prompt.append("- Write in first person (\"I am...\", \"I have...\", \"I love...\")\n");
        prompt.append("- Be professional but friendly and motivating\n");
        prompt.append("- Highlight their level and experience naturally\n");
        prompt.append("- Do NOT include email or sensitive info\n");
        prompt.append("- Return ONLY the bio text, no markdown, no quotes, no explanation, no extra text\n");
        prompt.append("- If a field is empty or null, skip it naturally\n");
        
        return prompt.toString();
    }
}
