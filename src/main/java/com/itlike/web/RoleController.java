package com.itlike.web;

import com.itlike.domain.AjaxRes;
import com.itlike.domain.PageListRes;
import com.itlike.domain.QueryVo;
import com.itlike.domain.Role;
import com.itlike.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class RoleController {
    @Autowired
    private RoleService roleService;

    @RequestMapping("/role")
    public String role(){
        return "role";
    }

    /*接受 角色请求列表*/
    @RequestMapping("/getRoles")
    @ResponseBody
    public PageListRes getRoles(QueryVo vo){
        /*调用业务层  查询角色列表*/
        PageListRes pageroles = roleService.getRoles(vo);
        return pageroles;
    }

    /*接收  保存角色请求地址*/
    @RequestMapping("/saveRole")
    @ResponseBody
    public AjaxRes saveRole(Role role){
        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*调用业务层  保存角色和权限*/
            roleService.saveRole(role);
            ajaxRes.setMsg("保存成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("保存失败");
            ajaxRes.setSuccess(false);

        }
        return ajaxRes;
    }

    /*编辑角色  更新他的请求*/
    @RequestMapping("/updateRole")
    @ResponseBody
    public AjaxRes updateRole(Role role){

        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*调用更新角色的业务*/
            roleService.updateRole(role);
            ajaxRes.setMsg("更新角色成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("更新角色失败");
            ajaxRes.setSuccess(false);
        }
        return ajaxRes;

    }

    /*接收删除的请求*/
    @RequestMapping("/delRole")
    @ResponseBody
    public AjaxRes delRole(Long rid){
        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*调用删除角色的业务*/
            roleService.deleteRole(rid);
            ajaxRes.setMsg("删除角色成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("删除角色失败");
            ajaxRes.setSuccess(false);
        }
        return ajaxRes;
    }
    @RequestMapping("/roleList")
    @ResponseBody
    public List<Role> roleList(){

        List<Role> roleList = roleService.roleList();
        return roleList;
    }

    /*根据当前用户的id   查出角色的*/
    @RequestMapping("/getRoleByEid")
    @ResponseBody
    public List<Long> getRoleByEid(Long id){
        /*查询对应的角色*/
        List<Long> roleByEid = roleService.getRoleByEid(id);
        return roleByEid;


    }
}
