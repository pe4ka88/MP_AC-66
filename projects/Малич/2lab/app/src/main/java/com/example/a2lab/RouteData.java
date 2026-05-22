package com.example.a2lab;

import java.io.Serializable;

public class RouteData implements Serializable {
    private String fromPoint;
    private String toPoint;
    private String date;
    private String time;
    private String passengers;
    private String comment;

    public RouteData(String fromPoint, String toPoint, String date,
                     String time, String passengers, String comment) {
        this.fromPoint = fromPoint != null ? fromPoint : "";
        this.toPoint = toPoint != null ? toPoint : "";
        this.date = date != null ? date : "";
        this.time = time != null ? time : "";
        this.passengers = passengers != null ? passengers : "1";
        this.comment = comment != null ? comment : "";
    }

    public String getFromPoint() { return fromPoint; }
    public String getToPoint() { return toPoint; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getPassengers() { return passengers; }
    public String getComment() { return comment; }
}