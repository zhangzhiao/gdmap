package com.yjy.gdmap.info;

/**
 * Created by Enzo Cotter on 2020-11-09.
 */
public class User {
    private String uname;
    private String upassword;
    private String uid;

    public User(String uname, String upassword, String uid) {
        this.uname = uname;
        this.upassword = upassword;
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpassword() {
        return upassword;
    }

    public void setUpassword(String upassword) {
        this.upassword = upassword;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
