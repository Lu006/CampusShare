package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.context.BaseContext;
import org.example.enity.Pojo.Collect;
import org.example.enity.Message.CollectMessage;
import org.example.enity.Vo.UserVo;
import org.example.result.Result;
import org.example.service.CollectService;
import org.example.service.PostService;
import org.example.service.UserService;
import org.example.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/collect")
public class CollectController {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private PostService postService;
    @Autowired
    private CollectService collectService;

    @Autowired
    private ObjectMapper objectMapper;
    
    private final String COLLECT="collect-";

    private final String MSG_COLLECT="message-collect-";


    @PostMapping("/{postId}")
    public Result collectPost(@PathVariable Integer postId) throws JsonProcessingException {
        Map result=new HashMap();
        Integer userId = BaseContext.get();
        if(userId==null)
            return Result.error().setMsg("用户未登录");
        String key= RedisKeyUtil.getCollectorKey(postId);
        if(!redisTemplate.hasKey(key)){
            List<Integer> userIds = collectService.selectUserIdsByPostId(postId);
            for(Integer _userId:userIds){
                redisTemplate.opsForSet().add(key,_userId.toString());
            }
        }
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId.toString());
        if(!isMember){
            Collect collect =new Collect().setPostId(postId).setUserId(userId);
            redisTemplate.opsForSet().add(key,userId.toString());
            this.sendCollectNotify(collect);
            result.put("isCollect",true);
            result.put("collects",redisTemplate.opsForSet().size(key));
        }else{
            Collect collect =new Collect().setPostId(postId).setUserId(userId);
            redisTemplate.opsForSet().remove(key,userId.toString());
            if(!redisTemplate.hasKey(key))
                collectService.cancelPostCollect(postId);
            this.sendCollectNotify(collect);
            result.put("isCollect",false);
            result.put("collects",redisTemplate.opsForSet().size(key));
        }
        return Result.success(result);
    }
    public void sendCollectNotify(Collect collect) throws JsonProcessingException {
        UserVo user=userService.findUserByUserId(collect.getUserId());
//        0 构造评论消息
        CollectMessage collectMessage=new CollectMessage().setPostId(collect.getPostId())
                .setPostCoverUrl(postService.getPostCoverUrlByPostId(collect.getPostId())).setTime(new Date())
                .setUserId(collect.getUserId()).setName(user.getName()).setAvatarUrl(user.getAvatarUrl())
                .setConstant("对你分享点了赞");
//        1 对分享 评论 parentid==null 将消息发给postgerid的用户 username对您的分享postId进行了评论
        String collectPostKey = MSG_COLLECT + collect.getUserId();
        redisTemplate.opsForList().rightPush(collectPostKey, objectMapper.writeValueAsString(collectMessage));
    }

    @GetMapping("/messages")
    public Result receiveCollectMessage() throws IOException {
        Integer userId = BaseContext.get();
        if(userId==null)
            return Result.success("[]");
        System.out.println("receive collect msgs");
        String key = MSG_COLLECT + userId;
        List msgs=new ArrayList();
        while (redisTemplate.opsForList().size(key)>0) {
            String msg = redisTemplate.opsForList().leftPop(key);
            CollectMessage collectMsg = objectMapper.readValue(msg, CollectMessage.class);
            System.out.println(msg);
            msgs.add(collectMsg);
        }
        System.out.println(msgs);
        return Result.success(msgs);
    }


}
