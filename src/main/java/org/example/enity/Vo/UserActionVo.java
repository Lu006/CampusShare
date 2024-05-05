package org.example.enity.Vo;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserActionVo {
    private Integer id;

    private String avatarUrl;

    private String name;

    private String phoneNumber;

    private Boolean state;


}
