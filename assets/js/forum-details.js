// Forum Details Page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    // Navbar scroll effect
    const navbar = document.querySelector('nav');
    const logo = document.querySelector('.logo');

    window.addEventListener('scroll', function() {
        if (window.scrollY > 50) {
            navbar.classList.add('navbar-scrolled');
        } else {
            navbar.classList.remove('navbar-scrolled');
        }
    });

    // Post filtering functionality
    const filterButtons = document.querySelectorAll('.filter-btn');
    const postCards = document.querySelectorAll('.post-card');

    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Remove active class from all buttons
            filterButtons.forEach(btn => btn.classList.remove('active'));
            // Add active class to clicked button
            this.classList.add('active');

            const filterType = this.textContent.toLowerCase();

            postCards.forEach(card => {
                const postType = card.querySelector('.post-type').textContent.toLowerCase();

                if (filterType === 'all' || postType.includes(filterType)) {
                    card.classList.remove('hidden');
                } else {
                    card.classList.add('hidden');
                }
            });
        });
    });

    // Reaction functionality
    const reactionButtons = document.querySelectorAll('.reaction-btn');

    reactionButtons.forEach(button => {
        button.addEventListener('click', function() {
            const reactionType = this.querySelector('span').textContent;
            const countElement = this.querySelector('.reaction-count');

            // Toggle active state
            if (this.classList.contains('active')) {
                this.classList.remove('active');
                // Decrease count
                let count = parseInt(countElement.textContent);
                countElement.textContent = count - 1;
            } else {
                this.classList.add('active');
                // Increase count
                let count = parseInt(countElement.textContent);
                countElement.textContent = count + 1;
            }

            // Add visual feedback
            this.style.transform = 'scale(1.1)';
            setTimeout(() => {
                this.style.transform = 'scale(1)';
            }, 150);
        });
    });

    // Save post functionality
    const saveButtons = document.querySelectorAll('.save-post');

    saveButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (this.classList.contains('saved')) {
                this.classList.remove('saved');
                this.innerHTML = '☆';
                this.style.color = '#999';
            } else {
                this.classList.add('saved');
                this.innerHTML = '★';
                this.style.color = '#FF6B4A';
            }

            // Add visual feedback
            this.style.transform = 'scale(1.2)';
            setTimeout(() => {
                this.style.transform = 'scale(1)';
            }, 150);
        });
    });

    // Join guild functionality
    const joinGuildBtn = document.querySelector('.btn-join-guild');

    if (joinGuildBtn) {
        joinGuildBtn.addEventListener('click', function() {
            if (this.textContent === 'Join Guild') {
                this.textContent = 'Leave Guild';
                this.style.background = 'linear-gradient(135deg, #4CAF50, #45a049)';
                this.style.boxShadow = '0 4px 20px rgba(76, 175, 80, 0.3)';

                // Show success message
                showNotification('Successfully joined the guild!', 'success');
            } else {
                this.textContent = 'Join Guild';
                this.style.background = 'linear-gradient(135deg, #FF6B4A, #FF8A65)';
                this.style.boxShadow = '0 4px 20px rgba(255, 107, 74, 0.3)';

                // Show message
                showNotification('Left the guild', 'info');
            }
        });
    }

    // Create post functionality
    const createPostBtn = document.querySelector('.btn-create-post');

    if (createPostBtn) {
        createPostBtn.addEventListener('click', function() {
            // Simple modal or redirect to create post page
            showNotification('Create post functionality coming soon!', 'info');
        });
    }

    // Post click functionality (expand/collapse)
    postCards.forEach(card => {
        const postContent = card.querySelector('.post-content');

        card.addEventListener('click', function(e) {
            // Don't trigger if clicking on buttons
            if (e.target.closest('.reaction-btn') || e.target.closest('.save-post')) {
                return;
            }

            // Toggle expanded state
            this.classList.toggle('expanded');

            if (this.classList.contains('expanded')) {
                postContent.style.maxHeight = 'none';
            } else {
                postContent.style.maxHeight = '100px';
                postContent.style.overflow = 'hidden';
            }
        });
    });

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

    // Add notification animations to CSS dynamically
    const style = document.createElement('style');
    style.textContent = `
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
    `;
    document.head.appendChild(style);

    // Smooth scrolling for anchor links
    const anchorLinks = document.querySelectorAll('a[href^="#"]');

    anchorLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            const targetId = this.getAttribute('href').substring(1);
            const targetElement = document.getElementById(targetId);

            if (targetElement) {
                targetElement.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Initialize post content max height for expandable posts
    postCards.forEach(card => {
        const postContent = card.querySelector('.post-content');
        if (postContent.scrollHeight > 100) {
            postContent.style.maxHeight = '100px';
            postContent.style.overflow = 'hidden';
        }
    });

    // Add loading animation for post cards
    postCards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';

        setTimeout(() => {
            card.style.transition = 'all 0.5s ease-out';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, index * 100);
    });

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl/Cmd + K to focus search (if search exists)
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            // Focus on first filter button
            if (filterButtons.length > 0) {
                filterButtons[0].focus();
            }
        }

        // Escape to close any modals (future feature)
        if (e.key === 'Escape') {
            // Close any open modals or expanded content
            postCards.forEach(card => {
                card.classList.remove('expanded');
                const postContent = card.querySelector('.post-content');
                if (postContent.scrollHeight > 100) {
                    postContent.style.maxHeight = '100px';
                    postContent.style.overflow = 'hidden';
                }
            });
        }
    });

    // Intersection Observer for lazy loading content
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px 50px 0px'
    };

    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('visible');
            }
        });
    }, observerOptions);

    // Observe post cards for animation
    postCards.forEach(card => {
        observer.observe(card);
    });

    // Add visible class styles dynamically
    const visibilityStyle = document.createElement('style');
    visibilityStyle.textContent = `
        .post-card {
            opacity: 0;
            transform: translateY(30px);
            transition: all 0.6s ease-out;
        }

        .post-card.visible {
            opacity: 1;
            transform: translateY(0);
        }
    `;
    document.head.appendChild(visibilityStyle);
});