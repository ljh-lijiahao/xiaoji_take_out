package com.example.xiaoji.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {
    R<Employee> login(HttpServletRequest request, Employee employee);

    R<String> logout(HttpServletRequest request);

    R<String> saveEmployee(Employee employee);

    R<Page<Employee>> pageEmployee(Integer page, Integer pageSize, String name);

    R<String> removeEmployee(Employee employee);

    R<Employee> getEmployeeById(Long id);
}
