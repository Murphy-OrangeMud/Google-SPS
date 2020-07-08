var id = ["cycling and traveling", "books/movies/music", "programming newbie"];
//var xPos = [100,200,300];
//var yPos = [300,400,500];
var xPos = [50, document.body.clientWidth - 20, document.body.clientWidth / 2];
var yPos = [document.body.clientHeight - 20, 50, document.body.clientHeight - 20];
var step = 1;
var delay = [8, 10, 12];
var height = [document.body.clientHeight, document.body.clientHeight, document.body.clientHeight];
var width = [document.body.clientWidth, document.body.clientWidth, document.body.clientWidth];
var Hoffset = [0, 0, 0];
var Woffset = [0, 0, 0];
var yon = [0, 0, 0];
var xon = [0, 0, 0];
var pause = [true, true, true];
var interval = [];
for (var i = 0; i < 3; i++) {
    document.getElementById(id[i]).style.left = xPos[i] + "px";
    document.getElementById(id[i]).style.top = yPos[i] + "px";
}
//function draw(button_id, button) {}
function changePos(button_id) {
    width[button_id] = document.body.clientWidth;
    height[button_id] = document.body.clientHeight;
    Hoffset[button_id] = document.getElementById(id[button_id]).offsetHeight;
    Woffset[button_id] = document.getElementById(id[button_id]).offsetWidth;
    document.getElementById(id[button_id]).style.left = (xPos[button_id] + document.body.scrollLeft) + "px";
    document.getElementById(id[button_id]).style.top = (yPos[button_id] + document.body.scrollTop) + "px";
    if (yon[button_id]) yPos[button_id] += step;
    else yPos[button_id] -= step;
    if (xon[button_id]) xPos[button_id] += step;
    else xPos[button_id] -= step;
    //yon[button_id] = Math.random() % 10 >= 2? 1: 0;
    //xon[button_id] = Math.random() % 10 >= 2? 1: 0;
    if (yPos[button_id] < 0) {
        yon[button_id] = 1;
        yPos[button_id] = 0;
    }
    if (yPos[button_id] >= (height[button_id] - Hoffset[button_id])) {
        yon[button_id] = 0;
        yPos[button_id] = (height[button_id] - Hoffset[button_id]);
    }
    if (xPos[button_id] < 0) {
        xPos[button_id] = 0;
        xon[button_id] = 1;
    }
    if (xPos[button_id] >= (width[button_id] - Woffset[button_id])) {
        xon[button_id] = 0;
        xPos[button_id] = (width[button_id] - Woffset[button_id]);
    }
}
function start() {
    //setInterval("changePos(0)", delay);
    interval.push(setInterval("changePos(0)", delay[0]));
    interval.push(setInterval("changePos(1)", delay[1]));
    interval.push(setInterval("changePos(2)", delay[2]));
}
start();