function fetch_hello() {
    fetch("/data").then((response) => { return response.text() }).
    then((greetings) => document.getElementById("no-comment").innerHTML = greetings);
}

function show_comment() {
    fetch("/blobstore").then((response) => { return response.text() }).then((ImageUploadUrl) => {
        const commentFile = document.getElementById("comment-area");
        commentFile.action = ImageUploadUrl;
        commentFile.classList.remove("hidden");
    });
}

fetch_hello();
show_comment();