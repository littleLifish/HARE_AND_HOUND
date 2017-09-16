package com.oose2017.yxiao15.hareandhounds;

public class Player {
    private String gameId;
    private String playerId;
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;

    public Player(){}

    public Player(String gameId, String playerId, int fromX, int fromY, int toX, int toY){
        this.gameId = gameId;
        this.playerId = playerId;
        this.fromX = fromX;
        this.fromY = fromY;
        this.toX = toX;
        this.toY = toY;
    }

    public void setGameId(String gameId){
        this.gameId = gameId;
    }
    public String getGameId(){
        return this.gameId;
    }

    public void setPlayerId(String playerId){
        this.playerId = playerId;
    }
    public String getPlayerId(){
        return this.playerId;
    }

    public void setFromX(int fromX){
        this.fromX = fromX;
    }
    public int getFromX(){
        return this.fromX;
    }

    public void setFromY(int fromY){
        this.fromY = fromY;
    }
    public int getFromY(){
        return this.fromY;
    }

    public void setToX(int toX){
        this.toX = toX;
    }
    public int getToX(){
        return this.toX;
    }

    public void setToY(int toY){
        this.toY = toY;
    }
    public int getToY(){
        return this.toY;
    }
}
