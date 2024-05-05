package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ThumbMapper {
    @Select("select user_id from thumb where comment_id = #{commentId}")
    List<Integer> selectUserIdsByCommentId(Integer commentId);
}
