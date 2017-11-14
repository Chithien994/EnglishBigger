package tcn.com.models;

import java.io.Serializable;

/**
 * Created by MyPC on 02/08/2017.
 */

public class DrawerModels implements Serializable{
    int id;
    int avatar;
    String name;

    public DrawerModels(int id, int avatar, String name) {
        this.id = id;
        this.avatar = avatar;
        this.name = name;
    }

    public DrawerModels(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
