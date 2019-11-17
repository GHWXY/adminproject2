<%--
  Created by IntelliJ IDEA.
  User: 13952
  Date: 2019/9/17
  Time: 14:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>角色权限管理</title>
    <%@include file="/static/common/common.jsp"%>
    <style>
        #dialog #myform .panel-header{
            height: 25px;
        }
        #dialog #myform .panel-title{
            color: black;
            margin-top: -5px;
        }
    </style>
</head>
<body>
<%--工具栏--%>
<div id="tb">
    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-add',plain:true" id="add">添加</a>
    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-edit',plain:true" id="edit">编辑</a>
    <a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:true" id="remove">删除</a>
</div>
<%--数据表格--%>
<div id="role_dg"></div>

<%--添加/编辑对话框 --%>
<div id="dialog">
    <form id="myform">
        <table align="center" style="border-spacing: 20px 30px">
            <input type="hidden" name="rid">
            <tr align="center">
                <td>角色编号: <input type="text" name="rnum" ></td>
                <td>角色名称: <input type="text" name="rname"></td>
            </tr>
            <tr>
                <td><div id="role_data1"></div></td>
                <td><div id="role_data2"></div></td>
            </tr>
        </table>
    </form>
</div>
<script>
    $(function () {
        /*角色列表展示*/
        $("#role_dg").datagrid({
            url:"getRoles",
            columns:[[
                {field:'rnum',title:'角色编号',width:100,align:'center'},
                {field:'rname',title:'角色名称',width:100,align:'center'}
            ]],
            fit:true,
            fitColumns:true,     /*真正的自动展开/收缩列的大小，以适应网格的宽度，防止水平滚动。*/
            pagination:true,    /*数据表格控件底部显示分页工具栏*/
            singleSelect:true,   /*只允许选择一行*/
            striped:true,       /*显示斑马线效果*/
            rownumbers:true,     /*是否显示行号*/
            toolbar: '#tb'

        });

        /*添加/编辑对话框*/
        $("#dialog").dialog({
            width:600,
            height:500,
            buttons:[{
                text:'保存',
                handler:function(){
                    /*判断当前时保存还是编辑*/
                   var rid = $("[name='rid']").val();
                   var url;
                   if (rid){
                       /*如果有值  就是编辑操作哟*/
                       url = "updateRole";
                   }else {
                       /*如果没有值  就是保存*/
                       url = "saveRole";
                   }

                    /*提交表单*/
                    $("#myform").form('submit',{
                        url:url,
                        onSubmit:function(param){     /*传递额外的参数   把已选的权限传过去*/
                            /*获取已经选择的权限*/
                           var allRows = $("#role_data2").datagrid('getRows');
                           /*遍历出每一个权限*/
                            for (var i=0; i<allRows.length; i++){
                                var row = allRows[i];
                                /*给他封装到集合中*/
                                param["permissions["+i+"].pid"] = row.pid;
                            }
                        },
                        success:function (data) {
                            data = $.parseJSON(data);
                            if (data.success) {
                                $.messager.alert("温馨提示",data.msg);
                                /*关闭对话框*/
                                $("#dialog").dialog("close");
                                /*重新加载数据表格*/
                                $("#role_dg").dialog("reload");
                            }else {
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
            }],
            closed:true
        });
        /*添加/编辑对话框----所有权限列表1*/
        $("#role_data1").datagrid({
            title:'所有权限',
            width:250,
            height:400,
            striped:true,
            singleSelect:true,
            fitColumns:true,
            url:'permissionList',
            columns:[[
                {field:'pname',title:'权限名称',width:100,align:'center'}
            ]],
            /* onClickRow:  点击一行时，回调
            * 在用户点击一行的时候触发，参数包括：
              rowIndex：点击的行的索引值，该索引值从0开始。
              rowData：对应于点击行的记录。
            * */
            onClickRow:function (rowIndex,rowData) {
                /*判断是否已经存在该权限*/
                /*取出所有的已选权限*/
               var allRows = $("#role_data2").datagrid("getRows");
               /*取出每一个进行判断*/
                for (var i=0; i<allRows.length; i++){
                    /*取出每一行*/
                    var row = allRows[i];
                    if (rowData.pid == row.pid){
                        /*已经存在该权限*/
                        /*让已经存在权限成为选中的状态*/
                        /*获取已经成为选中状态的角标*/
                       var index =  $("#role_data2").datagrid("getRowIndex",row);
                       /*让该行成为选中状态*/
                        $("#role_data2").datagrid("selectRow",index);
                       return;
                    }
                }
                /*把当前选中的添加到已选权限中*/
                $("#role_data2").datagrid("appendRow",rowData);
            }

        });

        /*添加/编辑对话框----选中权限列表2*/
        $("#role_data2").datagrid({
            title:'已选权限',
            width:250,
            height:400,
            striped:true,
            singleSelect:true,
            fitColumns:true,
            columns:[[
                {field:'pname',title:'权限名称',width:100,align:'center'}
            ]],
            onClickRow:function (rowIndex,rowData) {
                /*删除当前选中的一行*/
                $("#role_data2").datagrid("deleteRow",rowIndex);

            },


        });

        /*添加角色*/
        $("#add").click(function () {
            /*设置标题*/
            $("#dialog").dialog("setTitle","添加角色");
            /*清空表单*/
            $("#myform").form("clear");

            /*清空已选权限*/
            $("#role_data2").datagrid("loadData",{rows:[]});

            /*打开对话框*/
            $("#dialog").dialog("open");


        });

        /*监听编辑按钮点击*/
        $("#edit").click(function () {
            /*获取当前选中的行*/
            var rowData = $("#role_dg").datagrid("getSelected");
            if (!rowData) {
                $.messager.alert("温馨提示","请选择一行数据进行编辑");
                return;
            }
            /*加载当前角色下的权限*/
           var options = $("#role_data2").datagrid("options");
           options.url = "getPermissionByRid?rid="+rowData.rid;
           /*重新加载数据*/
            $("#role_data2").datagrid("load");


            /*打开对话框*/
            $("#dialog").dialog("open");
            /*设置标题*/
            $("#dialog").dialog("setTitle","编辑角色");
            /*回显表单*/
            $("#myform").form("load",rowData);


        });

        /*监听删除点击按钮*/
        $("#remove").click(function () {
            /*获取当前选中的一行*/
           var row = $("#role_dg").datagrid("getSelected");
           if (!row){
               $.messager.alert("温馨提示","请选择一行数据进行删除");
               return;
           }
           $.get("delRole?rid="+row.rid,function (data) {
               if (data.success){
                   $.messager.alert("温馨提示",data.msg);
                   /*重新加载数据表格*/
                   $("#role_dg").datagrid("reload");
               }else{
                   $.messager.alert("温馨提示",data.msg);
               }

           });


        });

    });
</script>
</body>
</html>
