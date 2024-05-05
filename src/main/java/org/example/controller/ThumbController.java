package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.context.BaseContext;
import org.example.enity.Pojo.Comment;
import org.example.enity.Pojo.Thumb;
import org.example.enity.Message.ThumbMessage;
import org.example.enity.Vo.CommentVo;
import org.example.result.Result;
import org.example.service.PostService;
import org.example.service.CommentService;
import org.example.service.ThumbService;
import org.example.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/thumb")
public class ThumbController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;

    private final String THUMB="thumb-";
    private final String MSG_THUMB="message-thumb:";

    @Autowired
    private ThumbService thumbService;


    @PostMapping("/{commentId}")
    public Result thumb(@PathVariable Integer commentId) throws JsonProcessingException {
        Map result=new HashMap();
        Integer userId = BaseContext.get();
        if(userId==null)
            return Result.error().setMsg("用户未登录");
        String key= RedisKeyUtil.getThumberKey(commentId);
        if(!redisTemplate.hasKey(key)){
            List<Integer> userIds = thumbService.getUserIdsByCommentId(commentId);
            for(Integer _userId:userIds){
                redisTemplate.opsForSet().add(key,_userId.toString());
            }
        }
        CommentVo commentVo = commentService.getCommentByCommentId(commentId);
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userId.toString());
        if(!isMember){
            redisTemplate.opsForSet().add(key,userId.toString());
            this.sendThumbNotify(commentVo);
            result.put("comment_id",commentVo.getId());
            result.put("isThumb",true);
            result.put("thumbs", redisTemplate.opsForSet().size(key));
        }else{
            redisTemplate.opsForSet().remove(key,userId.toString());
            result.put("comment_id",commentVo.getId());
            result.put("isThumb",false);
            result.put("thumbs", redisTemplate.opsForSet().size(key));
        }
        this.sendThumbNotify(commentVo);
        return Result.success(result);
    }

    public void sendThumbNotify(CommentVo commentVo) throws JsonProcessingException {
        String thumbKey=MSG_THUMB+commentVo.getUserId();
        ThumbMessage thumbMessage=new ThumbMessage()
                .setUserId(commentVo.getUserId()).setName(commentVo.getUserName()).setAvatarUrl(commentVo.getUserAvatarUrl())
                .setPostId(commentVo.getPostId()).setPostCoverUrl(postService.getPostCoverUrlByPostId(commentVo.getPostId()))
                .setTime(new Date()).setConstant("对你的评论点了赞").setContent(commentVo.getContent());
        redisTemplate.opsForList().rightPush(thumbKey,objectMapper.writeValueAsString(thumbMessage));
    }

    @GetMapping("/messages")
    public Result receiveThumbMessage(){
        Integer userId = BaseContext.get();
        if(userId==null)
            return Result.error().setMsg("用户未登录");
        System.out.println("receive collect msgs");
        String key = MSG_THUMB + userId;
        List msgs=new ArrayList();
        while (redisTemplate.opsForList().size(key)>0) {
            String msg = redisTemplate.opsForList().leftPop(key);
            System.out.println(msg);
            msgs.add(msg);
        }
        System.out.println(msgs);
        return Result.success(msgs);
    }
}
