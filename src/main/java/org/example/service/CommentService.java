package org.example.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.context.BaseContext;
import org.example.enity.Pojo.Comment;
import org.example.enity.Pojo.Thumb;
import org.example.enity.Pojo.User;
import org.example.enity.Vo.CommentActionVo;
import org.example.enity.Vo.CommentVo;
import org.example.enity.Vo.UserActionVo;
import org.example.enity.Vo.UserVo;
import org.example.mapper.CommentMapper;
import org.example.mapper.UserMapper;
import org.example.utils.RedisKeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;


    private final String THUMB_USER_SET="thumb:userid:";

    public boolean commitComment(Comment comment){
        boolean result = commentMapper.insertComment(comment);
        return result;
    }

    public PageInfo<CommentVo> findCommentByPostId(
            Integer postId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<CommentVo> _commentVos = commentMapper.selectPriCommentVosByPostId(postId);
        PageInfo<CommentVo> commentVos = new PageInfo<>(_commentVos);
        commentVos.getList().forEach(commentVo -> {
            this.completeComment(commentVo);
            List<CommentVo> subCommentVos =
                    commentMapper.selectSubCommentVosByParentId(commentVo.getId());
            subCommentVos.forEach(subCommentVo -> {
                this.completeComment(subCommentVo);
            });
            commentVo.setSubComments(subCommentVos);
        });
        return commentVos;
    }

    public void completeComment(CommentVo commentVo){
        Integer userId= BaseContext.get();
        if(userId!=null){
            commentVo.setIsThumb(redisTemplate.opsForSet()
                    .isMember(RedisKeyUtil.getThumberKey(commentVo.getId()),userId.toString()));
            User user = userMapper.findUserByUserId(commentVo.getUserId());
            commentVo.setUserName(user.getName()).setUserAvatarUrl(user.getAvatarUrl());
            if(commentVo.getReplyUserId()!=null){
                User replyUser=userMapper.findUserByUserId(commentVo.getReplyUserId());
                commentVo.setReplyUserName(replyUser.getName())
                        .setReplyAvatarUrl(replyUser.getAvatarUrl());
            }
        }
        commentVo.setThumbs(redisTemplate.opsForSet()
                .size(RedisKeyUtil.getThumberKey(commentVo.getId())).intValue());
        UserVo userVo = userService.findUserByUserId(commentVo.getUserId());
        commentVo.setUserName(userVo.getName()).setUserAvatarUrl(userVo.getAvatarUrl());
        if(commentVo.getReplyId()!=null){
            UserVo replyUserVo = userService.findUserByUserId(commentVo.getReplyUserId());
            replyUserVo.setName(replyUserVo.getName());
            replyUserVo.setAvatarUrl(replyUserVo.getAvatarUrl());
        }
    }

    public Integer findUserIdByParentId(Integer parentId){
        Integer parentUserId = commentMapper.selectUserIdByParentId(parentId);
        return parentUserId;
    }

    public String getContentByCommentId(Integer commentId){
        String content=commentMapper.selectContentByCommentId(commentId);
        return content;
    }

    public CommentVo getCommentByCommentId(Integer commentId){
        CommentVo commentVo=commentMapper.selectCommentVoByCommentId(commentId);
        return commentVo;
    }

    public boolean saveThumb(Thumb thumb){
        if(commentMapper.existThumbByCommentIdAndUserId(thumb.getCommentId(),thumb.getUserId())){
            commentMapper.updateThumbStatus(thumb);
        }else{
            commentMapper.insertThumb(thumb);
        }
        commentMapper.addCommentThumbs(thumb.getCommentId());
        return true;
    }

    public boolean cancelThumb(Thumb thumb){
        commentMapper.updateThumbStatus(thumb);
        commentMapper.subCommentThumbs(thumb.getCommentId());
        return true;
    }


    public PageInfo<CommentActionVo> findPageComments(int pageNum, int pageSize, String query){
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> comments = commentMapper.selectPageComments(query);
        System.out.println(comments);
        List<CommentActionVo> commentActionVos = comments.stream().map(comment -> {
            CommentActionVo commentActionVo = new CommentActionVo();
            BeanUtils.copyProperties(comment, commentActionVo);
            return commentActionVo;
        }).collect(Collectors.toList());
        PageInfo<CommentActionVo> commentPageInfo = new PageInfo<>(commentActionVos);
        return commentPageInfo;
    }

    public Boolean updateCommentState(Integer commentId,Boolean state){
        return commentMapper.updateCommentStateByCommentId(commentId,state);
    }

}
