package utils;

import com.google.gson.*;
import dao.UserDAO;
import models.User;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Mistral AI Agent Service for CodeQuest
 * 
 * This service provides an AI assistant that can:
 * 1. Answer questions about the user's profile and coding
 * 2. Update user profile fields in real-time using function calling
 * 3. Maintain conversation history for context
 * 4. Generate a personalised AI Coach to-do list (one-shot, no history)
 */
public class MistralAgentService {
    
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    
    /**
     * Send a message to the Mistral AI agent and get a response.
     * Backward-compatible 3-param overload (no recommendation context).
     */
    public static CompletableFuture<String> sendMessage(
            String userMessage,
            User currentUser,
            List<Map<String, String>> conversationHistory) {
        return sendMessage(userMessage, currentUser, conversationHistory,
                Collections.emptyList(), Collections.emptyList(),
                Collections.emptyList(), Collections.emptyList());
    }

    /**
     * Send a message to the Mistral AI agent and get a response.
     * Enhanced overload with skill scores and recommendation context.
     *
     * @param userMessage         The message from the user
     * @param currentUser         The current logged-in user
     * @param conversationHistory Full conversation history
     * @param skillScores         User skill scores (from getSkillScoresForUser)
     * @param recommendedCourses  Recommended courses (from getRecommendedCourses)
     * @param recommendedProblems Recommended problems (from getRecommendedProblems)
     * @param recommendedProjects Recommended projects (from getRecommendedProjects)
     * @return CompletableFuture with the AI response
     */
    public static CompletableFuture<String> sendMessage(
            String userMessage,
            User currentUser,
            List<Map<String, String>> conversationHistory,
            List<Map<String, Object>> skillScores,
            List<Map<String, Object>> recommendedCourses,
            List<Map<String, Object>> recommendedProblems,
            List<Map<String, Object>> recommendedProjects) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check if API is configured
                if (!MistralConfig.isConfigured()) {
                    return "❌ Mistral API key not configured. Please add your API key to MistralConfig.java at: https://console.mistral.ai";
                }
                
                // Build the system prompt with current user profile data + context
                String systemPrompt = buildSystemPrompt(currentUser, skillScores,
                        recommendedCourses, recommendedProblems, recommendedProjects);
                
                // Build the request body
                String requestBody = buildRequestBody(systemPrompt, userMessage, conversationHistory);
                
                // Create and send the HTTP request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(MistralConfig.API_URL))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + MistralConfig.API_KEY)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                        .build();
                
                System.out.println("🤖 Sending request to Mistral API...");
                
                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                
                // Handle HTTP errors
                if (response.statusCode() != 200) {
                    String errorMsg = "HTTP " + response.statusCode();
                    try {
                        JsonObject errorObj = JsonParser.parseString(response.body()).getAsJsonObject();
                        if (errorObj.has("error") && errorObj.get("error").isJsonObject()) {
                            errorMsg = errorObj.getAsJsonObject("error").get("message").getAsString();
                        }
                    } catch (Exception e) {
                        errorMsg = response.body();
                    }
                    return "❌ API Error: " + errorMsg;
                }
                
                // Parse the response
                JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
                
                // Check if there are tool calls to execute
                JsonArray choices = responseBody.getAsJsonArray("choices");
                if (choices != null && choices.size() > 0) {
                    JsonObject choice = choices.get(0).getAsJsonObject();
                    JsonObject message = choice.getAsJsonObject("message");
                    
                    // Check for tool calls
                    if (message.has("tool_calls") && !message.get("tool_calls").isJsonNull()) {
                        JsonArray toolCalls = message.getAsJsonArray("tool_calls");
                        String result = executeToolCalls(toolCalls, currentUser);
                        return result;
                    }
                    
                    // Otherwise return the text content
                    if (message.has("content") && !message.get("content").isJsonNull()) {
                        return message.get("content").getAsString();
                    }
                }
                
