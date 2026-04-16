// Users Page JavaScript
// Handles user filtering, animations, and interactive features

document.addEventListener('DOMContentLoaded', function() {
    // Initialize user filtering
    initUserFiltering();

    // Initialize navbar scroll effect
    initNavbarScrollEffect();

    // Initialize user interactions
    initUserInteractions();

    // Initialize page animations
    initPageAnimations();
});

// User filtering functionality
function initUserFiltering() {
    const filterCheckboxes = document.querySelectorAll('.filter-checkbox');
    const clearFiltersBtn = document.querySelector('.btn-clear-filters');
    const userCards = document.querySelectorAll('.user-card');

    // Add event listeners to filter checkboxes
    filterCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', filterUsers);
    });

    // Add event listener to clear filters button
    clearFiltersBtn.addEventListener('click', clearAllFilters);

    // Set CSS custom properties for animation delays
    userCards.forEach((card, index) => {
        card.style.setProperty('--card-index', index);
    });

    function filterUsers() {
        const activeFilters = getActiveFilters();

        userCards.forEach(card => {
            const cardData = {
                level: card.dataset.level,
                language: card.dataset.language,
                activity: card.dataset.activity
            };

            const shouldShow = shouldShowCard(cardData, activeFilters);
            toggleCardVisibility(card, shouldShow);
        });

        updateFilterCounts();
    }

    function getActiveFilters() {
        const filters = {
            level: [],
            language: [],
            activity: []
        };

        filterCheckboxes.forEach(checkbox => {
            if (checkbox.checked) {
                const filterType = checkbox.dataset.filter;
                const filterValue = checkbox.value;
                filters[filterType].push(filterValue);
            }
        });

        return filters;
    }

    function shouldShowCard(cardData, activeFilters) {
        // If no filters are active, show all cards
        const hasActiveFilters = activeFilters.level.length > 0 || activeFilters.language.length > 0 || activeFilters.activity.length > 0;
        if (!hasActiveFilters) {
            return true;
        }

        // Check level filter
        if (activeFilters.level.length > 0) {
            if (!activeFilters.level.includes(cardData.level)) {
                return false;
            }
        }

        // Check language filter
        if (activeFilters.language.length > 0) {
            const cardLanguages = cardData.language ? cardData.language.split(' ') : [];
            const hasMatchingLanguage = activeFilters.language.some(lang =>
                cardLanguages.includes(lang)
            );
            if (!hasMatchingLanguage) {
                return false;
            }
        }

        // Check activity filter
        if (activeFilters.activity.length > 0) {
            if (!activeFilters.activity.includes(cardData.activity)) {
                return false;
            }
        }

        return true;
    }

    function toggleCardVisibility(card, shouldShow) {
        if (shouldShow) {
            card.classList.remove('hidden');
            setTimeout(() => {
                card.style.opacity = '1';
                card.style.transform = 'scale(1)';
            }, 10);
        } else {
            card.style.opacity = '0';
            card.style.transform = 'scale(0.8)';
            setTimeout(() => {
                card.classList.add('hidden');
            }, 300);
        }
    }

    function clearAllFilters() {
        filterCheckboxes.forEach(checkbox => {
            checkbox.checked = false;
        });
        filterUsers();
    }

    function updateFilterCounts() {
        // Update visual feedback for active filters
        const filterGroups = document.querySelectorAll('.filter-group');

        filterGroups.forEach(group => {
            const checkboxes = group.querySelectorAll('.filter-checkbox');
            const activeCount = Array.from(checkboxes).filter(cb => cb.checked).length;

            if (activeCount > 0) {
                group.style.background = 'rgba(255, 107, 74, 0.05)';
                group.style.borderRadius = '12px';
                group.style.padding = '1rem';
                group.style.margin = '-1rem';
                group.style.marginBottom = '1rem';
            } else {
                group.style.background = 'none';
                group.style.padding = '0';
                group.style.margin = '0';
                group.style.marginBottom = '2rem';
            }
        });
    }
}

