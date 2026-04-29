package utils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Email Service for sending password reset codes via Gmail SMTP
 * 
 * ⚠️ IMPORTANT: This uses Gmail's SMTP server. You MUST use an App Password, NOT your regular Gmail password.
 * See instructions below on how to get one.
 */
public class EmailService {
    
    // ⚠️ REPLACE THESE WITH YOUR GMAIL CREDENTIALS (use App Password from Gmail)
    private static final String SENDER_EMAIL = "sindaaouay31@gmail.com";
    private static final String SENDER_APP_PASSWORD = "peab pvnf yqrx mobb";    
    
    // Gmail SMTP configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    
    /**
     * Send a password reset code to the user's email
     * This runs in a background thread to avoid blocking the UI
     * 
     * @param recipientEmail The user's email address
     * @param code The 6-digit reset code
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendPasswordResetCode(String recipientEmail, String code) {
        try {
            // Check if credentials are configured
            if (SENDER_EMAIL.equals("YOUR_GMAIL_ADDRESS") || SENDER_APP_PASSWORD.equals("YOUR_GMAIL_APP_PASSWORD")) {
                System.out.println("❌ Email credentials not configured in EmailService.java");
                return false;
            }
            
            // Configure SMTP properties for Gmail
            Properties properties = new Properties();
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            properties.put("mail.smtp.connectiontimeout", "5000");
            properties.put("mail.smtp.timeout", "5000");
            
            System.out.println("📧 Configuring Gmail SMTP...");
            
            // Create session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
                }
            });
            
            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "CodeQuest"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("CodeQuest - Password Reset Code");
            
            // Create HTML email body
            String htmlBody = createHtmlEmailBody(code);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            
            // Send the email
            System.out.println("📤 Sending email to: " + recipientEmail);
            Transport.send(message);
            
            System.out.println("✅ Email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Create the HTML email body with CodeQuest branding and styling
     */
    private static String createHtmlEmailBody(String code) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: 'Arial', sans-serif; line-height: 1.6; background-color: #f5f5f5; }\n" +
                "        .container { max-width: 500px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); padding: 30px; }\n" +
                "        .header { text-align: center; margin-bottom: 30px; border-bottom: 2px solid #FF6B4A; padding-bottom: 20px; }\n" +
                "        .logo { font-size: 32px; font-weight: bold; color: #333; }\n" +
                "        .logo .orange { color: #FF6B4A; }\n" +
                "        .content { text-align: center; }\n" +
                "        .code-box { background-color: #FFF3E0; border: 2px solid #FF6B4A; border-radius: 8px; padding: 25px; margin: 25px 0; }\n" +
                "        .code { font-size: 48px; font-weight: bold; color: #FF6B4A; letter-spacing: 8px; font-family: 'Courier New', monospace; }\n" +
                "        .code-title { font-size: 14px; color: #666; margin-bottom: 10px; }\n" +
                "        .warning { background-color: #FFF3CD; border-left: 4px solid #FF6B4A; padding: 12px 15px; margin: 20px 0; text-align: left; }\n" +
                "        .warning-text { font-size: 13px; color: #333; }\n" +
                "        .footer { text-align: center; margin-top: 25px; font-size: 12px; color: #999; }\n" +
                "        .btn-link { color: #FF6B4A; text-decoration: none; font-weight: bold; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"logo\">Code<span class=\"orange\">Quest</span></div>\n" +
                "            <p style=\"color: #666; margin: 10px 0 0 0;\">Password Recovery</p>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"content\">\n" +
                "            <h2 style=\"color: #333; margin-bottom: 10px;\">Password Reset Request</h2>\n" +
                "            <p style=\"color: #666; margin-bottom: 25px;\">We received a request to reset your CodeQuest password. Use the code below to continue:</p>\n" +
                "\n" +
                "            <div class=\"code-box\">\n" +
                "                <div class=\"code-title\">Your Recovery Code:</div>\n" +
                "                <div class=\"code\">" + code + "</div>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"warning\">\n" +
                "                <p class=\"warning-text\">⏰ <strong>This code expires in 10 minutes</strong>. If you didn't request a password reset, you can safely ignore this email.</p>\n" +
                "            </div>\n" +
                "\n" +
                "            <p style=\"color: #999; font-size: 12px; margin-top: 20px;\">\n" +
                "                If you didn't request this, please secure your account by changing your password immediately.\n" +
                "            </p>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2026 CodeQuest. All rights reserved.</p>\n" +
                "            <p>This is an automated email. Please do not reply.</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Send a payment verification code to the user's email.
     * Used during the checkout process for payment confirmation.
     * This runs in a background thread to avoid blocking the UI.
     * 
     * @param recipientEmail The user's email address
     * @param code The 6-digit verification code
     * @param packageName The package name (e.g., "Premium")
     * @return true if email sent successfully, false otherwise
     */
    public static boolean sendPaymentVerificationCode(String recipientEmail, String code, String packageName) {
        try {
            // Check if credentials are configured
            if (SENDER_EMAIL.equals("YOUR_GMAIL_ADDRESS") || SENDER_APP_PASSWORD.equals("YOUR_GMAIL_APP_PASSWORD")) {
                System.out.println("❌ Email credentials not configured in EmailService.java");
                return false;
            }
            
            // Configure SMTP properties for Gmail
            Properties properties = new Properties();
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.starttls.required", "true");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            properties.put("mail.smtp.connectiontimeout", "5000");
            properties.put("mail.smtp.timeout", "5000");
            
            System.out.println("📧 Configuring Gmail SMTP for payment verification...");
            
            // Create session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_APP_PASSWORD);
                }
            });
            
            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "CodeQuest"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("CodeQuest - Payment Verification Code");
            
            // Create HTML email body for payment verification
            String htmlBody = createPaymentVerificationEmailBody(code, packageName);
            message.setContent(htmlBody, "text/html; charset=utf-8");
            
            // Send the email
            System.out.println("📤 Sending payment verification email to: " + recipientEmail);
            Transport.send(message);
            
            System.out.println("✅ Payment verification email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Error sending payment verification email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create the HTML email body for payment verification with CodeQuest branding.
     */
    private static String createPaymentVerificationEmailBody(String code, String packageName) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: 'Arial', sans-serif; line-height: 1.6; background-color: #f5f5f5; }\n" +
                "        .container { max-width: 500px; margin: 20px auto; background-color: white; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); padding: 30px; }\n" +
                "        .header { text-align: center; margin-bottom: 30px; border-bottom: 3px solid #FF6B4A; padding-bottom: 20px; background: linear-gradient(135deg, #FF6B4A, #ff8c42); color: white; margin: -30px -30px 30px -30px; padding: 30px; border-radius: 8px 8px 0 0; }\n" +
                "        .logo { font-size: 32px; font-weight: bold; }\n" +
                "        .subtitle { font-size: 14px; opacity: 0.9; margin-top: 5px; }\n" +
                "        .content { text-align: center; }\n" +
                "        .code-box { background-color: #FFF3E0; border: 2px solid #FF6B4A; border-radius: 8px; padding: 25px; margin: 25px 0; }\n" +
                "        .code { font-size: 48px; font-weight: bold; color: #FF6B4A; letter-spacing: 8px; font-family: 'Courier New', monospace; }\n" +
                "        .code-title { font-size: 14px; color: #666; margin-bottom: 10px; }\n" +
                "        .package-badge { display: inline-block; background-color: #FF6B4A; color: white; padding: 8px 16px; border-radius: 20px; font-weight: bold; margin: 15px 0; font-size: 14px; }\n" +
                "        .warning { background-color: #FFF3CD; border-left: 4px solid #FF6B4A; padding: 12px 15px; margin: 20px 0; text-align: left; border-radius: 4px; }\n" +
                "        .warning-text { font-size: 13px; color: #333; margin: 0; }\n" +
                "        .footer { text-align: center; margin-top: 25px; font-size: 12px; color: #999; }\n" +
                "        .lock-icon { font-size: 40px; margin-bottom: 10px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div class=\"logo\">🔒 CodeQuest Premium Payment</div>\n" +
                "            <p class=\"subtitle\">Verify your payment</p>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"content\">\n" +
                "            <div class=\"lock-icon\">🔐</div>\n" +
                "            <h2 style=\"color: #333; margin-bottom: 10px;\">Payment Verification</h2>\n" +
                "            <p style=\"color: #666; margin-bottom: 15px;\">A 6-digit code has been generated to verify your payment:</p>\n" +
                "\n" +
                "            <div class=\"package-badge\">👑 " + packageName + " Package</div>\n" +
                "\n" +
                "            <div class=\"code-box\">\n" +
                "                <div class=\"code-title\">Your Verification Code:</div>\n" +
                "                <div class=\"code\">" + code + "</div>\n" +
                "            </div>\n" +
                "\n" +
                "            <div class=\"warning\">\n" +
                "                <p class=\"warning-text\">⏰ <strong>This code expires in 10 minutes</strong>. Please enter it to complete your payment.</p>\n" +
                "            </div>\n" +
                "\n" +
                "            <p style=\"color: #666; font-size: 13px; margin-top: 20px;\">\n" +
                "                💳 Your payment is secure and encrypted with 256-bit SSL.\n" +
                "            </p>\n" +
                "            <p style=\"color: #999; font-size: 12px;\">\n" +
                "                If you didn't initiate this payment, please contact our support team immediately.\n" +
                "            </p>\n" +
                "        </div>\n" +
                "\n" +
                "        <div class=\"footer\">\n" +
                "            <p>© 2026 CodeQuest. All rights reserved.</p>\n" +
                "            <p>This is an automated email. Please do not reply.</p>\n" +
                "            <p><strong>CodeQuest Support:</strong> support@codequest.com</p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }
}
