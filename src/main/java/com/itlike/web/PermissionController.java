package com.itlike.web;

import com.itlike.domain.Permission;
import com.itlike.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class PermissionController {
    /*注入业务*/
    @Autowired
    private PermissionService permissionService;
    @RequestMapping("/permissionList")
    @ResponseBody
    public List<Permission> permissionList(){
        System.out.println("------permissionList-------");
        List<Permission> permissionList = permissionService.getPermission();
        return permissionList;
    }

    /*根据角色查询对应的权限*/
    @RequestMapping("/getPermissionByRid")
    @ResponseBody
    public List<Permission> getPermissionByRid(Long rid){
        System.out.println("rid="+rid);
        List<Permission> permissionByRid = permissionService.getPermissionByRid(rid);
        return permissionByRid;


    }
}
