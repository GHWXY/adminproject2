package com.itlike.web.realm;

import com.itlike.domain.Employee;
import com.itlike.service.EmployeeService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class EmployeeRealm extends AuthorizingRealm {
    @Autowired
    private EmployeeService employeeService;
    /*认证*/
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("---------来到了认证-------");
        /*获取身份信息*/
        String username = (String) token.getPrincipal();
        System.out.println(username);

        /*到数据库当中查询有没有这个用户*/
        Employee employee = employeeService.getEmployeeWithUserName(username);
        System.out.println(employee);
        if (employee == null){
            return null;
        }

        /*认证*/
        /*参数：主体，正确的密码，盐，当前realm名称*/
        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(employee, employee.getPassword(), ByteSource.Util.bytes(employee.getUsername()), this.getName());
        return info;
    }

    /*授权
    * web  doGetAuthorizationInfo 什么时候调用
     1.发现访问路径对应的方法上面 有授权注解  就会调用doGetAuthorizationInfo
     2.页面当中有授权标签  也会调用doGetAuthorizationInfo
     * */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("-------授权调用------");
        /*获取用户的身份信息*/
        Employee employee = (Employee) principalCollection.getPrimaryPrincipal();
        /*根据当前用户，查询角色和权限*/
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        /*判断当前用户是不是管理员  如果是 拥有所有权限*/
        if (employee.getAdmin()){
            /*拥有所有的权限*/
            permissions.add("*:*");

        }else {
            /*查询角色*/
            roles = employeeService.getRolesById(employee.getId());
            //System.out.println(roles);

            /*根据用户的id查询权限的资源名称*/
            permissions = employeeService.getPermissionById(employee.getId());
            //System.out.println(permissions);
        }


        /*给授权信息*/
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
        info.addStringPermissions(permissions);
        return info;

    }

}
