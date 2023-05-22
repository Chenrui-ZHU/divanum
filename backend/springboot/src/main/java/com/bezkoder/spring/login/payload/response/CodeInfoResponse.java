package com.bezkoder.spring.login.payload.response;

import java.util.List;

public class CodeInfoResponse {

    Long user_id;

    List<String> code_info;

    public CodeInfoResponse(Long user_id, List<String> code_info){
        this.code_info = code_info;
        this.user_id = user_id;
    }
}
