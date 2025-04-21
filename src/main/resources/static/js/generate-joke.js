
document.addEventListener("DOMContentLoaded", function() {
    const jokeBtn = document.getElementById('generate-joke-btn');
    if (jokeBtn) {
        jokeBtn.addEventListener('click', function() {
            fetch('/ai/generate')
                .then(response => response.json())
                .then(data => {
                    document.getElementById('joke-display').textContent = data.generation;
                })
                .catch(error => console.error('Error fetching joke:', error));
        });
    } else {
        console.error("Button #generate-joke-btn not found! User might not have ROLE_PREMIUM.");
    }
});
