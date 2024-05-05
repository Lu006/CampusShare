package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.enity.Pojo.Comment;
import org.example.enity.Pojo.Thumb;
import org.example.enity.Vo.CommentActionVo;
import org.example.enity.Vo.CommentVo;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("select * from comment where post_id = #{postId} and parent_id is null and state=1")
    public List<CommentVo> selectPriCommentVosByPostId(Integer postId);

    @Select("select * from comment where parent_id=#{parentId} and state=1")
    public List<CommentVo> selectSubCommentVosByParentId(@Param("parentId") Integer parentId);

    @Insert("insert into comment(post_id,parent_id,user_id," +
            "reply_id,reply_user_id,content) " +
            "values(#{postId},#{parentId},#{userId}," +
            "#{replyId},#{replyUserId},#{content})")
    public boolean insertComment(Comment comment);

    @Select("select user_id from comment where id=#{parentId} and state=1")
    Integer selectUserIdByParentId(Integer parentId);


    @Select("select content from comment where id=#{commentId} and state=1")
    String selectContentByCommentId(Integer commentId);

    @Select("select * from comment where id=#{commentId} and state=1")
    CommentVo selectCommentVoByCommentId(Integer commentId);

    @Select("select count(1) from comment_thumb where comment_id=#{commentId} and user_id=#{userId} and state=1")
    boolean existThumbByCommentIdAndUserId(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

    @Update("update comment_thumb set state=#{state} where comment_id=#{commentId} and user_id=#{userId}")
    boolean updateThumbStatus(Thumb thumb);

    @Insert("insert comment_thumb values(#{commentId},#{userId},#{state})")
    boolean insertThumb(Thumb thumb);

    @Update("update comment set thumbs=thumbs+1 where id=#{commentId}")
    boolean addCommentThumbs(Integer commentId);

    @Update("update comment set thumbs=thumbs-1 where id=#{commentId}")
    boolean subCommentThumbs(Integer commentId);

    @Select("select * from comment where id like concat('%',#{query},'%') "+
            "or user_id like concat('%',#{query},'%')"+
            "or user_name like concat('%',#{query},'%')"+
            "or content like concat('%',#{query},'%')")
    List<Comment> selectPageComments(String query);

    @Update("update comment set state=#{state} where id = #{commentId}")
    Boolean updateCommentStateByCommentId(@Param("commentId") Integer commentId,@Param("state") Boolean state);
}
