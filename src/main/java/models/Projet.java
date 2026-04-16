package models;

import java.time.LocalDate;

public class Projet {
    private int id;
    private String titre;
    private String description;
    private String niveau;
    private String taches;
    private int pts;
    private String prizes;
    private LocalDate date_debut;
    private LocalDate date_limite;

    // No-arg constructor
    public Projet() {
    }

    // Parameterized constructor
    public Projet(int id, String titre, String description, String niveau, String taches, 
                  int pts, String prizes, LocalDate date_debut, LocalDate date_limite) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.niveau = niveau;
        this.taches = taches;
        this.pts = pts;
        this.prizes = prizes;
        this.date_debut = date_debut;
        this.date_limite = date_limite;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getTaches() {
        return taches;
    }

    public void setTaches(String taches) {
        this.taches = taches;
    }

    public int getPts() {
        return pts;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public String getPrizes() {
        return prizes;
    }

    public void setPrizes(String prizes) {
        this.prizes = prizes;
    }

    public LocalDate getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(LocalDate date_debut) {
        this.date_debut = date_debut;
    }

    public LocalDate getDate_limite() {
        return date_limite;
    }

    public void setDate_limite(LocalDate date_limite) {
        this.date_limite = date_limite;
    }

    @Override
    public String toString() {
        return "Projet{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", niveau='" + niveau + '\'' +
                ", taches=" + taches +
                ", pts=" + pts +
                ", prizes='" + prizes + '\'' +
                ", date_debut=" + date_debut +
                ", date_limite=" + date_limite +
                '}';
    }
}
