package com.xuyuan.common;

import com.xuyuan.entity.Employee;

public class LogStatus {
    /**
     * 已登录
     */
    public static final  String EmploylogStatus = "empolyee";
    public static final  String UserlogStatus = "user";
    /**
     * 未登录
     */
    public static final  String NotlogStatus = "NOTLOGIN";

    /**
     * 初始密码
     */
    public static final  String InitPassword = "123456";

    /**
     * 用户名重复
     */
    public static final String Common = "账号已存在";

    /**
     * 0:代表停售  1:代表起售
     */
    public static final Integer NOstatus = 0;

    public static final Integer OKstatus = 1;

    public static ThreadLocal<Long> EmployeeId = new ThreadLocal<>();

    /**
     * 存放session存放的id
     * @param id
     */
    public static void setCurrentSessionId(Long id){
        EmployeeId.set(id);
    }

    /**
     * 获取session存放的id
     * @param
     */
    public static Long getCurrentSessionId(){
     return EmployeeId.get();
    }
}
