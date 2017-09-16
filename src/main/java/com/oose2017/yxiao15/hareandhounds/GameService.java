package com.oose2017.yxiao15.hareandhounds;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.UUID;
import java.util.List;

import javax.sql.DataSource;

public class GameService {
    private Sql2o db;

    private final Logger logger = LoggerFactory.getLogger(GameService.class);

    /**
     * Construct the model with a pre-defined datasource. The current implementation
     * also ensures that the DB schema is created if necessary.
     *
     * @param dataSource
     */
    public GameService(DataSource dataSource) throws GameService.GameServiceException {
        db = new Sql2o(dataSource);

        //Create the schema for the database if necessary. This allows this
        //program to mostly self-contained. But this is not always what you want;
        //sometimes you want to create the schema externally via a script.
        try (Connection conn = db.open()) {
            //Create 3 tables: game, board state.
            String sql_createGame = "CREATE TABLE IF NOT EXISTS game (game_id TEXT, player_id TEXT, pieceType TEXT, " +
            "                                           primary key(game_id, player_id))" ;
            String sql_createBoard = "CREATE TABLE IF NOT EXISTS board (game_id TEXT, board_number INTEGER, " +
                    "                                  hare_x INTEGER, hare_y INTEGER, hound_x1 INTEGER, hound_y1 INTEGER," +
                    "                                  hound_x2 INTEGER, hound_y2 INTEGER, hound_x3 INTEGER, hound_y3 INTEGER, " +
                    "                                  primary key(game_id, board_number))" ;
            String sql_createState = "CREATE TABLE IF NOT EXISTS state (game_id TEXT PRIMARY KEY, " +
                    "                                  state TEXT)";

            conn.createQuery(sql_createGame).executeUpdate();
            conn.createQuery(sql_createBoard).executeUpdate();
            conn.createQuery(sql_createState).executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("Failed to create schema at startup", ex);
            throw new GameService.GameServiceException("Failed to create schema at startup", ex);
        }
    }

    /**
     * Fetch all game entries in the list
     *
     * @return List of all game entries
     */
    /*
    public List<Game> findAll() throws GameService.GameServiceException {
       //Todo
        return game;
    }
    */

    /**
     * Create a new Game entry.
     */
    public Game createNewGame(String body) throws GameService.GameServiceException {
        //get the pieceType from Json
        Game game = new Gson().fromJson(body, Game.class);
        Board board = new Board("null", 0, 4, 1, 1, 0, 0, 1, 1, 2);
        State state = new State("null", State.WAITING_FOR_SECOND_PLAYER);

        //generate a UUID as the gameId
        UUID uuid = UUID.randomUUID();
        String gameId = uuid.toString();

        //set gameId and playerId
        game.setGameId(gameId);
        game.setPlayerId("01");
        //set board gameId
        board.setGameId(gameId);
        //set state gameId
        state.setGameId(gameId);

        //write into game table
        String sql_game = "INSERT INTO game (game_id, player_id, pieceType) " +
                "             VALUES (:gameId, :playerId, :pieceType)" ;
        //write board into board table
        String sql_board = "INSERT INTO board (game_id, board_number, hare_x, hare_y, hound_x1, hound_y1, hound_x2, hound_y2, hound_x3, hound_y3) " +
                "             VALUES (:gameId, :boardNumber, :hareX, :hareY, :houndX1, :houndY1, :houndX2, :houndY2, :houndX3, :houndY3)" ;
        //write state into state table
        String sql_state = "INSERT INTO state (game_id, state) " +
                "             VALUES (:gameId, :state)" ;

        try (Connection conn = db.open()) {
            conn.createQuery(sql_game)
                    .bind(game)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("GameService.createNewGame: Failed to create new entry in game table", ex);
            throw new GameService.GameServiceException("GameService.createNewGame: Failed to create new entry", ex);
        }

        try (Connection conn = db.open()) {
            conn.createQuery(sql_board)
                    .bind(board)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("GameService.createNewGame: Failed to insert in the board table ", ex);
            throw new GameService.GameServiceException("GameService.joinGame: Failed to create new entry", ex);
        }

        try (Connection conn = db.open()) {
            conn.createQuery(sql_state)
                    .bind(state)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("GameService.createNewGame: Failed to insert in the state table", ex);
            throw new GameService.GameServiceException("GameService.joinGame: Failed to create new entry", ex);
        }

        return game;
    }

