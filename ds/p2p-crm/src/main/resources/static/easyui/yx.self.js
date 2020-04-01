function dateStr(value){
    var date = new Date(value);
    var y = date.getFullYear();
    var m = date.getMonth()+1;
    var d = date.getDate();
    return y + "-" + m + "-" + d;
}

function dateTimeStr(value){
    var date = new Date(value);
    var y = date.getFullYear();
    var m = date.getMonth()+1;
    var d = date.getDate();
    var h = date.getHours();
    var min = date.getMinutes();
    var sec = date.getSeconds();
    return dateStr(value) + " " + h + ":" + min + ":" + sec;
}

function genderStr(value){
    if(value == 0){
        return "女";
    }else if(value == 1){
        return "男";
    }else{
        return "未知";
    }
}