package com.esprit.services;

import com.esprit.entities.Cours;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoursServices implements ICrud<Cours> {
    private Connection con;

    public CoursServices() {
        con = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Cours cours) throws SQLException {
        String sql = "INSERT INTO cours (titre, description, niveau, objectif, nb_chapitres, histoire_intro, pts_total, id_skill) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, cours.getTitre());
        ps.setString(2, cours.getDescription());
        ps.setString(3, cours.getNiveau());
        ps.setString(4, cours.getObjectif());
        ps.setInt(5, cours.getNb_chapitres());
        ps.setString(6, cours.getHistoire_intro());
        ps.setInt(7, cours.getPts_total());
        ps.setInt(8, cours.getId_skill());
        ps.executeUpdate();
        System.out.println(" Cours ajouté : " + cours.getTitre());
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM cours WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("🗑️ Cours supprimé (id=" + id + ")");
    }

    @Override
    public void modifier(Cours cours) throws SQLException {
        String sql = "UPDATE cours SET titre=?, description=?, niveau=?, objectif=?, nb_chapitres=?, pts_total=?, id_skill=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, cours.getTitre());
        ps.setString(2, cours.getDescription());
        ps.setString(3, cours.getNiveau());
        ps.setString(4, cours.getObjectif());
        ps.setInt(5, cours.getNb_chapitres());
        ps.setInt(6, cours.getPts_total());
        ps.setInt(7, cours.getId_skill());
        ps.setInt(8, cours.getId());
        ps.executeUpdate();
        System.out.println(" Cours modifié (id=" + cours.getId() + ")");
    }

    @Override
    public List<Cours> afficher() throws SQLException {
        List<Cours> liste = new ArrayList<>();
        String sql = "SELECT * FROM cours";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Cours c = new Cours(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getString("niveau"),
                    rs.getString("objectif"),
                    rs.getInt("nb_chapitres"),
                    rs.getString("histoire_intro"),
                    rs.getInt("pts_total"),
                    rs.getInt("id_skill")
            );
            liste.add(c);
        }
        return liste;
    }
}