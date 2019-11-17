package com.itlike.service;

import com.itlike.domain.PageListRes;
import com.itlike.domain.QueryVo;
import com.itlike.domain.Role;

import java.util.List;

public interface RoleService {
    /*查询角色列表*/
    public PageListRes getRoles(QueryVo vo);

    /*调用业务层  保存角色和权限*/
    void saveRole(Role role);

    /*更新角色*/
    void updateRole(Role role);

    /*调用删除角色的业务*/
    void deleteRole(Long rid);

    /*查询角色列表  不带参数*/
    List<Role> roleList();

    /*查询对应的角色*/
    List<Long> getRoleByEid(Long id);
}
