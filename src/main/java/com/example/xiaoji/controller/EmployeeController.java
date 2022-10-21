package com.example.xiaoji.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.xiaoji.common.R;
import com.example.xiaoji.service.EmployeeService;
import com.example.xiaoji.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        return employeeService.login(request,employee);
    }

    /**
     * 员工退出
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        return employeeService.logout(request);
    }

    /**
     * 添加员工
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee) {
        return employeeService.saveEmployee(employee);
    }

    /**
     * 员工信息分页查询
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(Integer page, Integer pageSize, String name) {
        return employeeService.pageEmployee(page,pageSize,name);
    }

    /**
     * 根据id修改员工信息
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee) {
        return employeeService.removeEmployee(employee);
    }

    /**
     * 根据id查询员工信息
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }
}
