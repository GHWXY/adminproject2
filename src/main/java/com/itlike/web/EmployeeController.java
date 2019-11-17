package com.itlike.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itlike.domain.AjaxRes;
import com.itlike.domain.Employee;
import com.itlike.domain.PageListRes;
import com.itlike.domain.QueryVo;
import com.itlike.service.EmployeeService;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @RequestMapping("/employee")
    @RequiresPermissions("employee:index")
    public String employee(){
        System.out.println("employee------------");
        return "employee";
    }

    @RequestMapping("/employeelist")
    @ResponseBody
    public PageListRes employeelist(QueryVo vo){
        System.out.println(vo);
        /*调用业务层查询员工*/
        PageListRes pageemployee = employeeService.getEmployee(vo);
        return pageemployee;

    }

    /*接收员工添加的表单*/
    @RequestMapping("/saveEmployee")
    @ResponseBody
    @RequiresPermissions("employee:add")
    public AjaxRes saveEmployee(Employee employee){
        System.out.println(employee);
        AjaxRes ajaxRes = new AjaxRes();
        try {
            employee.setState(true);
            employeeService.saveEmployee(employee);
            ajaxRes.setMsg("保存成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("保存失败");
            ajaxRes.setSuccess(false);

        }
        return ajaxRes;
    }
    /*接收更新员工的请求*/
    @RequestMapping("/updateEmployee")
    @ResponseBody
    @RequiresPermissions("employee:edit")
    public AjaxRes updateEmployee(Employee employee){
        AjaxRes ajaxRes = new AjaxRes();

        try {
            /*调用业务层更新员工*/
            employeeService.updateEmployee(employee);
            ajaxRes.setMsg("更新成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("更新失败");
            ajaxRes.setSuccess(false);

        }
        return ajaxRes;
    }

    /*接受离职操作请求*/
    @RequestMapping("/updateState")
    @ResponseBody
    @RequiresPermissions("employee:delete")
    public AjaxRes updateState(Long id){
        AjaxRes ajaxRes = new AjaxRes();

        try {
            /*调用业务层,设置员工的离职状态*/
            employeeService.updateState(id);
            ajaxRes.setMsg("离职成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("离职失败");
            ajaxRes.setSuccess(false);

        }
        return ajaxRes;

    }

    @ExceptionHandler(AuthorizationException.class)
    public void handleShiroException(HandlerMethod method, HttpServletResponse response) throws Exception {
        /*跳转到一个页面   提示 没有权限*/
        /*判断当前的 请求是不是json请求  如果是  返回json给浏览器让它自己来做跳转*/
        /*获取方法上的注解*/
        ResponseBody methodAnnotation = method.getMethodAnnotation(ResponseBody.class);
        if (methodAnnotation != null){
            //Ajax
            AjaxRes ajaxRes = new AjaxRes();
            ajaxRes.setSuccess(false);
            ajaxRes.setMsg("你没有权限操作");

            String s = new ObjectMapper().writeValueAsString(ajaxRes);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(s);

        }else {
            response.sendRedirect("nopermission.jsp");
        }


    }
    @RequestMapping("/downloadExcel")
    @ResponseBody
    public void downloadExcel(HttpServletResponse response){
        try {
        System.out.println("-----------downloadExcel-----------");
        /*1.从数据当中取列表数据*/
        QueryVo queryVo = new QueryVo();
        queryVo.setPage(1);
        queryVo.setRows(10);
        PageListRes plr = employeeService.getEmployee(queryVo);
        List<Employee> rows = (List<Employee>) plr.getRows();

        /*2.创建excel，写到excel当中*/
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("员工数据");
        //创建一行
        HSSFRow row = sheet.createRow(0);
        //设置行的每一列数据
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("入职时间");
        row.createCell(3).setCellValue("电话");
        row.createCell(4).setCellValue("邮箱");
        row.createCell(5).setCellValue("部门");
        row.createCell(6).setCellValue("状态");
        row.createCell(7).setCellValue("管理员");

        HSSFRow employeeRow = null;
        /*取出每一个员工设置数据*/
        for (int i=0; i<rows.size(); i++){
            Employee employee = rows.get(i);
            employeeRow = sheet.createRow(i+1);
            employeeRow.createCell(0).setCellValue(employee.getId());
            employeeRow.createCell(1).setCellValue(employee.getUsername());
            if (employee.getInputtime() != null){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String format = sdf.format(employee.getInputtime());
                employeeRow.createCell(2).setCellValue(format);
            }else {
                employeeRow.createCell(2).setCellValue("");
            }

            employeeRow.createCell(3).setCellValue(employee.getTel());
            employeeRow.createCell(4).setCellValue(employee.getEmail());
            employeeRow.createCell(5).setCellValue(employee.getDepartment().getName());
            employeeRow.createCell(6).setCellValue(employee.getState());
            employeeRow.createCell(7).setCellValue(employee.getAdmin());


        }
        /*3.响应给浏览器*/
            String fileName = new String("员工数据.xls".getBytes("utf-8"), "iso8859-1");
            response.setHeader("content-Disposition","attachment;filename="+fileName);
            wb.write(response.getOutputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    /*下载excel模板*/
    @RequestMapping("/downloadExcelTpl")
    @ResponseBody
    public void downloadExcelTpl(HttpServletRequest request, HttpServletResponse response){
        FileInputStream file = null;
        try {
            String fileName = new String("EmployeeTpl.xls".getBytes("utf-8"), "iso8859-1");
            response.setHeader("content-Disposition","attachment;filename="+fileName);
            /*获取文件路径*/
            String realPath = request.getSession().getServletContext().getRealPath("static/ExcelTml.xls");
            /*读取文件*/
            file = new FileInputStream(realPath);
            IOUtils.copy(file,response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (file != null){
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /*记得 配置文件上传的解析器  mvc*/
    @RequestMapping("/uploadExcelFile")
    @ResponseBody
    public AjaxRes uploadExcelFile(MultipartFile excel){
        AjaxRes ajaxRes = new AjaxRes();

        try {
            ajaxRes.setMsg("导入成功");
            ajaxRes.setSuccess(true);
            HSSFWorkbook workbook = new HSSFWorkbook(excel.getInputStream());
            HSSFSheet sheet = workbook.getSheetAt(0);
            /*获取最大的行号*/
            int lastRowNum = sheet.getLastRowNum();
            Row employeeRow = null;
            for (int i=1; i<=lastRowNum; i++){
               employeeRow = sheet.getRow(i);
                Employee employee = new Employee();
                getCellValue(employeeRow.getCell(0));


            }


        }catch (Exception e){
            e.printStackTrace();
            ajaxRes.setMsg("导入失败");
            ajaxRes.setSuccess(false);

        }
       return ajaxRes;

    }
    private Object getCellValue(Cell cell){
        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                } else {
                    return cell.getNumericCellValue();
                }
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
        }
        return cell;
    }


}
