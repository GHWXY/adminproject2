package com.itlike.service.impl;

import com.itlike.domain.Permission;
import com.itlike.mapper.PermissionMapper;
import com.itlike.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionMapper permissionMapper;
    @Override
    public List<Permission> getPermission() {
        List<Permission> permissionList = permissionMapper.selectAll();
        return permissionList;
    }

    @Override
    public List<Permission> getPermissionByRid(Long rid) {
        List<Permission> permissions = permissionMapper.selectPermissionByRid(rid);
        return permissions;
    }
}
