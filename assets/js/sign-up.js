// Sign Up Page JavaScript
// Handles form validation, password toggle, and interactive features

document.addEventListener('DOMContentLoaded', function() {
    // Initialize form validation
    initFormValidation();

    // Initialize password toggle
    initPasswordToggle();

    // Initialize animations
    initPageAnimations();
});

// Form validation functionality
function initFormValidation() {
    const form = document.getElementById('signupForm');

    if (!form) return;

    form.addEventListener('submit', function(e) {
        e.preventDefault();

        if (validateForm()) {
            // Simulate form submission
            showSuccessMessage();
        }
    });

    // Real-time validation
    const inputs = form.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('blur', function() {
            validateField(this);
        });

        input.addEventListener('input', function() {
            clearFieldError(this);
        });
    });
}

function validateForm() {
    let isValid = true;
    const form = document.getElementById('signupForm');

    // Validate all required fields
    const requiredFields = form.querySelectorAll('input[required]');
    requiredFields.forEach(field => {
        if (!validateField(field)) {
            isValid = false;
        }
    });

    // Check terms acceptance
    const termsCheckbox = form.querySelector('#terms');
    if (!termsCheckbox.checked) {
        showFieldError(termsCheckbox, 'You must accept the terms and conditions');
        isValid = false;
    }

    return isValid;
}

function validateField(field) {
    const value = field.value.trim();
    let isValid = true;
    let errorMessage = '';

    switch (field.id) {
        case 'username':
            if (value.length < 3) {
                errorMessage = 'Username must be at least 3 characters long';
                isValid = false;
            } else if (!/^[a-zA-Z0-9_]+$/.test(value)) {
                errorMessage = 'Username can only contain letters, numbers, and underscores';
                isValid = false;
            }
            break;

        case 'email':
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                errorMessage = 'Please enter a valid email address';
                isValid = false;
            }
            break;

        case 'password':
            if (value.length < 8) {
                errorMessage = 'Password must be at least 8 characters long';
                isValid = false;
            } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
                errorMessage = 'Password must contain at least one uppercase letter, one lowercase letter, and one number';
                isValid = false;
            }
            break;

        case 'terms':
            // This will be checked in validateForm
            break;
    }

    if (!isValid) {
        showFieldError(field, errorMessage);
    } else {
        clearFieldError(field);
    }

    return isValid;
}

function showFieldError(field, message) {
    clearFieldError(field);

    const formGroup = field.closest('.form-group');
    const errorElement = document.createElement('div');
    errorElement.className = 'field-error';
    errorElement.textContent = message;

    formGroup.appendChild(errorElement);
    field.classList.add('error');
}

function clearFieldError(field) {
    const formGroup = field.closest('.form-group');
    const existingError = formGroup.querySelector('.field-error');

    if (existingError) {
        existingError.remove();
    }

    field.classList.remove('error');
}

// Password toggle functionality
function initPasswordToggle() {
    const passwordToggle = document.getElementById('passwordToggle');
    const passwordInput = document.getElementById('password');

    if (!passwordToggle || !passwordInput) return;

    passwordToggle.addEventListener('click', function() {
        const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
        passwordInput.setAttribute('type', type);

        // Update toggle icon
        this.textContent = type === 'password' ? '👁️' : '🙈';
    });
}

// Success message functionality
function showSuccessMessage() {
    const form = document.getElementById('signupForm');
    const formContent = form.querySelector('.form-content');

    // Hide form content
    formContent.style.opacity = '0';
    formContent.style.transform = 'scale(0.95)';

    setTimeout(() => {
        formContent.innerHTML = `
            <div class="success-message">
                <div class="success-icon">🎉</div>
                <h2>Welcome to CodeQuest!</h2>
                <p>Your account has been created successfully. Check your email for verification instructions.</p>
                <button class="btn-continue" onclick="window.location.href='home.html'">
                    Start Your Adventure
                </button>
            </div>
        `;

        // Animate in
        formContent.style.opacity = '1';
        formContent.style.transform = 'scale(1)';
        formContent.style.transition = 'all 0.5s ease';
    }, 300);
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

    // Observe animated elements
    const animatedElements = document.querySelectorAll('.signup-form, .signup-benefits > *');
    animatedElements.forEach(element => {
        element.style.animationPlayState = 'paused';
        observer.observe(element);
    });

    // Add hover effects to benefit items
    const benefitItems = document.querySelectorAll('.benefit-item');
    benefitItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-10px) rotate(2deg)';
        });

        item.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0) rotate(0deg)';
        });
    });

    // Add focus effects to form inputs
    const formInputs = document.querySelectorAll('.input-container input');
    formInputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.style.transform = 'scale(1.02)';
        });

        input.addEventListener('blur', function() {
            this.parentElement.style.transform = 'scale(1)';
        });
    });
}

// Add CSS for error states and success message
const additionalStyles = `
    .field-error {
        color: #e74c3c;
        font-size: 0.85rem;
        margin-top: 0.25rem;
        animation: shake 0.3s ease-in-out;
    }

    .error {
        border-color: #e74c3c !important;
        box-shadow: 0 0 0 3px rgba(231, 76, 60, 0.1) !important;
    }

    @keyframes shake {
        0%, 100% { transform: translateX(0); }
        25% { transform: translateX(-5px); }
        75% { transform: translateX(5px); }
    }

    .success-message {
        text-align: center;
        padding: 2rem 0;
    }

    .success-icon {
        font-size: 4rem;
        margin-bottom: 1rem;
        animation: bounce 0.6s ease;
    }

    .success-message h2 {
        color: #27ae60;
        margin-bottom: 1rem;
        font-size: 1.8rem;
    }

    .success-message p {
        color: #666;
        margin-bottom: 2rem;
        font-size: 1rem;
    }

    .btn-continue {
        background: linear-gradient(135deg, #27ae60, #2ecc71);
        color: white;
        border: none;
        padding: 1rem 2rem;
        border-radius: 12px;
        font-size: 1.1rem;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        box-shadow: 0 4px 20px rgba(39, 174, 96, 0.3);
    }

    .btn-continue:hover {
        transform: translateY(-2px);
        box-shadow: 0 8px 30px rgba(39, 174, 96, 0.4);
    }

    @keyframes bounce {
        0%, 20%, 50%, 80%, 100% { transform: translateY(0); }
        40% { transform: translateY(-10px); }
        60% { transform: translateY(-5px); }
    }
`;

const styleElement = document.createElement('style');
styleElement.textContent = additionalStyles;
document.head.appendChild(styleElement);