package com.ec.bgm.movil.application.model;

public class Chat {

    private boolean isWritting;
    private String idPlace;
    private long timestamp;
    private String idUser;
    private String idUserGuest;

    public Chat() {
    }

    public Chat(boolean isWritting, String idPlace, long timestamp, String idUser, String idUserGuest) {
        this.isWritting = isWritting;
        this.idPlace = idPlace;
        this.timestamp = timestamp;
        this.idUser = idUser;
        this.idUserGuest = idUserGuest;
    }

    public boolean getWritting() {
        return isWritting;
    }

    public void setWritting(boolean writting) {
        isWritting = writting;
    }

    public String getIdPlace() {
        return idPlace;
    }

    public void setIdPlace(String idPlace) {
        this.idPlace = idPlace;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdUserGuest() {
        return idUserGuest;
    }

    public void setIdUserGuest(String idUserGuest) {
        this.idUserGuest = idUserGuest;
    }
}