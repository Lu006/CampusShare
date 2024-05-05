package org.example.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import org.example.context.BaseContext;
import org.example.enity.Pojo.Comment;
import org.example.enity.Message.CommentMessage;
import org.example.enity.Vo.CommentDto;
import org.example.enity.Vo.CommentVo;
import org.example.result.Result;
import org.example.service.PostService;
import org.example.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/comment")
@RestController
public class CommentController {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PostService postService;

    private final String MSG_COMMENT="message-comment-";

    @GetMapping("/more/{postId}/{pageNum}/{pageSize}")
    public Result getComments(@PathVariable Integer postId,@PathVariable Integer pageNum,@PathVariable Integer pageSize){
        PageInfo<CommentVo> comments = commentService.findCommentByPostId(postId,pageNum,pageSize);
        System.out.println(comments.toString());
        return Result.success(comments);
    }

    @PostMapping("/commit")
    public Result commitComment(@RequestBody CommentDto commentDto) throws JsonProcessingException {
        System.out.println(commentDto);
        Comment comment=new Comment().setTime(new Date());
        BeanUtils.copyProperties(commentDto,comment);
        boolean is_success = commentService.commitComment(comment);
        return Result.success("回复评论成功");
    }

    public void sendCommentNotify(CommentDto commentDto) throws JsonProcessingException {

//        0 构造评论消息
        CommentMessage commentMessage = new CommentMessage().setUserId(commentDto.getUserId()).setName(commentDto.getUserName())
                .setAvatarUrl(commentDto.getUserAvatarUrl()).setTime(commentDto.getTime())
                .setPostId(commentDto.getPostId()).setPostCoverUrl(postService.getPostCoverUrlByPostId(commentDto.getPostId()));

//        1 对分享 评论 parentid==null 将消息发给postgerid的用户 username对您的分享postId进行了评论
        Integer postgerId = postService.findUserIdByPostId(commentDto.getPostId());
        String postUserKey=MSG_COMMENT+postgerId;
        commentMessage.setContent(commentDto.getContent()).setConstant("评论了你的分享");
        redisTemplate.opsForList().rightPush(postUserKey, objectMapper.writeValueAsString(commentMessage));

//        2 对评论 回复 parentid!=null replyid==null 将消息发给postgerid的用户和userid的用户 username对relpyname的评论进行了回复
        Integer parentId = commentDto.getParentId();
        Integer parentUserId = commentService.findUserIdByParentId(parentId);
        if(parentId!=null && !parentUserId.equals(postgerId)) {
            String perentCommentUserKey = MSG_COMMENT + parentUserId;
            commentMessage.setContent(commentDto.getContent()).setConstant("回复了你的评论");
            redisTemplate.opsForList().rightPush(perentCommentUserKey, objectMapper.writeValueAsString(commentMessage));
        }

//        3 对回复 回复 parentid!=null replyid!=null 将消息发给postgerid的用户和userid的用户以及 username对您的分享进行了评论
//        CommentMessagelogId(comment.getPostId()).postCoverUrl(postService.getPostCoverUrlByPostId(comment.getPostId()));
        Integer replyUserId = commentDto.getReplyUserId();
        if(replyUserId!=null && !replyUserId.equals(postgerId) && !replyUserId.equals(parentUserId)) {
            String replyUserKey = MSG_COMMENT + replyUserId;
//            String replyContent= commentService.getContentByCommentId(comment.getId());
            commentMessage.setContent(commentDto.getContent()).setConstant("回复了你的回复");
            redisTemplate.opsForList().rightPush(replyUserKey,objectMapper.writeValueAsString(commentMessage));
        }
    }

    @GetMapping("/messages")
    public Result receiveCommentMessage() throws JsonProcessingException {
        Integer userId = BaseContext.get();
        if(userId==null)
            return Result.success("[]");
        List msgs=new ArrayList();
        String key=MSG_COMMENT+userId;
        while (redisTemplate.opsForList().size(key)>0) {
            String msg = redisTemplate.opsForList().leftPop(key);
            msgs.add(msg);
        }
        System.out.println(msgs);
        return Result.success(msgs);
    }

}
