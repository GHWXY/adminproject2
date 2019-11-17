package com.itlike.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itlike.domain.*;
import com.itlike.mapper.MenuMapper;
import com.itlike.service.MenuService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class MenuServiceImpl implements MenuService {
    @Autowired
    private MenuMapper menuMapper;
    @Override
    public PageListRes getMenuList(QueryVo vo) {
        /*调用mapper查询菜单*/
        Page<Object> page = PageHelper.startPage(vo.getPage(), vo.getRows());
        List<Menu> menus = menuMapper.selectAll();

        /*封装成pagelist*/
        PageListRes pageListRes = new PageListRes();
        pageListRes.setTotal(page.getTotal());
        pageListRes.setRows(menus);
        return pageListRes;
    }

    @Override
    public List<Menu> parentList() {

        return menuMapper.selectAll();
    }

    /*保存菜单*/
    @Override
    public void saveMenu(Menu menu) {
        menuMapper.saveMenu(menu);

    }

    /*更新菜单*/
    @Override
    public AjaxRes updateMenu(Menu menu) {
        AjaxRes ajaxRes = new AjaxRes();
        /*判断 选择的父菜单  是不是自己的子菜单*/

        /*先取出当前 选择的父菜单的id*/
        Long id = menu.getParent().getId();
        /*查询id  对应的menu*/
       Long parent_id =  menuMapper.selectParentId(id);
        if (menu.getId() == parent_id){
            ajaxRes.setMsg("不能设置自己的子菜单为父菜单");
            ajaxRes.setSuccess(false);
            return ajaxRes;
        }
        /*更新*/
        try {
            menuMapper.updateByPrimaryKey(menu);
            ajaxRes.setMsg("保存成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("保存失败");
            ajaxRes.setSuccess(false);
        }

        return ajaxRes;
    }

    /*删除菜单*/
    @Override
    public AjaxRes deleteMenu(Long id) {


        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*1.打破菜单关系*/
            menuMapper.updateMenuRel(id);
            /*2.删除记录*/
            menuMapper.deleteByPrimaryKey(id);
            ajaxRes.setMsg("删除成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("删除失败");
            ajaxRes.setSuccess(false);
        }
        return ajaxRes;
    }

    /*获取树形菜单*/
    @Override
    public List<Menu> getTreeData() {
        List<Menu> treeData = menuMapper.getTreeData();
        /*
          判断用户有没有相应的权限
          如果没有就从集合中移除*/

        /*获取用户判断是不是管理员，是不需要做判断*/
        Subject subject = SecurityUtils.getSubject();
        /*当前的用户*/
        Employee employee = (Employee) subject.getPrincipal();
        if (!employee.getAdmin()){
            /*做校验权限*/
            checkPermission(treeData);

        }
        return treeData;
    }
    public void checkPermission(List<Menu> menus){
        //获取主体
        Subject subject = SecurityUtils.getSubject();

        //遍历所有的菜单及子菜单
        Iterator<Menu> iterator = menus.iterator();
        while (iterator.hasNext()){
            Menu menu = iterator.next();
            if (menu.getPermission() != null){
                //判断当前menu是否有权限对象，如果没有  当前遍历的菜单从集合中移除
                String presource = menu.getPermission().getPresource();
                if (!subject.isPermitted(presource)){
                    //当前遍历的菜单从集合中移除
                    iterator.remove();
                    continue;

                }
            }
            /*判断是否有子菜单   有子菜单也要做权限检验*/
            if (menu.getChildren().size() > 0){
                checkPermission(menu.getChildren());     //此处递归调用

            }

        }
    }
}
