package controllers;

import dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.application.Platform;
import javafx.geometry.Insets;
import utils.NavigationManager;
import utils.AppConfig;
import utils.FaceIdService;
import utils.MistralAgentService;
import utils.PaymentSession;
import models.User;
import utils.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

/**
 * Controller for the Profile view.
 * Now includes an integrated Mistral AI assistant for profile management.
 * Also features Premium welcome popup and subscription badges.
 */
public class ProfileController {

    // Flag to show Premium welcome popup on first navigation to profile after payment
    public static boolean showPremiumWelcome = false;

    // ── Header ─────────────────────────────────────────────
    @FXML private ImageView avatarImageView;
    @FXML private Label avatarFallbackLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label verifiedBadgeLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label metaAgeLabel;
    @FXML private Label metaEmailLabel;
    @FXML private Button modifyProfileButton;
    @FXML private Button downloadCvButton;

    // ── Tabs ───────────────────────────────────────────────
    @FXML private ToggleButton tabOverviewButton;
    @FXML private ToggleButton tabSkillsButton;
    @FXML private ToggleButton tabBadgesButton;
    @FXML private ToggleButton tabSubscriptionButton;

    @FXML private VBox tabOverviewContent;
    @FXML private VBox tabSkillsContent;
    @FXML private VBox tabBadgesContent;
    @FXML private VBox tabSubscriptionContent;

    @FXML
    private Label roleLabelRole;

    @FXML
    private Label niveauLabel;

    @FXML
    private Label educationLabel;

    @FXML
    private Label experienceLabel;

    @FXML
    private Label bioLabel;

    @FXML private FlowPane interestsPane;

    // Face ID
    @FXML private Label faceIdStatusLabel;
    @FXML private Label faceIdInfoLabel;
    @FXML private Button faceIdRegisterButton;
    @FXML private Button faceIdVerifyButton;

    // AI Coach Panel (new)
    @FXML private VBox aiCoachPanel;
    @FXML private TextArea aiCoachOutput;
    @FXML private Button refreshCoachBtn;
    @FXML private Label coachLoadingLabel;
    @FXML private Label xpLabel;

    // Skills / Badges / Subscription
    @FXML private GridPane skillsGrid;
    @FXML private VBox badgesContainer;
    @FXML private Label subscriptionStatusBadge;
    @FXML private VBox subscriptionActiveBox;
    @FXML private VBox subscriptionEmptyBox;
    @FXML private Label subscriptionPlanLabel;
    @FXML private Label subscriptionPriceLabel;
    @FXML private Label subscriptionMetaLabel;
    @FXML private Button upgradeNowButton;

    // AI Chat Panel components
    @FXML private VBox chatMessagesBox;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField aiInputField;
    @FXML private Button aiSendButton;
    @FXML private Label aiStatusLabel;

    // Conversation history to maintain context
    private List<Map<String, String>> conversationHistory = new ArrayList<>();

    // Cached DB data (fetched once on initialize)
    private List<Map<String, Object>> skillScores = new ArrayList<>();
    private List<Map<String, Object>> recommendedCourses = new ArrayList<>();
    private List<Map<String, Object>> recommendedProblems = new ArrayList<>();
    private List<Map<String, Object>> recommendedProjects = new ArrayList<>();

    @FXML
    public void initialize() {
        setupTabs();
        conversationHistory = new ArrayList<>();

        User user = Session.getCurrentUser();
        if (user != null) {
            // Fetch DB data asynchronously then update UI
            CompletableFuture.runAsync(() -> {
                UserDAO dao = new UserDAO();
                List<Map<String, Object>> ss = dao.getSkillScoresForUser(user.getId());
                List<Map<String, Object>> rc = dao.getRecommendedCourses(user.getId());
                List<Map<String, Object>> rp = dao.getRecommendedProblems(user.getId());
                List<Map<String, Object>> rpj = dao.getRecommendedProjects(user.getId());
                boolean hasFaceId = dao.hasFaceId(user.getId());
                Platform.runLater(() -> {
                    skillScores = ss;
                    recommendedCourses = rc;
                    recommendedProblems = rp;
                    recommendedProjects = rpj;
                    displayUserProfile(user);
                    updateFaceIdStatus(hasFaceId,
                            hasFaceId ? "Face ID is ready to use." : "Add Face ID to secure your account and enable faster sign-in.");
                    loadAiCoach(user);
                    if (showPremiumWelcome) {
                        showPremiumWelcomeMessage();
                        showPremiumWelcome = false;
                    }
                });
            });
        } else {
            System.out.println("No user logged in");
        }
    }

    private void setupTabs() {
        ToggleGroup group = new ToggleGroup();
        if (tabOverviewButton != null) tabOverviewButton.setToggleGroup(group);
        if (tabSkillsButton != null) tabSkillsButton.setToggleGroup(group);
        if (tabBadgesButton != null) tabBadgesButton.setToggleGroup(group);
        if (tabSubscriptionButton != null) tabSubscriptionButton.setToggleGroup(group);

        if (tabOverviewButton != null) tabOverviewButton.setSelected(true);
        showTab("overview");

        group.selectedToggleProperty().addListener((obs, oldT, newT) -> {
            if (!(newT instanceof ToggleButton tb)) return;
            if (tb == tabOverviewButton) showTab("overview");
            else if (tb == tabSkillsButton) showTab("skills");
            else if (tb == tabBadgesButton) showTab("badges");
            else if (tb == tabSubscriptionButton) showTab("subscription");
        });
    }

