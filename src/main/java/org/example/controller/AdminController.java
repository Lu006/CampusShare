package org.example.controller;


import com.github.pagehelper.PageInfo;
import javafx.geometry.Pos;
import org.example.enity.Pojo.Admin;
import org.example.enity.Pojo.Post;
import org.example.enity.Vo.CommentActionVo;
import org.example.enity.Vo.PostActionVo;
import org.example.enity.Vo.PostVo;
import org.example.enity.Vo.UserActionVo;
import org.example.result.Result;
import org.example.service.AdminService;
import org.example.service.CommentService;
import org.example.service.PostService;
import org.example.service.UserService;
import org.example.utils.IBCFRecommender;
import org.example.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    AdminService adminService;

    @Autowired
    UserService userService;

    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;

    @Autowired
    private IBCFRecommender ibcfRecommender;

    @RequestMapping("/login")
    public Result login(@RequestBody Admin admin){
        System.out.println(admin);
        Admin _admin = adminService.findAdminByUsername(admin.getUsername());
        if(admin.getPassword().equals(_admin.getPassword())) {
            String token=JwtUtils.generateAdminToken(admin.getUsername());
            System.out.println("返回的token:"+token);
            redisTemplate.opsForSet().add("tokens", token);
            return Result.success(token);
        }else{
            return Result.error().setMsg("用户名或密码错误，登陆失败!");
        }
    }

    @GetMapping({"/users/{pageNum}/{pageSize}/{query}","/users/{pageNum}/{pageSize}"})
    public Result findUsers(@PathVariable Integer pageNum, @PathVariable Integer pageSize,@PathVariable(required = false) String query){
        if(query==null)
            query="";
        System.out.println(pageNum);
        PageInfo<UserActionVo> users = userService.findPageUsers(pageNum, pageSize,query);
        System.out.println(users);
        return Result.success(users);
    }

    @PutMapping("/user/{userId}/state/{state}")
    public Result updateUserState(@PathVariable Integer userId,@PathVariable Boolean state){
        Boolean isSuccess=userService.updateUserState(userId,state);
        return isSuccess?Result.success(true):Result.error().setMsg("修改失败");
    }

    @GetMapping({"/posts/{pageNum}/{pageSize}/{query}","/posts/{pageNum}/{pageSize}"})
    public Result getPosts(@PathVariable Integer pageNum, @PathVariable Integer pageSize,@PathVariable(required = false) String query){
        if(query==null)
            query="";
        PageInfo<PostActionVo> posts = postService.findAllPosts(pageNum, pageSize,query);
        System.out.println(posts);
        return Result.success(posts);
    }

    @PutMapping("/post/{postId}/state/{state}")
    public Result updatePostState(@PathVariable Integer postId,@PathVariable Boolean state){
        Boolean isSuccess=postService.updatePostState(postId,state);
        return isSuccess?Result.success(true):Result.error().setMsg("修改失败");
    }

    @GetMapping({"/comments/{pageNum}/{pageSize}","/comments/{pageNum}/{pageSize}/{query}"})
    public Result findComments(@PathVariable Integer pageNum, @PathVariable Integer pageSize,@PathVariable(required = false) String query){
        if(query==null)
            query="";
        PageInfo<CommentActionVo> comments = commentService.findPageComments(pageNum, pageSize,query);
        System.out.println(comments.toString());
        return Result.success(comments);
    }

    @PutMapping("/comment/{commentId}/state/{state}")
    public Result updateCommentState(@PathVariable Integer commentId,@PathVariable Boolean state){
        Boolean isSuccess=commentService.updateCommentState(commentId,state);
        return isSuccess?Result.success(true):Result.error().setMsg("修改失败");
    }
    @GetMapping("/update")
    public void update(){
        ibcfRecommender.update();
    }
}
