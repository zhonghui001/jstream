package com.jyb.controller;

public class ServerConfig {

    private int serverPort = 8080;
    private int maxFromContentSize = 100;

    public ServerConfig setServerPort(int serverPort){
        this.serverPort = serverPort;
        return this;
    }

    public int getServerPort(){
        return serverPort;
    }

    public ServerConfig setMaxFromContentSize(int maxFormMaxFromContentSize){

        this.maxFromContentSize = maxFormMaxFromContentSize;
        return this;
    }

    public int getMaxFromContentSize(){
        return maxFromContentSize;
    }
}
