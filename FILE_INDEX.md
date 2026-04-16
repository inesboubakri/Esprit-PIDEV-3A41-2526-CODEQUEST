# JavaFX CodeQuest Project - Complete File Index

## Quick Reference

All files are located in: `javafx_project/src/`

---

## 📂 Directory Structure

```
javafx_project/
│
├── src/
│   ├── CodeQuestApp.java                  ⭐ START HERE - Main application entry point
│   │
│   ├── controllers/                       📋 All JavaFX Controllers
│   │   ├── HomeController.java
│   │   ├── CoursesController.java
│   │   ├── ProblemsController.java
│   │   ├── ProjectsController.java
│   │   ├── EventsController.java
│   │   ├── ForumController.java
│   │   ├── UsersController.java
│   │   ├── ProfileController.java
│   │   ├── SignInController.java
│   │   ├── SignUpController.java
│   │   ├── DashboardController.java
│   │   ├── UsersBackController.java
│   │   └── CoursesBackController.java
│   │
│   ├── views/                             🎨 All FXML Layout Files (Scene Builder Compatible)
│   │   ├── HomeView.fxml                  # Landing page with mentors grid
│   │   ├── CoursesView.fxml               # Course listing with filters
│   │   ├── ProblemsView.fxml              # Practice problems
│   │   ├── ProjectsView.fxml              # Portfolio projects
│   │   ├── EventsView.fxml                # Tournaments & events
│   │   ├── ForumView.fxml                 # Community forum
│   │   ├── UsersView.fxml                 # User directory/Arena
│   │   ├── ProfileView.fxml               # User profile page
│   │   ├── SignInView.fxml                # Sign-in form
│   │   ├── SignUpView.fxml                # Registration form
│   │   ├── DashboardView.fxml             # Admin dashboard
│   │   ├── UsersBackView.fxml             # User management (admin)
│   │   └── CoursesBackView.fxml           # Course management (admin)
│   │
│   ├── utils/                             🔧 Utility Classes
│   │   ├── NavigationManager.java         # Scene switching utility
│   │   └── AppConfig.java                 # Configuration constants
│   │
│   └── resources/
│       └── styles.css                     🎨 Main stylesheet (600+ lines)
│
├── README.md                              📖 Project documentation
└── DELIVERABLES.md                        📋 Complete deliverables summary
```

---

## 🎯 Page Overview

### Front-End Pages (User-Facing)

| # | Page | FXML File | Controller | Purpose |
|---|---|---|---|---|
| 1 | 🏠 Home | `HomeView.fxml` | `HomeController.java` | Landing page with hero, stats, mentors |
| 2 | 📚 Courses | `CoursesView.fxml` | `CoursesController.java` | Browse & filter courses |
| 3 | ✏️ Problems | `ProblemsView.fxml` | `ProblemsController.java` | Practice algorithmic problems |
| 4 | 🎯 Projects | `ProjectsView.fxml` | `ProjectsController.java` | Build portfolio projects |
| 5 | 🏆 Events | `EventsView.fxml` | `EventsController.java` | Tournaments & competitions |
| 6 | 🤝 Forum | `ForumView.fxml` | `ForumController.java` | Q&A community forum |
| 7 | ⚔️ Users | `UsersView.fxml` | `UsersController.java` | Arena - view user profiles |
| 8 | 👤 Profile | `ProfileView.fxml` | `ProfileController.java` | User profile & stats |
| 9 | 🔐 Sign In | `SignInView.fxml` | `SignInController.java` | Login page |
| 10 | 📝 Sign Up | `SignUpView.fxml` | `SignUpController.java` | Registration page |

### Back-End Pages (Admin Panel)

| # | Page | FXML File | Controller | Purpose |
|---|---|---|---|---|
| 11 | 📊 Dashboard | `DashboardView.fxml` | `DashboardController.java` | Admin statistics & overview |
| 12 | 👥 Users Mgmt | `UsersBackView.fxml` | `UsersBackController.java` | Manage user accounts |
| 13 | 📚 Courses Mgmt | `CoursesBackView.fxml` | `CoursesBackController.java` | Manage courses |

---

## 🎨 Styling System

### CSS File Location
`src/resources/styles.css` (600+ lines)

### Key CSS Classes

**Buttons**
- `.btn-primary` - Orange primary button
- `.btn-secondary` - White secondary button  
- `.btn-outline` - Transparent outline
- `.btn-sm`, `.btn-lg` - Size variants

**Cards**
- `.card` - Base card styling
- `.stat-card` - For statistics
- `.mentor-card` - For mentor profiles

**Navigation**
- `.sidebar` - Admin sidebar
- `.sidebar-nav-item` - Nav items
- `.sidebar-nav-item.active` - Active state

**Forms**
- `.text-input` - Text fields
- `.label-form` - Form labels
- `.filter-sidebar` - Filter panels

**Layouts**
- `.text-center`, `.text-left`, `.text-right`
- `.text-muted` - Muted text
- `.spacing-lg`, `.spacing-md`, `.spacing-sm`
- `.bg-light`, `.bg-white`

