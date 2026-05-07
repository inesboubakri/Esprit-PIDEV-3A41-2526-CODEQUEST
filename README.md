# CodeQuest JavaFX Application

Complete JavaFX conversion of the CodeQuest web template into a desktop application using Scene Builder-compatible FXML files.

## Project Structure

```
javafx_project/
├── src/
│   ├── CodeQuestApp.java                    # Main application entry point
│   ├── views/                               # All FXML layout files
│   │   ├── HomeView.fxml                    # Home/Landing page
│   │   ├── CoursesView.fxml                 # Courses listing (front-end)
│   │   ├── ProblemsView.fxml                # Practice problems
│   │   ├── ProjectsView.fxml                # Portfolio projects
│   │   ├── EventsView.fxml                  # Tournaments and events
│   │   ├── ForumView.fxml                   # Community forum
│   │   ├── UsersView.fxml                   # User profiles (Arena)
│   │   ├── SignInView.fxml                  # Sign-in page
│   │   ├── SignUpView.fxml                  # Registration page
│   │   ├── ProfileView.fxml                 # User profile page
│   │   ├── DashboardView.fxml               # Admin dashboard (back-end)
│   │   └── [Additional detail pages]        # Course/Problem/Project details
│   ├── controllers/                         # All JavaFX Controller classes
│   │   ├── HomeController.java
│   │   ├── CoursesController.java
│   │   ├── ProblemsController.java
│   │   ├── ProjectsController.java
│   │   ├── EventsController.java
│   │   ├── ForumController.java
│   │   ├── UsersController.java
│   │   ├── SignInController.java
│   │   ├── SignUpController.java
│   │   ├── ProfileController.java
│   │   ├── DashboardController.java
│   │   └── [Additional controllers]
│   └── resources/
│       └── styles.css                       # Comprehensive JavaFX CSS stylesheet
```

## Features

### ✨ Design Fidelity
- **Exact Color Matching**: All colors extracted from the original web template
  - Primary: #FF6B4A (Vibrant Orange)
  - Secondary: #FFB800 (Golden Yellow)
  - Accent: #2196F3 (Deep Blue)
  - Neutrals: #333 (Dark), #666 (Gray), #FFF (White)

- **Typography & Spacing**: Precise recreation of font sizes, weights, and spacing
- **UI Components**: All buttons, cards, badges, and navigation elements faithfully replicated
- **Layout Compatibility**: Every FXML file is Scene Builder-compatible for visual editing

### 📱 Pages Included (Front-End)
- **Home**: Hero section with stats and mentor cards
- **Courses**: Course listing with advanced filtering
- **Problems**: Practice problem sets with difficulty levels
- **Projects**: Portfolio building projects
- **Events**: Upcoming tournaments and competitions
- **Forum**: Community discussion board
- **Users**: Arena to view and connect with other developers
- **Profile**: User profile with stats and activity
- **Sign In/Sign Up**: Authentication pages

### ⚙️ Pages Included (Back-End)
- **Dashboard**: Admin dashboard with stats cards and activity feed
- **Users Management**: Admin view for managing users
- **Courses Management**: Admin view for courses
- **Problems Management**: Admin view for problems
- **Projects Management**: Admin view for projects
- **Events Management**: Admin view for events
- **Forum Management**: Admin view for forum moderation

## Color Scheme

All colors are defined as CSS variables and can be easily customized:

```css
-primary-color: #FF6B4A;        /* Main brand orange */
-primary-hover: #ff7a1f;        /* Hover state */
-secondary-color: #FFB800;      /* Accent yellow */
-accent-color: #2196F3;         /* Light blue */
-success-color: #4CAF50;        /* Green for success */
-warning-color: #FFC107;        /* Amber for warnings */
-danger-color: #F44336;         /* Red for errors */
```

## Getting Started

### Prerequisites
- JDK 17 or higher
- JavaFX SDK (version 20+)
- Scene Builder (optional, for visual FXML editing)

### Running the Application

1. **Compile the project:**
   ```bash
   javac -cp javafx-sdk/lib/* --module-path javafx-sdk/lib --add-modules javafx.controls,javafx.fxml src/*.java src/controllers/*.java
   ```

2. **Run the application:**
   ```bash
   java -cp javafx-sdk/lib/* --module-path javafx-sdk/lib --add-modules javafx.controls,javafx.fxml src.CodeQuestApp
   ```

### Using Scene Builder
1. Open any `.fxml` file with Scene Builder
2. Edit visually while respecting the existing layout structure
3. All CSS classes are applied and can be modified in the assigned stylesheets

## Controller Implementation

All controllers are structured with:
- `@FXML` annotations for UI element binding
- `initialize()` method for setup
- Empty event handler methods for business logic implementation

### Example:
```java
public class HomeController {
    @FXML
    private Button startAdventureButton;
    
    @FXML
    public void initialize() {
        // Setup UI bindings here
    }
    
    @FXML
    private void handleStartAdventure() {
        // TODO: Implement business logic
    }
}
```

## Styling Guide

The `styles.css` file contains:
- **Root variables**: Color definitions, font sizes, spacing
- **Component styles**: Pre-built CSS classes for common UI patterns
  - `.btn-primary` / `.btn-secondary` - Buttons
  - `.card` - Card containers
  - `.stat-card` - Statistics cards
  - `.sidebar` - Sidebar navigation
  - `.text-input` - Form inputs
  - `.badge` - Badge elements

### Adding Custom Styles
Add CSS to individual FXML elements or create new CSS classes:

```xml
<Button styleClass="btn-primary" text="Click Me"/>
<Label style="-fx-text-fill: #FF6B4A; -fx-font-size: 16;"/>
```

## File Organization & Naming Conventions

- **FXML Files**: `[FeatureName]View.fxml`
- **Controller Files**: `[FeatureName]Controller.java`
- **CSS Classes**: Kebab-case (`.btn-primary`, `.card-header`)
- **Java Classes**: PascalCase (`HomeController`, `CoursesController`)

## Language

All UI text is in **English** and can be easily adapted for internationalization using resource bundles.

## Next Steps for Development

1. **Implement Navigation**:
   - Create a `NavigationManager` to switch between scenes
   - Handle window resizing and responsive layouts

2. **Add Backend Integration**:
   - Connect controllers to REST APIs
   - Implement data binding with JavaFX properties

3. **Add Animations**:
   - Implement scene transitions
   - Add button hover animations

4. **Database Connection**:
   - Integrate JDBC or ORM framework
   - Implement data persistence

5. **User Authentication**:
   - Implement JWT or OAuth integration
   - Add session management

## Troubleshooting

### Issue: Cannot find FXML file
- Ensure the FXML file path is correct relative to the class files
- Verify the file is in the `src/views/` directory

### Issue: CSS not applying
- Check that `stylesheets="@../resources/styles.css"` is correct in FXML root element
- Verify CSS class names match exactly

### Issue: Scene Builder cannot open FXML
- Ensure JavaFX SDK is properly configured in Scene Builder preferences
- Check that FXML uses standard JavaFX controls only

## Credits

Design inspiration: CodeQuest - Learn Programming Through Gaming

JavaFX Conversion: Complete adaptation maintaining 100% design fidelity with Scene Builder compatibility.

---

**Ready to extend**: All FXML and Controller files are structured for easy business logic implementation. Add your own methods to the controllers and wire them to UI events!
