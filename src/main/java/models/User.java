package models;

public class User {
    private int id;
    private String nomComplet;
    private String email;
    private String password;
    private String role;
    private int age;
    private String niveauInfo;
    private String bio;
    private String education;
    private String experience;
    private boolean isAdmin;
    private int xp;
    private String formation;
    private String subscription; // "free" or "premium", default "free"

    // No-arg constructor for UI operations
    public User() {
        this.subscription = "free";
    }

    // Constructor for Sign Up
    public User(String nomComplet, String email, String password) {
        this.nomComplet = nomComplet;
        this.email = email;
        this.password = password;
        this.role = "Student";
        this.subscription = "free";
    }

    // Full constructor (for loading from DB)
    public User(int id, String nomComplet, String email, String role,
                int age, String niveauInfo, String bio,
                String education, String experience,
                boolean isAdmin, int xp, String formation, String subscription) {
        this.id = id;
        this.nomComplet = nomComplet;
        this.email = email;
        this.role = role;
        this.age = age;
        this.niveauInfo = niveauInfo;
        this.bio = bio;
        this.education = education;
        this.experience = experience;
        this.isAdmin = isAdmin;
        this.xp = xp;
        this.formation = formation;
        this.subscription = subscription != null ? subscription : "free";
    }

    // Getters
    public int getId()           { return id; }
    public String getNomComplet(){ return nomComplet; }
    public String getEmail()     { return email; }
    public String getPassword()  { return password; }
    public String getRole()      { return role; }
    public int getAge()          { return age; }
    public String getNiveauInfo(){ return niveauInfo; }
    public String getBio()       { return bio; }
    public String getEducation() { return education; }
    public String getExperience(){ return experience; }
    public boolean isAdmin()     { return isAdmin; }
    public int getXp()           { return xp; }
    public String getFormation() { return formation; }
    public String getSubscription() { return subscription; }

    // Setters
    public void setNomComplet(String nomComplet) { this.nomComplet = nomComplet; }
    public void setRole(String role) { this.role = role; }
    public void setAge(int age) { this.age = age; }
    public void setNiveauInfo(String niveauInfo) { this.niveauInfo = niveauInfo; }
    public void setBio(String bio) { this.bio = bio; }
    public void setEducation(String education) { this.education = education; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setFormation(String formation) { this.formation = formation; }
    public void setXp(int xp) { this.xp = xp; }
    public void setEmail(String email) { this.email = email; }
    public void setAdmin(boolean admin) { this.isAdmin = admin; }
    public void setId(int id) { this.id = id; }
    public void setSubscription(String subscription) { this.subscription = subscription != null ? subscription : "free"; }
    public void setPassword(String password) { this.password = password; }
}
