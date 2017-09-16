package com.oose2017.yxiao15.hareandhounds;

public class Board {
    private String gameId;
    private int boardNumber;
    //hare position
    private int hareX;
    private int hareY;
    //hound1 position
    private int houndX1;
    private int houndY1;
    //hound2 position
    private int houndX2;
    private int houndY2;
    //hound3 postion
    private int houndX3;
    private int houndY3;

    public Board(){}

    public Board(String gameId, int boardNumber, int hareX, int hareY, int houndX1, int houndY1, int houndX2, int houndY2,
                 int houndX3, int houndY3){
        this.gameId = gameId;
        this.boardNumber = boardNumber;
        this.hareX = hareX;
        this.hareY = hareY;
        this.houndX1 = houndX1;
        this.houndY1 = houndY1;
        this.houndX2 = houndX2;
        this.houndY2 = houndY2;
        this.houndX3 = houndX3;
        this.houndY3 = houndY3;
    }
    public String getGameId() {
        return gameId;
    }
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getBoardNumber() {
        return boardNumber;
    }
    public void setBoardNumber(int boardNumber) {
        this.boardNumber = boardNumber;
    }

    public int getHareX() {
        return hareX;
    }
    public void setHareX(int hareX) {
        this.hareX = hareX;
    }

    public int getHareY() {return hareY;}
    public void sethareY(int hareY) {
        this.hareY = hareY;
    }

    public int gethoundX1() {
        return houndX1;
    }
    public void sethoundX1(int houndX1) {
        this.houndX1 = houndX1;
    }

    public int gethoundY1() {return houndY1;}
    public void sethoundY1(int houndY1) {
        this.houndY1 = houndY1;
    }

    public int gethoundX2() {
        return houndX2;
    }
    public void sethoundX2(int houndX2) {
        this.houndX2 = houndX2;
    }

    public int gethoundY2() {return houndY2;}
    public void sethoundY2(int houndY2) {
        this.houndY2 = houndY2;
    }

    public int gethoundX3() {
        return houndX3;
    }
    public void sethoundX3(int houndX3) {
        this.houndX3 = houndX3;
    }

    public int gethoundY3() {return houndY3;}
    public void sethoundY3(int houndY3) {
        this.houndY3 = houndY3;
    }
}
