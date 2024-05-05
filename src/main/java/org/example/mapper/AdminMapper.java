package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.enity.Pojo.Admin;

import java.util.List;

@Mapper
public interface AdminMapper {

    @Select("select * from admin where username=#{username}")
    Admin selectAdminByUsername(String username);
}
