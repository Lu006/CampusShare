package org.example.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.example.enity.Pojo.Post;
import org.example.enity.Pojo.User;
import org.example.enity.Vo.*;
import org.example.enity.Pojo.Comment;
import org.example.mapper.CollectMapper;
import org.example.mapper.PostMapper;
import org.example.utils.IBCFRecommender;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CollectMapper collectMapper;

    @Autowired
    private CommentService commentService;

    @Autowired
    private IBCFRecommender recommender;

    public Post getPostByPostId(Integer postId){
        return postMapper.selectPostById(postId);
    }

    public PageInfo<PostVo> findLatestPostVos(Integer tag,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<PostVo> postVos;
        if(tag==0)
            postVos=postMapper.selectLatestPostVos();
        else
            postVos=postMapper.selectLatestPostVosWithTag(tag);
        System.out.println(postVos);
        PageInfo<PostVo> postPageInfo = new PageInfo<>(postVos,pageNum);
        return postPageInfo;
    }

    public PageInfo<PostVo> findCollectedPosts(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<PostVo> postVos = postMapper.selectCollectedPostVosByUserId(userId);
        PageInfo<PostVo> postPageInfo = new PageInfo<>(postVos,pageNum);
        return postPageInfo;
    }

    public PageInfo<PostVo> findBestPostVos(String query,Integer tag,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PostVo> postVos;
        if(tag==0){
            postVos= postMapper.selectBestPostVos(query);
        }else{
            postVos = postMapper.selectBestPostVosWithTab(query,tag);
        }
        PageInfo<PostVo> postPageInfo = new PageInfo<>(postVos, pageNum);
        return postPageInfo;
    }

    public PageInfo<PostVo> findMyPosts(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum, pageSize);
        List<PostVo> posts = postMapper.selectMyPostVosByUserId(userId);
        PageInfo<PostVo> postPageInfo = new PageInfo<>(posts);
        return postPageInfo;
    }

    public PostDetailVo getPostDetail(Integer id){
        PostDetailVo postDetailVo = new PostDetailVo();
        PostVo postVo = postMapper.selectPostVoById(id);
        BeanUtils.copyProperties(postVo, postDetailVo);
        PageInfo<CommentVo> commentVos=commentService.findCommentByPostId(id,1,3);
        postDetailVo.setComments(commentVos);
        return postDetailVo;
    }

    //修改
    public boolean savePost(Post post){
        int b = postMapper.insertPost(post);
        return b==1?true:false;
    }

    public Boolean updatePostState(Integer postId,Boolean state){
        return postMapper.updatePostStateByPostId(postId,state);
    }

    //杂项
    public Integer findUserIdByPostId(Integer postId){
        Integer userId = postMapper.selectUserIdByPostId(postId);
        return userId;
    }

    public String getPostCoverUrlByPostId(Integer postId){
        String mediaUrls= postMapper.selectMediaUrlsUrlByPostId(postId);
        String coverUrl=mediaUrls.split(",")[0];
        return coverUrl;
    }


    public List<PostVo> getRecommendPostVos(Integer lastCollectedPostId){
        List<Integer> recommendPostIds = recommender.recommend(lastCollectedPostId,2);
        List<PostVo> recommendPostVos = recommendPostIds.stream().map(recommendPostId ->
                postMapper.selectPostVoById(recommendPostId)).collect(Collectors.toList());
        return recommendPostVos;
    }

    public Boolean clearPostCollect(Integer postId){
        return collectMapper.deleteCollectByPostId(postId);
    }

    public void tramsform2Vos(PageInfo<Post> posts){
        posts.getList().stream().forEach(post -> {
            PostVo postVo = new PostVo();
            BeanUtils.copyProperties(post, postVo);
        });
    }


    //admin
    public PageInfo<PostActionVo> findAllPosts(int pageNum, int pageSize, String query){
        PageHelper.startPage(pageNum, pageSize);
        System.out.println(pageSize);
        List<PostActionVo> postActionVos = postMapper.selectAllPostActionVos(query);
        PageInfo<PostActionVo> postPageInfo = new PageInfo<>(postActionVos,1);
        return postPageInfo;
    }
}
