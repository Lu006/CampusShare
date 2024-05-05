package org.example.enity.Pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Collect {
    private Integer postId;
    private Integer userId;

    @Override
    public String toString() {
        return "Collect{" +
                "postId=" + postId +
                ", userId=" + userId +
                '}';
    }
}
