package com.zhenghao.usercenter.model.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@Data
public class UserLoginRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 5247525323546093944L;

    private String UserAccount;
    private String UserPassword;


}
