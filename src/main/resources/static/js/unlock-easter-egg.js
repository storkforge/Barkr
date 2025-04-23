document.addEventListener('DOMContentLoaded', function () {
    var form = document.getElementById('unlock-premium-form');
    if (!form) return;

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        var code = document.getElementById('code').value;
        var csrfToken = document.getElementById('csrfToken').value;

        var xhr = new XMLHttpRequest();
        xhr.open('POST', '/unlock-easter-egg', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);

        var body = 'code=' + encodeURIComponent(code); // <-- manually build form data string

        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                var responseText = xhr.responseText;
                if (xhr.status === 200 || xhr.status === 202) {
                    document.getElementById('premium-response').textContent = responseText;
                } else {
                    console.error("XHR error:", responseText);
                    document.getElementById('premium-response').textContent = '❌ ' + responseText;
                }
            }
        };

        xhr.send(body);
    });
});
