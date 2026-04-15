package com.esprit.services;

import com.esprit.entities.Avancement;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvancementServices implements ICrud<Avancement> {
    private Connection con;

    public AvancementServices() {
        con = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Avancement a) throws SQLException {
        String sql = "INSERT INTO avancement (chapitre_actuel, progression, is_completed, id_user, id_cours) VALUES (?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, a.getChapitre_actuel());
        ps.setInt(2, a.getProgression());
        ps.setBoolean(3, a.isIs_completed());
        ps.setInt(4, a.getId_user());
        ps.setInt(5, a.getId_cours());
        ps.executeUpdate();
        System.out.println("✅ Avancement ajouté");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM avancement WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("🗑️ Avancement supprimé (id=" + id + ")");
    }

    @Override
    public void modifier(Avancement a) throws SQLException {
        String sql = "UPDATE avancement SET chapitre_actuel=?, progression=?, is_completed=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, a.getChapitre_actuel());
        ps.setInt(2, a.getProgression());
        ps.setBoolean(3, a.isIs_completed());
        ps.setInt(4, a.getId());
        ps.executeUpdate();
        System.out.println("✏️ Avancement modifié (id=" + a.getId() + ")");
    }

    @Override
    public List<Avancement> afficher() throws SQLException {
        List<Avancement> liste = new ArrayList<>();
        String sql = "SELECT * FROM avancement";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            liste.add(new Avancement(
                    rs.getInt("id"), rs.getInt("chapitre_actuel"),
                    rs.getInt("progression"), rs.getBoolean("is_completed"),
                    rs.getInt("id_user"), rs.getInt("id_cours")
            ));
        }
        return liste;
    }
}