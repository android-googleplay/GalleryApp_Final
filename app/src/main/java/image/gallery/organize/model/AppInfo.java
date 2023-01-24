
package image.gallery.organize.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppInfo {

    private String flag;
    private String version;
    private String title;
    private String description;
    private String link;
    private String buttonName;
    private String buttonSkip;
    private String OneSignalAppId;

    public AppInfo() {
    }

    public AppInfo(String flag, String version, String title, String description, String link, String buttonName, String buttonSkip, String oneSignalAppId) {
        this.flag = flag;
        this.version = version;
        this.title = title;
        this.description = description;
        this.link = link;
        this.buttonName = buttonName;
        this.buttonSkip = buttonSkip;
        OneSignalAppId = oneSignalAppId;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getButtonName() {
        return buttonName;
    }

    public void setButtonName(String buttonName) {
        this.buttonName = buttonName;
    }

    public String getButtonSkip() {
        return buttonSkip;
    }

    public void setButtonSkip(String buttonSkip) {
        this.buttonSkip = buttonSkip;
    }

    public String getOneSignalAppId() {
        return OneSignalAppId;
    }

    public void setOneSignalAppId(String oneSignalAppId) {
        OneSignalAppId = oneSignalAppId;
    }
}
