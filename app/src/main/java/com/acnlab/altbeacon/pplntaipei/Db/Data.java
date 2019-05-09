package com.acnlab.altbeacon.pplntaipei.Db;

public class Data {
    private int urutan;
    private int id;
    private String dataUser;
    private String dataPos;
    private String status;
    private String timestamp;

    public Data(){

    }
    public Data(int urutan, int id, String dataUser, String dataPos, String status, String timestamp) {
        this.urutan=urutan;
        this.id = id;
        this.dataUser = dataUser;
        this.dataPos = dataPos;
        this.status = status;
        this.timestamp = timestamp;
    }

    public int getUrutan() {
        return urutan;
    }

    public void setUrutan(int urutan) {
        this.urutan = urutan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDataUser() {
        return dataUser;
    }

    public void setDataUser(String dataUser) {
        this.dataUser = dataUser;
    }

    public String getDataPos() {
        return dataPos;
    }

    public void setDataPos(String dataPos) {
        this.dataPos = dataPos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


}
