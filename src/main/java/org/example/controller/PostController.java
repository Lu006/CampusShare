package org.example.controller;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageInfo;
import javafx.geometry.Pos;
import org.apache.ibatis.annotations.Param;
import org.example.context.BaseContext;
import org.example.enity.Pojo.Post;
import org.example.enity.Vo.PostActionVo;
import org.example.enity.Vo.PostDetailVo;
import org.example.enity.Vo.PostVo;
import org.example.mapper.CollectMapper;
import org.example.result.Result;
import org.example.service.CollectService;
import org.example.service.PostService;
import org.example.utils.FilePathUtils;
import org.example.utils.IBCFRecommender;
import org.example.utils.RedisKeyUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/post")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private CollectService collectService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private IBCFRecommender recommender;


    private final String LIKE="collect-";
    private final String FOLLOW="follow-";

    @PostMapping("/publish")
    public Result publishPost(@RequestBody Post post) throws IOException {
        ArrayList<String> mediaUrls_arr=new ArrayList<>();
        String[] mediaTmpUrls = post.getMediaUrls().split(",");
        System.out.println(mediaTmpUrls);
        for(String url:mediaTmpUrls){
            System.out.println(url);
            String mediaUrl= FilePathUtils.transferTmpUrlToPostDir(url);
            mediaUrls_arr.add(mediaUrl);
        }
        String mediaUrls_str = String.join(",", mediaUrls_arr);
        post.setMediaUrls(mediaUrls_str);
        System.out.println(post.toString());
        boolean result = postService.savePost(post);
        if(result)
            return Result.success().setMsg("上传成功");
        else
            return Result.error().setMsg("上传失败，请重试");
    }
    @GetMapping("/detail/{postId}")
    public Result getPostDetail(@PathVariable Integer postId){
        Integer userId = BaseContext.get();
        PostDetailVo postDetailVo = postService.getPostDetail(postId);
        String collectKey=RedisKeyUtil.getCollectorKey(postId);
        String followerKey=RedisKeyUtil.getFollowerKey(postDetailVo.getUserId());
        if(userId!=null){
            String key= RedisKeyUtil.getCollectorKey(postId);
            if(!redisTemplate.hasKey(key)){
                List<Integer> userIds = collectService.selectUserIdsByPostId(postId);
                for(Integer _userId:userIds){
                    redisTemplate.opsForSet().add(key,_userId.toString());
                }
            }
            postDetailVo.setIsCollect(redisTemplate.opsForSet().isMember(collectKey,userId.toString()))
                    .setCollects(redisTemplate.opsForSet().size(collectKey).intValue());
            postDetailVo.setIsFollow(redisTemplate.opsForSet().isMember(followerKey,userId.toString()));
        }
        System.out.println(postDetailVo.toString());
        return Result.success(postDetailVo);
    }


    @GetMapping({"/new/{pageNum}/{pageSize}","/new/{pageNum}/{pageSize}/{tag}"})
    public Result newPost(@PathVariable(required = false) Integer tag,
                          @PathVariable Integer pageNum,@PathVariable Integer pageSize){
        System.out.println("new");
        if(tag==null)
            tag=0;
        PageInfo<PostVo> postVos = postService.findLatestPostVos(tag,pageNum,pageSize);
        postVos.getList().stream().forEach(postVo -> {
            this.completePostVo(postVo);
        });
        System.out.println(postVos);
        return Result.success(postVos);
    }

    @GetMapping({"/search/{pageNum}/{pageSize}/{query}","/search/{pageNum}/{pageSize}/{query}/{tag}"})
    public Result searchPost(@PathVariable Integer pageNum,@PathVariable Integer pageSize,
                @PathVariable(required = false) String query, @PathVariable(required = false) Integer tag){
        System.out.println("search");
        Integer userId= BaseContext.get();
        if(StrUtil.isBlank(query)){
            return Result.error().setMsg("搜索字符串不能为空");
        }
        System.out.println("tag:"+tag.toString());
        if(tag==null){
            tag=0;
        }
        PageInfo<PostVo> postVos = postService.findBestPostVos(query,tag,pageNum,pageSize);
        postVos.getList().stream().forEach(postVo -> {
            this.completePostVo(postVo);
        });
        System.out.println(postVos);
        return Result.success(postVos);
    }

    @GetMapping("/recommend/{lastCollectedPostId}")
    public Result recommendPosts(@PathVariable Integer lastCollectedPostId) {
        List<PostVo> recommendPostVos = postService.getRecommendPostVos(lastCollectedPostId);
        recommendPostVos.forEach(postVo -> {
            this.completePostVo(postVo);
        });
        return Result.success(recommendPostVos);
    }

    @GetMapping("/collected/{userId}/{pageNum}/{pageSize}")
    public Result getCollectedPost(@PathVariable Integer userId, @PathVariable Integer pageNum,@PathVariable Integer pageSize){
        PageInfo<PostVo> postVos= postService.findCollectedPosts(userId,pageNum,pageSize);
        postVos.getList().stream().forEach(postVo -> {
            this.completePostVo(postVo);
        });
        return Result.success(postVos);
    }

    @GetMapping("/shared/{userId}/{pageNum}/{pageSize}")
    public Result getMyPost(@PathVariable Integer userId, @PathVariable Integer pageNum, @PathVariable Integer pageSize){
        PageInfo<PostVo> postVos = postService.findMyPosts(userId,pageNum,pageSize);
        postVos.getList().stream().forEach(postVo -> {
            this.completePostVo(postVo);
        });
        System.out.println(postVos);
        return Result.success(postVos);
    }

    public void completePostVo(PostVo postVo){
        Integer userId = BaseContext.get();
        String key = RedisKeyUtil.getCollectorKey(postVo.getId());
        if(!redisTemplate.hasKey(key)){
            List<Integer> userIds = collectService.selectUserIdsByPostId(postVo.getId());
            for(Integer _userId:userIds){
                redisTemplate.opsForSet().add(key,_userId.toString());
            }
        }
        if(userId!=null){
            postVo.setIsCollect(redisTemplate.opsForSet().isMember(key, userId.toString()));
        }
        postVo.setCollects(redisTemplate.opsForSet().size(key).intValue());

    }
}

