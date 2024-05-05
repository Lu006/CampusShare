package org.example.service;

import org.example.enity.Pojo.Follow;
import org.example.enity.Pojo.User;
import org.example.enity.Vo.FollowUserVo;
import org.example.mapper.FollowMapper;
import org.example.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {
    @Autowired
    private FollowMapper followMapper;

    @Autowired
    private UserMapper userMapper;

//    public boolean toggleFollow(Follow follow){
//        Boolean state=followMapper.selectStatusByFromIdAndToId(follow);
//        System.out.println(state);
//        if(state!=null){
//            return followMapper.updateFollow(follow);
//        }else{
//            return followMapper.insertFollow(follow);
//        }
//    }

    public List<Follow> findFans(Integer toId){
        List<Follow> follows = followMapper.selectFollowsByFromId(toId);
        return follows;
    }

    public List<Integer> findFolloweeIds(Integer fromId){
        List<Follow> follows = followMapper.selectFollowsByFromId(fromId);
        List<Integer> followIds = follows.stream().map(follow -> follow.getToId()).collect(Collectors.toList());
        return followIds;
    }
    public List<Integer> findFollowerIds(Integer toId){
        List<Follow> follows = followMapper.selectFollowsByToId(toId);
        List<Integer> followIds = follows.stream().map(follow -> follow.getFromId()).collect(Collectors.toList());
        return followIds;
    }

    public List<FollowUserVo> findFollowUserVosByFromIdAndName(Integer fromId, String name){
        List<Follow> follows=followMapper.selectFollowsByFromId(fromId);
        List<Integer> toIds = follows.stream().map((a) -> a.getToId()).collect(Collectors.toList());
        List<FollowUserVo> followUserVos =new ArrayList<>();
        toIds.stream().forEach((toId)->{
            FollowUserVo followUserVo = new FollowUserVo();
            System.out.println(toId+"  "+name);
            User user = userMapper.findUserByIdAndName(toId,name);
            if(user==null)
                return;
            BeanUtils.copyProperties(user, followUserVo);
            followUserVos.add(followUserVo);
        });
        System.out.println(followUserVos);
        return followUserVos;
    }
}
