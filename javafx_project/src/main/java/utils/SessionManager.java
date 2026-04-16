package utils;

/**
 * Utility class for managing the current user session
 */
public class SessionManager {
    
    private static class User {
        int id;
        String nomComplet;
        String email;
        int isAdmin;
        int isBanned;
        
        User(int id, String nomComplet, String email, int isAdmin, int isBanned) {
            this.id = id;
            this.nomComplet = nomComplet;
            this.email = email;
            this.isAdmin = isAdmin;
            this.isBanned = isBanned;
        }
    }
    
    private static User currentUser = null;
    
    /**
     * Set the current logged-in user
     */
    public static void setCurrentUser(int id, String nomComplet, String email, int isAdmin, int isBanned) {
        currentUser = new User(id, nomComplet, email, isAdmin, isBanned);
    }
    
    /**
     * Clear the current session (logout)
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Get the current user ID
     */
    public static Integer getCurrentUserId() {
        return currentUser != null ? currentUser.id : null;
    }
    
    /**
     * Get the current user's full name
     */
    public static String getCurrentUserName() {
        return currentUser != null ? currentUser.nomComplet : null;
    }
    
    /**
     * Get the current user's email
     */
    public static String getCurrentUserEmail() {
        return currentUser != null ? currentUser.email : null;
    }
    
    /**
     * Check if current user is admin
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin == 1;
    }
    
    /**
     * Check if current user is banned
     */
    public static boolean isBanned() {
        return currentUser != null && currentUser.isBanned == 1;
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
