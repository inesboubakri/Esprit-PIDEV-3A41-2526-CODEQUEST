// CodeQuest Home Page JavaScript
// Handles smooth scrolling and typing animations

document.addEventListener('DOMContentLoaded', function() {
    // Smooth scrolling for navigation links
    initSmoothScrolling();

    // Initialize typing animations
    initTypingAnimations();

    // Initialize stat counter animations
    initStatCounters();

    // Initialize card animations
    initCardAnimations();

    // Initialize navbar scroll effects
    initNavbarScrollEffect();
});

// Smooth scrolling functionality
function initSmoothScrolling() {
    const navLinks = document.querySelectorAll('.nav-links a');

    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            const targetId = this.getAttribute('href').substring(1);
            const targetSection = document.getElementById(targetId);

            if (targetSection) {
                const headerOffset = 80; // Account for fixed navbar height
                const elementPosition = targetSection.offsetTop;
                const offsetPosition = elementPosition - headerOffset;

                window.scrollTo({
                    top: offsetPosition,
                    behavior: 'smooth'
                });
            }
        });
    });
}

// Typing animation functionality
function initTypingAnimations() {
    // Main hero title typing animation (endless loop)
    const heroTitle = document.querySelector('.hero h1');
    if (heroTitle) {
        const heroText = "Your Coding Adventure Awaits";
        createTypingAnimation(heroTitle, heroText, true);
    }

    // Hero subtitle typing animation
    const heroSubtitle = document.querySelector('.hero p');
    if (heroSubtitle) {
        const subtitleText = "Embark on an epic adventure to master programming. Complete quests, earn XP, and improve your coding skills!";
        createScrollTypingAnimation(heroSubtitle, subtitleText);
    }

    // Section titles typing animation
    const sectionTitles = document.querySelectorAll('h2');
    sectionTitles.forEach(title => {
        if (!title.closest('.hero')) { // Skip hero title
            const titleText = title.textContent.trim();
            createScrollTypingAnimation(title, titleText);
        }
    });

    // Section descriptions typing animation
    const sectionDescriptions = document.querySelectorAll('section > p:not(.hero p)');
    sectionDescriptions.forEach(desc => {
        const descText = desc.textContent.trim();
        createScrollTypingAnimation(desc, descText);
    });
}

// Create endless typing animation for main title
function createTypingAnimation(element, text, loop = false) {
    let index = 0;
    let isDeleting = false;
    const typingSpeed = 100;
    const deletingSpeed = 50;
    const pauseTime = 2000;

    function type() {
        const currentText = text.substring(0, index);

        element.textContent = currentText;

        if (!isDeleting) {
            index++;
            if (index > text.length) {
                if (loop) {
                    isDeleting = true;
                    setTimeout(type, pauseTime);
                }
                return;
            }
            setTimeout(type, typingSpeed);
        } else {
            index--;
            if (index < 0) {
                isDeleting = false;
                setTimeout(type, typingSpeed);
            } else {
                setTimeout(type, deletingSpeed);
            }
        }
    }

    // Start the animation
    setTimeout(type, 1000);
}

// Create scroll-triggered typing animation
function createScrollTypingAnimation(element, text) {
    let hasAnimated = false;
    const originalText = text;

    function checkScroll() {
        if (hasAnimated) return;

        const elementTop = element.getBoundingClientRect().top;
        const windowHeight = window.innerHeight;

        if (elementTop < windowHeight * 0.8) {
            hasAnimated = true;
            animateText();
        }
    }

    function animateText() {
        element.textContent = '';
        let index = 0;
        const typingSpeed = 50;

        function type() {
            if (index < originalText.length) {
                element.textContent += originalText.charAt(index);
                index++;
                setTimeout(type, typingSpeed);
            }
        }

        type();
    }

    // Check on scroll
    window.addEventListener('scroll', checkScroll);
    // Check on load
    checkScroll();
}

// Stat counter animation functionality
function initStatCounters() {
    const statElements = document.querySelectorAll('.community-stats h3[data-target]');

    statElements.forEach(element => {
        createScrollCounterAnimation(element);
    });
}

// Create scroll-triggered counter animation
function createScrollCounterAnimation(element) {
    let hasAnimated = false;
    const target = parseInt(element.getAttribute('data-target'));
    const isPercentage = element.nextElementSibling.textContent.includes('Solved'); // Check if it's a percentage

    function checkScroll() {
        if (hasAnimated) return;

        const elementTop = element.getBoundingClientRect().top;
        const windowHeight = window.innerHeight;

        if (elementTop < windowHeight * 0.8) {
            hasAnimated = true;
            animateCounter(element, target, isPercentage);
        }
    }

    // Check on scroll
    window.addEventListener('scroll', checkScroll);
    // Check on load
    checkScroll();
}

