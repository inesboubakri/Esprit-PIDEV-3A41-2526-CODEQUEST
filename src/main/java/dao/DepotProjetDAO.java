package dao;

import models.DepotProjet;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DepotProjetDAO {
    private DatabaseConnection dbConnection;

    public DepotProjetDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Retrieve all depot projects from the database
     */
    public List<DepotProjet> getAll() {
        List<DepotProjet> depots = new ArrayList<>();
        String query = "SELECT id, lien_repo, description, status, submitted_at, projet_id, user_id FROM depot_projet";

        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                DepotProjet depot = new DepotProjet(
                        rs.getInt("id"),
                        rs.getString("lien_repo"),
                        rs.getString("description"),
                        rs.getString("status"),
                        rs.getTimestamp("submitted_at").toLocalDateTime(),
                        rs.getInt("projet_id"),
                        rs.getInt("user_id")
                );
                depots.add(depot);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all depot projects: " + e.getMessage());
            e.printStackTrace();
        }

        return depots;
    }

    /**
     * Add a new depot project to the database
     */
    public boolean add(DepotProjet d) {
        String query = "INSERT INTO depot_projet (lien_repo, description, status, submitted_at, projet_id, user_id) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, d.getLien_repo());
            pstmt.setString(2, d.getDescription());
            pstmt.setString(3, d.getStatus());
            pstmt.setTimestamp(4, Timestamp.valueOf(d.getSubmitted_at()));
            pstmt.setInt(5, d.getProjet_id());
            pstmt.setInt(6, d.getUser_id());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding depot project: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing depot project in the database
     */
    public boolean update(DepotProjet d) {
        String query = "UPDATE depot_projet SET lien_repo = ?, description = ?, status = ?, " +
                       "submitted_at = ?, projet_id = ?, user_id = ? WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, d.getLien_repo());
            pstmt.setString(2, d.getDescription());
            pstmt.setString(3, d.getStatus());
            pstmt.setTimestamp(4, Timestamp.valueOf(d.getSubmitted_at()));
            pstmt.setInt(5, d.getProjet_id());
            pstmt.setInt(6, d.getUser_id());
            pstmt.setInt(7, d.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating depot project: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a depot project from the database by ID
     */
    public boolean delete(int id) {
        String query = "DELETE FROM depot_projet WHERE id = ?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting depot project: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