    private void showTab(String tab) {
        setTabVisible(tabOverviewContent, "overview".equals(tab));
        setTabVisible(tabSkillsContent, "skills".equals(tab));
        setTabVisible(tabBadgesContent, "badges".equals(tab));
        setTabVisible(tabSubscriptionContent, "subscription".equals(tab));

        styleTab(tabOverviewButton, "overview".equals(tab));
        styleTab(tabSkillsButton, "skills".equals(tab));
        styleTab(tabBadgesButton, "badges".equals(tab));
        styleTab(tabSubscriptionButton, "subscription".equals(tab));
    }

    private void setTabVisible(VBox box, boolean on) {
        if (box == null) return;
        box.setVisible(on);
        box.setManaged(on);
    }

    private void styleTab(ToggleButton btn, boolean active) {
        if (btn == null) return;
        if (active) {
            btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #333333; -fx-border-color: #FF6B4A; -fx-border-width: 0 0 3 0; -fx-cursor: hand;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #666666; -fx-border-color: transparent; -fx-border-width: 0 0 3 0; -fx-cursor: hand;");
        }
    }

    /**
     * Display user profile information on the UI.
     * Also displays Premium badge if user has premium subscription.
     */
    private void displayUserProfile(User user) {
        String fullName = user.getNomComplet() != null ? user.getNomComplet() : "User";
        if (fullNameLabel != null) fullNameLabel.setText(fullName);

        if (subtitleLabel != null) {
            String role = user.getRole() != null ? user.getRole() : "CodeQuest Member";
            String level = user.getNiveauInfo() != null ? user.getNiveauInfo() : "Beginner Level";
            subtitleLabel.setText(role + " | " + level);
        }
        if (metaAgeLabel != null)
            metaAgeLabel.setText("🎂 Age: " + (user.getAge() > 0 ? user.getAge() : "—"));
        if (metaEmailLabel != null)
            metaEmailLabel.setText("✉️ Email: " + (user.getEmail() != null ? user.getEmail() : "—"));
        if (xpLabel != null)
            xpLabel.setText("⭐ " + user.getXp() + " XP");

        boolean verified = "pro".equalsIgnoreCase(user.getSubscription());
        if (verifiedBadgeLabel != null) {
            verifiedBadgeLabel.setVisible(verified);
            verifiedBadgeLabel.setManaged(verified);
        }

        if (roleLabelRole != null) roleLabelRole.setText(user.getRole() != null ? user.getRole() : "Member");
        if (niveauLabel != null) niveauLabel.setText(user.getNiveauInfo() != null ? user.getNiveauInfo() : "Not set");
        if (educationLabel != null) educationLabel.setText(user.getEducation() != null ? user.getEducation() : "Not specified");
        if (experienceLabel != null) experienceLabel.setText(user.getExperience() != null ? user.getExperience() : "No experience listed");
        if (bioLabel != null) bioLabel.setText(user.getBio() != null && !user.getBio().isEmpty()
                ? user.getBio() : "No bio yet. Add a bio to tell other programmers about yourself!");

        renderInterests(user);
        renderSkillsTab(user);
        renderBadgesTab(user);
        renderSubscriptionTab(user);

        System.out.println("✅ Profile loaded for: " + user.getNomComplet());
    }

    private void renderInterests(User user) {
        if (interestsPane == null) return;
        interestsPane.getChildren().clear();
        List<String> tags = new ArrayList<>();
        if (user.getFormation() != null && !user.getFormation().isBlank()) tags.add(user.getFormation());
        if (user.getRole() != null && !user.getRole().isBlank()) tags.add(user.getRole());
        if (tags.isEmpty()) tags.add("No interests yet");

        for (String t : tags) {
            Label chip = new Label(t);
            chip.setStyle("-fx-background-color: rgba(255,107,74,0.12); -fx-text-fill: #FF6B4A; -fx-padding: 6 10 6 10; -fx-background-radius: 999; -fx-font-weight: bold;");
            interestsPane.getChildren().add(chip);
        }
    }

    private void renderSkillsTab(User user) {
        if (skillsGrid == null) return;
        skillsGrid.getChildren().clear();

        if (skillScores.isEmpty()) {
            Label empty = new Label("No skills data yet. Complete some exercises to see your scores here.");
            empty.setStyle("-fx-text-fill: #888888; -fx-font-size: 13;");
            skillsGrid.add(empty, 0, 0);
            return;
        }

        for (int i = 0; i < skillScores.size(); i++) {
            Map<String, Object> s = skillScores.get(i);
            String skillName = (String) s.get("skillName");
            int score = (int) s.get("score");

            String levelLabel;
            String lvlStyle;
            if (score >= 200) {
                levelLabel = "Advanced";
                lvlStyle = "-fx-background-color: rgba(76,175,80,0.18); -fx-text-fill: #2E7D32;";
            } else if (score >= 100) {
                levelLabel = "Intermediate";
                lvlStyle = "-fx-background-color: rgba(255,152,0,0.18); -fx-text-fill: #EF6C00;";
            } else {
                levelLabel = "Beginner";
                lvlStyle = "-fx-background-color: rgba(33,150,243,0.18); -fx-text-fill: #1565C0;";
            }
            double progress = Math.min(score / 300.0, 1.0);

            VBox card = new VBox(10);
            card.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 16; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #E0E0E0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 0, 2);");

            Label nameLabel = new Label(skillName);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 14;");

            Label levelLbl = new Label(levelLabel);
            levelLbl.setStyle(lvlStyle + " -fx-padding: 4 10 4 10; -fx-background-radius: 999; -fx-font-weight: bold; -fx-font-size: 11;");

            javafx.scene.control.ProgressBar pb = new javafx.scene.control.ProgressBar(progress);
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setStyle("-fx-accent: #FF6B4A;");

            Label xpLbl = new Label(score + " / 300 XP");
            xpLbl.setStyle("-fx-text-fill: #666666; -fx-font-size: 12;");

            HBox top = new HBox(10, nameLabel);
            Region sp = new Region();
            HBox.setHgrow(sp, javafx.scene.layout.Priority.ALWAYS);
            top.getChildren().addAll(sp, levelLbl);
            card.getChildren().addAll(top, pb, xpLbl);
            skillsGrid.add(card, i % 3, i / 3);
        }
    }

