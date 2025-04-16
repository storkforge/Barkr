document.getElementById('generate-joke-btn').addEventListener('click', function() {
    fetch('/ai/generate')
        .then(response => response.json())
        .then(data => {
            document.getElementById('joke-display').textContent = data.generation;
        })
        .catch(error => console.error('Error fetching joke:', error));
});
