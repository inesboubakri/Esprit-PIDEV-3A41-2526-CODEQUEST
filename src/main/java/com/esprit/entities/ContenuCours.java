package com.esprit.entities;

public class ContenuCours {
    private int id;
    private int numero_chapitre;
    private String titre_chapitre;
    private String contenu_text;
    private String lien_video;
    private int pts_chapitre;
    private int id_cours;          // clé étrangère vers cours

    public ContenuCours() {}

    public ContenuCours(int numero_chapitre, String titre_chapitre, String contenu_text,
                        String lien_video, int pts_chapitre, int id_cours) {
        this.numero_chapitre = numero_chapitre;
        this.titre_chapitre = titre_chapitre;
        this.contenu_text = contenu_text;
        this.lien_video = lien_video;
        this.pts_chapitre = pts_chapitre;
        this.id_cours = id_cours;
    }

    public ContenuCours(int id, int numero_chapitre, String titre_chapitre, String contenu_text,
                        String lien_video, int pts_chapitre, int id_cours) {
        this.id = id;
        this.numero_chapitre = numero_chapitre;
        this.titre_chapitre = titre_chapitre;
        this.contenu_text = contenu_text;
        this.lien_video = lien_video;
        this.pts_chapitre = pts_chapitre;
        this.id_cours = id_cours;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getNumero_chapitre() { return numero_chapitre; }
    public void setNumero_chapitre(int numero_chapitre) { this.numero_chapitre = numero_chapitre; }
    public String getTitre_chapitre() { return titre_chapitre; }
    public void setTitre_chapitre(String titre_chapitre) { this.titre_chapitre = titre_chapitre; }
    public String getContenu_text() { return contenu_text; }
    public void setContenu_text(String contenu_text) { this.contenu_text = contenu_text; }
    public String getLien_video() { return lien_video; }
    public void setLien_video(String lien_video) { this.lien_video = lien_video; }
    public int getPts_chapitre() { return pts_chapitre; }
    public void setPts_chapitre(int pts_chapitre) { this.pts_chapitre = pts_chapitre; }
    public int getId_cours() { return id_cours; }
    public void setId_cours(int id_cours) { this.id_cours = id_cours; }

    @Override
    public String toString() {
        return "ContenuCours{id=" + id + ", chapitre=" + numero_chapitre +
                ", titre='" + titre_chapitre + "', id_cours=" + id_cours + "}";
    }
}