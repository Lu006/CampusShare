package org.example.enity.Vo;

import com.github.pagehelper.PageInfo;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.example.enity.Pojo.Comment;

import java.util.List;

@Data
@Accessors(chain = true)
public class PostDetailVo {
    private Integer id;

    private Integer userId;

    private String title;

    private String name;

    private String avatarUrl;

    private String content;

    private String mediaUrls;

    private Integer collects;

    private PageInfo<CommentVo> comments;

    private Boolean isCollect;

    private Boolean isFollow;


    @Override
    public String toString() {
        return "PostDetailVo{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", content='" + content + '\'' +
                ", mediaUrls='" + mediaUrls + '\'' +
                ", collects=" + collects +
                ", comments=" + comments +
                '}';
    }
}
