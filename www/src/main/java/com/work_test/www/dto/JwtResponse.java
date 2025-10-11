package com.work_test.www.dto;

import lombok.Data;

@Data
public class JwtResponse {

    private String token; //Токен используется в дальнейшем для осуществления запросов

    public JwtResponse(String token){
        this.token = token;
    }
}
