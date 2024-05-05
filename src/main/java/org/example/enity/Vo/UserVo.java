package org.example.enity.Vo;

import lombok.Data;

@Data
public class UserVo {
    private Integer id;

    private String phoneNumber;

    private String name;

    private String sex;

    private String birth;

    private String briefIntroduction;

    private String bgPic;

    private String avatarUrl;

    @Override
    public String toString() {
        return "UserVo{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", birth='" + birth + '\'' +
                ", briefIntroduction='" + briefIntroduction + '\'' +
                ", bgPic='" + bgPic + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
