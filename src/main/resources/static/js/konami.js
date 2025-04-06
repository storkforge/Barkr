// The Konami code in keycodes
const konamiCode = [38, 38, 40, 40, 37, 39, 37, 39, 66, 65];
let currentPosition = 0;

document.addEventListener("keydown", (event) => {
    if (event.keyCode == konamiCode[currentPosition]) {
        currentPosition++;
        if (currentPosition == konamiCode.length) {
            currentPosition = 0;
            switchLang();
        }
    } else {
        currentPosition = 0;
    }
});

function switchLang() {
    const url = new URL(window.location.href);
    url.searchParams.set("lang", "uwu");
    window.location.href = url.toString();
}