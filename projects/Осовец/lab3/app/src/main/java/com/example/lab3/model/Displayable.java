package com.example.lab3.model;

import java.io.Serializable;

public interface Displayable extends Serializable {
    int getId();
    String getTitle();
    String getSubtitle();
    String getDetailInfo();
    String getImageUrl();
    String toCsvRow();
    String getTypeName();
}
