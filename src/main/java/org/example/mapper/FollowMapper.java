package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.enity.Pojo.Follow;

import java.util.List;

@Mapper
public interface FollowMapper {
//    @Insert("insert into follow(state) values(#{state}) where fromId=#{fromId} and toId=#{toId}")
    @Insert("insert into follow(from_id,to_id) values(#{fromId},#{toId})")
    public boolean insertFollow(Follow follow);

    @Delete("delete from follow where from_id=#{fromId}")
    public boolean deleteFollowByFromId(Integer fromId);

    @Delete("delete from follow where to_id=#{toId}")
    public boolean deleteFollowByToId(Integer toId);

    @Select("select * from follow where to_id=#{toId}")
    public List<Follow> selectFollowsByToId(Integer toId);

    @Select("select * from follow where from_id=#{fromId}")
    public List<Follow> selectFollowsByFromId(Integer fromId);

    @Select("select state from follow where from_id=#{fromId} and to_id=#{toId}")
    Boolean selectStatusByFromIdAndToId(Follow follow);
}
