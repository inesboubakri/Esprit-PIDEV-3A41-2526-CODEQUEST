package models;

/**
 * Record containing information about a user authenticated via OAuth2
 * 
 * This record is used to transfer user data from OAuth providers to the application
 * after successful authentication and authorization.
 */
public record SocialUserInfo(String email, String name, String provider) {

    /**
     * Get a human-readable provider name
     * 
     * @return Capitalized provider name (e.g., "Google", "GitHub", "Discord")
     */
    public String getProviderDisplayName() {
        return switch (provider.toLowerCase()) {
            case "google" -> "Google";
            case "github" -> "GitHub";
            case "discord" -> "Discord";
            default -> provider;
        };
    }

    /**
     * Get provider emoji for UI display
     * 
     * @return Emoji associated with the provider
     */
    public String getProviderEmoji() {
        return switch (provider.toLowerCase()) {
            case "google" -> "🔵";
            case "github" -> "🐙";
            case "discord" -> "🎮";
            default -> "🔐";
        };
    }

    @Override
    public String toString() {
        return "SocialUserInfo{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", provider='" + provider + '\'' +
                '}';
    }
}
