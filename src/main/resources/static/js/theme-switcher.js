// Theme toggle functionality
document.addEventListener('DOMContentLoaded', function () {
    const rootElement = document.documentElement;
    const themeToggle = document.querySelector('#theme-toggle');
    const themeIcon = document.querySelector('#theme-icon');
    const themeText = document.querySelector('#theme-text');

    // Check for saved theme preference or use default
    const savedTheme = localStorage.getItem('theme') || 'light';
    rootElement.className = savedTheme;
    updateThemeToggle(savedTheme);

    // Toggle theme when the button is clicked
    themeToggle.addEventListener('click', function () {
        const newTheme = rootElement.classList.contains("light") ? "dark" : "light";
        rootElement.classList.remove("light", "dark");
        rootElement.classList.add(newTheme);
        localStorage.setItem("theme", newTheme);
        updateThemeToggle(newTheme);
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
