$(function () {
    /*员工数据列表*/
    $("#dg").datagrid({
        url:"employeelist",
        columns:[[
            {field:'username',title:'姓名',width:100,align:'center'},
            {field:'inputtime',title:'入职时间',width:100,align:'center'},
            {field:'tel',title:'电话',width:100,align:'center'},
            {field:'email',title:'邮箱',width:100,align:'center'},
            {field:'department',title:'部门',width:100,align:'center',formatter: function(value,row,index){
                    if (value){
                        return value.name;
                    }
                }
            },
            {field:'state',title:'状态',width:100,align:'center',formatter: function(value,row,index){
                    if (row.state){
                        return "在职";
                    } else {
                        return "<font color='red'>离职</font>";
                    }
                }
            },
            {field:'admin',title:'管理员',width:100,align:'center',formatter: function(value,row,index){
                    if (row.admin){
                        return "是";
                    } else {
                        return "否";
                    }
                }
            }
        ]],
        fit:true,
        fitColumns:true,
        rownumbers:true,
        pagination:true,
        singleSelect:true,
        striped:true,
        toolbar: '#tb',
        /*判断所在行   是否为离职状态*/
        onClickRow:function (rowIndex,rowData) {
            /*判断当前行是否为离职状态*/
            if (!rowData.state) {
                /*离职时   禁用离职按钮*/
                $("#delete").linkbutton("disable")

            }else {
                /*在职时   启用离职按钮*/
                $("#delete").linkbutton("enable")
            }


        }

    });

    /*对话框*/
    $("#dialog").dialog({
        width:350,
        height:400,
        closed:true,
        buttons:[{
            text:'保存',
            handler:function(){
                /*判断当前是添加还是编辑*/
                var id =  $("[name='id']").val();
                var url;
                if (id){
                    /*编辑*/
                    url = "updateEmployee";
                }else {
                    /*添加*/
                    url = "saveEmployee";
                }

                /*提交表单*/
                $("#employeeForm").form("submit",{
                    url:url,
                    onSubmit:function(param){
                        /*获取选中的角色*/
                        var values = $("#role").combobox("getValues");
                        for (var i=0; i<values.length; i++){
                            var rid = values[i];
                            param["roles["+i+"].rid"] = rid;
                        }

                    },
                    success:function (data) {
                        data = $.parseJSON(data);
                        if (data.success){
                            $.messager.alert("温馨提示",data.msg);
                            /*关闭对话框 */
                            $("#dialog").dialog("close");
                            /*重新加载数据表格*/
                            $("#dg").datagrid("reload");
                        } else {
                            $.messager.alert("温馨提示",data.msg);
                        }
                    }
                });
            }
        },{
            text:'关闭',
            handler:function(){
                $("#dialog").dialog("close");

            }
        }]
    });

    /*监听添加按钮*/
    $("#add").click(function () {
        $("#dialog").dialog("setTitle","添加员工");
        $("#employeeForm").form("clear");
        /*添加密码验证*/
        $("[name='password']").validatebox({required:true});
        $("#dialog").dialog("open");
        $("#password").show();


    });

    /*监听编辑按钮点击*/
    $("#edit").click(function () {
        /*获取当前选中的行*/
        var rowData = $("#dg").datagrid("getSelected");
        if (!rowData){
            $.messager.alert("提示","请先选择一行数据");
            return;
        }
        /*取消密码验证*/
        $("[name='password']").validatebox({required:false});
        $("#password").hide();
        /*弹出对话框*/
        $("#dialog").dialog("setTitle","编辑员工");
        $("#dialog").dialog("open");
        /*回显部门*/
        rowData["department.id"] = rowData["department"].id;
        /*回显管理员*/
        rowData["admin"] = rowData["admin"] + "";
        /*回显角色*/
        /*根据当前用户的id   查出角色的id*/
        $.get("getRoleByEid?id="+rowData.id,function (data) {
            /*设置下拉列表数据回显*/
            $("#role").combobox("setValues",data);

        });

        /*数据回显*/
        $("#employeeForm").form("load",rowData);



    });

    /*部门选择   下拉列表*/
    $("#department").combobox({
        width:150,
        panelHeight:'auto',
        editable:false,   /*禁止编辑*/
        url:'departList',
        textField:'name',
        valueField:'id',
        onLoadSuccess:function () { /*数据加载完毕之后回调*/
            $("#department").each(function(i){
                var span = $(this).siblings("span")[i];
                var targetInput = $(span).find("input:first");
                if(targetInput){
                    $(targetInput).attr("placeholder", $(this).attr("placeholder"));
                }
            });
        }
    });

    /*是否为管理员    下拉列表*/
    $("#admin").combobox({
        width:150,
        panelHeight:'auto',
        valueField:'value',
        textField:'label',
        editable:false,   /*禁止编辑*/
        data:[{
            label:'是',
            value:'true'
        },{
            label:'否',
            value:'false'
        }],
        onLoadSuccess:function () {/*数据加载完毕之后的做的回调*/
            $("#admin").each(function(i){
                var span = $(this).siblings("span")[i];
                var targetInput = $(span).find("input:first");
                if(targetInput){
                    $(targetInput).attr("placeholder", $(this).attr("placeholder"));
                }
            });
        }
    });

    /*选择角色   下拉列表*/
    $("#role").combobox({
        width:150,
        panelHeight:'auto',
        url:'roleList',
        valueField:'rid',
        textField:'rname',
        editable:false,   /*禁止编辑*/
        multiple:true,   /*定义是否支持多选*/
        onLoadSuccess:function () {/*数据加载完毕之后的做的回调*/
            $("#role").each(function(i){   /*默认的占位符*/
                var span = $(this).siblings("span")[i];
                var targetInput = $(span).find("input:first");
                if(targetInput){
                    $(targetInput).attr("placeholder", $(this).attr("placeholder"));
                }
            });
        }
    });

    /*监听离职按钮点击*/
    $("#delete").click(function () {
        /*获取当前选中的行*/
        var rowData = $("#dg").datagrid("getSelected");
        if (!rowData){
            $.messager.alert("提示","请先选择一行数据");
            return;
        }

        /*提醒用户是否做离职操作*/
        $.messager.confirm("提示","是否离职",function (res) {
            if (res){
                /*做离职操作*/
                $.get("updateState?id="+rowData.id,function (data) {/*get自动解析json数据*/
                    if (data.success){
                        $.messager.alert("温馨提示",data.msg);
                        /*关闭对话框 */
                        $("#dialog").dialog("close");
                        /*重新加载数据表格*/
                        $("#dg").datagrid("reload");
                    } else {
                        $.messager.alert("温馨提示",data.msg);
                    }
                });
            }

        })

    });

    /*监听搜索按钮的点击*/
    $("#searchbtn").click(function () {
        /*获取搜索的内容*/
        var keyword = $("[name='keyword']").val();

        /*重新加载列表，把参数keyword传过去*/
        $("#dg").datagrid("load",{keyword:keyword});

    });

    /*监听刷新按钮*/
    $("#reload").click(function () {
        /*清空搜索内容*/
        var keyword = $("[name='keyword']").val('');
        /*重新加载*/
        $("#dg").datagrid("load",{})

    });
    /*监听导出按钮*/
    $("#excelOut").click(function () {
        window.open('downloadExcel');
    });

    $("#excelUpload").dialog({
        width:260,
        height:180,
        title:"导入Excel",
        buttons:[{
            text:'保存',
            handler:function(){
                $("#uploadForm").form("submit",{
                   url:"uploadExcelFile",
                    success:function (data) {
                      data = $.parseJSON(data);
                      if (data.success) {
                          $.messager.alert("温馨提示",data.msg);
                          /*关闭对话框*/
                          $("#excelUpload").dialog("close");
                          /*重新加载数据表格*/
                          $("#dg").datagrid("reload");

                      }else {
                          $.messager.alert("温馨提示",data.msg);
                      }
                        
                    }
                });
            }
        },{
            text:'关闭',
            handler:function(){
                $("#excelUpload").dialog("close");
            }
        }],
        closed:true
    })

    $("#excelImpot").click(function () {
        $("#excelUpload").dialog("open");
    });
    /*监听导入按钮*/
    $("#excelIn").click(function () {
        $("#excelUpload").dialog("open");
    });
    /*下载excel模板*/
    $("#downloadTml").click(function () {
        window.open("downloadExcelTpl");
        
    });

});