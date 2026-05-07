package com.esprit.entities;

public class Cours {
    private int id;
    private String titre;
    private String description;
    private String niveau;        // "Beginner", "Intermediate", "Hard"
    private String objectif;
    private int nb_chapitres;
    private String histoire_intro;
    private int pts_total;
    private int id_skill;         // clé étrangère vers skills

    // Constructeur vide (obligatoire)
    public Cours() {}

    // Constructeur sans id (pour INSERT)
    public Cours(String titre, String description, String niveau, String objectif,
                 int nb_chapitres, String histoire_intro, int pts_total, int id_skill) {
        this.titre = titre;
        this.description = description;
        this.niveau = niveau;
        this.objectif = objectif;
        this.nb_chapitres = nb_chapitres;
        this.histoire_intro = histoire_intro;
        this.pts_total = pts_total;
        this.id_skill = id_skill;
    }

    // Constructeur complet (pour SELECT)
    public Cours(int id, String titre, String description, String niveau, String objectif,
                 int nb_chapitres, String histoire_intro, int pts_total, int id_skill) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.niveau = niveau;
        this.objectif = objectif;
        this.nb_chapitres = nb_chapitres;
        this.histoire_intro = histoire_intro;
        this.pts_total = pts_total;
        this.id_skill = id_skill;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNiveau() { return niveau; }
    public void setNiveau(String niveau) { this.niveau = niveau; }

    public String getObjectif() { return objectif; }
    public void setObjectif(String objectif) { this.objectif = objectif; }

    public int getNb_chapitres() { return nb_chapitres; }
    public void setNb_chapitres(int nb_chapitres) { this.nb_chapitres = nb_chapitres; }

    public String getHistoire_intro() { return histoire_intro; }
    public void setHistoire_intro(String histoire_intro) { this.histoire_intro = histoire_intro; }

    public int getPts_total() { return pts_total; }
    public void setPts_total(int pts_total) { this.pts_total = pts_total; }

    public int getId_skill() { return id_skill; }
    public void setId_skill(int id_skill) { this.id_skill = id_skill; }

    @Override
    public String toString() {
        return "Cours{id=" + id + ", titre='" + titre + "', niveau='" + niveau +
                "', pts_total=" + pts_total + ", id_skill=" + id_skill + "}";
    }
}