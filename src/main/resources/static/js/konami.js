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
        feedback.style.position = 'fixed';
        feedback.style.top = '50%';
        feedback.style.left = '50%';
        feedback.style.transform = 'translate(-50%, -50%)';
        feedback.style.padding = '20px';
        feedback.style.backgroundColor = 'rgba(0,0,0,0.8)';
        feedback.style.color = 'white';
        feedback.style.borderRadius = '10px';
        feedback.style.zIndex = '9999';
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