// User interactions (follow buttons, etc.)
function initUserInteractions() {
    const followButtons = document.querySelectorAll('.follow-btn');
    const viewProfileButtons = document.querySelectorAll('.btn-view-profile');
    const messageButtons = document.querySelectorAll('.btn-message');

    // Handle follow button clicks
    followButtons.forEach(button => {
        button.addEventListener('click', function() {
            toggleFollow(this);
        });
    });

    // Handle view profile button clicks
    viewProfileButtons.forEach(button => {
        button.addEventListener('click', function() {
            const userName = this.closest('.user-card').querySelector('h3').textContent;
            showNotification(`Viewing ${userName}'s profile...`, 'info');
        });
    });

    // Handle message button clicks
    messageButtons.forEach(button => {
        button.addEventListener('click', function() {
            const userName = this.closest('.user-card').querySelector('h3').textContent;
            showNotification(`Opening chat with ${userName}...`, 'info');
        });
    });

    function toggleFollow(button) {
        const userCard = button.closest('.user-card');
        const userName = userCard.querySelector('h3').textContent;

        if (button.classList.contains('following')) {
            button.classList.remove('following');
            button.textContent = 'Follow';
            showNotification(`Unfollowed ${userName}`, 'info');
        } else {
            button.classList.add('following');
            button.textContent = 'Following';
            showNotification(`Now following ${userName}!`, 'success');
        }
    }
}

// Navbar scroll effect functionality
function initNavbarScrollEffect() {
    const navbar = document.querySelector('nav');
    const navLinks = document.querySelectorAll('.nav-links a');
    const logo = document.querySelector('.logo');
    const navContainer = document.querySelector('.nav-container');

    let lastScrollY = window.scrollY;

    function updateNavbar() {
        const currentScrollY = window.scrollY;

        // Add scrolled class when scrolled down
        if (currentScrollY > 100) {
            navbar.classList.add('navbar-scrolled');
        } else {
            navbar.classList.remove('navbar-scrolled');
        }

        lastScrollY = currentScrollY;
    }

    // Initial check
    updateNavbar();

    // Update on scroll
    window.addEventListener('scroll', updateNavbar);

    // Also update on resize
    window.addEventListener('resize', updateNavbar);
}

// Page animations
function initPageAnimations() {
    // Add intersection observer for animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.animationPlayState = 'running';
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);

    // Observe user cards
    const userCards = document.querySelectorAll('.user-card');
    userCards.forEach(card => {
        card.style.animationPlayState = 'paused';
        observer.observe(card);
    });

    // Add hover effects to user cards
    userCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-10px) scale(1.02)';
        });

        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) scale(1)';
        });
    });

    // Add focus effects to filter options
    const filterOptions = document.querySelectorAll('.filter-option');
    filterOptions.forEach(option => {
        option.addEventListener('mouseenter', function() {
            this.style.background = 'rgba(255, 107, 74, 0.1)';
        });

        option.addEventListener('mouseleave', function() {
            if (!this.querySelector('input').checked) {
                this.style.background = 'none';
            }
        });
    });
}

// Notification system
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());

    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;

    // Style the notification
    notification.style.position = 'fixed';
    notification.style.top = '100px';
    notification.style.right = '20px';
    notification.style.padding = '1rem 1.5rem';
    notification.style.borderRadius = '8px';
    notification.style.boxShadow = '0 4px 20px rgba(0, 0, 0, 0.15)';
    notification.style.zIndex = '10000';
    notification.style.fontWeight = '500';
    notification.style.animation = 'slideIn 0.3s ease-out';

    // Color based on type
    if (type === 'success') {
        notification.style.background = '#4CAF50';
        notification.style.color = 'white';
    } else if (type === 'error') {
        notification.style.background = '#f44336';
        notification.style.color = 'white';
    } else {
        notification.style.background = '#FF6B4A';
        notification.style.color = 'white';
    }

    // Add to page
    document.body.appendChild(notification);

    // Auto remove after 3 seconds
    setTimeout(() => {
        notification.style.animation = 'slideOut 0.3s ease-out';
        setTimeout(() => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        }, 300);
    }, 3000);
}

// Add CSS for animations and effects
const additionalStyles = `
    .user-card.hidden {
        display: none !important;
    }

    .filter-group.active {
        background: rgba(255, 107, 74, 0.05);
        border-radius: 12px;
        padding: 1rem;
        margin: -1rem;
        margin-bottom: 1rem;
        transition: all 0.3s ease;
    }

    @keyframes fadeInUp {
        from {
            opacity: 0;
            transform: translateY(30px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }

    @keyframes slideInLeft {
        from {
            opacity: 0;
            transform: translateX(-30px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }

    @keyframes slideIn {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }

    @keyframes slideOut {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }

    .filter-sidebar {
        animation: slideInLeft 0.8s ease-out;
    }

    .users-header {
        animation: fadeInUp 0.8s ease-out 0.2s both;
    }
`;

const styleElement = document.createElement('style');
styleElement.textContent = additionalStyles;
document.head.appendChild(styleElement);