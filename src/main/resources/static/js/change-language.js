function changeLanguage(language) {
    const url = new URL(window.location.href);
    url.searchParams.set("lang", language);
    window.location.href = url.toString();
}