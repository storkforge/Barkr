let currentPage = 0;
let isLoading = false;
const loading = document.querySelector("#loading");

window.addEventListener("scroll", () => {
    if (isLoading) return;

    if (window.innerHeight + window.scrollY >= document.body.offsetHeight - 100) loadMorePosts();

});

function loadMorePosts() {
    isLoading = true;
    loading.style.display = 'block';
    currentPage++;

    fetch(`/post/load?page=${currentPage}`)
        .then(res => res.text())
        .then(html =>{
            document.getElementById("posts-container").insertAdjacentHTML("beforeend", html);
            loading.style.display = "none";
            isLoading = false;
        })
        .catch(err => {
            isLoading = false;
            console.error("Error loading posts: ", err);
        })
}