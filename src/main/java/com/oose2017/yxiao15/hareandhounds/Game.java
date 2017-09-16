package com.oose2017.yxiao15.hareandhounds;

public class Game {
    private String gameId;
    private String playerId;
    private String pieceType;
    /*
    private int x0;
    private int y0;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    */

    public Game(){}

    public Game(String gameId, String playerId, String pieceType) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.pieceType = pieceType;
    }

    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPlayerId() {
        return playerId;
    }
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPieceType() {
        return pieceType;
    }
    public void setPieceType(String pieceType) {
        this.pieceType = pieceType;
    }
/*
    public int getX0() {
        return x0;
    }
    public void setX0(int x0) {
        this.x0 = x0;
    }

    public int getY0() {return y0;}
    public void setY0(int y0) {
        this.y0 = y0;
    }

    public int getX1() {
        return x1;
    }
    public void setX1(int x1) {
        this.x1 = x1;
    }

    public int getY1() {return y1;}
    public void setY1(int y1) {
        this.y1 = y1;
    }

    public int getX2() {
        return x2;
    }
    public void setX2(int x2) {
        this.x2 = x2;
    }

    public int getY2() {return y2;}
    public void setY2(int y2) {
        this.y2 = y2;
    }
*/

    @Override
    public String toString() {
        return "Game{" +
                "gameId='" + gameId + '\'' +
                ", playerId='" + playerId + '\'' +
                ", pieceType=" + pieceType +
                /*
                ", x0=" + x0 +
                ", y0=" + y0 +
                ", x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                */
                '}';
    }
}
