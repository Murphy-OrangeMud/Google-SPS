function setframe() {
    document.body.clientHeight = screen.availHeight;
    document.body.clientWidth = screen.availWidth;
    document.getElementById("bg").height = screen.availHeight;
    document.getElementById("bg").width = screen.availWidth;
    document.getElementById("main").height = screen.availHeight;
    document.getElementById("main").width = screen.availWidth;
}
setframe();