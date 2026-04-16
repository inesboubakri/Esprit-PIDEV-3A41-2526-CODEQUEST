# FRONTEND PAGE STRUCTURE & FEATURES

## 📄 Front-End Pages Overview

Your front-end has 15 HTML pages organized in `templates/front/`. Here's what each page should contain:

---

## 🏠 PAGE 1: Home (home.html)
**Purpose:** Landing page / Portal entry point  
**Expected Route:** `/templates/front/home.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ NAVIGATION BAR                          │
│ Logo | Nav Links | Sign In | Sign Up    │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│ HERO SECTION                            │
│ Title: "Your Coding Adventure Awaits"   │
│ Level Badge: ⚡ Level 1 • 2,450 XP      │
│ CTA Buttons: Start Adventure | Trailer  │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│ STATS SECTION                           │
│ 50K+ Adventurers | 1,000+ Quests | ...  │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│ FEATURED CONTENT SECTIONS               │
│ - Featured Courses                      │
│ - Popular Challenges                    │
│ - Top Leaderboards                      │
│ - Success Stories                       │
└─────────────────────────────────────────┘
┌─────────────────────────────────────────┐
│ CALL-TO-ACTION SECTION                  │
│ "Ready to Level Up?" | Sign Up Button   │
└─────────────────────────────────────────┘
```

### JavaScript Functions (home.js)
- `initializePage()` - Setup page on load
- `startAdventure()` - Handle Start button
- `playTrailer()` - Handle Trailer button
- `toggleTheme()` - Light/dark mode toggle
- `handleNavigation()` - Menu navigation

### CSS File
- `style.css` - Main styles
- `home.css` - (If page-specific styles needed)

---

## 📚 PAGE 2: Courses (courses.html)
**Purpose:** Browse and enroll in courses  
**Expected Route:** `/templates/front/courses.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ NAVIGATION & SEARCH BAR                 │
│ Search | Filters | Sort                 │
└─────────────────────────────────────────┘
┌──────────────────┬──────────────────────┐
│                  │ COURSE GRID          │
│ FILTER SIDEBAR   │ ┌──────────────────┐ │
│ • Category       │ │ Course Card      │ │
│ • Level          │ │ - Title          │ │
│ • Price          │ │ - Instructor     │ │
│ • Rating         │ │ - Students: 1K   │ │
│ • Duration       │ │ - Rating: 4.8★   │ │
│                  │ │ [Enroll Button]  │ │
│                  │ └──────────────────┘ │
│                  │ [More course cards] │
└──────────────────┴──────────────────────┘
```

### Key Features
- Course cards display in grid (3-4 per row)
- Card shows: Image, title, instructor, price, rating, student count
- Filter sidebar on left
- Search bar at top
- Sorting options (popular, newest, rating)
- "Enroll" button on each card → course-details.html

### JavaScript Functions (courses.js)
- `loadCourses()` - Fetch and display courses
- `filterCourses()` - Apply filters
- `searchCourses()` - Search functionality
- `enrollCourse()` - Handle enrollment
- `sortCourses()` - Sort by rating, date, etc.

---

## 📖 PAGE 3: Course Details (course-details.html)
**Purpose:** Detailed course information and enrollment  
**Expected Route:** `/templates/front/course-details.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ COURSE HERO                             │
│ Large Banner Image                      │
│ Title | Instructor | Rating             │
└─────────────────────────────────────────┘
┌──────────────────┬──────────────────────┐
│ COURSE INFO      │ SIDEBAR              │
│ • Description    │ Price                │
│ • Learning Goals │ Enroll Button        │
│ • Prerequisites  │ Course Stats         │
│ • Modules/Weeks  │ Instructor Info      │
│ • Lessons        │ Student Reviews      │
│ • Resources      │ Share Options        │
│                  │ Wishlist Button      │
└──────────────────┴──────────────────────┘
│ REVIEWS SECTION                         │
└─────────────────────────────────────────┘
```

---

