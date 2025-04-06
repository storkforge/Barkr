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
    const url = new URL(window.location.href);
    url.searchParams.set("lang", "uwu");
    window.location.href = url.toString();
}