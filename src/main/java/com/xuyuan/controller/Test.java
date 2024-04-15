package com.xuyuan.controller;

import com.xuyuan.util.SMSUtils;
import com.xuyuan.util.ValidateCodeUtils;

public class Test {
    public static void main(String[] args) {
        String code = ValidateCodeUtils.generateValidateCode(4).toString();
        try {
            SMSUtils.ssm("18778959134",code);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
