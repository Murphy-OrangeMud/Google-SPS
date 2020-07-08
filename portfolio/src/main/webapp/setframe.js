function setframe() {
    document.body.clientHeight = screen.availHeight;
    document.body.clientWidth = screen.availWidth;
    document.getElementsByClassName("main").height = screen.availHeight;
    document.getElementsByClassName("main").width = screen.availWidth;
}
setframe();