    private void renderBadgesTab(User user) {
        if (badgesContainer == null) return;
        badgesContainer.getChildren().clear();

        UserDAO dao = new UserDAO();
        List<Map<String, Object>> badges = dao.getUserBadges(user.getId());

        if (badges.isEmpty()) {
            Label empty = new Label("🔒 No badges earned yet. Complete challenges to unlock badges!");
            empty.setStyle("-fx-text-fill: #888888; -fx-font-size: 13;");
            badgesContainer.getChildren().add(empty);
            return;
        }

        // Group badges by skillId
        Map<Integer, List<Map<String, Object>>> bySkill = new LinkedHashMap<>();
        for (Map<String, Object> b : badges) {
            int sid = (int) b.getOrDefault("skillId", 0);
            bySkill.computeIfAbsent(sid, k -> new ArrayList<>()).add(b);
        }

        // Look up skill name from skillScores cache; fallback to "General"
        Map<Integer, String> skillNameMap = new HashMap<>();
        for (Map<String, Object> s : skillScores) {
            skillNameMap.put((int) s.get("skillId"), (String) s.get("skillName"));
        }

        for (Map.Entry<Integer, List<Map<String, Object>>> entry : bySkill.entrySet()) {
            int sid = entry.getKey();
            String groupName = skillNameMap.getOrDefault(sid, sid == 0 ? "General" : "Skill #" + sid);
            List<Map<String, Object>> groupBadges = entry.getValue();

            VBox group = new VBox(10);
            group.setStyle("-fx-background-color: #FAFAFA; -fx-padding: 14; -fx-background-radius: 12; -fx-border-color: #E0E0E0; -fx-border-radius: 12;");

            Label title = new Label(groupName);
            title.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size: 14;");
            group.getChildren().add(title);

            FlowPane badgeRow = new FlowPane(12, 8);
            for (Map<String, Object> badge : groupBadges) {
                String bName = (String) badge.get("badgeName");
                String earnedAt = (String) badge.get("earnedAt");
                String desc = (String) badge.get("description");

                VBox bCard = new VBox(6);
                bCard.setPrefWidth(180);
                bCard.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 12; -fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #E0E0E0;");

                Label bTitle = new Label("🏅 " + (bName != null ? bName : "Badge"));
                bTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;");

                Label bDate = new Label("Earned: " + (earnedAt != null && !earnedAt.isEmpty() ? earnedAt : "—"));
                bDate.setStyle("-fx-text-fill: #666666; -fx-font-size: 11;");

                bCard.getChildren().addAll(bTitle, bDate);
                if (desc != null && !desc.isBlank()) {
                    Label bDesc = new Label(desc);
                    bDesc.setWrapText(true);
                    bDesc.setStyle("-fx-text-fill: #888888; -fx-font-size: 11;");
                    bCard.getChildren().add(bDesc);
                }
                badgeRow.getChildren().add(bCard);
            }
            group.getChildren().add(badgeRow);
            badgesContainer.getChildren().add(group);
        }
    }

