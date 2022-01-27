package com.ec.bgm.movil.application.model;

import java.util.ArrayList;

public class Chat {

    private String idChat;
    private boolean isWritting;
    private String idPlace;
    private long timestamp;
    private String idUser;
    private String idUserGuest;
    private ArrayList<String> ids;

    public Chat() {
    }

    public Chat(String idChat, boolean isWritting, String idPlace, long timestamp, String idUser, String idUserGuest, ArrayList<String> ids) {
        this.idChat = idChat;
        this.isWritting = isWritting;
        this.idPlace = idPlace;
        this.timestamp = timestamp;
        this.idUser = idUser;
        this.idUserGuest = idUserGuest;
        this.ids = ids;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
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

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }
}