package org.example.enity.Vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PostVo {
    private Integer id;

    private Integer userId;

    private String title;

    private String name;

    private String avatarUrl;

    private String content;

    private String mediaUrls;

    private Integer collects;

    private Boolean isCollect;

    private Boolean isFollow;
}
