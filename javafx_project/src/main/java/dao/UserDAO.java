package dao;

import models.User;
import utils.DBConnection;
import utils.PasswordUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User operations
 */
public class UserDAO {
    
    /**
     * Add a new user to the database
     */
    public static boolean addUser(String nomComplet, String email, String password, String role,
                                   int age, String niveauInfo, String bio, String education,
                                   String experience, String formation) {
        String sql = "INSERT INTO users (nom_complet, email, password, role, age, niveau_info, " +
                     "bio, education, experience, is_admin, is_banned, formation, created_at, xp) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, ?, NOW(), 0)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomComplet);
            stmt.setString(2, email);
            stmt.setString(3, PasswordUtils.hashPassword(password));
            stmt.setString(4, role);
            stmt.setInt(5, age);
            stmt.setString(6, niveauInfo);
            stmt.setString(7, bio);
            stmt.setString(8, education);
            stmt.setString(9, experience);
            stmt.setString(10, formation);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get user by email
     */
    public static User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get user by ID
     */
    public static User getUserById(int userId) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all users sorted by XP (descending - leaderboard style)
     */
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE is_banned = 0 ORDER BY xp DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Update user information
     */
    public static boolean updateUser(int userId, String nomComplet, String email, String role,
                                     int age, String niveauInfo, String bio, String education,
                                     String experience, String formation) {
        String sql = "UPDATE users SET nom_complet = ?, email = ?, role = ?, age = ?, " +
                     "niveau_info = ?, bio = ?, education = ?, experience = ?, formation = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nomComplet);
            stmt.setString(2, email);
            stmt.setString(3, role);
            stmt.setInt(4, age);
            stmt.setString(5, niveauInfo);
            stmt.setString(6, bio);
            stmt.setString(7, education);
            stmt.setString(8, experience);
            stmt.setString(9, formation);
            stmt.setInt(10, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete user by ID (cascade delete handled by database)
     */
    public static boolean deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Ban user by ID
     */
    public static boolean banUser(int userId) {
        String sql = "UPDATE users SET is_banned = 1 WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error banning user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Unban user by ID
     */
    public static boolean unbanUser(int userId) {
        String sql = "UPDATE users SET is_banned = 0 WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error unbanning user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Verify user login credentials
     */
    public static User verifyLogin(String email, String password) {
        System.out.println("DEBUG: UserDAO.verifyLogin called with email: " + email);
        User user = getUserByEmail(email);
        
        if (user == null) {
            System.out.println("DEBUG: User not found in database for email: " + email);
            return null;
        }
        
        System.out.println("DEBUG: User found in database. Verifying password...");
        
        if (user.getPassword() != null) {
            boolean passwordMatch = PasswordUtils.verifyPassword(password, user.getPassword());
            System.out.println("DEBUG: Password verification result: " + passwordMatch);
            
            if (passwordMatch) {
                System.out.println("DEBUG: Password matched! User authenticated.");
                return user;
            } else {
                System.out.println("DEBUG: Password did not match.");
            }
        } else {
            System.out.println("DEBUG: User password is null in database");
        }
        
        return null;
    }
    
    /**
     * Check if email already exists
     */
    public static boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking if email exists: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update user password
     */
    public static boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, PasswordUtils.hashPassword(newPassword));
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Make user admin
     */
    public static boolean makeAdmin(int userId) {
        String sql = "UPDATE users SET is_admin = 1 WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error making user admin: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Remove admin from user
     */
    public static boolean removeAdmin(int userId) {
        String sql = "UPDATE users SET is_admin = 0 WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing admin from user: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get total number of users
     */
    public static int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total users: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get total number of students (non-admins)
     */
    public static int getTotalStudents() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_admin = 0";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total students: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get total number of admins
     */
    public static int getTotalAdmins() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_admin = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total admins: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get total number of banned users
     */
    public static int getTotalBannedUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_banned = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting total banned users: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Get all users (including banned) - for admin dashboard
     */
    public static List<User> getAllUsersAdmin() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting all users (admin): " + e.getMessage());
        }
        
        return users;
    }
    
    /**
     * Helper method to map ResultSet to User object
     */
    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNomComplet(rs.getString("nom_complet"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        user.setAge(rs.getInt("age"));
        user.setNiveauInfo(rs.getString("niveau_info"));
        user.setBio(rs.getString("bio"));
        user.setEducation(rs.getString("education"));
        user.setExperience(rs.getString("experience"));
        user.setIsAdmin(rs.getInt("is_admin"));
        user.setIsBanned(rs.getInt("is_banned"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        user.setProfilePhoto(rs.getString("profile_photo"));
        user.setXp(rs.getInt("xp"));
        user.setFormation(rs.getString("formation"));
        
        return user;
    }
}
