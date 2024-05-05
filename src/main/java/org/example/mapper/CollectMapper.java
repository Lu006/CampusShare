package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.enity.Pojo.Collect;

import java.util.List;

@Mapper
public interface CollectMapper {
    @Select("select count(1) from collect where post_id=#{postId} and user_id=#{userId}")
    boolean existCollectByPostIdAndUserId(@Param("postId") Integer postId,@Param("userId") Integer userId);

    @Insert("insert into collect(post_id,user_id)" +
            " values(#{postId},#{userId})")
    boolean insertCollect(Collect collect);

    @Delete("delete from collect where post_id=#{post_id}")
    boolean deleteCollectByPostId(Integer post_id);

    @Select("select * from collect")
    List<Collect> selectAllCollects();

    @Select("select user_id from collect where post_id = #{post_id}")
    List<Integer> selectUserIdsByPostId(Integer postId);
}
