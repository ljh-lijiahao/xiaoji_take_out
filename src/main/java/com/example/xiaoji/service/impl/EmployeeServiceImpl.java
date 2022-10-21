package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.common.R;
import com.example.xiaoji.mapper.EmployeeMapper;
import com.example.xiaoji.service.EmployeeService;
import com.example.xiaoji.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        String password = DigestUtils.md5DigestAsHex(employee.getPassword().getBytes());
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = getOne(queryWrapper);
        if (emp == null || !emp.getPassword().equals(password)) {
            log.info("用户 {} 正在尝试登陆...\t用户名或密码错误", employee.getUsername());
            return R.error("用户名或密码错误");
        }
        if (emp.getStatus() == 0) {
            log.info("用户 {} 正在尝试登陆...\t账号已被禁用", employee.getUsername());
            return R.error("账号已被禁用，请联系管理员");
        }
        log.info("用户 {} 正在尝试登陆...\t登陆成功", employee.getUsername());
        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }

    @Override
    public R<String> logout(HttpServletRequest request) {
        log.info("用户 {} 正在尝试退出...退出成功", request.getQueryString());
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @Override
    public R<String> saveEmployee(Employee employee) {
        String idNumber = employee.getIdNumber();
        employee.setPassword(DigestUtils.md5DigestAsHex(idNumber.substring(idNumber.length() - 6).getBytes()));
        save(employee);
        log.info("用户 {} 添加成功", employee.getUsername());
        return R.success("添加员工成功");
    }

    @Override
    public R<Page<Employee>> pageEmployee(Integer page, Integer pageSize, String name) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        queryWrapper.orderByAsc(Employee::getUpdateTime);
        Page<Employee> employeePage = page(new Page<>(page, pageSize), queryWrapper);
        return R.success(employeePage);
    }

    @Override
    public R<String> removeEmployee(Employee employee) {
        updateById(employee);
        return R.success("员工信息修改成功");
    }

    @Override
    public R<Employee> getEmployeeById(Long id) {
        Employee employee = getById(id);
        return R.success(employee);
    }
}
