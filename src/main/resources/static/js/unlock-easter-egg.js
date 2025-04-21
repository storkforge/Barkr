    document.getElementById('unlock-premium-form').addEventListener('submit', function(event) {
    event.preventDefault(); // 💥 CRUCIAL: Prevent the form from reloading the page

    const code = document.getElementById('code').value;
    const csrfToken = document.getElementById('csrfToken').value;

    fetch('/unlock-easter-egg', {
    method: 'POST',
    headers: {
    'Content-Type': 'application/x-www-form-urlencoded',
    'X-CSRF-TOKEN': csrfToken
},
    body: new URLSearchParams({ code })
})
    .then(async response => {
    const message = await response.text();
    if (!response.ok) {
    throw new Error(message);
}
    return message;
})
    .then(data => {
    document.getElementById('premium-response').textContent = data;
})
    .catch(error => {
    document.getElementById('premium-response').textContent = '❌ ' + error.message;
});
});
