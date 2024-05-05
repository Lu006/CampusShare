package org.example.enity.Message;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Accessors(chain = true)
@ToString(callSuper = true)
//@Builder
public class CommentMessage {
    private Integer userId;
    private String name;
    private String replyName;
    private String content;
    private String constant;
    private String avatarUrl;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    private Integer postId;
    private String postCoverUrl;

    @Override
    public String toString() {
        return "{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", replyName='" + replyName + '\'' +
                ", content='" + content + '\'' +
                ", constant='" + constant + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", time=" + time +
                ", postId=" + postId +
                ", postCoverUrl='" + postCoverUrl + '\'' +
                '}';
    }
}
