// Problem Details Page JavaScript
// Handles Monaco editor, code execution, chat assistant, and voice commands

let editor;
let recognition;
let isListening = false;

// Initialize everything when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initMonacoEditor();
    initNavbarScrollEffect();
    initChatFunctionality();
    initVoiceRecognition();
    initKeyboardNavigation();
});

// Initialize Monaco Editor
function initMonacoEditor() {
    // Load Monaco Editor
    require.config({ paths: { vs: 'https://unpkg.com/monaco-editor@0.45.0/min/vs' } });

    require(['vs/editor/editor.main'], function() {
        editor = monaco.editor.create(document.getElementById('code-editor'), {
            value: getDefaultCode('javascript'),
            language: 'javascript',
            theme: 'vs-dark',
            fontSize: 14,
            minimap: { enabled: false },
            scrollBeyondLastLine: false,
            automaticLayout: true,
            wordWrap: 'on',
            tabSize: 2,
            insertSpaces: true
        });

        // Handle language change
        document.getElementById('language-select').addEventListener('change', function(e) {
            const language = e.target.value;
            const model = editor.getModel();
            monaco.editor.setModelLanguage(model, language);
            editor.setValue(getDefaultCode(language));
        });
    });
}

// Get default code for different languages
function getDefaultCode(language) {
    const templates = {
        javascript: `function twoSum(nums, target) {
    // Write your solution here
    // Return indices of two numbers that add up to target

}

console.log(twoSum([2, 7, 11, 15], 9)); // Expected: [0, 1]`,

        python: `def two_sum(nums, target):
    """
    :type nums: List[int]
    :type target: int
    :rtype: List[int]
    """
    # Write your solution here
    # Return indices of two numbers that add up to target
    pass

# Test the function
print(two_sum([2, 7, 11, 15], 9))  # Expected: [0, 1]`,

        java: `import java.util.*;

class Solution {
    public int[] twoSum(int[] nums, int target) {
        // Write your solution here
        // Return indices of two numbers that add up to target
        return new int[]{};
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        int[] result = solution.twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println(Arrays.toString(result)); // Expected: [0, 1]
    }
}`,

        cpp: `#include <vector>
#include <iostream>
#include <unordered_map>

using namespace std;

class Solution {
public:
    vector<int> twoSum(vector<int>& nums, int target) {
        // Write your solution here
        // Return indices of two numbers that add up to target
        return {};
    }
};

int main() {
    Solution solution;
    vector<int> nums = {2, 7, 11, 15};
    int target = 9;
    vector<int> result = solution.twoSum(nums, target);

    cout << "[";
    for (size_t i = 0; i < result.size(); ++i) {
        cout << result[i];
        if (i < result.size() - 1) cout << ",";
    }
    cout << "]" << endl; // Expected: [0,1]

    return 0;
}`
    };

    return templates[language] || templates.javascript;
}

// Run code functionality
function runCode() {
    const code = editor.getValue();
    const language = document.getElementById('language-select').value;

    // Update status
    document.getElementById('results-status').textContent = 'Running...';
    document.getElementById('results-status').style.color = '#FF6B4A';

    // Simulate code execution (in a real app, this would send to a backend)
    setTimeout(() => {
        const result = simulateCodeExecution(code, language);
        displayTestResults(result);
    }, 1000);
}

// Simulate code execution (mock implementation)
function simulateCodeExecution(code, language) {
    // This is a simplified mock - in reality, you'd send to a code execution service
    const hasTwoSum = code.includes('twoSum') || code.includes('two_sum') || code.includes('twoSum(');

    if (hasTwoSum) {
        return {
            status: 'success',
            output: '[0, 1]',
            passed: true
        };
    } else {
        return {
            status: 'error',
            output: 'Function twoSum not found or incorrect implementation',
            passed: false
        };
    }
}

// Display test results
function displayTestResults(result) {
    const statusEl = document.getElementById('results-status');
    const contentEl = document.getElementById('results-content');
    const testCaseEl = contentEl.querySelector('.test-case');
    const actualEl = document.getElementById('test-actual-1');
    const statusIconEl = document.getElementById('test-status-1');

    if (result.passed) {
        statusEl.textContent = 'All tests passed!';
        statusEl.style.color = '#28a745';
        testCaseEl.className = 'test-case test-pass';
        actualEl.textContent = `Output: ${result.output}`;
        statusIconEl.textContent = '✅';
    } else {
        statusEl.textContent = 'Some tests failed';
        statusEl.style.color = '#dc3545';
        testCaseEl.className = 'test-case test-fail';
        actualEl.textContent = `Error: ${result.output}`;
        statusIconEl.textContent = '❌';
    }
}

// Submit code functionality
function submitCode() {
    const code = editor.getValue();
    const language = document.getElementById('language-select').value;

    // Show submission feedback
    alert(`Code submitted in ${language}!\n\nYour solution will be evaluated against all test cases.`);

    // In a real app, this would send the code to be judged
}

// Initialize chat functionality
function initChatFunctionality() {
    // Chat is already set up in HTML, just add any dynamic behavior here
}

// Handle chat input
function handleChatKeyPress(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
}

function sendMessage() {
    const inputEl = document.getElementById('chat-input');
    const message = inputEl.value.trim();

    if (message) {
        addMessageToChat('user', message);
        inputEl.value = '';

        // Simulate bot response
        setTimeout(() => {
            const response = getBotResponse(message);
            addMessageToChat('bot', response);
        }, 1000);
    }
}

