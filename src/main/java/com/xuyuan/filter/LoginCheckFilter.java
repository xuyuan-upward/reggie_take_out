package com.xuyuan.filter;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.xuyuan.common.LogStatus;
import com.xuyuan.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户是否已经完成登录,过滤器
 */
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求的url
        String requestURI = request.getRequestURI();

        //判断哪些放行
        String[] PermissionUrls = new String[]{
                "/employee/login", "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg"
        };
        boolean check = check(PermissionUrls, requestURI);
        //可以放行
        if (check) {
            // 放行拦截的请求
            log.info("本次{}请求放行:",requestURI);
            filterChain.doFilter(request, response);
            //放行之后必须return返回
            return;
        }
        log.info("拦截到的请求：{}",request.getRequestURI());
        //1-判断登录状态,登录就放行
        Long Employstatus = (Long) request.getSession().getAttribute(LogStatus.EmploylogStatus);
        if (Employstatus != null) {
            //存放session的值到隔离的Threalocal空间内
            LogStatus.setCurrentSessionId(Employstatus);
            filterChain.doFilter(request, response);
            log.info("已经登录放行:",requestURI);
            return;
        }
        //2-判断登录状态,登录就放行
        Long Userstatus = (Long) request.getSession().getAttribute(LogStatus.UserlogStatus);
        if (Userstatus != null) {
            //存放session的值到隔离的Threalocal空间内
            LogStatus.setCurrentSessionId(Userstatus);
            filterChain.doFilter(request, response);
            log.info("已经登录放行:",requestURI);
            return;
        }
        //未登录返回登录界面，返回数据
        log.info("未登录:");
        response.getWriter().write(JSON.toJSONString(R.error(LogStatus.NotlogStatus)));
        return ;


    }

    /**
     * 是否放行
     *
     * @param PermissionUrls
     * @param requestURL
     * @return
     */
    public boolean check(String[] PermissionUrls, String requestURL) {
        for (String Url : PermissionUrls) {
            boolean match = PATH_MATCHER.match(Url, requestURL);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
