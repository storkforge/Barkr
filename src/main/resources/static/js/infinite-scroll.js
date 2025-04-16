let currentPage = 0;
let isLoading = false;
const loading = document.querySelector("#loading");

function handleScroll() {
    if (isLoading) return;

    if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 100) loadMorePosts();
}

window.addEventListener("scroll", handleScroll)

function loadMorePosts() {
    isLoading = true;
    loading.style.display = 'block';
    currentPage++;

    fetch(`/post/load?page=${currentPage}`)
        .then(res => res.text())
        .then(html => {
            if (html.trim().length === 0) {
                window.removeEventListener("scroll", handleScroll);
                loading.style.display = "none";
                return;
            }
            document.getElementById("posts-container").insertAdjacentHTML("beforeend", html);
            loading.style.display = "none";
            isLoading = false;
        })
        .catch(err => {
            isLoading = false;
            console.error("Error loading posts: ", err);
        })
}