package org.example.enity.Pojo;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Admin {
    private String username;

    private String password;
}
