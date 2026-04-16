package models;

import java.time.LocalDateTime;

/**
 * User model representing a user in the codyquest database
 */
public class User {
    
    private int id;
    private String nomComplet;
    private String email;
    private String password; // BCrypt hashed
    private String role;
    private int age;
    private String niveauInfo;
    private String bio;
    private String education;
    private String experience;
    private int isAdmin;
    private int isBanned;
    private LocalDateTime createdAt;
    private String profilePhoto;
    private int xp;
    private String formation;
    
    // Default constructor
    public User() {
    }
    
    // Constructor with essential fields
    public User(int id, String nomComplet, String email, int isAdmin, int isBanned) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.email = email;
        this.isAdmin = isAdmin;
        this.isBanned = isBanned;
    }
    
    // Constructor with all fields
    public User(int id, String nomComplet, String email, String password, String role,
                int age, String niveauInfo, String bio, String education, String experience,
                int isAdmin, int isBanned, LocalDateTime createdAt, String profilePhoto,
                int xp, String formation) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.email = email;
        this.password = password;
        this.role = role;
        this.age = age;
        this.niveauInfo = niveauInfo;
        this.bio = bio;
        this.education = education;
        this.experience = experience;
        this.isAdmin = isAdmin;
        this.isBanned = isBanned;
        this.createdAt = createdAt;
        this.profilePhoto = profilePhoto;
        this.xp = xp;
        this.formation = formation;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNomComplet() {
        return nomComplet;
    }
    
    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    public String getNiveauInfo() {
        return niveauInfo;
    }
    
    public void setNiveauInfo(String niveauInfo) {
        this.niveauInfo = niveauInfo;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
    }
    
    public String getEducation() {
        return education;
    }
    
    public void setEducation(String education) {
        this.education = education;
    }
    
    public String getExperience() {
        return experience;
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
    }
    
    public int getIsAdmin() {
        return isAdmin;
    }
    
    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }
    
    public int getIsBanned() {
        return isBanned;
    }
    
    public void setIsBanned(int isBanned) {
        this.isBanned = isBanned;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getProfilePhoto() {
        return profilePhoto;
    }
    
    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
    
    public int getXp() {
        return xp;
    }
    
    public void setXp(int xp) {
        this.xp = xp;
    }
    
    public String getFormation() {
        return formation;
    }
    
    public void setFormation(String formation) {
        this.formation = formation;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nomComplet='" + nomComplet + '\'' +
                ", email='" + email + '\'' +
                ", isAdmin=" + isAdmin +
                ", isBanned=" + isBanned +
                '}';
    }
}
