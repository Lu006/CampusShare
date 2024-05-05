package org.example.enity.Message;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Accessors(chain = true)
public class FollowMessage {
    private Integer fansId;
    private String fansName;
    private String constant;
    private String avatarUrl;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

}

