// Dashboard JavaScript
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
    
    // Search functionality
    const searchInput = document.querySelector('.search-bar input');
    const searchButton = document.querySelector('.search-bar button');
    
    searchButton.addEventListener('click', function() {
        const query = searchInput.value.trim();
        if (query) {
            alert(`Searching for: ${query}`);
            // Here you would implement actual search logic
        }
    });
    
    // Allow search on Enter key
    searchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchButton.click();
        }
    });
    
    // Sidebar navigation active state
    const sidebarLinks = document.querySelectorAll('.sidebar nav ul li a');
    
    sidebarLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // Remove active class from all links
            sidebarLinks.forEach(l => l.classList.remove('active'));
            // Add active class to clicked link
            this.classList.add('active');
            
            // Here you would implement navigation logic
            e.preventDefault();
            alert(`Navigating to: ${this.textContent}`);
        });
    });
    
    // Simulate real-time updates (for demo purposes)
    setInterval(() => {
        // Randomly update stats (in a real app, this would come from an API)
        const stats = document.querySelectorAll('.stat-number');
        stats.forEach(stat => {
            const currentValue = parseInt(stat.textContent.replace(',', ''));
            const change = Math.floor(Math.random() * 20) - 10; // -10 to +10
            const newValue = Math.max(0, currentValue + change);
            stat.textContent = newValue.toLocaleString();
        });
    }, 30000); // Update every 30 seconds
    
    // Activity feed animation
    const activityItems = document.querySelectorAll('.activity-item');
    activityItems.forEach((item, index) => {
        item.style.animationDelay = `${index * 0.1}s`;
        item.classList.add('fade-in');
        
        // Add click functionality for activity items
        item.addEventListener('click', function() {
            const activityText = this.querySelector('p').textContent;
            alert(`Activity Details:\n${activityText}\n\n(This would navigate to the relevant page)`);
        });
    });
    
    // Quick actions functionality
    const actionButtons = document.querySelectorAll('.action-btn');
    actionButtons.forEach(button => {
        button.addEventListener('click', function() {
            const action = this.textContent;
            alert(`Action: ${action}\n(This would open the corresponding management page)`);
        });
    });
    
    // Animate charts on scroll
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('animate-chart');
            }
        });
    }, observerOptions);
    
    // Observe chart elements
    document.querySelectorAll('.chart-bar .bar-fill, .pie-segment, .doughnut-segment').forEach(el => {
        observer.observe(el);
    });
});

// Add some CSS animation via JS
const style = document.createElement('style');
style.textContent = `
    .fade-in {
        animation: fadeIn 0.5s ease-in-out forwards;
        opacity: 0;
    }
    
    @keyframes fadeIn {
        to {
            opacity: 1;
        }
    }
    
    .animate-chart {
        animation: chartAnimation 1.5s ease-out forwards;
    }
    
    @keyframes chartAnimation {
        from {
            transform: scaleY(0);
            transform-origin: bottom;
        }
        to {
            transform: scaleY(1);
            transform-origin: bottom;
        }
    }
    
    .pie-segment, .doughnut-segment {
        transform-origin: center;
        animation: pieAnimation 2s ease-out forwards;
    }
    
    @keyframes pieAnimation {
        from {
            stroke-dasharray: 0 251.2;
        }
        to {
            stroke-dasharray: var(--dash-array);
        }
    }
`;
document.head.appendChild(style);