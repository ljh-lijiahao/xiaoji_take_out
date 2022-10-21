package com.example.xiaoji.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.xiaoji.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
