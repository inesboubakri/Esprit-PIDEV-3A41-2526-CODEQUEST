package dao;

import models.User;
import models.SocialUserInfo;
import utils.DatabaseConnection;
import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class UserDAO {

    // ── SIGN UP ──────────────────────────────────────────────
    // Creates user and assigns free subscription via user_subscription table
    public boolean register(User user) {
        String sqlUser = "INSERT INTO users (nom_complet, email, password, role, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getNomComplet());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword()); // already hashed
            ps.setString(4, "Student");
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            
            // Get the generated user ID
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                
                // Create free subscription record
                String sqlSubscription = "INSERT INTO user_subscription (user_id, plan, status, started_at, last_updated) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psSubscription = conn.prepareStatement(sqlSubscription)) {
                    psSubscription.setInt(1, userId);
                    psSubscription.setString(2, "free");
                    psSubscription.setString(3, "active");
                    psSubscription.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    psSubscription.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                    psSubscription.executeUpdate();
                    System.out.println("✅ User registered with free subscription: " + user.getEmail());
                    return true;
                }
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── SIGN IN ──────────────────────────────────────────────
    public User login(String email, String password) {
        String sql = "SELECT u.*, COALESCE(us.plan, 'free') as subscription FROM users u " +
                     "LEFT JOIN user_subscription us ON u.id = us.user_id AND us.status = 'active' " +
                     "WHERE u.email = ? ORDER BY us.started_at DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                // Verify password using BCrypt
                if (BCrypt.checkpw(password, storedHash)) {
                    return extractUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // login failed
    }

    // ── FIND USER BY EMAIL ───────────────────────────────────
    /**
     * Find a user by email address (used for OAuth and email lookups)
     * Joins with user_subscription to get the current plan.
     * 
     * @param email The user's email address
     * @return User if found, null otherwise
     */
    public User findByEmail(String email) {
        String sql = "SELECT u.*, COALESCE(us.plan, 'free') as subscription FROM users u " +
                     "LEFT JOIN user_subscription us ON u.id = us.user_id AND us.status = 'active' " +
                     "WHERE u.email = ? ORDER BY us.started_at DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── REGISTER FROM SOCIAL (OAuth) ─────────────────────────
    /**
     * Create a new user account from OAuth2 social login
     * 
     * @param socialUserInfo Information retrieved from OAuth provider
     * @return The newly created User, or null if registration failed
     */
    public User registerFromSocial(SocialUserInfo socialUserInfo) {
        System.out.println("📝 Registering new user from " + socialUserInfo.getProviderDisplayName() + " OAuth: " + socialUserInfo.email());
        
        // First check if user already exists
        User existingUser = findByEmail(socialUserInfo.email());
        if (existingUser != null) {
            System.out.println("⚠️  User with email " + socialUserInfo.email() + " already exists!");
            return null;
        }

        String sql = "INSERT INTO users (nom_complet, email, password, role, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Generate a random secure password (user won't need it for OAuth-only accounts)
            String randomPassword = generateSecureRandomPassword();
            String hashedPassword = BCrypt.hashpw(randomPassword, BCrypt.gensalt());

            ps.setString(1, socialUserInfo.name());
            ps.setString(2, socialUserInfo.email());
            ps.setString(3, hashedPassword);
            ps.setString(4, "Student");
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();

            // Get the generated user ID
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                System.out.println("✅ User registered successfully via " + socialUserInfo.getProviderDisplayName() + 
                                   " with ID: " + userId);
                
                // Return the new user
                return findByEmail(socialUserInfo.email());
            }

        } catch (SQLException e) {
            System.err.println("❌ Error registering user from social: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // ── HELPER: Generate Secure Random Password ──────────────
    /**
     * Generate a random secure password for OAuth-only accounts
     * This password won't be used for login but is required by the schema
     * 
     * @return A random 32-character base64-encoded password
     */
    private static String generateSecureRandomPassword() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24]; // 24 bytes = 32 base64 chars
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    // ── GET ALL USERS ─────────────────────────────────────────
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_banned = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // ── GET ONE USER BY ID ────────────────────────────────────
    public User getUserById(int id) {
        return findById(id);
    }

    // ── FIND USER BY ID ─────────────────────────────────────
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return extractUser(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ── FACE ID STATUS ─────────────────────────────────────
    public boolean hasFaceId(int userId) {
        String sql = "SELECT 1 FROM users WHERE id = ? AND face_embedding IS NOT NULL AND face_embedding <> '' LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE PROFILE ─────────────────────────────────────────
    public boolean updateProfile(User user) {
        String sql = "UPDATE users SET nom_complet = ?, role = ?, age = ?, niveau_info = ?, " +
                     "bio = ?, education = ?, experience = ?, formation = ? " +
                     "WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("🔄 Updating profile for user ID: " + user.getId());
            System.out.println("📝 SQL: " + sql);
            
            ps.setString(1, user.getNomComplet());
            ps.setString(2, user.getRole());
            ps.setInt(3, user.getAge());
            ps.setString(4, user.getNiveauInfo());
            ps.setString(5, user.getBio());
            ps.setString(6, user.getEducation());
            ps.setString(7, user.getExperience());
            ps.setString(8, user.getFormation());
            ps.setInt(9, user.getId());

            System.out.println("📊 Parameters: name=" + user.getNomComplet() + ", id=" + user.getId());
            
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Rows affected: " + rowsAffected);
            
            if (rowsAffected > 0) {
                System.out.println("✅ Profile updated successfully!");
                return true;
            } else {
                System.out.println("❌ No rows affected - user ID might not exist or no changes were made");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ SQL Error updating profile: " + e.getMessage());
            System.err.println("❌ SQL State: " + e.getSQLState());
            System.err.println("❌ Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("❌ Unexpected error in updateProfile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE PASSWORD ─────────────────────────────────────────
    /**
     * Update ONLY the password field for a given user
     * Used for password reset functionality
     * 
     * @param userId The ID of the user
     * @param hashedNewPassword The new password (already hashed with BCrypt)
     * @return true if update was successful, false otherwise
     */
    public boolean updatePassword(int userId, String hashedNewPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            System.out.println("🔐 Resetting password for user ID: " + userId);
            ps.setString(1, hashedNewPassword);
            ps.setInt(2, userId);

            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("✅ Password updated successfully for user ID: " + userId);
                return true;
            } else {
                System.out.println("❌ No rows affected - user ID might not exist");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error updating password: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── DELETE USER ─────────────────────────────────────────
    public boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            System.out.println("🗑️ Deleted user ID: " + userId);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── ADD USER (Admin Panel) ──────────────────────────────
    public boolean addUser(User user, String password) {
        String sql = "INSERT INTO users (nom_complet, email, password, role, age, niveau_info, bio, education, experience, is_admin, xp, formation, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Hash password using BCrypt
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            ps.setString(1, user.getNomComplet());
            ps.setString(2, user.getEmail());
            ps.setString(3, hashedPassword);
            ps.setString(4, "Student");
            ps.setInt(5, user.getAge());
            ps.setString(6, user.getNiveauInfo());
            ps.setString(7, user.getBio());
            ps.setString(8, user.getEducation());
            ps.setString(9, user.getExperience());
            ps.setBoolean(10, user.isAdmin());
            ps.setInt(11, 0); // Initial XP
            ps.setString(12, user.getFormation());
            ps.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));

            ps.executeUpdate();
            System.out.println("✅ User added: " + user.getNomComplet());
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error adding user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE SUBSCRIPTION ─────────────────────────────────────
    /**
     * Update the subscription plan for a user by inserting a new record in user_subscription.
     * Deactivates the old subscription if one exists.
     * 
     * @param userId The user's ID
     * @param plan The subscription plan ("free" or "pro")
     * @return true if update was successful, false otherwise
     */
    public boolean updateSubscription(int userId, String plan) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            System.out.println("💳 Updating subscription for user ID: " + userId + " to '" + plan + "'");

            // Same plan already active — nothing to do
            String sqlCheck = "SELECT COUNT(*) FROM user_subscription WHERE user_id = ? AND status = 'active' AND plan = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, userId);
                psCheck.setString(2, plan);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("ℹ️ User already has active '" + plan + "' subscription — no update needed");
                        return true;
                    }
                }
            }

            // Sign-up creates one row per user (unique user_id). Update that row instead of inserting another.
            String sqlUpdate = "UPDATE user_subscription SET plan = ?, status = 'active', last_updated = ? WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, plan);
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(3, userId);
                int updated = ps.executeUpdate();
                if (updated > 0) {
                    System.out.println("✅ Subscription updated successfully to '" + plan + "'");
                    return true;
                }
            }

            // Legacy user without a subscription row
            String sqlInsert = "INSERT INTO user_subscription (user_id, plan, status, started_at, last_updated) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, userId);
                psInsert.setString(2, plan);
                psInsert.setString(3, "active");
                psInsert.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                psInsert.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                if (psInsert.executeUpdate() > 0) {
                    System.out.println("✅ Subscription row created with plan '" + plan + "'");
                    return true;
                }
            }

            System.err.println("❌ Failed to update or insert subscription");
            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error updating subscription: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ── GET SKILL SCORES FOR USER ────────────────────────────
    /**
     * Returns the list of skill scores for the given user.
     * Each map contains: skillName (String), score (int), skillId (int)
     */
    public List<Map<String, Object>> getSkillScoresForUser(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT ss.score, s.nom_skill, s.id " +
                     "FROM skill_score ss " +
                     "JOIN skills s ON ss.id_skill = s.id " +
                     "WHERE ss.id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("skillName", rs.getString("nom_skill"));
                    row.put("score", rs.getInt("score"));
                    row.put("skillId", rs.getInt("id"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching skill scores: " + e.getMessage());
        }
        return result;
    }

    // ── GET USER BADGES ──────────────────────────────────────
    /**
     * Returns the list of badges earned by the user.
     * Each map contains: badgeName (String), earnedAt (String), description (String), skillId (int)
     */
    public List<Map<String, Object>> getUserBadges(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT ub.badge_name, ub.earned_at, b.description, b.id_skill " +
                     "FROM user_badges ub " +
                     "LEFT JOIN badges b ON b.nom = ub.badge_name " +
                     "WHERE ub.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("badgeName", rs.getString("badge_name"));
                    Timestamp earnedAt = rs.getTimestamp("earned_at");
                    row.put("earnedAt", earnedAt != null ? earnedAt.toLocalDateTime().toLocalDate().toString() : "");
                    row.put("description", rs.getString("description") != null ? rs.getString("description") : "");
                    row.put("skillId", rs.getInt("id_skill"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching user badges: " + e.getMessage());
        }
        return result;
    }

    // ── GET USER SUBSCRIPTION ────────────────────────────────
    /**
     * Returns the active subscription details for the user.
     * Keys: plan, status, transactionId, amountPaid, paidAt, startedAt, lastUpdated
     * Returns null if no active subscription found.
     */
    public Map<String, Object> getUserSubscription(int userId) {
        String sql = "SELECT * FROM user_subscription " +
                     "WHERE user_id = ? AND status = 'active' " +
                     "ORDER BY started_at DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("plan", rs.getString("plan") != null ? rs.getString("plan") : "free");
                    row.put("status", rs.getString("status") != null ? rs.getString("status") : "inactive");
                    row.put("transactionId", rs.getString("transaction_id"));
                    row.put("amountPaid", rs.getObject("amount_paid")); // may be null
                    Timestamp paidAt = rs.getTimestamp("paid_at");
                    row.put("paidAt", paidAt != null ? paidAt.toLocalDateTime().toLocalDate().toString() : null);
                    Timestamp startedAt = rs.getTimestamp("started_at");
                    row.put("startedAt", startedAt != null ? startedAt.toLocalDateTime().toLocalDate().toString() : null);
                    Timestamp lastUpdated = rs.getTimestamp("last_updated");
                    row.put("lastUpdated", lastUpdated != null ? lastUpdated.toLocalDateTime().toLocalDate().toString() : null);
                    return row;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching user subscription: " + e.getMessage());
        }
        return null;
    }

    // ── GET RECOMMENDED COURSES ──────────────────────────────
    /**
     * Returns up to 3 courses for skills where the user has a low score (< 100).
     * Falls back to top 3 courses by pts_total if no skill_score rows exist.
     * Each map contains: id, titre, niveau, ptsTotal, skillName
     */
    public List<Map<String, Object>> getRecommendedCourses(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT c.id, c.titre, c.niveau, c.pts_total, s.nom_skill " +
                     "FROM cours c " +
                     "JOIN skills s ON c.id_skill = s.id " +
                     "JOIN skill_score ss ON ss.id_skill = s.id AND ss.id_user = ? " +
                     "WHERE ss.score < 100 " +
                     "ORDER BY ss.score ASC LIMIT 3";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("titre", rs.getString("titre"));
                    row.put("niveau", rs.getString("niveau"));
                    row.put("ptsTotal", rs.getInt("pts_total"));
                    row.put("skillName", rs.getString("nom_skill"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching recommended courses (with skill filter): " + e.getMessage());
        }

        // Fallback: no results (user has no skill_score rows) — return top 3 by pts_total
        if (result.isEmpty()) {
            String fallbackSql = "SELECT c.id, c.titre, c.niveau, c.pts_total, s.nom_skill " +
                                 "FROM cours c JOIN skills s ON c.id_skill = s.id " +
                                 "ORDER BY c.pts_total DESC LIMIT 3";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(fallbackSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("titre", rs.getString("titre"));
                    row.put("niveau", rs.getString("niveau"));
                    row.put("ptsTotal", rs.getInt("pts_total"));
                    row.put("skillName", rs.getString("nom_skill"));
                    result.add(row);
                }
            } catch (SQLException e) {
                System.err.println("❌ Error fetching fallback courses: " + e.getMessage());
            }
        }
        return result;
    }

    // ── GET RECOMMENDED PROBLEMS ─────────────────────────────
    /**
     * Returns up to 3 unsolved problems for the user.
     * Each map contains: id, titre, difficulte, pts, skillName
     */
    public List<Map<String, Object>> getRecommendedProblems(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT p.id, p.titre, p.difficulte, p.pts, s.nom_skill " +
                     "FROM problemes p " +
                     "JOIN skills s ON p.id_skill = s.id " +
                     "LEFT JOIN solutions sol ON sol.id_probleme = p.id AND sol.id_user = ? " +
                     "WHERE sol.id IS NULL " +
                     "ORDER BY p.pts ASC LIMIT 3";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("titre", rs.getString("titre"));
                    row.put("difficulte", rs.getString("difficulte"));
                    row.put("pts", rs.getInt("pts"));
                    row.put("skillName", rs.getString("nom_skill"));
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching recommended problems: " + e.getMessage());
            // Table 'solutions' might not exist — return empty list gracefully
        }
        return result;
    }

    // ── GET RECOMMENDED PROJECTS ─────────────────────────────
    /**
     * Returns up to 2 recommended projects ordered by pts ASC.
     * Each map contains: id, titre, niveau, pts, skillName
     */
    public List<Map<String, Object>> getRecommendedProjects(int userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT pr.id, pr.titre, pr.niveau, pr.pts, s.nom_skill " +
                     "FROM projets pr " +
                     "JOIN skills s ON pr.id_skill = s.id " +
                     "ORDER BY pr.pts ASC LIMIT 2";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("titre", rs.getString("titre"));
                row.put("niveau", rs.getString("niveau"));
                row.put("pts", rs.getInt("pts"));
                row.put("skillName", rs.getString("nom_skill"));
                result.add(row);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching recommended projects: " + e.getMessage());
        }
        return result;
    }

    // Helper — maps a ResultSet row to a User object
    private User extractUser(ResultSet rs) throws SQLException {
        String subscription = "free";
        try {
            subscription = rs.getString("subscription");
            if (subscription == null) subscription = "free";
        } catch (Exception e) {
            // Column might not exist in older databases
            subscription = "free";
        }
        
        return new User(
            rs.getInt("id"),
            rs.getString("nom_complet"),
            rs.getString("email"),
            rs.getString("role"),
            rs.getInt("age"),
            rs.getString("niveau_info"),
            rs.getString("bio"),
            rs.getString("education"),
            rs.getString("experience"),
            rs.getBoolean("is_admin"),
            rs.getInt("xp"),
            rs.getString("formation"),
            subscription
        );
    }
}