    /**
     * Join a Game.
     */
    public Game joinGame(String gameId, String pieceType01) throws GameService.GameServiceException {
        //get the pieceType from Json
        Game game = new Game();
        State state = new State(gameId, State.TURN_HOUND);

        game.setGameId(gameId);
        game.setPlayerId("02");
        if (pieceType01.equals("HOUND"))
            game.setPieceType("HARE");
        else
            game.setPieceType("HOUND");

        //Insert the second player
        String sql_game = "INSERT INTO game (game_id, player_id, pieceType) " +
                "             VALUES (:gameId, :playerId, :pieceType)" ;

        try (Connection conn = db.open()) {
            conn.createQuery(sql_game)
                    .bind(game)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("GameService.createNewGame: Failed to join the second player", ex);
            throw new GameService.GameServiceException("GameService.joinGame: Failed to join the second player", ex);
        }

        //Update the game state to TURN_HOUND
        stateUpdate(state);
        return game;
    }

    /**
     * Play a Game.
     */
    public String play(String gameId, String body) throws GameService.GameServiceException {
        Player player = new Gson().fromJson(body, Player.class);
        Game game = new Game();
        String playerId = player.getPlayerId();

        //Check whether gameId, playerId is invalid
        /*
        **
        */
        List<Game> games = gameFindAll(gameId);
        if (games.size() == 1) {
            throw new GameService.GameServiceException("GameService.playGame: " + GameController.INCORRECT_TURNS);
        }
        else if(games.size() != 2)
            throw new GameService.GameServiceException("GameService.playGame: " + GameController.INVALID_GAME_ID);
        for (Game tmpGame : games){
            if (tmpGame.getPlayerId().equals(player.getPlayerId()))
                game = tmpGame;
        }
        if (game == null)
            throw new GameService.GameServiceException("GameService.playGame: " + GameController.INVALID_PLAYER_ID);

        /*
        **Check if it is an invalid turn
        */
        State state = stateFind(gameId);
        if (!state.getState().equals("TURN_" + game.getPieceType()))
            throw new GameService.GameServiceException("GameService.playGame: " + GameController.INCORRECT_TURNS);


        Board board = boardFind(gameId);
        String preHare = String.valueOf(board.getHareX()) + String.valueOf(board.getHareY());
        String preHound1 = String.valueOf(board.gethoundX1()) + String.valueOf(board.gethoundY1());
        String preHound2 = String.valueOf(board.gethoundX2()) + String.valueOf(board.gethoundY2());
        String preHound3 = String.valueOf(board.gethoundX3()) + String.valueOf(board.gethoundY3());

        /*
        **Check if it is illegal move
        **If is, throw exception
        **If not, Insert the new board into table, Check whether a player wins, Update the state
        */
        //If If TURN_HARE
        if (game.getPieceType().equals("HARE")){
            //check if is the hate's position on the board
            if (board.getHareX() == player.getFromX() && board.getHareY() == player.getFromY()){
                String curHare = String.valueOf(player.getToX()) + String.valueOf(player.getToY());
                //check if X-x<=1, Y-y<=1, (X,Y) != (x,y), (X,Y) != FOUR_Empty_Cells,
                //check (X,Y) != other hounds' positions on the board
                if (Math.abs(player.getFromX() - player.getToX()) <= 1
                        && Math.abs(player.getFromY() - player.getToY()) <= 1
                        && !preHare.equals(curHare) && !curHare.equals(preHound1)
                        //&& !curHare.equals("00") && !curHare.equals("02") && !curHare.equals("40") && !curHare.equals("42")
                         && !curHare.equals(preHound2) && !curHare.equals(preHound3)){

                    //Insert the new board into board table
                    board.setBoardNumber(board.getBoardNumber() + 1);
                    board.setHareX(player.getToX());
                    board.sethareY(player.getToY());
                    boardInsert(board);

                    //Check whether the hare wins
                    //Update the state
                    if (player.getToX() <= board.gethoundX1() && player.getToX() <= board.gethoundX2()
                            && player.getToX() <= board.gethoundX3()){
                        state.setState(State.WIN_HARE_BY_ESCAPE);
                        stateUpdate(state);
                    }
                    else{
                        //Update state
                        state.setState(State.TURN_HOUND);
                        stateUpdate(state);
                    }

                }
                else {
                        logger.error("GameService.playGame: " + GameController.ILLEGAL_MOVE);
                        throw new GameService.GameServiceException("GameService.playGame: " + GameController.ILLEGAL_MOVE);
                    }
                }
            else {
                    logger.error("GameService.playGame: " + GameController.ILLEGAL_MOVE + " empty position");
                    throw new GameService.GameServiceException("GameService.playGame: " + GameController.ILLEGAL_MOVE);
                }
            }
        else {
            //If TURN_HOUND
            String fromHound = String.valueOf(player.getFromX()) + String.valueOf(player.getFromY());
            String toHound = String.valueOf(player.getToX()) + String.valueOf(player.getToY());
            int whichHound = -1;
            //check if fromHound is one of the hounds' positions
            if (fromHound.equals(preHound1))
                whichHound = 1;
            else if(fromHound.equals(preHound2))
                whichHound = 2;
            else if(fromHound.equals(preHound3))
                whichHound = 3;
            if (whichHound > 0){
                //check if it is a valid move. -1<=X-x<=0, to(X,Y) is empty
                if ( player.getToX() - player.getFromX() <= 1 && player.getToX() - player.getFromX() >= 0
                        && Math.abs(player.getFromY() - player.getToY()) <= 1
                        && !toHound.equals(preHare) && !toHound.equals(preHound1)
                        && !toHound.equals(preHound2) && !toHound.equals(preHound3)){

                    //Insert new board into table board
                    int newBoardNumber = board.getBoardNumber() + 1;
                    switch (whichHound) {
                        case 1:
                            board.setBoardNumber(newBoardNumber);
                            board.sethoundX1(player.getToX());
                            board.sethoundY1(player.getToY());
                            boardInsert(board);
                            break;
                        case 2:
                            board.setBoardNumber(newBoardNumber);
                            board.sethoundX2(player.getToX());
                            board.sethoundY2(player.getToY());
                            boardInsert(board);
                            break;
                        case 3:
                            board.setBoardNumber(newBoardNumber);
                            board.sethoundX3(player.getToX());
                            board.sethoundY3(player.getToY());
                            boardInsert(board);
                            break;
                        default:
                            break;
                    }

                    //Todo
                    /*
                    * Check whether the hound wins
                    * --Check whether HARE is trapped,
                    * --Check whether the same board position occurs three times
                    */
                    //List<Board> boards = boardFindALL(gameId);
                    /*
                    if (){
                        state.setState(State.WIN_HOUND);
                        state.setState(State.WIN_HARE_BY_STALLING);
                        stateUpdate(state);
                    }
                    else{
                        //Update state
                        state.setState(State.TURN_HARE);
                        stateUpdate(state);
                    }
                    */
                    //Update state
                    state.setState(State.TURN_HARE);
                    stateUpdate(state);

                }
                else {
                    logger.error("GameService.playGame: " + GameController.ILLEGAL_MOVE);
                    throw new GameService.GameServiceException("GameService.playGame: " + GameController.ILLEGAL_MOVE);
                }
            }
            else {
                logger.error("GameService.playGame: " + GameController.ILLEGAL_MOVE + " empty position");
                throw new GameService.GameServiceException("GameService.playGame: " + GameController.ILLEGAL_MOVE);
            }
        }

        return playerId;
    }

