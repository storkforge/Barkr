// Theme toggle functionality
document.addEventListener('DOMContentLoaded', function () {
    const htmlElement = document.documentElement;
    const themeToggle = document.getElementById('theme-toggle');
    const themeIcon = document.getElementById('theme-icon');
    const themeText = document.getElementById('theme-text');

    // Check for saved theme preference or use default
    const savedTheme = localStorage.getItem('theme') || 'light';
    htmlElement.className = savedTheme;
    updateThemeToggle(savedTheme);

    // Toggle theme when the button is clicked
    themeToggle.addEventListener('click', function () {
        if (htmlElement.classList.contains('light')) {
            htmlElement.classList.remove('light');
            htmlElement.classList.add('dark');
            localStorage.setItem('theme', 'dark');
            updateThemeToggle('dark');
        } else {
            htmlElement.classList.remove('dark');
            htmlElement.classList.add('light');
            localStorage.setItem('theme', 'light');
            updateThemeToggle('light');
        }
    });

    // Update toggle button appearance based on current theme
    function updateThemeToggle(theme) {
        if (theme === 'dark') {
            themeIcon.textContent = '🌙';
            themeText.textContent = 'Light Mode';
        } else {
            themeIcon.textContent = '☀️';
            themeText.textContent = 'Dark Mode';
        }
    }
});
