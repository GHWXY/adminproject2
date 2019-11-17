package com.itlike.service;

import com.itlike.domain.Employee;
import com.itlike.domain.PageListRes;
import com.itlike.domain.QueryVo;

import java.util.List;

public interface EmployeeService {
    /*查询员工*/
    public PageListRes getEmployee(QueryVo vo);

    /*保存员工*/
    public void saveEmployee(Employee employee);

    /*更新员工*/
   public void updateEmployee(Employee employee);

    public void updateState(Long id);

    /*根据用户名查询有没有这个用户*/
    Employee getEmployeeWithUserName(String username);

    /*根据用户的id查询角色的编号名称*/
    List<String> getRolesById(Long id);

    /*根据用户的id查询权限的资源名称*/
    List<String> getPermissionById(Long id);
}
