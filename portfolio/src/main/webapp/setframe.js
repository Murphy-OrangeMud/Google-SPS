function setframe() {
    document.body.clientHeight = screen.availHeight;
    document.body.clientWidth = screen.availWidth;
    document.getElementByClassName("bg").height = screen.availHeight;
    document.getElementByClassName("bg").width = screen.availWidth;
    document.getElementsByClassName("main").height = screen.availHeight;
    document.getElementsByClassName("main").width = screen.availWidth;
}
setframe();