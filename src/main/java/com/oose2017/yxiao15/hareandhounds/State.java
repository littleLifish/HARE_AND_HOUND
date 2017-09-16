package com.oose2017.yxiao15.hareandhounds;

public class State {
    private String gameId;
    private String state;

    //states of the game
    public static final String WAITING_FOR_SECOND_PLAYER = "WAITING_FOR_SECOND_PLAYER";
    public static final String TURN_HARE = "TURN_HARE";
    public static final String TURN_HOUND = "TURN_HOUND";
    public static final String WIN_HARE_BY_ESCAPE = "WIN_HARE_BY_ESCAPE";
    public static final String WIN_HARE_BY_STALLING = "WIN_HARE_BY_STALLING";
    public static final String WIN_HOUND = "WIN_HOUND";

    public State(){}
    public State(String gameId, String state){
        this.gameId = gameId;
        this.state = state;
    }

    public void setGameId(String gameId){
        this.gameId = gameId;
    }
    public void setState(String state){
        this.state = state;
    }

    public String getGameId(){
        return this.gameId;
    }
    public String getState(){
        return this.state;
    }

}
