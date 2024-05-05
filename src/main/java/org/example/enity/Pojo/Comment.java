package org.example.enity.Pojo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;


@Data
@Accessors(chain = true)
public class Comment {
    private Integer id;

    private Integer postId;


    private Integer parentId;

    private Integer userId;

    private Integer replyId;

    private Integer replyUserId;


    private String content;

//    @JsonIgnore
    private List<Comment> subComments;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private Integer thumbs;

    private Boolean isThumb;

    private Boolean state;

}
