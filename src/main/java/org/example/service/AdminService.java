package org.example.service;

import org.apache.ibatis.annotations.Select;
import org.example.enity.Pojo.Admin;
import org.example.enity.Vo.UserActionVo;
import org.example.enity.Vo.UserVo;
import org.example.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private UserService userService;

    public Admin findAdminByUsername(String username){
        Admin admin = adminMapper.selectAdminByUsername(username);
        return admin;
    }

}
