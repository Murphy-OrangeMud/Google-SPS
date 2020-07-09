function fetch_hello() {
    fetch("/data").then((response) => response.text()).
    then((greetings) => document.getElementById("no-comment").innerText = greetings);
}
fetch_hello();