package org.example.enity.Vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class CommentDto {
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

}

