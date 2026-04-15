package com.esprit.entities;

public class Users {
    private int id;
    private String nom_complet;
    private String email;
    private String password;
    private String role;
    private int age;
    private boolean is_admin;

    public Users() {}

    public Users(String nom_complet, String email, String password, String role, int age, boolean is_admin) {
        this.nom_complet = nom_complet;
        this.email = email;
        this.password = password;
        this.role = role;
        this.age = age;
        this.is_admin = is_admin;
    }

    public Users(int id, String nom_complet, String email, String password,
                 String role, int age, boolean is_admin) {
        this.id = id;
        this.nom_complet = nom_complet;
        this.email = email;
        this.password = password;
        this.role = role;
        this.age = age;
        this.is_admin = is_admin;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom_complet() { return nom_complet; }
    public void setNom_complet(String nom_complet) { this.nom_complet = nom_complet; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public boolean isIs_admin() { return is_admin; }
    public void setIs_admin(boolean is_admin) { this.is_admin = is_admin; }

    @Override
    public String toString() {
        return "Users{id=" + id + ", nom='" + nom_complet + "', email='" + email +
                "', role='" + role + "', is_admin=" + is_admin + "}";
    }
}