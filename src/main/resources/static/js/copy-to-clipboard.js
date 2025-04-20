    function copyKey() {
    const keyInput = document.getElementById("rawKey");
    const valueInput = document.getElementById("apiKey");

    const key = keyInput.value;
    const value = valueInput.value;

    const credentials = `key: ${value}\nvalue: ${key}`;

    navigator.clipboard.writeText(credentials)
    .then(() => alert("API key copied!"))
    .catch(err => alert("Failed to copy."));
}
