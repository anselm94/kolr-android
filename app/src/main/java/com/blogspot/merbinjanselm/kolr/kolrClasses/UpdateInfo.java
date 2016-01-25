package com.blogspot.merbinjanselm.kolr.kolrClasses;

/**
 * Created by anselm94 on 17/7/15.
 */
public class UpdateInfo {

    private String infoHeader;
    private  String infoDetail;
    private String btnText;
    private String btnActionURL;

    private int versionCode;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public UpdateInfo()
    {
        infoHeader = "null";
        infoDetail = "null";
        btnText = "null";
        btnActionURL = "null";
    }

    public String getInfoHeader() {
        return infoHeader;
    }

    public void setInfoHeader(String infoHeader) {
        this.infoHeader = infoHeader;
    }

    public String getInfoDetail() {
        return infoDetail;
    }

    public void setInfoDetail(String infoDetail) {
        this.infoDetail = infoDetail;
    }

    public String getBtnText() {
        return btnText;
    }

    public void setBtnText(String btnText) {
        this.btnText = btnText;
    }

    public String getBtnActionURL() {
        return btnActionURL;
    }

    public void setBtnActionURL(String btnActionURL) {
        this.btnActionURL = btnActionURL;
    }
}
