package org.example.controller;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.ibatis.annotations.Param;
import org.example.constant.CONSTANT;
import org.example.enity.Pojo.User;
import org.example.result.Result;
import org.example.service.UserService;
import org.example.utils.JwtUtils;
import org.example.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import sun.net.www.http.HttpClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @PostMapping("/code2session/{code}")
    public Result code2session(@PathVariable String code) throws IOException {
        String appid="wx7d0e25287ead12ff";
        String secret="7fcad27003e63d7841f9576882848280";
        Map<String,Object> result=new HashMap<String,Object>();
        try{
            if(code == null || code.equals("")){
                return Result.error().setMsg("code不能为空");
            }
            Map<String, String> data = new HashMap<String, String>();
            data.put("appid", appid);
            data.put("secret", secret);
            data.put("js_code", code);
            data.put("grant_type", "authorization_code");
            String response = HttpRequest.get(String.format("https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",appid,secret,code))
                    .execute().body();
            result = objectMapper.readValue(response.toString(),Map.class);
            result.remove("session_key"); //删除session_key 避免泄露用户信息
            String openid = (String)result.remove("openid");//删除session_key 避免泄露用户信息
            User user = userService.findUserByOpenId(openid);
            Integer userId;
            if(user==null){
                user=new User().setName("新用户")
                        .setAvatarUrl(String.format("%s/upload/avatars/default_avatar.png", CONSTANT.domain))
                        .setBriefIntroduction("这个用户很懒，什么也没留下....")
                        .setOpenId(openid);
                userId=userService.createUser(user);
            }
            userId=user.getId();
            String token = JwtUtils.generateToken(userId);
            result.put("token",token);
            result.put("myInfo",user);
            stringRedisTemplate.opsForSet().add("tokens",token);
        }catch (Exception ex){
            System.out.println(ex);
            result.put("errcode","10004");
            result.put("errmsg","获取失败，发生未知错误");
        }
        System.out.println(result);
        return Result.success().setMsg("登录成功").setData(result);
    }

//    @GetMapping("send_message")
//    public String sendMessage(@RequestParam String phoneNumber){
//        System.out.println(phoneNumber);
//        RandomUtils RandomUtils;
//        String vertify_code= RandomUtils.generateVerificationCode(6);
//        System.out.println(vertify_code);
//        stringRedisTemplate.opsForValue().set(String.format("message_login:phone_number:%s", phoneNumber),vertify_code,5, TimeUnit.MINUTES);
//        return "发送短信成功";
//    }

//    @PostMapping("/messageLogin")
//    public String login(@RequestParam String phoneNumber,@RequestParam String vertifyCode){
//        System.out.println(phoneNumber);
//        System.out.println(vertifyCode);
//        String _vertifyCode = stringRedisTemplate.opsForValue().get(String.format("message_login:phone_number:%s", phoneNumber));
//        if(!vertifyCode.equals(_vertifyCode)){
//            return "验证码错误，请重新输入";
//        }
//        return "验证登录成功，转到index页面";
//    }




}
