package org.example.enity.Pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User {
    private Integer id;

    private String phoneNumber;

    private String name;

    private String sex;

    private String birth;

    private String briefIntroduction;

    private String bgPic;

    private String openId;

    private String avatarUrl;

    private Boolean state;
}