## 🎯 PAGE 4: Events (events.html)
**Purpose:** View and register for events/tournaments  
**Expected Route:** `/templates/front/events.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ CALENDAR / EVENT LIST / CARD VIEW       │
│ [Upcoming] [Past] [Filter Options]     │
├─────────────────────────────────────────┤
│ EVENT CARD                              │
│ Date/Time | Location | Participants    │
│ Title | Description | [Register]       │
├─────────────────────────────────────────┤
│ More event cards...                     │
└─────────────────────────────────────────┘
```

### JavaScript Functions (events.js)
- `loadEvents()` - Load event data
- `filterEvents()` - Filter by date, type, location
- `registerEvent()` - Register for event
- `displayCalendar()` - Calendar view
- `handleEventClick()` - Show event details

---

## 🎪 PAGE 5: Event Details (event-details.html)
**Purpose:** Detailed event information  
**Expected Route:** `/templates/front/event-details.html`

### Sections
- Event banner/header
- Event details: Date, time, location, description
- Agenda/Schedule
- Participant list
- Registration form
- FAQ section

---

## 💬 PAGE 6: Forum (forum.html)
**Purpose:** Discussion forum and community  
**Expected Route:** `/templates/front/forum.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ CREATE NEW THREAD | SEARCH | SORT       │
├─────────────────┬──────────────────────┐
│ CATEGORIES      │ THREAD LIST          │
│ • General       │ ┌──────────────────┐ │
│ • Java          │ │ Thread Title      │ │
│ • Python        │ │ Author | 12 Replies│
│ • JavaScript    │ │ Last reply: 2m ago │
│ • Help & Support│ │ Category: Python   │
│                 │ └──────────────────┘ │
│                 │ [More threads]       │
└─────────────────┴──────────────────────┘
```

### JavaScript Functions (forum.js)
- `loadThreads()` - Load forum threads
- `filterByCategory()` - Filter by topic
- `searchThreads()` - Search functionality
- `createThread()` - Show new thread form
- `replyToThread()` - Reply functionality

---

## 📝 PAGE 7: Forum Details (forum-details.html)
**Purpose:** View forum thread and replies  
**Expected Route:** `/templates/front/forum-details.html`

### Sections
- Original post/thread
- All replies (threaded or flat view)
- Reply form
- User info for each post
- Like/vote buttons

---

## ✏️ PAGE 8: Problems/Quests (problems.html)
**Purpose:** Coding challenges/problems  
**Expected Route:** `/templates/front/problems.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ DIFFICULTY FILTERS | SEARCH             │
├──────────────────┬──────────────────────┤
│ FILTER SIDEBAR   │ PROBLEM CARDS        │
│ • Easy (🟢)      │ ┌──────────────────┐ │
│ • Medium (🟡)    │ │ Problem Title    │ │
│ • Hard (🔴)      │ │ Difficulty: Hard │ │
│ • Status         │ │ Solved: 453      │ │
│ • Tags           │ │ [Solve Button]   │ │
│                  │ └──────────────────┘ │
│                  │ [More problems]     │
└──────────────────┴──────────────────────┘
```

### JavaScript Functions (problems.js)
- `loadProblems()` - Fetch problems
- `filterByDifficulty()` - Filter by level
- `searchProblems()` - Search functionality
- `solveProblem()` - Navigate to editor
- `trackProgress()` - Update progress tracker

---

## 💻 PAGE 9: Problem Details (problem-details.html)
**Purpose:** Code editor for solving problems  
**Expected Route:** `/templates/front/problem-details.html`

### Sections
```
┌──────────────────────┬──────────────────┐
│ PROBLEM STATEMENT    │ CODE EDITOR      │
│ • Description        │ Write code here  │
│ • Examples           │                  │
│ • Constraints        │                  │
│ • Test Cases         │ [Run Tests]      │
└──────────────────────┴──────────────────┘
```

---

## 🎯 PAGE 10: Projects (projects.html)
**Purpose:** Portfolio/community projects showcase  
**Expected Route:** `/templates/front/projects.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ PROJECT GRID / GALLERY                  │
│ [All] [Featured] [Filter]               │
├─────────────────────────────────────────┤
│ ┌──────────────┐ ┌──────────────┐      │
│ │ Project Card │ │ Project Card │ ...  │
│ │ - Thumbnail  │ │ - Thumbnail  │      │
│ │ - Title      │ │ - Title      │      │
│ │ - Author     │ │ - Author     │      │
│ │ - Tags       │ │ - Tags       │      │
│ │ - Views      │ │ - Views      │      │
│ └──────────────┘ └──────────────┘      │
└─────────────────────────────────────────┘
```

