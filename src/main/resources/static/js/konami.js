// The Konami code in keycodes
const konamiCode = ["ArrowUp", "ArrowUp", "ArrowDown", "ArrowDown", "ArrowLeft", "ArrowRight", "ArrowLeft", "ArrowRight", "b", "a"];
let currentPosition = 0;

document.addEventListener("keydown", (event) => {
    if (event.key === konamiCode[currentPosition]) {
        currentPosition++;
        if (currentPosition === konamiCode.length) {
            currentPosition = 0;
            switchLang();
        }
    } else {
        currentPosition = 0;
    }
});

function switchLang() {
    try {
        const feedback = document.createElement('div');
        feedback.textContent = '🎉 Konami Code Activated! 🎉';
        feedback.className = "konami-feedback"
        document.body.appendChild(feedback);

        setTimeout(() => {
            const url = new URL(window.location.href);
            url.searchParams.set("lang", "uwu");
            window.location.href = url.toString();
        }, 1000);
    } catch (error) {
        console.error('Failed to switch language:', error);
    }
}