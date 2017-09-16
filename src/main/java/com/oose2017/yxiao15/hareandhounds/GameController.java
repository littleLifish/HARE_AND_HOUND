package com.oose2017.yxiao15.hareandhounds;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import static spark.Spark.*;

public class GameController {

    private static final String API_CONTEXT = "hareandhounds/api";

    private final GameService gameService;

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    public static final String INVALID_GAME_ID = "INVALID_GAME_ID";
    public static final String INVALID_PLAYER_ID = "INVALID_PLAYER_ID";
    public static final String INCORRECT_TURNS = "INCORRECT_TURNS";
    public static final String ILLEGAL_MOVE = "ILLEGAL_MOVE";
    public static final String MALFORMED_REQUEST = "MALFORMED_REQUEST";
    public static final String SECOND_PLAYER_ALREADY_JOINED = "SECOND_PLAYER_ALREADY_JOINED";
    public static final String SUCCESS = "SUCCESS";


    public GameController(GameService gameService) {
        this.gameService = gameService;
        setupEndpoints();
    }

    private void setupEndpoints() {
        /**
         * New Game
         */
        post(API_CONTEXT + "/games", "application/json", (request, response) -> {
            try {
                Game ret_game = gameService.createNewGame(request.body());
                response.status(201);
                return ret_game;
            } catch (GameService.GameServiceException ex) {
                if (ex.getMessage().equals("GameService.newGame: " + MALFORMED_REQUEST)) {
                    logger.error("Failed to new the game, MALFORMED_REQUEST");
                    response.status(400);
                    return Collections.EMPTY_MAP;
                }
                else {
                    System.out.println("Failed to create new game" + ex.getCause() + ex.getMessage());
                    logger.error("Failed to create new game" + ex.getCause() + ex.getMessage());
                    response.status(500);
                }
            }
            return Collections.EMPTY_MAP;
        }, new JsonTransformer());

        /**
         * Join the game
         */
        put(API_CONTEXT + "/games/:gameId", "application/json", (request, response) -> {
            try {
                Game game = gameService.joinGame(request.params(":gameId"));
                response.status(200);
                return game;
            } catch (GameService.GameServiceException ex) {
                if (ex.getMessage().equals("GameService.joinGame: " + INVALID_GAME_ID)){
                    logger.error("Failed to join the game, INVALID_GAME_ID");
                    response.status(404);
                    return Collections.EMPTY_MAP;
                }
                else if (ex.getMessage().equals("GameService.joinGame: " + SECOND_PLAYER_ALREADY_JOINED)){
                    logger.error("Failed to join the game, SECOND_PLAYER_ALREADY_JOINED");
                    response.status(410);
                    return Collections.EMPTY_MAP;
                }
                else{
                    logger.error(String.format("Failed to join the game with id: %s", request.params(":gameId")));
                    response.status(500);
                    return Collections.EMPTY_MAP;
                }
            }
        }, new JsonTransformer());
        /*
        put(API_CONTEXT + "/games/:gameId", "application/json", (request, response) -> {
            try {
                //Todo (delete) final: if 0, illegal; if 2, second joined; else, join
                List<Game> games =  gameService.gameFindAll(request.params(":gameId"));
                //invalid game Id
                if (games.size() == 0){
                    logger.error(String.format("Invalid game Id: %s", request.params(":gameId")));
                    response.status(404);
                    return Collections.EMPTY_MAP;
                }
                //Second player already joined
                else if (games.size() >= 2){
                    logger.error(String.format("Second player already joined the game: %s", request.params(":gameId")));
                    response.status(410);
                    return Collections.EMPTY_MAP;
                }
                else {
                    //Join in the game
                    String pieceType_01 = games.get(0).getPieceType();
                    Game game = gameService.joinGame(request.params(":gameId"), pieceType_01);
                    response.status(200);
                    return game;
                }
            } catch (GameService.GameServiceException ex) {
                //Todo 也许要考虑正在加入的时候已经存在
                logger.error(String.format("Failed to join the game with id: %s", request.params(":gameId")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());
        */

        /**
         * Play the game.
         */
        //Todo
        post(API_CONTEXT + "/games/:gameId/turns", "application/json", (request, response) -> {
            try {
                String playerId = gameService.play(request.params(":gameId"), request.body());
                response.status(200);
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("playerId", playerId);
                return map;
            } catch (GameService.GameServiceException ex) {
                if (ex.getMessage().equals("GameService.playGame: " + INVALID_GAME_ID)){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("reason","INVALID_GAME_ID");
                    logger.error("Failed to play the game, INVALID_GAME_ID");
                    response.status(404);
                    return map;
                }
                else if (ex.getMessage().equals("GameService.playGame: " + INVALID_PLAYER_ID)){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("reason","INVALID_PLAYER_ID");
                    logger.error("Failed to play the game, INVALID_PLAYER_ID");
                    response.status(404);
                    return map;
                }
                else if (ex.getMessage().equals("GameService.playGame: " + INCORRECT_TURNS)){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("reason","INCORRECT_TURN");
                    logger.error("Failed to play the game, INCORRECT_TURN");
                    response.status(422);
                    return map;
                }
                else if (ex.getMessage().equals("GameService.playGame: " + ILLEGAL_MOVE)){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("reason","ILLEGAL_MOVE");
                    logger.error("Failed to play the game, ILLEGAL_MOVE");
                    response.status(422);
                    return map;
                }
                else if (ex.getMessage().equals("GameService.playGame: " + MALFORMED_REQUEST)){
                    logger.error("Failed to play the game, MALFORMED_REQUEST");
                    response.status(400);
                    return Collections.EMPTY_MAP;
                }
                else {
                    logger.error("Failed to play the game" + ex.getCause());
                    response.status(500);
                    return Collections.EMPTY_MAP;
                }
            }
        }, new JsonTransformer());

        /**
         * Get the game board.
         */
        get(API_CONTEXT + "/games/:gameId/board", "application/json", (request, response) -> {
            try {
                Board board = gameService.getBoard(request.params(":gameId"));
                response.status(200);
                if (board != null){
                    //return format
                    HashMap<String,String> map1 = new HashMap<String,String>();
                    map1.put("pieceType", "HARE");
                    map1.put("x", String.valueOf(board.getHareX()));
                    map1.put("y", String.valueOf(board.getHareY()));
                    HashMap<String,String> map2 = new HashMap<String,String>();
                    map2.put("pieceType", "HOUND");
                    map2.put("x", String.valueOf(board.gethoundX1()));
                    map2.put("y", String.valueOf(board.gethoundY1()));
                    HashMap<String,String> map3 = new HashMap<String,String>();
                    map3.put("pieceType", "HOUND");
                    map3.put("x", String.valueOf(board.gethoundX2()));
                    map3.put("y", String.valueOf(board.gethoundY2()));
                    HashMap<String,String> map4 = new HashMap<String,String>();
                    map4.put("pieceType", "HOUND");
                    map4.put("x", String.valueOf(board.gethoundX3()));
                    map4.put("y", String.valueOf(board.gethoundY3()));

                    ArrayList<HashMap<String,String>> array = new ArrayList<HashMap<String, String>>();
                    array.add(map1);
                    array.add(map2);
                    array.add(map3);
                    array.add(map4);

                    return array;
                }
                else{
                    logger.error(String.format("Invalid game Id: %s", request.params(":gameId")));
                    response.status(404);
                    return Collections.EMPTY_MAP;
                }

            } catch (GameService.GameServiceException ex) {
                logger.error(String.format("Failed to find object with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());

        /**
         * Get the game state.
         */
        get(API_CONTEXT + "/games/:gameId/state", "application/json", (request, response) -> {
            try {
                State state = gameService.getState(request.params(":gameId"));
                if (state != null){
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("state", state.getState());
                    response.status(200);
                    return map;
                }
                else{
                    logger.error(String.format("Invalid game Id: %s", request.params(":gameId")));
                    response.status(404);
                    return Collections.EMPTY_MAP;
                }
            } catch (GameService.GameServiceException ex) {
                logger.error(String.format("Failed to find object with id: %s", request.params(":id")));
                response.status(500);
                return Collections.EMPTY_MAP;
            }
        }, new JsonTransformer());


    }
}