                return "❌ Unexpected response format from API";
                
            } catch (Exception e) {
                System.err.println("❌ Error in MistralAgentService: " + e.getMessage());
                e.printStackTrace();
                return "❌ Error: " + e.getMessage();
            }
        });
    }

    /**
     * Generate a personalised daily to-do list using Mistral AI.
     * One-shot call — no conversation history, no function calling tools.
     *
     * @param user                The current user
     * @param skillScores         User skill scores
     * @param recommendedCourses  Recommended courses from DB
     * @param recommendedProblems Recommended problems from DB
     * @param recommendedProjects Recommended projects from DB
     * @return CompletableFuture<String> with the formatted coach response
     */
    public static CompletableFuture<String> generateTodoList(
            User user,
            List<Map<String, Object>> skillScores,
            List<Map<String, Object>> recommendedCourses,
            List<Map<String, Object>> recommendedProblems,
            List<Map<String, Object>> recommendedProjects) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                if (!MistralConfig.isConfigured()) {
                    return "❌ Mistral API key not configured.";
                }

                // Build the coach prompt
                StringBuilder prompt = new StringBuilder();
                prompt.append("You are an AI coach for CodeQuest, a coding learning platform.\n");
                prompt.append("The user's name is ").append(user.getNomComplet() != null ? user.getNomComplet() : "Student");
                prompt.append(", level: ").append(user.getNiveauInfo() != null ? user.getNiveauInfo() : "Beginner");
                prompt.append(", XP: ").append(user.getXp()).append(".\n\n");

                // Skill scores
                if (!skillScores.isEmpty()) {
                    prompt.append("Their current skill scores:\n");
                    for (Map<String, Object> s : skillScores) {
                        prompt.append("  - ").append(s.get("skillName")).append(": ").append(s.get("score")).append(" pts\n");
                    }
                } else {
                    prompt.append("They haven't been scored on any skills yet.\n");
                }

                // Recommended courses
                prompt.append("\nRecommended courses for them:\n");
                if (!recommendedCourses.isEmpty()) {
                    for (Map<String, Object> c : recommendedCourses) {
                        prompt.append("  - ").append(c.get("titre"))
                              .append(" (").append(c.get("niveau")).append(", ").append(c.get("ptsTotal")).append(" XP)")
                              .append(" — skill: ").append(c.get("skillName")).append("\n");
                    }
                } else {
                    prompt.append("  (none available)\n");
                }

                // Recommended problems
                prompt.append("\nProblems they haven't solved yet:\n");
                if (!recommendedProblems.isEmpty()) {
                    for (Map<String, Object> p : recommendedProblems) {
                        prompt.append("  - ").append(p.get("titre"))
                              .append(" (").append(p.get("difficulte")).append(", ").append(p.get("pts")).append(" pts)")
                              .append(" — skill: ").append(p.get("skillName")).append("\n");
                    }
                } else {
                    prompt.append("  (none available)\n");
                }

                // Recommended projects
                prompt.append("\nAvailable projects:\n");
                if (!recommendedProjects.isEmpty()) {
                    for (Map<String, Object> pr : recommendedProjects) {
                        prompt.append("  - ").append(pr.get("titre"))
                              .append(" (").append(pr.get("niveau")).append(", ").append(pr.get("pts")).append(" pts)\n");
                    }
                } else {
                    prompt.append("  (none available)\n");
                }

                prompt.append("\nBased on this data, generate:\n");
                prompt.append("1. A short motivational message (2 sentences, energetic and personal)\n");
                prompt.append("2. A TO-DO list with exactly 5 actionable tasks they should do TODAY to improve their skills. ");
                prompt.append("Each task should reference a specific course, problem, or project from the lists above.\n");
                prompt.append("3. End with one encouraging sentence.\n\n");
                prompt.append("Format the response clearly with emojis. Be concise and motivating.");

                // Build one-shot request body (no tools, no history)
                JsonObject requestObj = new JsonObject();
                requestObj.addProperty("model", MistralConfig.MODEL);
                requestObj.addProperty("temperature", MistralConfig.TEMPERATURE);
                requestObj.addProperty("max_tokens", 800);

                JsonArray messages = new JsonArray();
                JsonObject userMsg = new JsonObject();
                userMsg.addProperty("role", "user");
                userMsg.addProperty("content", prompt.toString());
                messages.add(userMsg);
                requestObj.add("messages", messages);

                String requestBody = GSON.toJson(requestObj);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(MistralConfig.API_URL))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + MistralConfig.API_KEY)
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                        .build();

                System.out.println("🤖 Generating AI Coach to-do list...");

                HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    return "❌ API Error: HTTP " + response.statusCode();
                }

                JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
                JsonArray choices = responseBody.getAsJsonArray("choices");
                if (choices != null && choices.size() > 0) {
                    JsonObject msg = choices.get(0).getAsJsonObject().getAsJsonObject("message");
                    if (msg.has("content") && !msg.get("content").isJsonNull()) {
                        return msg.get("content").getAsString();
                    }
                }

                return "❌ Unexpected response format from AI.";

            } catch (Exception e) {
                System.err.println("❌ Error generating to-do list: " + e.getMessage());
                e.printStackTrace();
                return "❌ Error: " + e.getMessage();
            }
        });
    }
    
    /**
     * Build the system prompt with current user profile data + enriched context.
     * The existing tool-calling instructions are preserved and recommendations are appended.
     */
    private static String buildSystemPrompt(
            User currentUser,
            List<Map<String, Object>> skillScores,
            List<Map<String, Object>> recommendedCourses,
            List<Map<String, Object>> recommendedProblems,
            List<Map<String, Object>> recommendedProjects) {

        // ── Existing core system prompt (unchanged logic) ──
        StringBuilder sb = new StringBuilder();
        sb.append("You are a smart and friendly AI profile assistant for CodeQuest, a coding learning platform.\n");
        sb.append("You are helping the user manage their profile and answer questions about coding.\n\n");
        sb.append("Current user profile:\n");
        sb.append("- Name: ").append(currentUser.getNomComplet() != null ? currentUser.getNomComplet() : "Not set").append("\n");
        sb.append("- Email: ").append(currentUser.getEmail() != null ? currentUser.getEmail() : "Not set").append("\n");
        sb.append("- Role: ").append(currentUser.getRole() != null ? currentUser.getRole() : "Student").append("\n");
        sb.append("- Age: ").append(currentUser.getAge()).append("\n");
        sb.append("- Level/NiveauInfo: ").append(currentUser.getNiveauInfo() != null ? currentUser.getNiveauInfo() : "Beginner").append("\n");
        sb.append("- Bio: ").append(currentUser.getBio() != null && !currentUser.getBio().isEmpty() ? currentUser.getBio() : "Not set").append("\n");
        sb.append("- Education: ").append(currentUser.getEducation() != null ? currentUser.getEducation() : "Not set").append("\n");
        sb.append("- Experience: ").append(currentUser.getExperience() != null ? currentUser.getExperience() : "Not set").append("\n");
        sb.append("- Formation: ").append(currentUser.getFormation() != null ? currentUser.getFormation() : "Not set").append("\n");
        sb.append("- XP: ").append(currentUser.getXp()).append("\n");
        sb.append("- Subscription: ").append(currentUser.getSubscription() != null ? currentUser.getSubscription() : "free").append("\n\n");

        // ── Skill scores context ──
        if (skillScores != null && !skillScores.isEmpty()) {
            sb.append("User's skill scores:\n");
            for (Map<String, Object> s : skillScores) {
                sb.append("  - ").append(s.get("skillName")).append(": ").append(s.get("score")).append(" XP\n");
            }
            sb.append("\n");
        }

        // ── Recommendations context ──
        boolean hasRecs = (recommendedCourses != null && !recommendedCourses.isEmpty())
                || (recommendedProblems != null && !recommendedProblems.isEmpty())
                || (recommendedProjects != null && !recommendedProjects.isEmpty());

        if (hasRecs) {
            sb.append("You also have access to these recommendations for this user:\n");
            if (recommendedCourses != null && !recommendedCourses.isEmpty()) {
                sb.append("- Recommended courses: ");
                List<String> titles = new ArrayList<>();
                for (Map<String, Object> c : recommendedCourses) titles.add((String) c.get("titre"));
                sb.append(String.join(", ", titles)).append("\n");
            }
            if (recommendedProblems != null && !recommendedProblems.isEmpty()) {
                sb.append("- Unsolved problems: ");
                List<String> titles = new ArrayList<>();
                for (Map<String, Object> p : recommendedProblems) titles.add((String) p.get("titre"));
                sb.append(String.join(", ", titles)).append("\n");
            }
            if (recommendedProjects != null && !recommendedProjects.isEmpty()) {
                sb.append("- Available projects: ");
                List<String> titles = new ArrayList<>();
                for (Map<String, Object> pr : recommendedProjects) titles.add((String) pr.get("titre"));
                sb.append(String.join(", ", titles)).append("\n");
            }
            sb.append("You can reference these when giving advice or motivation.\n");
            sb.append("If the user asks what they should do or what to learn next, use this data to give specific, personalised suggestions.\n\n");
        }

        // ── Existing tool/update instructions (unchanged) ──
        sb.append("You can answer any question, give coding advice, discuss the user's profile, or update their profile fields.\n");
        sb.append("When the user asks to change something, use the update_profile_field tool.\n");
        sb.append("Updatable fields: nomComplet, bio, education, experience, formation, niveauInfo, role (only: Student, Developer, Mentor)\n");
        sb.append("Do NOT update: email, password, isAdmin, xp, id.\n");
        sb.append("Always confirm after updating. Be warm, encouraging, and concise.");

        return sb.toString();
    }

    /**
     * Build the request body for Mistral API (with tool calling support).
     */
    private static String buildRequestBody(
            String systemPrompt,
            String userMessage,
            List<Map<String, String>> conversationHistory) {
        
        JsonObject requestObj = new JsonObject();
        requestObj.addProperty("model", MistralConfig.MODEL);
        requestObj.addProperty("temperature", MistralConfig.TEMPERATURE);
        requestObj.addProperty("max_tokens", MistralConfig.MAX_TOKENS);
        
        // Build messages array: system + history + new message
        JsonArray messagesArray = new JsonArray();
        
        // Add system message
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", systemPrompt);
        messagesArray.add(systemMsg);
        
        // Add conversation history
        for (Map<String, String> msg : conversationHistory) {
            JsonObject historyMsg = new JsonObject();
            historyMsg.addProperty("role", msg.get("role"));
            historyMsg.addProperty("content", msg.get("content"));
            messagesArray.add(historyMsg);
        }
        
        // Add current user message
        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messagesArray.add(userMsg);
        
        requestObj.add("messages", messagesArray);
        
        // Add tools definition for function calling (unchanged)
        JsonArray toolsArray = new JsonArray();
        toolsArray.add(createToolDefinition());
        requestObj.add("tools", toolsArray);
        requestObj.addProperty("tool_choice", "auto");
        
        return GSON.toJson(requestObj);
    }
    
    /**
     * Create the tool definition for update_profile_field
     */
    private static JsonObject createToolDefinition() {
        JsonObject tool = new JsonObject();
        tool.addProperty("type", "function");
        
        JsonObject function = new JsonObject();
        function.addProperty("name", "update_profile_field");
        function.addProperty("description", "Updates a specific field in the user's profile in the database");
        
        JsonObject parameters = new JsonObject();
        parameters.addProperty("type", "object");
        
        JsonObject properties = new JsonObject();
        
        JsonObject fieldNameProp = new JsonObject();
        fieldNameProp.addProperty("type", "string");
        fieldNameProp.addProperty("description", "The field to update: nomComplet, bio, education, experience, formation, niveauInfo, or role");
        properties.add("field_name", fieldNameProp);
        
        JsonObject newValueProp = new JsonObject();
        newValueProp.addProperty("type", "string");
        newValueProp.addProperty("description", "The new value to set for the field");
        properties.add("new_value", newValueProp);
        
        parameters.add("properties", properties);
        
        JsonArray required = new JsonArray();
        required.add("field_name");
        required.add("new_value");
        parameters.add("required", required);
        
        function.add("parameters", parameters);
        tool.add("function", function);
        
        return tool;
    }
    
    /**
     * Execute tool calls from the API response
     */
    private static String executeToolCalls(JsonArray toolCalls, User currentUser) {
        StringBuilder results = new StringBuilder();
        
        for (int i = 0; i < toolCalls.size(); i++) {
            JsonObject toolCall = toolCalls.get(i).getAsJsonObject();
            String toolName = toolCall.get("function").getAsJsonObject().get("name").getAsString();
            JsonObject arguments = JsonParser.parseString(
                    toolCall.get("function").getAsJsonObject().get("arguments").getAsString()
            ).getAsJsonObject();
            
            if ("update_profile_field".equals(toolName)) {
                String fieldName = arguments.get("field_name").getAsString();
                String newValue = arguments.get("new_value").getAsString();
                String result = updateProfileField(fieldName, newValue, currentUser);
                results.append(result).append("\n");
            }
        }
        
        return results.toString().trim();
    }
    
    /**
     * Update a profile field and persist to database
     */
    private static String updateProfileField(String fieldName, String newValue, User currentUser) {
        try {
            // Validate field name to prevent unauthorized updates
            if (!isUpdatableField(fieldName)) {
                return "❌ Cannot update field: " + fieldName + " (not updatable)";
            }
            
            // Update the user object
            switch (fieldName.toLowerCase()) {
                case "nomcomplet":
                    currentUser.setNomComplet(newValue);
                    break;
                case "bio":
                    currentUser.setBio(newValue);
                    break;
                case "education":
                    currentUser.setEducation(newValue);
                    break;
                case "experience":
                    currentUser.setExperience(newValue);
                    break;
                case "formation":
                    currentUser.setFormation(newValue);
                    break;
                case "niveauinfo":
                    currentUser.setNiveauInfo(newValue);
                    break;
                case "role":
                    // Validate role
                    if (!isValidRole(newValue)) {
                        return "❌ Invalid role. Must be: Student, Developer, or Mentor";
                    }
                    currentUser.setRole(newValue);
                    break;
                default:
                    return "❌ Unknown field: " + fieldName;
            }
            
            // Persist to database
            new UserDAO().updateProfile(currentUser);
            
            // Update session with modified user
            Session.setCurrentUser(currentUser);
            
            return "✅ Your " + fieldName + " has been updated successfully!";
            
        } catch (Exception e) {
            System.err.println("❌ Error updating profile field: " + e.getMessage());
            e.printStackTrace();
            return "❌ Error updating " + fieldName + ": " + e.getMessage();
        }
    }
    
    /**
     * Check if a field is allowed to be updated
     */
    private static boolean isUpdatableField(String fieldName) {
        String field = fieldName.toLowerCase();
        return field.equals("nomcomplet") ||
               field.equals("bio") ||
               field.equals("education") ||
               field.equals("experience") ||
               field.equals("formation") ||
               field.equals("niveauinfo") ||
               field.equals("role");
    }
    
    /**
     * Validate role values
     */
    private static boolean isValidRole(String role) {
        return role.equalsIgnoreCase("Student") ||
               role.equalsIgnoreCase("Developer") ||
               role.equalsIgnoreCase("Mentor");
    }
}
