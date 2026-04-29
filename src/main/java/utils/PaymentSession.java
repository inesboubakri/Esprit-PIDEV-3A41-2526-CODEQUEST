package utils;

/**
 * PaymentSession utility class for managing in-memory payment flow data.
 * Handles storage and validation of payment verification codes with time-based expiry.
 * 
 * This class encapsulates payment state during the checkout flow:
 * - Stores the email of the user completing payment
 * - Stores the verification code sent to their email
 * - Manages code expiry (10 minutes)
 * - Tracks the selected payment package
 * 
 * Data is cleared after successful payment or can be manually cleared.
 */
public class PaymentSession {

    /** User must pick Free vs Premium on the payment screen (e.g. right after sign-up). */
    public static final String PACKAGE_PENDING_CHOICE = "pending_plan";
    
    // ── In-Memory Payment State ──────────────────────────────
    private static String pendingEmail = null;      // Email of user completing payment
    private static String pendingCode = null;       // 6-digit verification code
    private static long codeExpiryTime = 0;         // System.currentTimeMillis() + 10 minutes
    private static String selectedPackage = null;   // "premium" or "free"
    
    private static final long CODE_EXPIRY_DURATION = 10 * 60 * 1000; // 10 minutes in milliseconds

    /**
     * Store a payment verification code with 10-minute expiry.
     * Overwrites any existing code for this email.
     * 
     * @param email The user's email address
     * @param code The 6-digit verification code
     */
    public static void storePaymentCode(String email, String code) {
        System.out.println("🔐 Storing payment code for email: " + email);
        pendingEmail = email;
        pendingCode = code;
        codeExpiryTime = System.currentTimeMillis() + CODE_EXPIRY_DURATION;
        System.out.println("📅 Code expires at: " + codeExpiryTime);
    }

    /**
     * Verify that the provided code is valid and not expired.
     * Checks email match, code match, and timestamp validity.
     * 
     * @param email The email to verify against
     * @param code The code to verify
     * @return true if code is valid and not expired, false otherwise
     */
    public static boolean isCodeValid(String email, String code) {
        if (pendingEmail == null || pendingCode == null) {
            System.out.println("❌ No payment code stored");
            return false;
        }
        
        // Check email match
        if (!email.equals(pendingEmail)) {
            System.out.println("❌ Email mismatch: expected " + pendingEmail + ", got " + email);
            return false;
        }
        
        // Check code match
        if (!code.equals(pendingCode)) {
            System.out.println("❌ Code mismatch");
            return false;
        }
        
        // Check expiry
        long currentTime = System.currentTimeMillis();
        if (currentTime > codeExpiryTime) {
            System.out.println("❌ Code expired");
            return false;
        }
        
        System.out.println("✅ Code is valid");
        return true;
    }

    /**
     * Get the pending email from the current payment session.
     * 
     * @return The email address, or null if no payment is in progress
     */
    public static String getPendingEmail() {
        return pendingEmail;
    }

    /**
     * Get the selected payment package.
     * 
     * @return The package name (e.g., "premium"), or null if not set
     */
    public static String getSelectedPackage() {
        return selectedPackage;
    }

    /**
     * Store email and selected package for the current payment session.
     * Call this before starting the payment flow.
     * 
     * @param email The user's email
     * @param packageName The selected package (e.g., "premium")
     */
    public static void store(String email, String packageName) {
        System.out.println("🎁 Storing payment session: " + email + " -> " + packageName);
        pendingEmail = email;
        selectedPackage = packageName;
    }

    /**
     * Clear all payment session data.
     * Call this after successful payment confirmation.
     */
    public static void clear() {
        System.out.println("🧹 Clearing payment session");
        pendingEmail = null;
        pendingCode = null;
        codeExpiryTime = 0;
        selectedPackage = null;
    }
}
