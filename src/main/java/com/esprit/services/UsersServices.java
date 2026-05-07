package com.esprit.services;

import com.esprit.entities.Users;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsersServices implements ICrud<Users> {
    private Connection con;

    public UsersServices() {
        con = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Users u) throws SQLException {
        String sql = "INSERT INTO users (nom_complet, email, password, role, age, is_admin, created_at) VALUES (?,?,?,?,?,?, NOW())";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, u.getNom_complet());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getPassword());
        ps.setString(4, u.getRole());
        ps.setInt(5, u.getAge());
        ps.setBoolean(6, u.isIs_admin());
        ps.executeUpdate();
        System.out.println("✅ Utilisateur ajouté : " + u.getNom_complet());
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("🗑️ Utilisateur supprimé (id=" + id + ")");
    }

    @Override
    public void modifier(Users u) throws SQLException {
        String sql = "UPDATE users SET nom_complet=?, email=?, role=?, age=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, u.getNom_complet());
        ps.setString(2, u.getEmail());
        ps.setString(3, u.getRole());
        ps.setInt(4, u.getAge());
        ps.setInt(5, u.getId());
        ps.executeUpdate();
        System.out.println("✏️ Utilisateur modifié (id=" + u.getId() + ")");
    }

    @Override
    public List<Users> afficher() throws SQLException {
        List<Users> liste = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            liste.add(new Users(
                    rs.getInt("id"), rs.getString("nom_complet"),
                    rs.getString("email"), rs.getString("password"),
                    rs.getString("role"), rs.getInt("age"),
                    rs.getBoolean("is_admin")
            ));
        }
        return liste;
    }

    // Pour le login plus tard (Sign In)
    public Users login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE email=? AND password=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, email);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Users(
                    rs.getInt("id"), rs.getString("nom_complet"),
                    rs.getString("email"), rs.getString("password"),
                    rs.getString("role"), rs.getInt("age"),
                    rs.getBoolean("is_admin")
            );
        }
        return null; // login raté
    }
}