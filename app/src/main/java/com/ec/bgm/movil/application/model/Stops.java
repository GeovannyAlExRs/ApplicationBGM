package com.ec.bgm.movil.application.model;

public class Stops {

    private String idStops;
    private String idSender;
    private String idReceiver;
    private String idChat;
    private String idPlace;
    private String message;
    private long timestamp;
    private boolean status;

    public Stops() {
    }

    public Stops(String idStops, String idSender, String idReceiver, String idChat, String idPlace, String message, long timestamp, boolean status) {
        this.idStops = idStops;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.idChat = idChat;
        this.idPlace = idPlace;
        this.message = message;
        this.timestamp = timestamp;
        this.status = status;
    }

    public String getIdStops() {
        return idStops;
    }

    public void setIdStops(String idStops) {
        this.idStops = idStops;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
