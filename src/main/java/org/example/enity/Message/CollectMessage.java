package org.example.enity.Message;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class CollectMessage {
    private Integer userId;
    private String name;
    private String constant;
    private String avatarUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    private Integer postId;
    private String postCoverUrl;

}

