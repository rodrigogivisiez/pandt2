package com.potatoandtomato.games.bots;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.utils.Strings;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.helpers.ArrayLists;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.helpers.RoomMsgHandler;
import com.potatoandtomato.games.models.BoardModel;
import com.potatoandtomato.games.models.GraveModel;
import com.potatoandtomato.games.references.BattleRef;
import com.potatoandtomato.games.references.MovementRef;
import com.potatoandtomato.games.references.StatusRef;
import com.potatoandtomato.games.screens.TerrainLogic;
import com.potatoandtomato.games.statics.Global;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 4/8/2016.
 */
public class Bot implements Disposable {

    private boolean enabled;
    private RoomMsgHandler roomMsgHandler;
    private ChessColor botChessColor;
    private boolean disposed;

    public Bot(RoomMsgHandler roomMsgHandler) {
        this.roomMsgHandler = roomMsgHandler;
    }

    public void start(ChessColor chessColor){
        enabled = true;
        this.botChessColor = chessColor;
    }

    //terrainLogics array contains 32 elements, starting from top left to right
    public void requestMove(final ArrayList<TerrainLogic> terrainLogics, final GraveModel graveModel,
                            final BoardModel boardModel, BattleRef battleRef,
                            final MovementRef movementRef, StatusRef statusRef){

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {

                Threadings.sleep(500);

                int i = 0;
                while (Global.ANIMATION_COUNT > 0){
                    Threadings.sleep(100);
                    if(disposed) return;

                    i++;
                    if(i > 60){     //6 secs, if still no finish animate, force continue;
                        break;
                    }
                }

                Threadings.sleep(1000);


                ////////////////////todo: bot AI decision, start from here

                for(TerrainLogic terrainLogic : terrainLogics){
                    if(!terrainLogic.getChessLogic().getChessModel().getOpened()){
                        openChess(terrainLogic, graveModel);
                        break;
                    }
                }


                ///////////////////////////

            }
        });
    }

    private void openChess(TerrainLogic terrainLogic, GraveModel graveModel){
        String random = Strings.joinArr(ArrayLists.randomNumericArray(2, 0, 4), ",");
        roomMsgHandler.sendChessOpenFull(getBotUserId(), terrainLogic.getTerrainModel().getCol(),
                terrainLogic.getTerrainModel().getRow(), random,
                graveModel.getLeftTimeInt(botChessColor));
    }

    private void moveChess(TerrainLogic fromTerrainLogic, TerrainLogic toTerrainLogic, BattleRef battleRef, GraveModel graveModel){
        int r = MathUtils.random(0, 100);
        int random = 0;
        if(r < 15) random = 1;

        boolean isFromWon = battleRef.getFromIsWinner(fromTerrainLogic.getChessLogic().getChessModel(),
                toTerrainLogic.getChessLogic().getChessModel());

        roomMsgHandler.sendMoveChess(getBotUserId(),
                fromTerrainLogic.getTerrainModel().getCol(), fromTerrainLogic.getTerrainModel().getRow(),
                toTerrainLogic.getTerrainModel().getCol(), toTerrainLogic.getTerrainModel().getRow(),
                isFromWon, String.valueOf(random),
                graveModel.getLeftTimeInt(botChessColor));
    }


    public boolean isEnabled() {
        return enabled;
    }

    private String getBotUserId(){
        return "bot";
    }

    @Override
    public void dispose() {
        disposed = true;
    }
}