    /**
     * Describe the game board.
     */
    public Board getBoard(String gameId) throws GameServiceException {
        //find gameId in the board table
        Board board =  boardFind(gameId);
        if (board != null)
            return board;
        else
            return null;
    }

    /**
     * Describe the game state.
     */
    public State getState(String gameId)throws GameServiceException {
        //find gameId in the board table
        State state =  stateFind(gameId);
        if (state != null)
            return state;
        else
            return null;
    }

    //-----------------------------------------------------------------------------//
    // Database operation
    //-----------------------------------------------------------------------------//
    public List<Game> gameFindAll(String gameId)throws GameService.GameServiceException {
        String sql = "SELECT * FROM game WHERE game_id = :gameId" ;
        try (Connection conn = db.open()) {
            List<Game> games =  conn.createQuery(sql)
                    .addParameter("gameId", gameId)
                    .addColumnMapping("game_id", "gameId")
                    .addColumnMapping("player_id", "playerId")
                    .executeAndFetch(Game.class);
            return games;
        } catch(Sql2oException ex) {
            logger.error("GameService.findAll: Failed to query database", ex);
            throw new GameServiceException("GameService.findAll: Failed to query database", ex);
        }
    }

    //Find the current board, the onw with the largest board_number
    public Board boardFind(String gameId)throws GameService.GameServiceException {
        String sql = "SELECT * FROM board WHERE game_id = :gameId AND board_number = " +
                "(SELECT MAX(board_number) FROM board WHERE game_id = :gameId) " ;
        //game_id, hare_x, hare_y, hound_x1, hound_y1, hound_x2, hound_y2, " + "hound_x3, hound_y3"
        try (Connection conn = db.open()) {
            Board board =  conn.createQuery(sql)
                    .addParameter("gameId", gameId)
                    .addColumnMapping("game_id", "gameId")
                    .addColumnMapping("board_number", "boardNumber")
                    .addColumnMapping("hare_x", "hareX")
                    .addColumnMapping("hare_y", "hareY")
                    .addColumnMapping("hound_x1", "houndX1")
                    .addColumnMapping("hound_y1", "houndY1")
                    .addColumnMapping("hound_x2", "houndX2")
                    .addColumnMapping("hound_y2", "houndY2")
                    .addColumnMapping("hound_x3", "houndX3")
                    .addColumnMapping("hound_y3", "houndY3")
                    .executeAndFetchFirst(Board.class);
            return board;
        } catch(Sql2oException ex) {
            logger.error("GameService.findAll: Failed to query database board", ex);
            throw new GameServiceException("GameService.find: Failed to query database board", ex);
        }
    }

