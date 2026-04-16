package models;

import java.time.LocalDateTime;

public class DepotProjet {
    private int id;
    private String lien_repo;
    private String description;
    private String status;
    private LocalDateTime submitted_at;
    private int projet_id;
    private int user_id;

    // No-arg constructor
    public DepotProjet() {
    }

    // Parameterized constructor
    public DepotProjet(int id, String lien_repo, String description, String status,
                       LocalDateTime submitted_at, int projet_id, int user_id) {
        this.id = id;
        this.lien_repo = lien_repo;
        this.description = description;
        this.status = status;
        this.submitted_at = submitted_at;
        this.projet_id = projet_id;
        this.user_id = user_id;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLien_repo() {
        return lien_repo;
    }

    public void setLien_repo(String lien_repo) {
        this.lien_repo = lien_repo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSubmitted_at() {
        return submitted_at;
    }

    public void setSubmitted_at(LocalDateTime submitted_at) {
        this.submitted_at = submitted_at;
    }

    public int getProjet_id() {
        return projet_id;
    }

    public void setProjet_id(int projet_id) {
        this.projet_id = projet_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "DepotProjet{" +
                "id=" + id +
                ", lien_repo='" + lien_repo + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", submitted_at=" + submitted_at +
                ", projet_id=" + projet_id +
                ", user_id=" + user_id +
                '}';
    }
}
