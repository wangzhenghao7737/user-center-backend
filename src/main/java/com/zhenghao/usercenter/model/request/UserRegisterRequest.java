package com.zhenghao.usercenter.model.request;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -6808925245589871794L;

    private String userAccount;
    private String userPassword;
    private String check;
    private String planetCode;
}
