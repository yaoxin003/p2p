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

function ajaxAddUpdate(formId,dialogId,dgId,type,url){
    $.ajax({
        type: type,
        url : url,
        data: $("#" + formId).serialize(),
        dataType : 'json',
        cache : false,
        success:function(result){
            //1.关闭窗口
            $("#" + dialogId).dialog("close");
            //2.刷新datagrid
            $("#"+dgId).datagrid('reload');
            //3.提示信息
            $.messager.show({
                title:result.status,
                msg:result.message
            });
        }
    });
}

function ajaxModifyOpen(id,formId,dialogId,type,url){
    $.ajax({
        type: type,
        url : url,
        data: {"id":id},
        dataType : 'json',
        cache : false,
        success:function(result){
            var formObj = $("#" + formId);
            formObj.form('reset');
            //显示信息
            formObj.form('load',result);
            //打开窗口
            $("#" + dialogId).dialog("open");
        }
    });
}

function postDelete(ps,url,dgId){
    $.post(url,ps,function(result){
        //1.刷新数据
        $("#"+dgId).datagrid("reload");
        //2.提示信息
        $.messager.show({
            title:"提示信息",
            msg : "操作成功！"
        });
    });
}