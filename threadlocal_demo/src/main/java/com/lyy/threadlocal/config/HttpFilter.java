package com.lyy.threadlocal.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpFilter implements Filter {

//初始化需要做的事情
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    //核心操作在这个里面
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
//        request.getSession().getAttribute("user");
        System.out.println("do filter："+Thread.currentThread().getId()+"："+request.getServletPath());
        RequestHolder.add(Thread.currentThread().getId());
        //让这个请求完，，同时做下一步处理
        filterChain.doFilter(servletRequest,servletResponse);


    }

    //不再使用的时候做的事情
    @Override
    public void destroy() {

    }
}