---

## 📊 PAGE 11: Project Details (project-details.html)
**Purpose:** Full project showcase  
**Expected Route:** `/templates/front/project-details.html`

### Sections
- Project hero/banner
- Full description
- Screenshots/gallery
- Technologies used
- Author/team info
- Live demo link
- GitHub repo link
- Comments section

---

## 👤 PAGE 12: Profile (profile.html)
**Purpose:** User profile page (own profile)  
**Expected Route:** `/templates/front/profile.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ PROFILE HEADER                          │
│ Avatar | Name | Level | XP | Bio        │
│ [Edit Profile] [Settings]               │
├──────────────────┬──────────────────────┤
│ STATS            │ ACTIVITY             │
│ • Level          │ • Recent Courses     │
│ • XP             │ • Completed Quests   │
│ • Achievements   │ • Forum Posts        │
│ • Streak         │ • Project Gallery    │
├──────────────────┴──────────────────────┤
│ COURSES ENROLLED / PROJECTS / ACTIVITY  │
└─────────────────────────────────────────┘
```

### JavaScript Functions (profile.js)
- `loadProfileData()` - Load user profile
- `editProfile()` - Show edit form
- `updateProfilePic()` - Change avatar
- `displayStats()` - Show user stats
- `trackActivity()` - Show activity log

---

## 👥 PAGE 13: Users/Community (users.html)
**Purpose:** Leaderboard / Community members  
**Expected Route:** `/templates/front/users.html`

### Page Sections
```
┌─────────────────────────────────────────┐
│ LEADERBOARD / SORT OPTIONS              │
│ [By Level] [By XP] [By Achievements]   │
├─────────────────────────────────────────┤
│ RANK | NAME | LEVEL | XP | BADGES      │
│ 1️⃣   | Alice | Lv 45 | 125K | ⭐⭐⭐    │
│ 2️⃣   | Bob   | Lv 42 | 112K | ⭐⭐    │
│ 3️⃣   | Carol | Lv 40 | 98K  | ⭐⭐    │
│ ... | ... | ... | ...  | ...            │
└─────────────────────────────────────────┘
```

### Features
- Sortable leaderboard
- Filter by region/country (optional)
- User cards with profile links
- Search users

---

## 🔐 PAGE 14: Sign In (sign-in.html)
**Purpose:** User login  
**Expected Route:** `/templates/front/sign-in.html`

### Form Layout
```
┌─────────────────────────────────────────┐
│                                         │
│        CodeQuest Sign In                │
│                                         │
│ ┌───────────────────────────────────┐  │
│ │ Email Address: [________________] │  │
│ └───────────────────────────────────┘  │
│                                         │
│ ┌───────────────────────────────────┐  │
│ │ Password:      [________________] │  │
│ └───────────────────────────────────┘  │
│                                         │
│ ☐ Remember me        [Forgot Password] │
│                                         │
│ ┌───────────────────────────────────┐  │
│ │        [Sign In Button]            │  │
│ └───────────────────────────────────┘  │
│                                         │
│ Don't have an account? [Sign Up]       │
│                                         │
└─────────────────────────────────────────┘
```

### JavaScript Functions (sign-in.js)
- `validateForm()` - Validate credentials
- `submitLogin()` - Handle login
- `togglePasswordVisibility()` - Show/hide password
- `handleForgotPassword()` - Password recovery
- `redirectToSignUp()` - Navigation

---

## 📝 PAGE 15: Sign Up (sign-up.html)
**Purpose:** New user registration  
**Expected Route:** `/templates/front/sign-up.html`

