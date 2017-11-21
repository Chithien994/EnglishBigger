package tcn.com.models;

import java.io.Serializable;

/**
 * Created by MyPC on 20/08/2017.
 */

public class TopicModels implements Serializable{
    int id;
    String name;
    String linkImage;
    int percent;
    int total; //%
    String idUser;
    String nameUser;

    public TopicModels(int id, String name, String linkImage, int percent, int total) {
        this.id = id;
        this.name = name;
        this.linkImage = linkImage;
        this.percent = percent;
        this.total = total;
    }

    public TopicModels(int id, String name, String linkImage, int percent, int total, String idUser, String nameUser) {
        this.id = id;
        this.name = name;
        this.linkImage = linkImage;
        this.percent = percent;
        this.total = total;
        this.idUser = idUser;
        this.nameUser = nameUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
