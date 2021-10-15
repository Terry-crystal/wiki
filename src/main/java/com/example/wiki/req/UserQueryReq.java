package com.example.wiki.req;

/**
 * @author Crystal
 * @version 1.0
 * @date 2021/9/25 15:48
 */
public class UserQueryReq extends PageReq {

    private String loginName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "UserQueryReq{" +
                "loginName='" + loginName + '\'' +
                "} " + super.toString();
    }

}
