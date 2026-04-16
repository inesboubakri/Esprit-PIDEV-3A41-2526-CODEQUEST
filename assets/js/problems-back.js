// Problems Management JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Dark/Light mode toggle
    const themeToggle = document.getElementById('themeToggle');
    const themeIcon = themeToggle.querySelector('.theme-icon');

    // Check for saved theme preference or default to light mode
    const currentTheme = localStorage.getItem('theme') || 'light';
    document.body.classList.toggle('dark-mode', currentTheme === 'dark');
    updateThemeIcon();

    themeToggle.addEventListener('click', function() {
        document.body.classList.toggle('dark-mode');
        const theme = document.body.classList.contains('dark-mode') ? 'dark' : 'light';
        localStorage.setItem('theme', theme);
        updateThemeIcon();
    });

    function updateThemeIcon() {
        const isDark = document.body.classList.contains('dark-mode');
        themeIcon.textContent = isDark ? '☀️' : '🌙';
    }

    // Admin profile click
    const adminProfile = document.querySelector('.admin-profile');
    adminProfile.addEventListener('click', function() {
        alert('Admin Profile - This would open admin settings/profile page');
    });

    // Search functionality
    const searchInput = document.querySelector('.search-bar input');
    const searchButton = document.querySelector('.search-bar button');

    searchButton.addEventListener('click', function() {
        const query = searchInput.value.trim();
        if (query) {
            alert(`Searching for problems: ${query}`);
            // Here you would implement actual search logic
        }
    });

    // Allow search on Enter key
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchButton.click();
        }
    });

    // Filter buttons functionality
    const filterButtons = document.querySelectorAll('.filter-btn');
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active class from all buttons
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Add active class to clicked button
            this.classList.add('active');

            const filter = this.dataset.filter;
            filterProblems(filter);
        });
    });

    function filterProblems(filter) {
        const rows = document.querySelectorAll('.users-table tbody tr');

        rows.forEach(row => {
            const statusBadge = row.querySelector('.status-badge');
            const levelBadge = row.querySelector('.level-badge');

            let show = false;

            switch(filter) {
                case 'all':
                    show = true;
                    break;
                case 'easy':
                    show = levelBadge.textContent.toLowerCase() === 'easy';
                    break;
                case 'medium':
                    show = levelBadge.textContent.toLowerCase() === 'medium';
                    break;
                case 'hard':
                    show = levelBadge.textContent.toLowerCase() === 'hard';
                    break;
                case 'solved':
                    show = statusBadge.classList.contains('active'); // Assuming active means solved
                    break;
                case 'unsolved':
                    show = statusBadge.classList.contains('inactive'); // Assuming inactive means unsolved
                    break;
            }

            row.style.display = show ? '' : 'none';
        });
    }

    // Add problem button
    const addUserBtn = document.querySelector('.add-user-btn');
    addUserBtn.addEventListener('click', function() {
        alert('Add New Problem - This would open a problem creation modal/form');
        // Here you would open a modal or navigate to add problem page
    });

    // Stats button
    const statsBtn = document.querySelector('.stats-btn');
    statsBtn.addEventListener('click', function() {
        alert('View Stats - This would open detailed statistics and analytics page');
        // Here you would navigate to stats page or open stats modal
    });

    // Close dropdowns when clicking outside
    document.addEventListener('click', function(e) {
        if (!e.target.closest('.actions-dropdown')) {
            // Close all dropdowns
            document.querySelectorAll('.dropdown-menu').forEach(menu => {
                menu.style.opacity = '0';
                menu.style.visibility = 'hidden';
                menu.style.transform = 'translateY(-10px)';
            });
        }
    });
});

// Problem action functions
function viewProblemDetails(problemId) {
    alert(`View Details for Problem ID: ${problemId}\nThis would open problem details modal/page`);
}

function modifyProblem(problemId) {
    alert(`Modify Problem ID: ${problemId}\nThis would open problem edit modal/form`);
}

function deleteProblem(problemId) {
    if (confirm(`Are you sure you want to delete Problem ID: ${problemId}?`)) {
        alert(`Problem ${problemId} deleted successfully`);
        // Here you would implement actual delete logic
    }
}