    private void renderSubscriptionTab(User user) {
        if (user == null) return;
        UserDAO dao = new UserDAO();
        Map<String, Object> sub = dao.getUserSubscription(user.getId());

        boolean active = sub != null;
        String plan = active ? (String) sub.getOrDefault("plan", "free") : user.getSubscription();
        boolean isPro = "pro".equalsIgnoreCase(plan);

        if (subscriptionStatusBadge != null) {
            subscriptionStatusBadge.setText(active ? "Active" : "Inactive");
            subscriptionStatusBadge.setStyle(active
                ? "-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #1B5E20; -fx-background-color: rgba(76,175,80,0.18); -fx-padding: 4 10 4 10; -fx-background-radius: 999;"
                : "-fx-font-size: 11; -fx-font-weight: bold; -fx-text-fill: #666666; -fx-background-color: #F5F5F5; -fx-padding: 4 10 4 10; -fx-background-radius: 999;");
        }
        if (subscriptionActiveBox != null) {
            subscriptionActiveBox.setVisible(isPro && active);
            subscriptionActiveBox.setManaged(isPro && active);
        }
        if (subscriptionEmptyBox != null) {
            subscriptionEmptyBox.setVisible(!(isPro && active));
            subscriptionEmptyBox.setManaged(!(isPro && active));
        }
        if (isPro && active) {
            Object amount = sub.get("amountPaid");
            String txn = (String) sub.getOrDefault("transactionId", "—");
            String startedAt = (String) sub.getOrDefault("startedAt", "—");
            String paidAt = (String) sub.getOrDefault("paidAt", "—");
            String lastUpdated = (String) sub.getOrDefault("lastUpdated", "—");

            if (subscriptionPlanLabel != null)
                subscriptionPlanLabel.setText("Plan: " + plan.substring(0, 1).toUpperCase() + plan.substring(1));
            if (subscriptionPriceLabel != null)
                subscriptionPriceLabel.setText("Amount paid: " + (amount != null ? "$" + amount : "—"));
            if (subscriptionMetaLabel != null)
                subscriptionMetaLabel.setText("Transaction: " + (txn != null ? txn : "—")
                    + " | Started: " + startedAt + " | Paid: " + paidAt + " | Updated: " + lastUpdated);
        }
    }

    
    /**
     * Send a message to the AI assistant
     * This is called when the user clicks Send or presses Enter in the text field
     */
    @FXML
    public void handleAiSend() {
        String message = aiInputField.getText().trim();
        
        // Ignore empty messages
        if (message.isEmpty()) {
            return;
        }
        
        // Clear input field
        aiInputField.clear();
        
        // Add user message bubble to chat
        addMessageBubble(message, true);
        
        // Show thinking status
        aiStatusLabel.setVisible(true);
        aiStatusLabel.setManaged(true);
        aiStatusLabel.setText("🤖 Thinking...");
        
        // Add to conversation history
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", message);
        conversationHistory.add(userMsg);
        
        // Get current user for the API call
        User currentUser = Session.getCurrentUser();
        
        // Check if user is logged in
        if (currentUser == null) {
            addMessageBubble("❌ Error: You are not logged in. Please log in first.", false);
            aiStatusLabel.setVisible(false);
            aiStatusLabel.setManaged(false);
            return;
        }
        
        // Send to Mistral API asynchronously (with enriched context)
        MistralAgentService.sendMessage(message, currentUser, conversationHistory,
                        skillScores, recommendedCourses, recommendedProblems, recommendedProjects)
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        addMessageBubble(response, false);
                        Map<String, String> assistantMsg = new HashMap<>();
                        assistantMsg.put("role", "assistant");
                        assistantMsg.put("content", response);
                        conversationHistory.add(assistantMsg);
                        aiStatusLabel.setVisible(false);
                        aiStatusLabel.setManaged(false);
                        if (response.contains("✅") && response.toLowerCase().contains("updated")) {
                            User updatedUser = Session.getCurrentUser();
                            if (updatedUser != null) {
                                displayUserProfile(updatedUser);
                                System.out.println("✅ Profile refreshed after AI update");
                            }
                        }
                        scrollToBottom();
                    });
                })
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        addMessageBubble("❌ Error: " + ex.getMessage(), false);
                        aiStatusLabel.setVisible(false);
                        aiStatusLabel.setManaged(false);
                        scrollToBottom();
                    });
                    return null;
                });
    }

    @FXML
    public void handleRefreshCoach() {
        loadAiCoach(Session.getCurrentUser());
    }

    private void loadAiCoach(User user) {
        if (user == null) return;
        if (coachLoadingLabel != null) { coachLoadingLabel.setVisible(true); coachLoadingLabel.setManaged(true); }
        if (refreshCoachBtn != null) refreshCoachBtn.setDisable(true);
        if (aiCoachOutput != null) aiCoachOutput.setText("");

        MistralAgentService.generateTodoList(user, skillScores, recommendedCourses, recommendedProblems, recommendedProjects)
                .thenAccept(result -> Platform.runLater(() -> {
                    if (aiCoachOutput != null) aiCoachOutput.setText(result);
                    if (coachLoadingLabel != null) { coachLoadingLabel.setVisible(false); coachLoadingLabel.setManaged(false); }
                    if (refreshCoachBtn != null) refreshCoachBtn.setDisable(false);
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> {
                        if (aiCoachOutput != null) aiCoachOutput.setText("Could not load coach. Try refreshing.");
                        if (coachLoadingLabel != null) { coachLoadingLabel.setVisible(false); coachLoadingLabel.setManaged(false); }
                        if (refreshCoachBtn != null) refreshCoachBtn.setDisable(false);
                    });
                    return null;
                });
    }

    @FXML
    public void handleRegisterFaceId() {
        User user = Session.getCurrentUser();
        if (user == null) {
            showFaceIdAlert(Alert.AlertType.ERROR, "Face ID Registration", "No logged-in user was found.");
            return;
        }

        setFaceIdButtonsDisabled(true);

        CompletableFuture.supplyAsync(() -> FaceIdService.register(user.getId()))
                .whenComplete((result, throwable) -> Platform.runLater(() -> {
                    setFaceIdButtonsDisabled(false);

                    if (throwable != null) {
                        showFaceIdAlert(Alert.AlertType.ERROR, "Face ID Registration", throwable.getMessage());
                        return;
                    }

                    if (result == null) {
                        showFaceIdAlert(Alert.AlertType.ERROR, "Face ID Registration", "No result was returned by the Face ID service.");
                        return;
                    }

                    showFaceIdAlert(result.success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                            "Face ID Registration", result.message);
                    if (result.success) {
                        updateFaceIdStatus(true, "Face ID registered successfully.");
                    }
                }));
    }

    @FXML
    public void handleVerifyFaceId() {
        User user = Session.getCurrentUser();
        if (user == null) {
            showFaceIdAlert(Alert.AlertType.ERROR, "Face ID Verification", "No logged-in user was found.");
            return;
        }

        setFaceIdButtonsDisabled(true);

        CompletableFuture.supplyAsync(() -> FaceIdService.check(user.getId()))
                .whenComplete((result, throwable) -> Platform.runLater(() -> {
                    setFaceIdButtonsDisabled(false);

                    if (throwable != null) {
                        showFaceIdAlert(Alert.AlertType.ERROR, "Face ID Verification", throwable.getMessage());
                        return;
                    }

                    if (result == null) {
                        showFaceIdAlert(Alert.AlertType.ERROR, "Face ID Verification", "No result was returned by the Face ID service.");
                        return;
                    }

                    showFaceIdAlert(result.success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR,
                            "Face ID Verification", result.message);
                    if (result.success) {
                        updateFaceIdStatus(true, "Face ID verified successfully.");
                    }
                }));
    }

    private void setFaceIdButtonsDisabled(boolean disabled) {
        if (faceIdRegisterButton != null) {
            faceIdRegisterButton.setDisable(disabled);
        }
        if (faceIdVerifyButton != null) {
            faceIdVerifyButton.setDisable(disabled);
        }
    }

    private void updateFaceIdStatus(boolean active, String infoText) {
        if (faceIdStatusLabel != null) {
            faceIdStatusLabel.setText(active ? "✅ Face ID registered" : "❌ No Face ID registered");
        }
        if (faceIdInfoLabel != null) {
            faceIdInfoLabel.setText(infoText);
        }
        if (faceIdRegisterButton != null) {
            faceIdRegisterButton.setText(active ? "🔄 Re-register Face ID" : "📷 Register Face ID");
        }
    }

    private void showFaceIdAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message != null ? message : "");
        alert.showAndWait();
    }

    @FXML
    private void handleDownloadCv() {
        // Get current user
        User user = Session.getCurrentUser();
        if (user == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("❌ No user session found.");
            alert.showAndWait();
            return;
        }

        // Show file chooser on FX thread
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CV");
        fileChooser.setInitialFileName(user.getNomComplet().replace(" ", "_") + "_CV.pdf");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        Stage stage = getStage();
        if (stage == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("❌ Could not find application window.");
            alert.showAndWait();
            return;
        }
        
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return; // user cancelled

        // Run PDF generation on background thread
        CompletableFuture.runAsync(() -> {
            try {
                generateAndSavePdf(user, file);
                
                // Show success alert on FX thread
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("CV Downloaded");
                    alert.setHeaderText(null);
                    alert.setContentText("✅ Your CV has been saved to:\n" + file.getAbsolutePath());
                    alert.showAndWait();
                });
            } catch (Exception e) {
                // Show error alert on FX thread
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("❌ Failed to generate CV: " + e.getMessage());
                    alert.showAndWait();
                });
                e.printStackTrace();
            }
        });
    }

    /**
     * Generate and save a PDF CV for the user
     */
    private void generateAndSavePdf(User user, File file) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
        document.open();

        // Colors
        BaseColor orange = new BaseColor(255, 107, 74);
        BaseColor darkGray = new BaseColor(51, 51, 51);
        BaseColor lightGray = new BaseColor(102, 102, 102);

        // ─── HEADER ───
        com.itextpdf.text.Font nameFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 24, com.itextpdf.text.Font.BOLD, orange);
        Paragraph nameP = new Paragraph(user.getNomComplet(), nameFont);
        nameP.setAlignment(Element.ALIGN_CENTER);
        document.add(nameP);

        // Role | Level
        StringBuilder roleLevel = new StringBuilder();
        if (user.getRole() != null && !user.getRole().isEmpty()) {
            roleLevel.append(user.getRole());
        }
        if (user.getNiveauInfo() != null && !user.getNiveauInfo().isEmpty()) {
            if (roleLevel.length() > 0) roleLevel.append(" | ");
            roleLevel.append(user.getNiveauInfo());
        }
        if (roleLevel.length() > 0) {
            com.itextpdf.text.Font roleLevelFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 13, com.itextpdf.text.Font.NORMAL, lightGray);
            Paragraph roleLevelP = new Paragraph(roleLevel.toString(), roleLevelFont);
            roleLevelP.setAlignment(Element.ALIGN_CENTER);
            document.add(roleLevelP);
        }

        // Email • Age
        StringBuilder contactInfo = new StringBuilder();
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            contactInfo.append(user.getEmail());
        }
        if (user.getAge() > 0) {
            if (contactInfo.length() > 0) contactInfo.append(" • ");
            contactInfo.append(user.getAge());
        }
        if (contactInfo.length() > 0) {
            com.itextpdf.text.Font contactFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL, lightGray);
            Paragraph contactP = new Paragraph(contactInfo.toString(), contactFont);
            contactP.setAlignment(Element.ALIGN_CENTER);
            document.add(contactP);
        }

        // Horizontal line separator
        Paragraph separator = new Paragraph();
        separator.add(new LineSeparator());
        document.add(separator);
        document.add(new Paragraph(" ")); // space

        // ─── ABOUT ME ───
        if (user.getBio() != null && !user.getBio().isEmpty()) {
            com.itextpdf.text.Font sectionFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD, darkGray);
            Paragraph sectionP = new Paragraph("About Me", sectionFont);
            document.add(sectionP);

            com.itextpdf.text.Font bioFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
            Paragraph bioP = new Paragraph(user.getBio(), bioFont);
            bioP.setAlignment(Element.ALIGN_JUSTIFIED);
            document.add(bioP);
            document.add(new Paragraph(" ")); // space
        }

        // ─── EDUCATION & EXPERIENCE ───
        if ((user.getEducation() != null && !user.getEducation().isEmpty()) ||
            (user.getExperience() != null && !user.getExperience().isEmpty()) ||
            (user.getFormation() != null && !user.getFormation().isEmpty())) {

            com.itextpdf.text.Font sectionFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD, darkGray);
            com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);

            if (user.getEducation() != null && !user.getEducation().isEmpty()) {
                Paragraph labelP = new Paragraph("🎓 Education", labelFont);
                document.add(labelP);
                Paragraph contentP = new Paragraph(user.getEducation(), contentFont);
                contentP.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(contentP);
            }

            if (user.getExperience() != null && !user.getExperience().isEmpty()) {
                Paragraph labelP = new Paragraph("💼 Experience", labelFont);
                document.add(labelP);
                Paragraph contentP = new Paragraph(user.getExperience(), contentFont);
                contentP.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(contentP);
            }

            if (user.getFormation() != null && !user.getFormation().isEmpty()) {
                Paragraph labelP = new Paragraph("🏫 Formation", labelFont);
                document.add(labelP);
                Paragraph contentP = new Paragraph(user.getFormation(), contentFont);
                contentP.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(contentP);
            }

            document.add(new Paragraph(" ")); // space
        }

        // ─── SKILLS ───
        UserDAO dao = new UserDAO();
        List<Map<String, Object>> skills = dao.getSkillScoresForUser(user.getId());

        com.itextpdf.text.Font sectionFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD, darkGray);
        Paragraph skillsHeaderP = new Paragraph("Skills", sectionFont);
        document.add(skillsHeaderP);

        com.itextpdf.text.Font skillContentFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.NORMAL);

        if (skills.isEmpty()) {
            Paragraph noSkillsP = new Paragraph("No skills recorded yet.", skillContentFont);
            document.add(noSkillsP);
        } else {
            for (Map<String, Object> skill : skills) {
                String skillName = (String) skill.get("skillName");
                int score = (int) skill.get("score");
                
                // Determine level
                String level;
                if (score >= 200) {
                    level = "Advanced";
                } else if (score >= 100) {
                    level = "Intermediate";
                } else {
                    level = "Beginner";
                }
                
                Paragraph skillP = new Paragraph(
                    skillName + " — " + score + " points (" + level + ")",
                    skillContentFont
                );
                document.add(skillP);
            }
        }
        document.add(new Paragraph(" ")); // space

        // ─── BADGES ───
        List<Map<String, Object>> badges = dao.getUserBadges(user.getId());
        Paragraph badgesHeaderP = new Paragraph("Badges", sectionFont);
        document.add(badgesHeaderP);

        if (badges.isEmpty()) {
            Paragraph noBadgesP = new Paragraph("No badges earned yet.", skillContentFont);
            document.add(noBadgesP);
        } else {
            for (Map<String, Object> badge : badges) {
                String badgeName = (String) badge.get("badgeName");
                String earnedAt = (String) badge.get("earnedAt");
                String displayText = badgeName + " — earned on " + (earnedAt != null ? earnedAt : "N/A");
                Paragraph badgeP = new Paragraph(displayText, skillContentFont);
                document.add(badgeP);
            }
        }
        document.add(new Paragraph(" ")); // space

        // ─── SUBSCRIPTION ───
        Map<String, Object> subscription = dao.getUserSubscription(user.getId());
        Paragraph subHeaderP = new Paragraph("Subscription", sectionFont);
        document.add(subHeaderP);

        if (subscription != null) {
            String plan = (String) subscription.get("plan");
            String status = (String) subscription.get("status");
            Object amountPaid = subscription.get("amountPaid");
            String startedAt = (String) subscription.get("startedAt");
            
            Paragraph planP = new Paragraph("Plan: " + (plan != null ? plan : "Free"), skillContentFont);
            document.add(planP);
            
            Paragraph statusP = new Paragraph("Status: " + (status != null ? status : "N/A"), skillContentFont);
            document.add(statusP);
            
            if (amountPaid != null) {
                Paragraph amountP = new Paragraph("Amount Paid: $" + amountPaid, skillContentFont);
                document.add(amountP);
            }
            
            if (startedAt != null) {
                Paragraph memberP = new Paragraph("Member Since: " + startedAt, skillContentFont);
                document.add(memberP);
            }
        } else {
            Paragraph freePlanP = new Paragraph("Free plan", skillContentFont);
            document.add(freePlanP);
        }
        document.add(new Paragraph(" ")); // space

        // ─── FOOTER ───
        String todayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL, lightGray);
        Paragraph footerP = new Paragraph("Generated by CodeQuest • " + todayDate, footerFont);
        footerP.setAlignment(Element.ALIGN_CENTER);
        document.add(footerP);

        document.close();
    }

    @FXML
    private void handleUpgradeNow() {
        Stage stage = getStage();
        if (stage == null) return;

        User user = Session.getCurrentUser();
        if (user != null && user.getEmail() != null) {
            // Populate both PaymentSession and PackageSelectionController so
            // PaymentController.initialize() never sees a null email.
            PaymentSession.store(user.getEmail(), "pro");
            PackageSelectionController.pendingEmail = user.getEmail();
            PackageSelectionController.pendingName = user.getNomComplet();
            System.out.println("💳 Upgrade flow: email=" + user.getEmail() + ", package=pro");
        } else {
            System.err.println("❌ handleUpgradeNow: no logged-in user with email");
        }
        NavigationManager.navigateTo(stage, AppConfig.VIEW_PAYMENT, "Payment");
    }

    @FXML
    private void handleBackToHome() {
        Stage stage = getStage();
        if (stage != null) {
            NavigationManager.navigateTo(stage, AppConfig.VIEW_HOME, "Home");
        }
    }
    
    /**
     * Add a message bubble to the chat display
     * @param text The message text
     * @param isUser true if user message (right-aligned, orange), false if AI message (left-aligned, gray)
     */
    private void addMessageBubble(String text, boolean isUser) {
        // Create a label with the message text
        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setPrefWidth(420);  // Fixed width for proper wrapping
        messageLabel.setStyle(
            isUser ? 
            "-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #00c8ff, #0080ff); " +
            "-fx-text-fill: white; " +
            "-fx-padding: 10 14 10 14; " +
            "-fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-font-size: 12; " :
            "-fx-background-color: rgba(255,255,255,0.12); " +
            "-fx-text-fill: rgba(255,255,255,0.92); " +
            "-fx-padding: 10 14 10 14; " +
            "-fx-border-radius: 12; " +
            "-fx-background-radius: 12; " +
            "-fx-font-size: 12; "
        );
        
        // Wrap in HBox for alignment
        HBox bubbleContainer = new HBox();
        bubbleContainer.setStyle("-fx-fill-height: false;");
        if (isUser) {
            bubbleContainer.setStyle("-fx-alignment: TOP_RIGHT;");
            bubbleContainer.setPadding(new Insets(5, 10, 5, 0));
        } else {
            bubbleContainer.setStyle("-fx-alignment: TOP_LEFT;");
            bubbleContainer.setPadding(new Insets(5, 0, 5, 10));
        }
        bubbleContainer.getChildren().add(messageLabel);
        
        // Add to chat
        chatMessagesBox.getChildren().add(bubbleContainer);
    }
    
    /**
     * Auto-scroll the chat to the bottom
     */
    private void scrollToBottom() {
        chatScrollPane.setVvalue(1.0);
    }

    // ========== Navbar navigation handlers ==========
    
    @FXML
    private void handleNavHome() {
        navigateToView(AppConfig.VIEW_HOME, "Home");
    }

    @FXML
    private void handleNavCourses() {
        navigateToView(AppConfig.VIEW_COURSES, "Courses");
    }

    @FXML
    private void handleNavProblems() {
        navigateToView(AppConfig.VIEW_PROBLEMS, "Problems");
    }

    @FXML
    private void handleNavProjects() {
        navigateToView(AppConfig.VIEW_PROJECTS, "Projects");
    }

    @FXML
    private void handleNavEvents() {
        navigateToView(AppConfig.VIEW_EVENTS, "Events");
    }

    @FXML
    private void handleNavForum() {
        navigateToView(AppConfig.VIEW_FORUM, "Forum");
    }

    @FXML
    private void handleNavUsers() {
        navigateToView(AppConfig.VIEW_USERS, "Users");
    }

    @FXML
    private void handleNavProfile() {
        navigateToView(AppConfig.VIEW_PROFILE, "Profile");
    }

    @FXML
    public void handleEditProfile() {
        System.out.println("🔧 Edit Profile button clicked - attempting to open edit window");
        try {
            openEditProfileWindow();
        } catch (Exception e) {
            System.err.println("❌ Exception in handleEditProfile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Open the Edit Profile window as a separate dialog/stage
     */
    private void openEditProfileWindow() throws IOException {
        System.out.println("📂 Loading FXML from: " + AppConfig.VIEW_EDIT_PROFILE);
        
        FXMLLoader loader = NavigationManager.getFXMLLoader(AppConfig.VIEW_EDIT_PROFILE);
        System.out.println("✅ FXMLLoader created successfully");
        
        Parent root = loader.load();
        System.out.println("✅ FXML loaded successfully");
        
        Scene scene = new Scene(root);

        // Apply stylesheet
        try {
            java.net.URL cssResource = getClass().getResource("/styles.css");
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource.toExternalForm());
                System.out.println("✅ Stylesheet applied");
            } else {
                System.out.println("⚠️ Stylesheet not found");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error applying stylesheet: " + e.getMessage());
        }

        Stage editStage = new Stage();
        editStage.setTitle("Edit Your Profile");
        editStage.setScene(scene);
        editStage.setWidth(650);
        editStage.setHeight(900);
        editStage.setResizable(true);

        System.out.println("📂 Showing edit profile window");
        editStage.showAndWait();

        // Refresh profile display when dialog closes (in case user made changes)
        User user = Session.getCurrentUser();
        if (user != null) {
            displayUserProfile(user);
        }
    }

    @FXML
    public void handleSettings() {
        System.out.println("Settings");
    }

    /**
     * Helper method to navigate to a view.
     */
    private void navigateToView(String viewPath, String title) {
        try {
            Stage stage = getStage();
            if (stage == null) {
                System.err.println("❌ Navigation error: stage is null");
                return;
            }
            NavigationManager.navigateTo(stage, viewPath, title);
        } catch (Exception e) {
            System.err.println("Error navigating to: " + viewPath);
            e.printStackTrace();
        }
    }

    private Stage getStage() {
        if (tabOverviewContent != null && tabOverviewContent.getScene() != null) {
            return (Stage) tabOverviewContent.getScene().getWindow();
        }
        if (chatScrollPane != null && chatScrollPane.getScene() != null) {
            return (Stage) chatScrollPane.getScene().getWindow();
        }
        return null;
    }

    @FXML
    private void handleLogOut() {
        navigateToView(AppConfig.VIEW_SIGNIN, "Sign In");
    }

    /**
     * Display the Premium welcome popup after successful payment.
     * Creates a styled overlay with welcome message and action button.
     */
    private void showPremiumWelcomeMessage() {
        try {
            System.out.println("🎉 Showing Premium welcome popup");
            
            // Get the current scene
            Stage stage = getStage();
            Scene scene = stage != null ? stage.getScene() : null;
            if (scene == null) {
                System.err.println("❌ Scene is null!");
                return;
            }

            // Create semi-transparent overlay background
            VBox overlay = new VBox();
            overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            overlay.setAlignment(javafx.geometry.Pos.CENTER);

            // Create white card for the popup
            VBox card = new VBox();
            card.setSpacing(20);
            card.setStyle("-fx-background-color: white; " +
                         "-fx-padding: 40; " +
                         "-fx-border-radius: 16; " +
                         "-fx-background-radius: 16; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 20, 0, 0, 5);");
            card.setPrefWidth(500);
            card.setMaxWidth(500);
            card.setAlignment(javafx.geometry.Pos.TOP_CENTER);

            // Title
            Label titleLabel = new Label("🎉 Welcome to Premium!");
            titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #FF6B4A;");
            titleLabel.setAlignment(javafx.geometry.Pos.CENTER);

            // Subtitle
            Label subtitleLabel = new Label("Your Premium subscription is now active.");
            subtitleLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-wrap-text: true;");
            subtitleLabel.setWrapText(true);
            subtitleLabel.setAlignment(javafx.geometry.Pos.CENTER);

            // Features message
            Label featuresLabel = new Label("✨ You now have access to all Premium features including AI Assistant, unlimited problems, and exclusive projects.");
            featuresLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666666; -fx-wrap-text: true;");
            featuresLabel.setWrapText(true);
            featuresLabel.setAlignment(javafx.geometry.Pos.CENTER);

            // User badge with name
            User currentUser = Session.getCurrentUser();
            if (currentUser != null) {
                Label userBadge = new Label("👑 " + currentUser.getNomComplet());
                userBadge.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #FF6B4A, #ff8c42); " +
                                  "-fx-text-fill: white; " +
                                  "-fx-padding: 10 20 10 20; " +
                                  "-fx-border-radius: 20; " +
                                  "-fx-background-radius: 20; " +
                                  "-fx-font-weight: bold; " +
                                  "-fx-font-size: 14px;");
                userBadge.setAlignment(javafx.geometry.Pos.CENTER);
                HBox badgeBox = new HBox();
                badgeBox.setAlignment(javafx.geometry.Pos.CENTER);
                badgeBox.getChildren().add(userBadge);
                card.getChildren().addAll(titleLabel, subtitleLabel, featuresLabel, badgeBox);
            } else {
                card.getChildren().addAll(titleLabel, subtitleLabel, featuresLabel);
            }

            // Action Button
            Button startButton = new Button("🚀 Start Exploring");
            startButton.setStyle("-fx-font-size: 14px; " +
                               "-fx-font-weight: bold; " +
                               "-fx-padding: 12 30 12 30; " +
                               "-fx-background-color: #FF6B4A; " +
                               "-fx-text-fill: white; " +
                               "-fx-border-radius: 8; " +
                               "-fx-cursor: hand;");
            startButton.setPrefWidth(200);

            startButton.setOnAction(e -> {
                System.out.println("✅ User dismissed Premium welcome popup");
                var parent = overlay.getParent();
                if (parent instanceof javafx.scene.layout.Pane pane) {
                    pane.getChildren().remove(overlay);
                } else if (parent instanceof javafx.scene.Group group) {
                    group.getChildren().remove(overlay);
                } else {
                    // Fallback: if we can't mutate the parent children list, just hide the overlay.
                    overlay.setVisible(false);
                    overlay.setManaged(false);
                }
            });

            HBox buttonBox = new HBox();
            buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
            buttonBox.getChildren().add(startButton);
            card.getChildren().add(buttonBox);

            // Add card to overlay
            overlay.getChildren().add(card);

            // Create StackPane to layer overlay on top of existing content
            if (scene.getRoot() instanceof javafx.scene.layout.StackPane) {
                // Root is already a StackPane, just add overlay
                ((javafx.scene.layout.StackPane) scene.getRoot()).getChildren().add(overlay);
            } else {
                // Root is not a StackPane, need to wrap it
                javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane();
                stackPane.getChildren().addAll(scene.getRoot(), overlay);
                scene.setRoot(stackPane);
            }

            // Fade in animation
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(400),
                overlay
            );
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            System.out.println("✅ Premium welcome popup displayed and animated");

        } catch (Exception e) {
            System.err.println("❌ Error showing premium welcome message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
