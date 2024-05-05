package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.context.BaseContext;
import org.example.enity.Pojo.Collect;
import org.example.enity.Pojo.Follow;
import org.example.enity.Message.FollowMessage;
import org.example.enity.Pojo.User;
import org.example.enity.Vo.AtUserVo;
import org.example.enity.Vo.FollowUserVo;
import org.example.enity.Vo.UserVo;
import org.example.result.Result;
import org.example.service.FollowService;
import org.example.service.UserService;
import org.example.utils.RedisKeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/follow")
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private String FOLLOW="follow-";

    private String MSG_FOLLOW="message-follow-";

    @PostMapping("/{toId}")
    public Result followUser(@PathVariable Integer toId) throws JsonProcessingException {
        Map result=new HashMap();
        Integer myId = BaseContext.get();
        if(myId==null)
            return Result.error().setMsg("用户未登录");
        String followeeKey= RedisKeyUtil.getFolloweeKey(myId);
        String followerKey= RedisKeyUtil.getFollowerKey(toId);
        if(!redisTemplate.hasKey(followeeKey)){
            List<Integer> followeeIds = followService.findFolloweeIds(myId);
            for(Integer followeeId:followeeIds){
                redisTemplate.opsForSet().add(followeeKey,followeeId.toString());
            }
        }
        if(!redisTemplate.hasKey(followerKey)){
            List<Integer> followerIds = followService.findFollowerIds(myId);
            for(Integer followerId:followerIds){
                redisTemplate.opsForSet().add(followerKey,followerId.toString());
            }
        }
        Boolean isMember = redisTemplate.opsForSet().isMember(followeeKey, toId.toString());
        System.out.println(isMember);
        if(!isMember){
            Follow follow=new Follow().setFromId(myId).setToId(toId);
            redisTemplate.opsForSet().add(followeeKey,toId.toString());
            redisTemplate.opsForSet().add(followerKey,myId.toString());
            result.put("isFollow",true);
        }else{
            Follow follow=new Follow().setFromId(myId).setToId(toId);
            redisTemplate.opsForSet().remove(followeeKey,toId.toString());
            redisTemplate.opsForSet().remove(followerKey,myId.toString());
            result.put("isFollow",false);
        }
        return Result.success(result);
    }

    public void sendFollowNotify(Follow follow) throws JsonProcessingException {
        UserVo fansUser = userService.findUserByUserId(follow.getFromId());
//        0 构造评论消息
        FollowMessage followMessage=new FollowMessage().setTime(new Date())
                .setFansId(fansUser.getId()).setFansName(fansUser.getName()).setAvatarUrl(fansUser.getAvatarUrl())
                .setConstant("关注了你,成为你的粉丝");
//        1 对分享 评论 parentid==null 将消息发给postgerid的用户 username对您的分享postId进行了评论
        String followKey = MSG_FOLLOW + follow.getToId();
        redisTemplate.opsForList().rightPush(followKey, objectMapper.writeValueAsString(followMessage));
    }

    @GetMapping("/messages")
    public Result receiveFollowMessage() throws IOException {
        Integer userId = BaseContext.get();
        if(userId==null)
            return Result.success("[]");
        System.out.println("receive collect msgs");
        String key = MSG_FOLLOW + userId;
        List msgs=new ArrayList();
        while (redisTemplate.opsForList().size(key)>0) {
            String msg = redisTemplate.opsForList().leftPop(key);
            FollowMessage followMessage = objectMapper.readValue(msg, FollowMessage.class);
            msgs.add(followMessage);
        }
        System.out.println(msgs);
        return Result.success(msgs);
    }

//    @GetMapping("/followers/{toId}")
//    public Result getfollowers(@PathVariable Integer toId){
//        List<Follow> fans = followService.findFans(toId);
//        System.out.println(fans);
//        return Result.success(fans);
//    }


    @GetMapping(value={"/users/{fromId}/{name}","/users/{fromId}"})
    public Result getFolloweeUsers(@PathVariable Integer fromId,@PathVariable(required = false) String name){
        if(name==null)
            name="";
        List<Integer> followeeIds = redisTemplate.opsForSet().members(RedisKeyUtil.getFolloweeKey(fromId)).stream()
                .mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
        List<FollowUserVo> followUserVos = followeeIds.stream().map((userId) -> {
            FollowUserVo followUserVo = new FollowUserVo();
            UserVo userVo = userService.findUserByUserId(userId);
            BeanUtils.copyProperties(userVo, followUserVo);
            return followUserVo;
        }).collect(Collectors.toList());
        return Result.success().setData(followUserVos);
    }

}
