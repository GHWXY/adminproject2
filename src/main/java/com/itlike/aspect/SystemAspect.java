package com.itlike.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itlike.domain.Systemlog;
import com.itlike.mapper.SystemlogMapper;
import com.itlike.util.RequestUtil;
import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class SystemAspect {
    @Autowired
    private SystemlogMapper systemlogMapper;
    public void writelog(JoinPoint joinPoint) throws JsonProcessingException {
        System.out.println("--------记录日志---------");
        //设置时间
        Systemlog systemlog = new Systemlog();
        systemlog.setOptime(new Date());
        //设置IP地址  request 添加拦截器  获取请求对象
        HttpServletRequest request = RequestUtil.getRequest();
        if (request != null){
            String ip = request.getRemoteAddr();
            System.out.println(ip);
            systemlog.setIp(ip);
        }
        //方法
        //获取目标执行方法的全路径
        String name = joinPoint.getTarget().getClass().getName();
        //获取方法名称
        String signature = joinPoint.getSignature().getName();
        String func = name+":"+signature;
        systemlog.setFunction(func);


        //获取方法参数
        String s = new ObjectMapper().writeValueAsString(joinPoint.getArgs());
        systemlog.setParams(s);
        systemlogMapper.insert(systemlog);


    }
}
