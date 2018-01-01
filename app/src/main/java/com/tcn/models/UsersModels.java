package com.tcn.models;

import java.io.Serializable;

/**
 * Created by MyPC on 14/11/2017.
 */

public class UsersModels implements Serializable {
    String idUser;
    String linkAvatar;
    String name;

    public UsersModels(String idUser, String linkAvatar, String name) {
        this.idUser = idUser;
        this.linkAvatar = linkAvatar;
        this.name = name;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
