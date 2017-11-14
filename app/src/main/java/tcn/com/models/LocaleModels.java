package tcn.com.models;

/**
 * Created by MyPC on 08/08/2017.
 */

public class LocaleModels {
    String linkAvatar;
    String language;

    public LocaleModels(String language) {
        this.language = language;
    }

    public LocaleModels(String linkAvatar, String language) {
        this.linkAvatar = linkAvatar;
        this.language = language;
    }

    public String getLinkAvatar() {
        return linkAvatar;
    }

    public void setLinkAvatar(String linkAvatar) {
        this.linkAvatar = linkAvatar;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