### Form Layout
```
┌─────────────────────────────────────────┐
│        CodeQuest Sign Up                │
│                                         │
│ ┌───────────────────────────────────┐  │
│ │ Username:  [________________]      │  │
│ └───────────────────────────────────┘  │
│                                         │
│ ┌───────────────────────────────────┐  │
│ │ Email:     [________________]      │  │
│ └───────────────────────────────────┘  │
│                                         │
│ ┌───────────────────────────────────┐  │
│ │ Password:  [________________]      │  │
│ └───────────────────────────────────┘  │
│                                         │
│ ┌───────────────────────────────────┐  │
│ │ Confirm:   [________________]      │  │
│ └───────────────────────────────────┘  │
│                                         │
│ ☐ I agree to Terms & Conditions        │
│ ☐ Subscribe to newsletter              │
│                                         │
│ ┌───────────────────────────────────┐  │
│ │        [Create Account]            │  │
│ └───────────────────────────────────┘  │
│                                         │
│ Already have account? [Sign In]        │
│                                         │
└─────────────────────────────────────────┘
```

### JavaScript Functions (sign-up.js)
- `validateForm()` - Validate all fields
- `checkPasswordMatch()` - Confirm password match
- `checkEmailAvailability()` - AJAX check
- `submitSignUp()` - Register user
- `showSuccessMessage()` - Confirmation

---

## 🔗 NAVIGATION MENU (All Pages)

Every page should have this navigation bar:

```
┌─────────────────────────────────────────────────────┐
│ CodeQuest  │📖 Adventures │📚 Courses │✏️ Quests    │
│            │🎯 Missions   │🏆 Tournaments          │
│            │🤝 Guild      │⚔️ Arena                │
│                                    [Log In] [Sign Up]│
└─────────────────────────────────────────────────────┘
```

**Navigation always links to:**
- Home (click logo)
- Courses
- Events/Tournaments
- Forum/Guild
- Problems/Quests
- Projects
- Profile (after login)
- Account menu

---

## 📁 ASSETS STRUCTURE

```
assets/
├── css/
│   ├── style.css (MAIN - used by all pages)
│   ├── home.css
│   ├── courses.css
│   ├── course-details.css
│   ├── events.css
│   ├── event-details.css
│   ├── forum.css
│   ├── forum-details.css
│   ├── problems.css
│   ├── problem-details.css
│   ├── projects.css
│   ├── project-details.css
│   ├── profile.css
│   ├── users.css
│   ├── sign-in.css
│   ├── sign-up.css
│   └── dashboard.css
│
├── js/
│   ├── home.js
│   ├── courses.js
│   ├── course-details.js
│   ├── events.js
│   ├── event-details.js
│   ├── forum.js
│   ├── forum-details.js
│   ├── problems.js
│   ├── problem-details.js
│   ├── projects.js
│   ├── project-details.js
│   ├── profile.js
│   ├── users.js (leaderboard)
│   ├── sign-in.js
│   ├── sign-up.js
│   └── dashboard.js
│
├── images/ (Logos, banners, course thumbnails)
├── icons/ (Small icon images)
└── backgrounds/ (Background images, GIFs)
```

---

## ✅ QUICK CHECK - Each Page Should Have

### HTML File
- [ ] DOCTYPE declaration
- [ ] Meta tags (charset, viewport)
- [ ] Title tag (page name)
- [ ] Link to style.css
- [ ] Link to page-specific CSS (if exists)
- [ ] Navigation bar
- [ ] Main content
- [ ] Footer (if exists)
- [ ] Script tags for JavaScript

### CSS File (per page)
- [ ] Page-specific styling
- [ ] Color scheme matches brand
- [ ] Responsive design (media queries)
- [ ] Proper spacing and alignment

### JavaScript File (per page)
- [ ] Document ready handler
- [ ] Event listeners setup
- [ ] Main functionality for page
- [ ] Error handling
- [ ] Console logging for debugging

---

## 🎯 Testing Each Page

For each page, verify:
1. ✅ Page loads without errors
2. ✅ All CSS styles apply
3. ✅ All JavaScript functions work
4. ✅ Forms validate input
5. ✅ Buttons are clickable
6. ✅ Navigation works
7. ✅ Responsive on mobile
8. ✅ No console errors (F12 → Console)

**See FRONTEND_TESTING_GUIDE.md for detailed testing steps!**

