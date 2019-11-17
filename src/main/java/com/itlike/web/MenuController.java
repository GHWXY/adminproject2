package com.itlike.web;

import com.itlike.domain.AjaxRes;
import com.itlike.domain.Menu;
import com.itlike.domain.PageListRes;
import com.itlike.domain.QueryVo;
import com.itlike.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class MenuController {
    @Autowired
    private MenuService menuService;


    @RequestMapping("/menu")
    public String menu(){
        System.out.println("--------menu----------");
        return "menu";
    }

    @RequestMapping("/menuList")
    @ResponseBody
    public PageListRes menuList(QueryVo vo){
        System.out.println("--------menuList--------");
        System.out.println(vo);
        /*调用业务层查询菜单*/
        PageListRes pagemenu = menuService.getMenuList(vo);
        return pagemenu;

    }

    /*加载父级菜单*/
    @RequestMapping("/parentList")
    @ResponseBody
    public List<Menu> parentList(){
        System.out.println("---------parentList-----------");
        return menuService.parentList();

    }

    @RequestMapping("/saveMenu")
    @ResponseBody
    public AjaxRes saveMenu(Menu menu){
        System.out.println("--------saveMenu---------");
        AjaxRes ajaxRes = new AjaxRes();
        try {
            /*调用业务层保存菜单*/
            menuService.saveMenu(menu);
            ajaxRes.setMsg("保存成功");
            ajaxRes.setSuccess(true);
        }catch (Exception e){
            ajaxRes.setMsg("保存失败");
            ajaxRes.setSuccess(false);
        }

        return ajaxRes;
    }

    /*更新菜单*/
    @RequestMapping("/updateMenu")
    @ResponseBody
    public AjaxRes updateMenu(Menu menu){
        return menuService.updateMenu(menu);

    }

    /*删除菜单*/
    @RequestMapping("/deleteMenu")
    @ResponseBody
    public AjaxRes deleteMenu(Long id){
        return menuService.deleteMenu(id);
    }

    /*获取树形菜单*/
    @RequestMapping("/getTreeData")
    @ResponseBody
    public List<Menu> getTreeData(){
        return menuService.getTreeData();


    }
}