// Animate counter from 0 to target
function animateCounter(element, target, isPercentage) {
    const duration = 2000; // 2 seconds
    const start = 0;
    const startTime = performance.now();

    function updateCounter(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);

        // Easing function for smooth animation
        const easeOutQuart = 1 - Math.pow(1 - progress, 4);
        const current = Math.floor(start + (target - start) * easeOutQuart);

        // Format the number
        let displayValue;
        if (isPercentage) {
            displayValue = current + '%';
        } else if (target >= 1000) {
            if (target >= 50000) {
                displayValue = Math.floor(current / 1000) + 'K+';
            } else {
                displayValue = Math.floor(current / 1000) + 'K';
            }
        } else {
            displayValue = current.toString();
        }

        element.textContent = displayValue;

        if (progress < 1) {
            requestAnimationFrame(updateCounter);
        }
    }

    requestAnimationFrame(updateCounter);
}

// Card animation functionality
function initCardAnimations() {
    // Add intersection observer for card entrance animations
    initIntersectionObserver();

    // Add interactive card effects
    initCardInteractions();
}

// Intersection Observer for card animations
function initIntersectionObserver() {
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

    // Observe all animated cards
    const cards = document.querySelectorAll('.adventure-card, .tournament-card, .guild-card, .champion-card, .leaderboard-item, .quest-item, .podium-position');
    cards.forEach(card => {
        card.style.animationPlayState = 'paused';
        observer.observe(card);
    });
}

// Interactive card effects
function initCardInteractions() {
    // Add click ripple effect to buttons
    const buttons = document.querySelectorAll('.btn-continue, .btn-start-adventure, .guild-card button, .tournament-card button');
    buttons.forEach(button => {
        button.addEventListener('click', createRippleEffect);
    });

    // Add magnetic effect to some cards on mouse move
    const magneticCards = document.querySelectorAll('.guild-card, .tournament-card');
    magneticCards.forEach(card => {
        card.addEventListener('mousemove', (e) => {
            const rect = card.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const centerX = rect.width / 2;
            const centerY = rect.height / 2;

            const rotateX = (y - centerY) / 10;
            const rotateY = (centerX - x) / 10;

            card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) translateZ(20px)`;
        });

        card.addEventListener('mouseleave', () => {
            card.style.transform = '';
        });
    });

    // Add staggered hover effects for grid items
    const gridItems = document.querySelectorAll('.adventure-grid > *, .guilds-grid > *, .tournament-grid > *');
    gridItems.forEach((item, index) => {
        item.addEventListener('mouseenter', () => {
            // Add a subtle glow effect to neighboring items
            const siblings = Array.from(item.parentNode.children);
            const currentIndex = siblings.indexOf(item);

            if (currentIndex > 0) {
                siblings[currentIndex - 1].style.transform = 'scale(0.98)';
                siblings[currentIndex - 1].style.opacity = '0.8';
            }
            if (currentIndex < siblings.length - 1) {
                siblings[currentIndex + 1].style.transform = 'scale(0.98)';
                siblings[currentIndex + 1].style.opacity = '0.8';
            }
        });

        item.addEventListener('mouseleave', () => {
            const siblings = Array.from(item.parentNode.children);
            siblings.forEach(sibling => {
                sibling.style.transform = '';
                sibling.style.opacity = '';
            });
        });
    });
}

// Create ripple effect for buttons
function createRippleEffect(e) {
    const button = e.currentTarget;
    const ripple = document.createElement('span');
    const rect = button.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = e.clientX - rect.left - size / 2;
    const y = e.clientY - rect.top - size / 2;

    ripple.style.cssText = `
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.6);
        transform: scale(0);
        animation: ripple 0.6s linear;
        width: ${size}px;
        height: ${size}px;
        left: ${x}px;
        top: ${y}px;
        pointer-events: none;
    `;

    button.style.position = 'relative';
    button.style.overflow = 'hidden';
    button.appendChild(ripple);

    setTimeout(() => {
        ripple.remove();
    }, 600);
}

// Add ripple animation to CSS (via JavaScript since we can't modify CSS file easily)
const style = document.createElement('style');
style.textContent = `
    @keyframes ripple {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);

// Navbar scroll effect functionality
function initNavbarScrollEffect() {
    const navbar = document.querySelector('nav');
    const navLinks = document.querySelectorAll('.nav-links a');
    const logo = document.querySelector('.logo');
    const navContainer = document.querySelector('.nav-container');

    let lastScrollY = window.scrollY;

    function updateNavbar() {
        const currentScrollY = window.scrollY;
        const heroSection = document.querySelector('.hero');
        const heroHeight = heroSection ? heroSection.offsetHeight : 600;

        // Add scrolled class when past hero section
        if (currentScrollY > heroHeight - 100) {
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