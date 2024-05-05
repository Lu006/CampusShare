package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.enity.Pojo.User;
import org.example.enity.Vo.UserVo;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("select * from user where id like concat('%',#{query},'%') or name like concat('%',#{query},'%')")
    List<User> selectUsers(String query);

    @Select("select * from user where open_id = #{openId} and state =1")
    User findUserByOpenId(String openId);

    @Select("select * from user where id = #{userId} and state=1")
    User findUserByUserId(Integer userId);


    @Select("select * from user where id = #{id} and name collect concat('%','${name}','%') where state=1")
    User findUserByIdAndName(@Param("id")Integer id,@Param("name") String name);

    @Update("update user set name=#{name},avatar_url=#{avatarUrl},sex=#{sex},birth=#{birth}," +
            "brief_introduction=#{briefIntroduction},bg_pic=#{bgPic}," +
            "phone_number=#{phoneNumber} where id=#{id}")
    boolean updateUser(User user);

    @Update("update user set state=#{state} where id=#{userId}")
    Boolean updateUserStateByUserId(@Param("userId") Integer userId,@Param("state") Boolean state);

    @Select("SELECT MAX(id) FROM user")
    Integer selectMaxUserId();

    @Insert("insert into user(name,avatar_url,brief_introduction,open_id) " +
            "values(#{name},#{avatarUrl},#{briefIntroduction},#{openId})")
    Integer insertUser(User user);
}
