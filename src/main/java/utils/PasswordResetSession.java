package utils;

/**
 * In-memory session storage for password reset requests
 * 
 * Maintains the current pending password reset state:
 * - Email address of the user requesting reset
 * - 6-digit recovery code
 * - Expiry timestamp (10 minutes from generation)
 */
public class PasswordResetSession {
    
    private static String pendingEmail = null;
    private static String pendingCode = null;
    private static long codeExpiryTime = 0;
    
    // Code validity duration: 10 minutes
    private static final long CODE_VALIDITY_MILLIS = 10 * 60 * 1000; // 10 minutes
    
    /**
     * Store a new password reset code with the user's email
     * Sets expiry time to 10 minutes from now
     * 
     * @param email The user's email address
     * @param code The 6-digit recovery code
     */
    public static void storeCode(String email, String code) {
        pendingEmail = email;
        pendingCode = code;
        codeExpiryTime = System.currentTimeMillis() + CODE_VALIDITY_MILLIS;
        System.out.println("✅ Stored reset code for: " + email + " (expires in 10 minutes)");
    }
    
    /**
     * Verify if a code is valid for the given email
     * Checks both:
     * 1. Email matches the stored email
     * 2. Code matches the stored code
     * 3. Code has not expired
     * 
     * @param email The user's email address to verify against
     * @param code The code entered by the user
     * @return true if code is valid and not expired, false otherwise
     */
    public static boolean isCodeValid(String email, String code) {
        // Check if we have an active reset session
        if (pendingEmail == null || pendingCode == null) {
            System.out.println("❌ No active password reset session");
            return false;
        }
        
        // Check if code has expired
        if (System.currentTimeMillis() > codeExpiryTime) {
            System.out.println("❌ Password reset code expired");
            return false;
        }
        
        // Check if email matches
        if (!email.equalsIgnoreCase(pendingEmail)) {
            System.out.println("❌ Email mismatch: expected " + pendingEmail + ", got " + email);
            return false;
        }
        
        // Check if code matches
        if (!code.equals(pendingCode)) {
            System.out.println("❌ Code mismatch");
            return false;
        }
        
        System.out.println("✅ Code verified successfully");
        return true;
    }
    
    /**
     * Get the email address of the current pending password reset
     * 
     * @return The pending email, or null if no active reset session
     */
    public static String getPendingEmail() {
        return pendingEmail;
    }
    
    /**
     * Clear all password reset session data
     * Call this after successful password reset
     */
    public static void clear() {
        pendingEmail = null;
        pendingCode = null;
        codeExpiryTime = 0;
        System.out.println("✅ Password reset session cleared");
    }
    
    /**
     * Get remaining time for the current code in seconds
     * Used for displaying countdown timer (optional)
     * 
     * @return Seconds remaining, or 0 if expired
     */
    public static int getRemainingSeconds() {
        if (codeExpiryTime == 0) {
            return 0;
        }
        long remainingMs = codeExpiryTime - System.currentTimeMillis();
        return Math.max(0, (int) (remainingMs / 1000));
    }
}
