package org.example.enity.Vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class CommentVo {
    private Integer id;

    private Integer postId;


    private Integer parentId;

    private Integer userId;

    private String userName;

    private String userAvatarUrl;

    private Integer replyId;

    private Integer replyUserId;

    private String replyUserName;

    private String replyAvatarUrl;

    private String content;

    //    @JsonIgnore
    private List<org.example.enity.Vo.CommentVo> subComments;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private Integer thumbs;

    private Boolean isThumb;

    private Boolean state;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", postId=" + postId +
                ", parentId=" + parentId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userAvatarUrl='" + userAvatarUrl + '\'' +
                ", replyId=" + replyId +
                ", replyUserId=" + replyUserId +
                ", replyUserName='" + replyUserName + '\'' +
                ", replyAvatarUrl='" + replyAvatarUrl + '\'' +
                ", content='" + content + '\'' +
                ", subComments=" + subComments +
                ", time=" + time +
                ", thumbs=" + thumbs +
                ", isThumb=" + isThumb +
                ", state=" + state +
                '}';
    }
}

