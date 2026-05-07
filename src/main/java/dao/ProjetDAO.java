package dao;

import models.Projet;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjetDAO {
    private DatabaseConnection dbConnection;

    public ProjetDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * Retrieve all projects from the database
     */
    public List<Projet> getAll() {
        List<Projet> projets = new ArrayList<>();
        String query = "SELECT id, titre, description, niveau, taches, pts, prizes, date_debut, date_limite FROM projets";

        Connection conn = dbConnection.getConnection();
        if (conn == null) {
            System.err.println("[ProjetDAO] ✗ FATAL: Database connection is NULL!");
            System.err.println("[ProjetDAO] ✗ Unable to execute query. Check DatabaseConnection logs above.");
            return projets;
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("[ProjetDAO] Executing query: " + query);
            int count = 0;
            while (rs.next()) {
                Date dateDebut = rs.getDate("date_debut");
                Date dateLimite = rs.getDate("date_limite");
                
                Projet projet = new Projet(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("niveau"),
                        rs.getString("taches"),
                        rs.getInt("pts"),
                        rs.getString("prizes"),
                        dateDebut != null ? dateDebut.toLocalDate() : null,
                        dateLimite != null ? dateLimite.toLocalDate() : null
                );
                projets.add(projet);
                count++;
                System.out.println("[ProjetDAO] Loaded project #" + count + ": " + projet.getTitre());
            }
            System.out.println("[ProjetDAO] ✓ Total projects loaded: " + count);
        } catch (SQLException e) {
            System.err.println("[ProjetDAO] ✗ Error fetching all projects: " + e.getMessage());
            System.err.println("[ProjetDAO] ✗ SQL State: " + e.getSQLState());
            System.err.println("[ProjetDAO] ✗ Error Code: " + e.getErrorCode());
            e.printStackTrace();
        }

        return projets;
    }

    /**
     * Add a new project to the database
     */
    public boolean add(Projet p) {
        Connection conn = dbConnection.getConnection();
        if (conn == null) {
            System.err.println("[ProjetDAO] ✗ Cannot add project: Database connection is NULL");
            return false;
        }

        String query = "INSERT INTO projets (titre, description, niveau, taches, pts, prizes, date_debut, date_limite) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, p.getTitre());
            pstmt.setString(2, p.getDescription());
            pstmt.setString(3, p.getNiveau());
            pstmt.setString(4, p.getTaches());
            pstmt.setInt(5, p.getPts());
            pstmt.setString(6, p.getPrizes());
            pstmt.setDate(7, p.getDate_debut() != null ? Date.valueOf(p.getDate_debut()) : null);
            pstmt.setDate(8, p.getDate_limite() != null ? Date.valueOf(p.getDate_limite()) : null);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error adding project: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update an existing project in the database
     */
    public boolean update(Projet p) {
        Connection conn = dbConnection.getConnection();
        if (conn == null) {
            System.err.println("[ProjetDAO] ✗ Cannot update project: Database connection is NULL");
            return false;
        }

        String query = "UPDATE projets SET titre = ?, description = ?, niveau = ?, taches = ?, " +
                       "pts = ?, prizes = ?, date_debut = ?, date_limite = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, p.getTitre());
            pstmt.setString(2, p.getDescription());
            pstmt.setString(3, p.getNiveau());
            pstmt.setString(4, p.getTaches());
            pstmt.setInt(5, p.getPts());
            pstmt.setString(6, p.getPrizes());
            pstmt.setDate(7, p.getDate_debut() != null ? Date.valueOf(p.getDate_debut()) : null);
            pstmt.setDate(8, p.getDate_limite() != null ? Date.valueOf(p.getDate_limite()) : null);
            pstmt.setInt(9, p.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error updating project: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a project from the database by ID
     */
    public boolean delete(int id) {
        Connection conn = dbConnection.getConnection();
        if (conn == null) {
            System.err.println("[ProjetDAO] ✗ Cannot delete project: Database connection is NULL");
            return false;
        }

        String query = "DELETE FROM projets WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Error deleting project: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