function addMessageToChat(type, content) {
    const messagesEl = document.getElementById('chat-messages');
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}-message`;

    const avatar = type === 'bot' ? '🤖' : '👤';

    messageDiv.innerHTML = `
        <div class="message-avatar">${avatar}</div>
        <div class="message-content">
            <p>${content}</p>
        </div>
    `;

    messagesEl.appendChild(messageDiv);
    messagesEl.scrollTop = messagesEl.scrollHeight;
}

// Simple bot responses (in a real app, this would use AI)
function getBotResponse(message) {
    const responses = {
        'hint': 'Try using a hash map to store numbers you\'ve seen and their indices. For each number, check if target - number exists in the map.',
        'help': 'The Two Sum problem requires finding two numbers in an array that add up to a target value. Return their indices.',
        'time': 'This is typically an O(n) time complexity problem using a hash map, or O(n²) with nested loops.',
        'space': 'The optimal solution uses O(n) space for the hash map, while the brute force uses O(1) extra space.',
        'default': 'I\'m here to help! Ask me about hints, time complexity, or any specific part of the problem.'
    };

    const lowerMessage = message.toLowerCase();

    if (lowerMessage.includes('hint')) return responses.hint;
    if (lowerMessage.includes('help')) return responses.help;
    if (lowerMessage.includes('time') || lowerMessage.includes('complexity')) return responses.time;
    if (lowerMessage.includes('space')) return responses.space;

    return responses.default;
}

function clearChat() {
    const messagesEl = document.getElementById('chat-messages');
    messagesEl.innerHTML = `
        <div class="message bot-message">
            <div class="message-avatar">🤖</div>
            <div class="message-content">
                <p>Hello! I'm here to help you solve this problem. Ask me anything about the Two Sum problem!</p>
            </div>
        </div>
    `;
}

// Voice recognition functionality
function initVoiceRecognition() {
    if ('webkitSpeechRecognition' in window || 'SpeechRecognition' in window) {
        const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
        recognition = new SpeechRecognition();
        recognition.continuous = false;
        recognition.interimResults = false;
        recognition.lang = 'en-US';

        recognition.onstart = function() {
            isListening = true;
            document.getElementById('voice-indicator').style.display = 'block';
            document.getElementById('voice-btn').classList.add('active');
        };

        recognition.onend = function() {
            isListening = false;
            document.getElementById('voice-indicator').style.display = 'none';
            document.getElementById('voice-btn').classList.remove('active');
        };

        recognition.onresult = function(event) {
            const transcript = event.results[0][0].transcript;
            document.getElementById('chat-input').value = transcript;
            sendMessage();
        };

        recognition.onerror = function(event) {
            console.error('Speech recognition error:', event.error);
            alert('Voice recognition error. Please try again.');
        };
    } else {
        document.getElementById('voice-btn').style.display = 'none';
    }
}

function toggleVoice() {
    if (!recognition) {
        alert('Voice recognition is not supported in your browser.');
        return;
    }

    if (isListening) {
        recognition.stop();
    } else {
        recognition.start();
    }
}

function stopVoice() {
    if (recognition && isListening) {
        recognition.stop();
    }
}

// Toggle hints functionality
function toggleHints() {
    const hintsContent = document.getElementById('hints-content');
    const arrow = document.querySelector('.hint-arrow');

    if (hintsContent.style.display === 'none') {
        hintsContent.style.display = 'block';
        arrow.style.transform = 'rotate(180deg)';
    } else {
        hintsContent.style.display = 'none';
        arrow.style.transform = 'rotate(0deg)';
    }
}

// Initialize keyboard navigation for sections
function initKeyboardNavigation() {
    const sections = [
        document.querySelector('.problem-section'),
        document.querySelector('.editor-section'),
        document.querySelector('.chat-section')
    ];

    // Make sections focusable and add click to focus
    sections.forEach((section, index) => {
        section.classList.add('scrollable-section');
        section.setAttribute('tabindex', '0');
        section.addEventListener('click', () => {
            section.focus();
        });
    });

    // Keyboard navigation
    document.addEventListener('keydown', function(event) {
        const activeElement = document.activeElement;

        // Check if a scrollable section is focused
        if (activeElement.classList.contains('scrollable-section')) {
            const scrollAmount = 50; // pixels to scroll

            switch(event.key) {
                case 'ArrowUp':
                    event.preventDefault();
                    activeElement.scrollTop -= scrollAmount;
                    break;
                case 'ArrowDown':
                    event.preventDefault();
                    activeElement.scrollTop += scrollAmount;
                    break;
                case 'ArrowLeft':
                    event.preventDefault();
                    // Move focus to previous section
                    const currentIndex = sections.indexOf(activeElement);
                    const prevIndex = currentIndex > 0 ? currentIndex - 1 : sections.length - 1;
                    sections[prevIndex].focus();
                    break;
                case 'ArrowRight':
                    event.preventDefault();
                    // Move focus to next section
                    const currentIndex2 = sections.indexOf(activeElement);
                    const nextIndex = currentIndex2 < sections.length - 1 ? currentIndex2 + 1 : 0;
                    sections[nextIndex].focus();
                    break;
                case 'PageUp':
                    event.preventDefault();
                    activeElement.scrollTop -= activeElement.clientHeight;
                    break;
                case 'PageDown':
                    event.preventDefault();
                    activeElement.scrollTop += activeElement.clientHeight;
                    break;
                case 'Home':
                    event.preventDefault();
                    activeElement.scrollTop = 0;
                    break;
                case 'End':
                    event.preventDefault();
                    activeElement.scrollTop = activeElement.scrollHeight;
                    break;
            }
        }
    });

    // Add visual feedback for focused sections
    const style = document.createElement('style');
    style.textContent = `
        .scrollable-section:focus {
            outline: 2px solid #FF6B4A;
            outline-offset: -2px;
        }

        .scrollable-section:focus-visible {
            outline: 2px solid #FF6B4A;
            outline-offset: -2px;
        }
    `;
    document.head.appendChild(style);
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