package com.xuyuan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuyuan.common.LogStatus;
import com.xuyuan.common.R;
import com.xuyuan.entity.Employee;
import com.xuyuan.service.EmployService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployService employService;

    /**
     * 员工登录
     *
     * @param employee
     * @param request
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request) {
        //1.密码加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.数据库查询是否用户是否存在

        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<Employee>().
                eq(Employee::getUsername, employee.getUsername());
        Employee emp = employService.getOne(wrapper);
        if (emp == null) {
            return R.error("登录失败");
        }

        //3.比对密码
        if (!password.equals(emp.getPassword())) {
            return R.error("登录失败");
        }

        //4.检查状态
        if (emp.getStatus() == 0) {
            return R.error("账号已经被禁用");
        }

        //5.登录成功，将账号id存入session并返回记录
        request.getSession().setAttribute(LogStatus.EmploylogStatus, emp.getId());
        return R.success(emp);
    }

    /**
     * 退出界面
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {

        //1.清理员工的session
        request.getSession().removeAttribute("emloyee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee, HttpServletRequest request) {
        log.info("新增员工：{}", employee);
    /*    //设置初始密码123456,，并进行加密
        employee.setPassword(DigestUtils.md5DigestAsHex(LogStatus.InitPassword.getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        Long createUserId = (Long) request.getSession().getAttribute(LogStatus.logStatus);

        employee.setCreateUser(createUserId);
        employee.setUpdateUser(createUserId);*/
       employService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工分页查询
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page = {}, pageSize = {}, name = {}", page, pageSize, name);

        //构造分页构造器
        Page<Employee> pageInfo = new Page(page, pageSize);
        //构造条件分页构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(name), Employee::getName, name)
                .orderByDesc(Employee::getUpdateTime);
        //执行查询
        employService.page(pageInfo, wrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据员工id修改信息
     */
    @PutMapping
    public R<String>  update(@RequestBody Employee employee,HttpServletRequest request){
        log.info(employee.toString());
        employService.updateById(employee);
        return R.success("修改成功");
    }

    /**
     * 根据员工id查询信息
     */
    @GetMapping("/{id}")
    public R<Employee> getId(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employeeId = employService.getById(id);
        return R.success(employeeId);
    }

}
