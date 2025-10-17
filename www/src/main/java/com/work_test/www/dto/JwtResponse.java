package com.work_test.www.dto;

import lombok.Data;

@Data
public class JwtResponse {
    private final String accessToken; //Токен используется в дальнейшем для осуществления запросов
    private final String refreshToken;
}
