package org.example.enity.Pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Post {
    private Integer id;

    private Integer userId;

    private Integer tag;

    private String title;

    private String content;

    private String mediaUrls;

    private Integer collects;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    private Boolean state;

}
