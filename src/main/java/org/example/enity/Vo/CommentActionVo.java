package org.example.enity.Vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.example.enity.Pojo.Comment;

import java.util.Date;
import java.util.List;

@Data
public class CommentActionVo {
    private Integer id;

    private Integer postId;

    private Integer userId;

    private String userName;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;

    private Boolean state;

    @Override
    public String toString() {
        return "CommentActionVo{" +
                "id=" + id +
                ", postId=" + postId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", state=" + state +
                '}';
    }
}
