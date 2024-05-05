package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.context.BaseContext;
import org.example.enity.Vo.AtUserVo;
import org.example.enity.Pojo.User;
import org.example.enity.Vo.UserVo;
import org.example.result.Result;
import org.example.service.FollowService;
import org.example.service.UserService;
import org.example.utils.FilePathUtils;
import org.example.utils.JwtUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    private ObjectMapper objectMapper=new ObjectMapper();


    @GetMapping("/info/{userId}")
    public Result findUserByUserId(@PathVariable Integer userId){
        UserVo userVo = userService.findUserByUserId(userId);
        System.out.println("user:"+userVo.toString());
        Map result=new HashMap();
        String token = JwtUtils.generateToken(userVo.getId());
        redisTemplate.opsForSet().add("tokens",token);
        System.out.println("token:"+token);
//        result.put("token",token);
        result.put("userInfo",userVo);

//        Integer ownId = BaseContext.get();
//        if(!ownId.equals(userId)){
//            redisTemplate.opsForSet().add();
//        }

        return Result.success(result);
    }


    @PostMapping("/profile")
    public Result updateUser(@RequestBody User user) throws IOException {
        if(!user.getAvatarUrl().contains("upload/avatar")) {
            String avatarUrl = FilePathUtils.transferTmpUrlToAvatarDir(user.getAvatarUrl());
            user.setAvatarUrl(avatarUrl);
        }
        boolean is_success = userService.updateUser(user);
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(user,userVo);
        return is_success?Result.success(userVo)
                .setMsg("更新成功"):Result.error().setMsg("更新失败，请重试");
    }
}
