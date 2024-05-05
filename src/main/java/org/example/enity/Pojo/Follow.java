package org.example.enity.Pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Follow {
    private Integer fromId;
    private Integer toId;

}
