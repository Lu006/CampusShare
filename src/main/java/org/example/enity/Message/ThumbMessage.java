package org.example.enity.Message;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class ThumbMessage {
    private Integer userId;
    private String name;
    private String constant;
    private String content;
    private String avatarUrl;
    private Date time;
    private Integer postId;
    private String postCoverUrl;

}
