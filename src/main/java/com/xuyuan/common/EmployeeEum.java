package com.xuyuan.common;

public enum EmployeeEum {
    Common("账号重复");
    private String msg;
    private EmployeeEum(String msg){
        R.error(msg);
    }
}
