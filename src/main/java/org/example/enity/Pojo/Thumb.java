package org.example.enity.Pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Thumb {

    private Integer userId;

    private Integer commentId;
}
