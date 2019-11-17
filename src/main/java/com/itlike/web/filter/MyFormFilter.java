package com.itlike.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itlike.domain.AjaxRes;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public class MyFormFilter extends FormAuthenticationFilter {
    /*当认证成功的时候会调用*/
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request, ServletResponse response) throws Exception {
        response.setCharacterEncoding("utf-8");
        System.out.println("---------认证成功----------");
        AjaxRes ajaxRes = new AjaxRes();
        ajaxRes.setSuccess(true);
        ajaxRes.setMsg("登录成功");
        /*把对象转成json格式的字符串*/
        String s = new ObjectMapper().writeValueAsString(ajaxRes);

        response.getWriter().print(s);
        /*响应给浏览器*/
        return false;
    }

    /*当认证失败的时候会调用*/
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {

        System.out.println("---------认证失败----------");
        AjaxRes ajaxRes = new AjaxRes();
        ajaxRes.setSuccess(false);

        if (e != null){
            /*获取异常名称*/
            String name = e.getClass().getName();
            if (name.equals(UnknownAccountException.class.getName())){
                /*没有账号*/
                ajaxRes.setMsg("账号不正确");
                response.setCharacterEncoding("UTF-8");
            }else if (name.equals(IncorrectCredentialsException.class.getName())){
                /*密码错误*/
                ajaxRes.setMsg("密码错误");

            }else {
                ajaxRes.setMsg("未知错误");

            }
        }
        try {
            /*把对象转成json格式的字符串*/
            String s = new ObjectMapper().writeValueAsString(ajaxRes);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(s);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        /*响应给浏览器*/
        return false;
    }

}
