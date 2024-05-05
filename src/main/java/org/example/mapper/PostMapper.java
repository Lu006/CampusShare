package org.example.mapper;

import org.apache.ibatis.annotations.*;
import org.example.enity.Pojo.Post;
import org.example.enity.Pojo.User;
import org.example.enity.Vo.PostActionVo;
import org.example.enity.Vo.PostVo;

import java.util.List;

@Mapper
public interface PostMapper {

    @Select("select * from post where post.id=#{id} and post.state=1")
    Post selectPostById(Integer id);

    @Select("select post.*,user.name as name,user.avatar_url as avatar_url from post join user on post.user_id=user.id where post.id=#{id} and post.state=1")
    PostVo selectPostVoById(Integer id);

//    @Select("select * from post where state=1 and (post.id like concat('%',#{query},'%') or title like concat('%',#{query},'%')) order by created_time desc")
//    List<PostVo> selectLatestPostVos(String query);

    @Select("select post.*,user.name as name,user.avatar_url as avatar_url from post join user on post.user_id=user.id where post.state=1 order by created_time desc")
    List<PostVo> selectLatestPostVos();

    @Select("select post.*,user.name as name,user.avatar_url as avatar_url from post join user on post.user_id=user.id where tag=#{tag} and post.state=1 order by created_time desc")
    List<PostVo> selectLatestPostVosWithTag(Integer tag);

    @Select("select post.*,user.name as name,user.avatar_url as avatar_url from post join user on post.user_id=user.id where post.state=1 and (post.id like concat('%',#{query},'%') or title like concat('%',#{query},'%')) order by collects desc")
    List<PostVo> selectBestPostVos(String query);

    @Select("select post.*,user.name as name,user.avatar_url as avatar_url from post join user on post.user_id=user.id where tag=#{tag} and post.state=1 and (post.id like concat('%',#{query},'%') or title like concat('%',#{query},'%')) order by collects desc")
    List<PostVo> selectBestPostVosWithTab(@Param("query") String query,@Param("tag") Integer tag);


    @Select("select post.*,user.name as name,user.avatar_url as avatar_url from post join user on post.user_id=user.id where post.state=1 and user_id=#{userId}")
    List<PostVo> selectMyPostVosByUserId(Integer userId);


    @Select("select post.*,user.name as name,user.avatar_url as avatar_url from post join user on post.user_id=user.id where post.id in (select collect.post_id from collect where collect.user_id=#{userId})")
    List<PostVo> selectCollectedPostVosByUserId(Integer userId);

    @Select("select * from post where post.id like concat('%',#{query},'%') or title like concat('%',#{query},'%')")
    List<PostActionVo> selectAllPostActionVos(String query);

    //    杂项
    @Select("select user_id from post where id=#{postId}")
    Integer selectUserIdByPostId(Integer postId);

    @Select("select media_urls from post where id=#{postId}")
    String selectMediaUrlsUrlByPostId(Integer postId);

    @Select("SELECT MAX(id) FROM post")
    Integer selectMaxPostId();

    //修改
    @Update("update post set post.state=#{state} where id = #{postId}")
    Boolean updatePostStateByPostId(@Param("postId") Integer postId, @Param("state")Boolean state);

    @Insert("insert into post(user_id,tag,title,content,media_urls) values(#{userId},#{tag},#{title},#{content},#{mediaUrls})")
    int insertPost(Post post);


    @Update("update post set collects = collects +1 where id=#{postId}")
    boolean addCollects(Integer postId);

    @Update("update post set collects = collects -1 where id=#{postId}")
    boolean subCollects(Integer postId);

    @Update("update post set collects = #{total} where id=#{postId}")
    boolean updateCollects(@Param("postId") Integer postId,@Param("total") Integer total);
}
