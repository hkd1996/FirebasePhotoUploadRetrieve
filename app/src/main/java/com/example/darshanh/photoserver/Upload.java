package com.example.darshanh.photoserver;

import java.io.Serializable;

public class  Upload implements Serializable {

    String url;
    Upload(){}
    public Upload(String url) {

        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUri(String url) {
        this.url = url;
    }
}
