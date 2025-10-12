package com.work_test.www.dto;

import com.work_test.www.model.RoleName;
import lombok.Data;

@Data
public class RegisterRequest {

    private String userName;
    private String password;
    private RoleName role;
}
