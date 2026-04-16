// Forums Page JavaScript
// Handles forum filtering, animations, and interactive features

document.addEventListener('DOMContentLoaded', function() {
    // Initialize forum filtering
    initForumFiltering();

    // Initialize navbar scroll effect
    initNavbarScrollEffect();

    // Initialize page animations
    initPageAnimations();
});

// Forum filtering functionality
function initForumFiltering() {
    const filterCheckboxes = document.querySelectorAll('.filter-checkbox');
    const clearFiltersBtn = document.querySelector('.btn-clear-filters');
    const forumCards = document.querySelectorAll('.forum-card');

    // Add event listeners to filter checkboxes
    filterCheckboxes.forEach(checkbox => {
        checkbox.addEventListener('change', filterForums);
    });

    // Add event listener to clear filters button
    clearFiltersBtn.addEventListener('click', clearAllFilters);

    // Set CSS custom properties for animation delays
    forumCards.forEach((card, index) => {
        card.style.setProperty('--card-index', index);
    });

    function filterForums() {
        const activeFilters = getActiveFilters();

        forumCards.forEach(card => {
            const cardData = {
                category: card.dataset.category,
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
            category: [],
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
        const hasActiveFilters = activeFilters.category.length > 0 || activeFilters.language.length > 0 || activeFilters.activity.length > 0;
        if (!hasActiveFilters) {
            return true;
        }

        // Check category filter
        if (activeFilters.category.length > 0) {
            if (!activeFilters.category.includes(cardData.category)) {
                return false;
            }
        }

        // Check language filter
        if (activeFilters.language.length > 0) {
            if (!activeFilters.language.includes(cardData.language)) {
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
        filterForums();
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

    // Observe forum cards
    const forumCards = document.querySelectorAll('.forum-card');
    forumCards.forEach(card => {
        card.style.animationPlayState = 'paused';
        observer.observe(card);
    });

    // Add hover effects to forum cards
    forumCards.forEach(card => {
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

// Add CSS for animations and effects
const additionalStyles = `
    .forum-card.hidden {
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

    .filter-sidebar {
        animation: slideInLeft 0.8s ease-out;
    }

    .forums-header {
        animation: fadeInUp 0.8s ease-out 0.2s both;
    }
`;

const styleElement = document.createElement('style');
styleElement.textContent = additionalStyles;
document.head.appendChild(styleElement);