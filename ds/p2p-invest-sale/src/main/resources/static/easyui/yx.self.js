/*--------------------日期格式转换--------------------*/
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

/*--------------------列表页面，个性化显示--------------------*/
function genderStr(value){
    if(value == 0){
        return "女";
    }else if(value == 1){
        return "男";
    }else{
        return "未知";
    }
}

//小数转为百分率
function percentageStr(value) {
    var newVal = value*100;
    return newVal + "%";
}
//人民币
function rmbStr(value){
    return "￥" + value + "元";
}

/*--------------------ajax--------------------*/
//添加/修改表单提交
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
            messger_show_center(result.status,result.message);
        }
    });
}
//修改显示页面
function ajaxModifyOpen(dataParams,formId,dialogId,type,url){
    $.ajax({
        type: type,
        url : url,
        data: dataParams,
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
//删除提交页面
function postDelete(ps,url,dgId){
    $.post(url,ps,function(result){
        //1.刷新数据
        $("#"+dgId).datagrid("reload");
        //2.提示信息
        messger_show_center('提示信息','操作成功！');
    });
}
/*--------------------表单验证--------------------*/
//身份证号码规则验证
$.extend($.fn.validatebox.defaults.rules,{
    idCard:{//身份证
        validator : function(value){
            var flag = idCardValidate(value);
            return flag;
        },
        message : '身份证号码格式错误'
    }
});

function idCardValidate(value){
    var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
    if(!reg.test(value)){
        return false;
    }
    return true;
}
//提示信息（居中显示）
function messger_show_center(title,msg){
    $.messager.show({
        title : title,
        msg: msg,
        showType:'fade',
        style:{
            right:'',
            bottom:''
        }
    });
}