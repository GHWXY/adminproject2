package com.itlike.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itlike.domain.PageListRes;
import com.itlike.domain.Permission;
import com.itlike.domain.QueryVo;
import com.itlike.domain.Role;
import com.itlike.mapper.RoleMapper;
import com.itlike.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;
    @Override
    public PageListRes getRoles(QueryVo vo) {
       // System.out.println("-----来到了角色查询------");
        /*调用mapper查询数据*/
        Page<Object> page = PageHelper.startPage(vo.getPage(), vo.getRows());/*分页查询*/
        List<Role> roles = roleMapper.selectAll();
        PageListRes pageListRes = new PageListRes();
        pageListRes.setTotal(page.getTotal());
        pageListRes.setRows(roles);
        return pageListRes;
    }

    @Override
    public void saveRole(Role role) {
        /*1.保存角色*/
        roleMapper.insert(role);
        /*2.保存角色而与权限之间的关系*/
        for (Permission permission : role.getPermissions()) {
            roleMapper.insertRoleAndPermissionRel(role.getRid(),permission.getPid());
            
        }

    }

    /*更新角色*/
    @Override
    public void updateRole(Role role) {
        /*打破角色与权限之间的关系*/
        roleMapper.deletePermissionRel(role.getRid());
        /*更新角色*/
        roleMapper.updateByPrimaryKey(role);
        /*重新建立与权限的关系*/
        for (Permission permission : role.getPermissions()) {
            roleMapper.insertRoleAndPermissionRel(role.getRid(),permission.getPid());
        }

    }

    /*删除角色*/
    @Override
    public void deleteRole(Long rid) {
        /*1.删除关联的权限*/
        roleMapper.deletePermissionRel(rid);
        /*删除对应的角色*/
        roleMapper.deleteByPrimaryKey(rid);


    }

    @Override
    public List<Role> roleList() {
        List<Role> roles = roleMapper.selectAll();
        return roles;
    }

    /*据当前用户的id   查出角色*/
    @Override
    public List<Long> getRoleByEid(Long id) {
       List<Long> role = roleMapper.getRoleWithId(id);
        return null;
    }
}
