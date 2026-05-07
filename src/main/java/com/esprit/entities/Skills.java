package com.esprit.entities;

public class Skills {
    private int id;
    private String nom_skill;

    public Skills() {}
    public Skills(String nom_skill) { this.nom_skill = nom_skill; }
    public Skills(int id, String nom_skill) { this.id = id; this.nom_skill = nom_skill; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom_skill() { return nom_skill; }
    public void setNom_skill(String nom_skill) { this.nom_skill = nom_skill; }

    @Override
    public String toString() {
        return "Skills{id=" + id + ", nom='" + nom_skill + "'}";
    }
}