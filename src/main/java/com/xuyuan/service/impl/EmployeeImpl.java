package com.xuyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuyuan.entity.Employee;
import com.xuyuan.mapper.EmployeeMapper;
import com.xuyuan.service.EmployService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployService {

}