    public List<Board> boardFindALL(String gameId)throws GameService.GameServiceException {
        String sql = "SELECT * FROM board WHERE game_id = :gameId" ;
        try (Connection conn = db.open()) {
            List<Board> boards =  conn.createQuery(sql)
                    .addParameter("gameId", gameId)
                    .addColumnMapping("game_id", "gameId")
                    .addColumnMapping("board_number", "boardNumber")
                    .addColumnMapping("hare_x", "hareX")
                    .addColumnMapping("hare_y", "hareY")
                    .addColumnMapping("hound_x1", "houndX1")
                    .addColumnMapping("hound_y1", "houndY1")
                    .addColumnMapping("hound_x2", "houndX2")
                    .addColumnMapping("hound_y2", "houndY2")
                    .addColumnMapping("hound_x3", "houndX3")
                    .addColumnMapping("hound_y3", "houndY3")
                    .executeAndFetch(Board.class);
            return boards;
        } catch(Sql2oException ex) {
            logger.error("GameService.findAll: Failed to query database board", ex);
            throw new GameServiceException("GameService.find: Failed to query database board", ex);
        }
    }

    public void boardInsert(Board board)throws GameService.GameServiceException {
        String sql_board = "INSERT INTO board (game_id, board_number, hare_x, hare_y, hound_x1, hound_y1, hound_x2, hound_y2, hound_x3, hound_y3) " +
                "             VALUES (:gameId, :boardNumber, :hareX, :hareY, :houndX1, :houndY1, :houndX2, :houndY2, :houndX3, :houndY3)" ;
        try (Connection conn = db.open()) {
            conn.createQuery(sql_board)
                    .bind(board)
                    .executeUpdate();
        } catch(Sql2oException ex) {
            logger.error("GameService.findAll: Failed to insert a record into database board", ex);
            throw new GameServiceException("GameService.find: Failed to insert a record into database board", ex);
        }
    }

    public State stateFind(String gameId)throws GameService.GameServiceException {
        String sql = "SELECT * FROM state WHERE game_id = :gameId" ;
        try (Connection conn = db.open()) {
            State state =  conn.createQuery(sql)
                    .addParameter("gameId", gameId)
                    .addColumnMapping("game_id", "gameId")
                    .executeAndFetchFirst(State.class);
            return state;
        } catch(Sql2oException ex) {
            logger.error("GameService.findAll: Failed to find Id in database state table", ex);
            throw new GameServiceException("GameService.find: Failed to find Id database state", ex);
        }
    }

    public void stateUpdate(State state)throws GameService.GameServiceException {
        String sql = "UPDATE state SET state = :state WHERE game_id = :gameId ";
        String gameId = state.getGameId();
        try (Connection conn = db.open()) {
            //Update the item
            conn.createQuery(sql)
                    .bind(state)
                    .addParameter("gameId", gameId)
                    .executeUpdate();

            //Verify that we did indeed update something
            if (getChangedRows(conn) != 1) {
                logger.error(String.format("GameService.update: Table state. Update operation did not update rows. Incorrect id(?): %s", gameId));
                throw new GameServiceException(String.format("GameService.update: Table state. Update operation did not update rows. Incorrect id (?): %s", gameId), null);
            }
        } catch(Sql2oException ex) {
            logger.error(String.format("TodoService.update: Failed to update state table for id: %s", gameId), ex);
            throw new GameServiceException(String.format("TodoService.update: Failed to update database table state for id: %s", gameId), ex);
        }

    }


    //-----------------------------------------------------------------------------//
    // Helper Classes and Methods
    //-----------------------------------------------------------------------------//

    public static class GameServiceException extends Exception {
        public GameServiceException(String message, Throwable cause) {
            super(message, cause);
        }

        public GameServiceException(String message) {
            super(message);
        }
    }

    /**
     * This Sqlite specific method returns the number of rows changed by the most recent
     * INSERT, UPDATE, DELETE operation. Note that you MUST use the same connection to get
     * this information
     */
    private int getChangedRows(Connection conn) throws Sql2oException {
        return conn.createQuery("SELECT changes()").executeScalar(Integer.class);
    }
}
