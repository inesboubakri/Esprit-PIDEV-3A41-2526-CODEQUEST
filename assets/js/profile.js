// Profile Details Page JavaScript
// Handles tab switching and interactive features

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tab functionality
    initTabs();

    // Initialize navbar scroll effect
    initNavbarScrollEffect();

    // Initialize profile interactions
    initProfileInteractions();
});

// Tab functionality
function initTabs() {
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    tabBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const tabId = this.getAttribute('data-tab');

            // Remove active class from all buttons and contents
            tabBtns.forEach(b => b.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));

            // Add active class to clicked button and corresponding content
            this.classList.add('active');
            document.getElementById(tabId).classList.add('active');
        });
    });
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

// Profile interactions
function initProfileInteractions() {
    // Profile action buttons
    const messageBtn = document.querySelector('.btn-message');
    const followBtn = document.querySelector('.btn-follow');
    const shareBtn = document.querySelector('.btn-share');

    // Message button
    if (messageBtn) {
        messageBtn.addEventListener('click', function() {
            showNotification('Messaging feature coming soon!', 'info');
        });
    }

    // Follow button
    if (followBtn) {
        followBtn.addEventListener('click', function() {
            const isFollowing = this.textContent.includes('Following');
            if (isFollowing) {
                this.textContent = '👥 Follow';
                this.style.background = '#2196F3';
                showNotification('Unfollowed CodeMaster_99', 'info');
            } else {
                this.textContent = '✅ Following';
                this.style.background = '#4CAF50';
                showNotification('Now following CodeMaster_99!', 'success');
            }
        });
    }

    // Share button
    if (shareBtn) {
        shareBtn.addEventListener('click', function() {
            const profileUrl = window.location.href;
            navigator.clipboard.writeText(profileUrl).then(() => {
                showNotification('Profile link copied to clipboard!', 'success');
            }).catch(() => {
                showNotification('Failed to copy profile link', 'error');
            });
        });
    }

    // Project links
    const projectLinks = document.querySelectorAll('.project-link');
    projectLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const linkType = this.textContent.includes('Live Demo') ? 'Live Demo' : 'GitHub';
            showNotification(`Opening ${linkType}...`, 'info');
        });
    });

    // Profile links
    const profileLinks = document.querySelectorAll('.profile-link');
    profileLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const linkType = this.textContent.includes('github.com') ? 'GitHub' :
                           this.textContent.includes('linkedin.com') ? 'LinkedIn' : 'Website';
            showNotification(`Opening ${linkType}...`, 'info');
        });
    });

    // Course items hover effects
    const courseItems = document.querySelectorAll('.course-item');
    courseItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });

        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // Event items hover effects
    const eventItems = document.querySelectorAll('.event-item');
    eventItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });

        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // Project items hover effects
    const projectItems = document.querySelectorAll('.project-item');
    projectItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });

        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // Skill badges hover effects
    const skillBadges = document.querySelectorAll('.skill-badge');
    skillBadges.forEach(badge => {
        badge.addEventListener('mouseenter', function() {
            this.style.transform = 'scale(1.05)';
        });

        badge.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    });

    // Achievement items hover effects
    const achievementItems = document.querySelectorAll('.achievement-item');
    achievementItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.opacity = '0.8';
        });

        item.addEventListener('mouseleave', function() {
            this.style.opacity = '1';
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
    notification.style.fontFamily = "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif";
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

// Add CSS for animations
const additionalStyles = `
    .notification {
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
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

    .course-item, .event-item, .project-item {
        transition: transform 0.3s ease;
    }

    .skill-badge {
        transition: transform 0.3s ease;
    }

    .achievement-item {
        transition: opacity 0.3s ease;
    }
`;

// Apply additional styles
const styleElement = document.createElement('style');
styleElement.textContent = additionalStyles;
document.head.appendChild(styleElement);

// Keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Number keys 1-6 for quick tab navigation
    if (e.key >= '1' && e.key <= '6') {
        const tabBtns = document.querySelectorAll('.tab-btn');
        const index = parseInt(e.key) - 1;
        if (tabBtns[index]) {
            tabBtns[index].click();
        }
    }

    // Escape key to close any modals or notifications
    if (e.key === 'Escape') {
        const notifications = document.querySelectorAll('.notification');
        notifications.forEach(notification => {
            if (notification.parentNode) {
                notification.parentNode.removeChild(notification);
            }
        });
    }
});