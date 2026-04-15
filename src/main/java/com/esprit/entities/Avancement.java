package com.esprit.entities;

public class Avancement {
    private int id;
    private int chapitre_actuel;
    private int progression;       // 0 à 100 (%)
    private boolean is_completed;
    private int id_user;
    private int id_cours;

    public Avancement() {}

    public Avancement(int chapitre_actuel, int progression, boolean is_completed,
                      int id_user, int id_cours) {
        this.chapitre_actuel = chapitre_actuel;
        this.progression = progression;
        this.is_completed = is_completed;
        this.id_user = id_user;
        this.id_cours = id_cours;
    }

    public Avancement(int id, int chapitre_actuel, int progression, boolean is_completed,
                      int id_user, int id_cours) {
        this.id = id;
        this.chapitre_actuel = chapitre_actuel;
        this.progression = progression;
        this.is_completed = is_completed;
        this.id_user = id_user;
        this.id_cours = id_cours;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getChapitre_actuel() { return chapitre_actuel; }
    public void setChapitre_actuel(int chapitre_actuel) { this.chapitre_actuel = chapitre_actuel; }
    public int getProgression() { return progression; }
    public void setProgression(int progression) { this.progression = progression; }
    public boolean isIs_completed() { return is_completed; }
    public void setIs_completed(boolean is_completed) { this.is_completed = is_completed; }
    public int getId_user() { return id_user; }
    public void setId_user(int id_user) { this.id_user = id_user; }
    public int getId_cours() { return id_cours; }
    public void setId_cours(int id_cours) { this.id_cours = id_cours; }

    @Override
    public String toString() {
        return "Avancement{id=" + id + ", progression=" + progression +
                "%, completed=" + is_completed + ", id_user=" + id_user + ", id_cours=" + id_cours + "}";
    }
}