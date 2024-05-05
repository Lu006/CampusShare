package org.example.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.enity.Vo.AtUserVo;
import org.example.enity.Pojo.Follow;
import org.example.enity.Vo.FollowUserVo;
import org.example.enity.Vo.UserActionVo;
import org.example.enity.Vo.UserVo;
import org.example.mapper.FollowMapper;
import org.example.mapper.UserMapper;
import org.example.enity.Pojo.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FollowMapper followMapper;


    public Integer createUser(User user){
        return userMapper.insertUser(user);
    }

    public PageInfo<UserActionVo> findPageUsers(int pageNum, int pageSize,String query){
        PageHelper.startPage(pageNum, pageSize);
        List<User> users = userMapper.selectUsers(query);
        List<UserActionVo> userActionVos = users.stream().map(user -> {
            UserActionVo userActionVo = new UserActionVo();
            BeanUtils.copyProperties(user, userActionVo);
            return userActionVo;
        }).collect(Collectors.toList());
        PageInfo<UserActionVo> userPageInfo = new PageInfo<>(userActionVos);
        return userPageInfo;
    }


    public Boolean updateUserState(Integer userId,Boolean state){
        return userMapper.updateUserStateByUserId(userId,state);
    }

    public User findUserByOpenId(String openId){
        User user = userMapper.findUserByOpenId(openId);
        return user;
    }

    public UserVo findUserByUserId(Integer userId){
        User user = userMapper.findUserByUserId(userId);
        UserVo userVo=new UserVo();
        BeanUtils.copyProperties(user,userVo);
        return userVo;
    }

    public List<AtUserVo> findAtUserVosByFromIdAndName(Integer fromId,String name){
        List<Follow> attentions=followMapper.selectFollowsByFromId(fromId);
        List<Integer> toIds = attentions.stream().map((a) -> a.getToId()).collect(Collectors.toList());
        List<AtUserVo> atUserVos =new ArrayList<>();
        toIds.stream().forEach((toId)->{
            AtUserVo atUserVo = new AtUserVo();
            System.out.println(toId+"  "+name);
            User user = userMapper.findUserByIdAndName(toId,name);
            if(user==null)
                return;
            BeanUtils.copyProperties(user, atUserVo);
            atUserVos.add(atUserVo);
        });
        System.out.println(atUserVos);
        return atUserVos;
    }


    public boolean updateUser(User user){
        System.out.println(user);
        System.out.println(user.toString());
        boolean result = userMapper.updateUser(user);
        System.out.println(result);
        return result;

    }

}