---

## 🚀 Getting Started

### Step 1: Setup JavaFX SDK
Download JavaFX SDK from https://gluonhq.com/products/javafx/

### Step 2: Compile
```bash
javac --module-path javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/*.java src/controllers/*.java src/utils/*.java
```

### Step 3: Run
```bash
java --module-path javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp bin CodeQuestApp
```

### Step 4: Open in Scene Builder (Optional)
Right-click any `.fxml` file → Open with Scene Builder

---

## 📊 Color Reference

All colors extracted exactly from the web template:

```
Primary Brand:    #FF6B4A (Vibrant Orange)
Primary Hover:    #ff7a1f (Darker Orange)
Secondary:        #FFB800 (Golden Yellow)
Accent:           #2196F3 (Deep Blue)
Success:          #4CAF50 (Green)
Warning:          #FFC107 (Amber)
Danger:           #F44336 (Red)
Dark Text:        #333333
Light Text:       #666666
Light Background: #F8F9FA
Border:           #E0E0E0
White:            #FFFFFF
```

---

## 🔧 Utility Classes

### NavigationManager
```java
// Navigate to a view
NavigationManager.navigateTo(stage, "views/CoursesView.fxml", "Courses");

// Load FXML directly
Parent root = NavigationManager.loadFXML("views/HomeView.fxml");
```

### AppConfig
Centralized constants:
```java
AppConfig.APP_TITLE
AppConfig.DEFAULT_WINDOW_WIDTH
AppConfig.COLOR_PRIMARY
AppConfig.VIEW_HOME
AppConfig.STYLESHEET
// ... and many more
```

---

## 📝 Controller Template

All controllers follow this structure:

```java
public class [Feature]Controller {
    
    @FXML
    private [Component] elementId;
    
    @FXML
    public void initialize() {
        // Setup code
    }
    
    @FXML
    private void handleEvent() {
        // TODO: Implement business logic
    }
}
```

---

## ✨ Features Summary

✅ **13 Complete FXML Views**
✅ **13 Corresponding Controllers**
✅ **Comprehensive CSS Stylesheet** (600+ lines)
✅ **100% Design Fidelity** (All colors, fonts exact)
✅ **Scene Builder Compatible** (All standard controls)
✅ **Professional Code Structure**
✅ **Navigation Utilities** 
✅ **Configuration Management**
✅ **English Language** (All UI text in English)
✅ **Full Documentation**

---

## 🎓 Implementation Checklist

- [ ] Compile the project
- [ ] Run CodeQuestApp.java
- [ ] Test scene navigation
- [ ] Open FXML files in Scene Builder
- [ ] Review CSS styling in styles.css
- [ ] Implement event handlers in controllers
- [ ] Connect to backend API
- [ ] Add database integration
- [ ] Implement user authentication
- [ ] Add data persistence

---

## 📚 File Descriptions

### Main Application
**CodeQuestApp.java** (35 lines)
- Application entry point
- Loads HomeView.fxml as initial scene
- Sets window title and dimensions

### Controllers (13 files)
**[Feature]Controller.java**
- Each controller has @FXML-annotated fields
- Empty initialize() methods for setup
- Empty event handler methods for business logic

### Views (13 files)
**[Feature]View.fxml**
- Scene Builder-compatible FXML layout
- Binds to corresponding controller
- References stylesheet

### Utilities (2 files)
**NavigationManager.java** (40 lines)
- loadFXML() - Load FXML into Parent
- navigateTo() - Switch scenes with styling

**AppConfig.java** (80 lines)
- 30+ configuration constants
- Color hex values
- View paths
- Dimension settings

### Stylesheet (1 file)
**styles.css** (600+ lines)
- Root CSS variables
- Component-specific styles
- Color scheme definitions
- Typography settings

---

## 🆘 Common Questions

**Q: How do I change a color?**
Edit `.root {}` section in `styles.css` or individual style properties.

**Q: Can I edit FXML files visually?**
Yes! Open any FXML file with Scene Builder 4.0+

**Q: How do I navigate between pages?**
Use `NavigationManager.navigateTo()` in event handlers.

**Q: Where should I add business logic?**
In the TODO methods within controller classes.

**Q: How do I connect to a database?**
Update controller methods to call your data service layer.

---

## 📞 File References Quick Links

- **Main App**: `src/CodeQuestApp.java`
- **Navigation**: `src/utils/NavigationManager.java`
- **Config**: `src/utils/AppConfig.java`
- **Styling**: `src/resources/styles.css`
- **Docs**: `README.md`, `DELIVERABLES.md`

---

## 🎉 Ready to Build!

You now have:
- ✅ Full scene structure in FXML
- ✅ All controllers scaffolded
- ✅ Professional CSS styling
- ✅ Navigation utilities
- ✅ Configuration management

**Next steps**: Implement business logic in the controller event handlers and connect to your backend!

---

**Version**: 1.0  
**Created**: April 14, 2024  
**Language**: English  
**Framework**: JavaFX 20+, Scene Builder compatible
