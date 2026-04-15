package com.esprit.services;

import com.esprit.entities.ContenuCours;
import com.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ContenuCoursServices implements ICrud<ContenuCours> {
    private Connection con;

    public ContenuCoursServices() {
        con = MyDataBase.getInstance().getConnection();
    }

    @Override
    public void ajouter(ContenuCours cc) throws SQLException {
        String sql = "INSERT INTO contenu_cours (numero_chapitre, titre_chapitre, contenu_text, lien_video, pts_chapitre, id_cours) VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, cc.getNumero_chapitre());
        ps.setString(2, cc.getTitre_chapitre());
        ps.setString(3, cc.getContenu_text());
        ps.setString(4, cc.getLien_video());
        ps.setInt(5, cc.getPts_chapitre());
        ps.setInt(6, cc.getId_cours());
        ps.executeUpdate();
        System.out.println("✅ Contenu ajouté : " + cc.getTitre_chapitre());
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM contenu_cours WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        System.out.println("🗑️ Contenu supprimé (id=" + id + ")");
    }

    @Override
    public void modifier(ContenuCours cc) throws SQLException {
        String sql = "UPDATE contenu_cours SET numero_chapitre=?, titre_chapitre=?, contenu_text=?, lien_video=?, pts_chapitre=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, cc.getNumero_chapitre());
        ps.setString(2, cc.getTitre_chapitre());
        ps.setString(3, cc.getContenu_text());
        ps.setString(4, cc.getLien_video());
        ps.setInt(5, cc.getPts_chapitre());
        ps.setInt(6, cc.getId());
        ps.executeUpdate();
        System.out.println("✏️ Contenu modifié (id=" + cc.getId() + ")");
    }

    @Override
    public List<ContenuCours> afficher() throws SQLException {
        List<ContenuCours> liste = new ArrayList<>();
        String sql = "SELECT * FROM contenu_cours";
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            ContenuCours cc = new ContenuCours(
                    rs.getInt("id"),
                    rs.getInt("numero_chapitre"),
                    rs.getString("titre_chapitre"),
                    rs.getString("contenu_text"),
                    rs.getString("lien_video"),
                    rs.getInt("pts_chapitre"),
                    rs.getInt("id_cours")
            );
            liste.add(cc);
        }
        return liste;
    }

    // Méthode utile : afficher les contenus d'un cours spécifique
    public List<ContenuCours> afficherParCours(int id_cours) throws SQLException {
        List<ContenuCours> liste = new ArrayList<>();
        String sql = "SELECT * FROM contenu_cours WHERE id_cours=? ORDER BY numero_chapitre";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id_cours);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            liste.add(new ContenuCours(
                    rs.getInt("id"), rs.getInt("numero_chapitre"),
                    rs.getString("titre_chapitre"), rs.getString("contenu_text"),
                    rs.getString("lien_video"), rs.getInt("pts_chapitre"), rs.getInt("id_cours")
            ));
        }
        return liste;
    }
}