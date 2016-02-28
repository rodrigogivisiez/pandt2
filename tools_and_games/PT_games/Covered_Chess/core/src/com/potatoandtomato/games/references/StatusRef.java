package com.potatoandtomato.games.references;

import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.games.enums.ChessAnimal;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;
import com.potatoandtomato.games.helpers.SoundsWrapper;
import com.potatoandtomato.games.helpers.Terrains;
import com.potatoandtomato.games.screens.ChessLogic;
import com.potatoandtomato.games.screens.TerrainLogic;
import org.omg.PortableServer.THREAD_POLICY_ID;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by SiongLeng on 25/2/2016.
 */
public class StatusRef {

    private SoundsWrapper _soundsWrapper;

    public StatusRef(SoundsWrapper _soundsWrapper) {
        this._soundsWrapper = _soundsWrapper;
    }

    public void chessOpened(final ArrayList<TerrainLogic> terrains, final TerrainLogic openedLogic, ChessColor myChessColor, String random,
                            Runnable onFinish){
        switch (openedLogic.getChessLogic().getChessModel().getChessAnimal()){
            case DOG:
                dogEffect(terrains, openedLogic, myChessColor, onFinish);
                break;
            case CAT:
                catEffect(terrains, openedLogic, random, onFinish);
                break;
            case TIGER:
                tigerEffect(terrains, openedLogic, onFinish);
                break;
            case LION:
                lionEffect(terrains, openedLogic, onFinish);
                break;
            case ELEPHANT:
                elephantEffect(terrains, openedLogic, onFinish);
                break;
            default:
                onFinish.run();
                break;
        }
    }

    public void chessMoved(final ArrayList<TerrainLogic> terrains, final TerrainLogic winnerLogic, ChessType winnerChessType,
                           final ChessType loserChessType, final String random, final Runnable onFinish){

        final Runnable transformRunnable = new Runnable() {
            @Override
            public void run() {
                if(loserChessType != ChessType.NONE){
                    winnerLogic.getChessLogic().getChessModel().addKillCount();
                    transformAnimalIfNeeded(winnerLogic, random, onFinish == null ? null : onFinish);
                }
                else{
                    if(onFinish != null) onFinish.run();
                }
            }
        };

        switch (loserChessType.toChessAnimal()){
            case MOUSE:
                mouseEffect(winnerLogic, loserChessType, transformRunnable);
                break;
            case WOLF:
                wolfEffect(terrains, loserChessType, transformRunnable);
                break;
            default:
                transformRunnable.run();
                break;
        }
    }

    public void suddenDeathStatus(final ArrayList<TerrainLogic> terrains, final Runnable onFinish){
        for(TerrainLogic terrainLogic : terrains){
            if(terrainLogic.getChessLogic().getChessModel().getStatus() != Status.ANGRY){
                setStatus(terrainLogic, Status.ANGRY, null);
            }
        }
        onFinish.run();
    }

    public void turnOver(final ArrayList<TerrainLogic> terrains){
        for(TerrainLogic terrainLogic : terrains){
            terrainLogic.getChessLogic().getChessModel().addStatusTurn();
            if(terrainLogic.getChessLogic().getChessModel().getStatus() == Status.POISON){
                if(terrainLogic.getChessLogic().getChessModel().getStatusTurn() > 8){
                    setStatus(terrainLogic, Status.NONE, null);
                }
            }
            if(terrainLogic.getChessLogic().getChessModel().getStatus() == Status.PARALYZED){
                if(terrainLogic.getChessLogic().getChessModel().getStatusTurn() > 4){
                    setStatus(terrainLogic, Status.NONE, null);
                }
            }
            if(terrainLogic.getChessLogic().getChessModel().getStatus() == Status.DECREASE){
                if(terrainLogic.getChessLogic().getChessModel().getStatusTurn() > 4){
                    setStatus(terrainLogic, Status.NONE, null);
                }
            }
        }
    }

    private void lionEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic lionLogic, final Runnable onFinish){
        showAbility(lionLogic, lionLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.LION, new Runnable() {
            @Override
            public void run() {
                ChessColor lionChessColor = lionLogic.getChessLogic().getChessModel().getChessColor();
                for(TerrainLogic terrainLogic : terrains){
                    if(terrainLogic.getChessLogic().getChessModel().getChessColor() != lionChessColor && terrainLogic.isOpened()){
                        setStatus(terrainLogic, Status.DECREASE, null);
                    }
                }
                if(onFinish != null) onFinish.run();
            }
        });
    }

    private void tigerEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic tigerLogic, final Runnable onFinish){
        showAbility(tigerLogic, tigerLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.TIGER, new Runnable() {
            @Override
            public void run() {
                ChessColor tigerChessColor = tigerLogic.getChessLogic().getChessModel().getChessColor();
                for(TerrainLogic terrainLogic : terrains){
                    if(terrainLogic.getChessLogic().getChessModel().getChessColor() != tigerChessColor && terrainLogic.isOpened()){
                        setStatus(terrainLogic, Status.PARALYZED, null);
                    }
                }
                if(onFinish != null) onFinish.run();
            }
        });
    }

    private void elephantEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic elephantLogic, final Runnable onFinish){
        showAbility(elephantLogic, elephantLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.ELEPHANT, new Runnable() {
            @Override
            public void run() {
                for (final TerrainLogic terrainLogic : terrains) {
                    if (isNegativeStatus(terrainLogic.getChessLogic().getChessModel().getStatus()) &&
                            terrainLogic.getChessLogic().getChessModel().getChessColor() == elephantLogic.getChessLogic().getChessModel().getChessColor()) {
                        setStatus(terrainLogic, Status.NONE, null);
                    }
                }
                if(onFinish != null) onFinish.run();
            }
        });
    }

    private void wolfEffect(final ArrayList<TerrainLogic> terrains, final ChessType wolfChessType, final Runnable onFinish){
        boolean found = false;
        for(final TerrainLogic terrainLogic : terrains){
            if(terrainLogic.getChessLogic().getChessModel().getChessType() == wolfChessType){
                final Runnable effectRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showAbility(terrainLogic, wolfChessType, true, ChessAnimal.WOLF, new Runnable() {
                            @Override
                            public void run() {
                                setStatus(terrainLogic, Status.INCREASE, onFinish == null ? null : onFinish);
                            }
                        });
                    }
                };

                if(!terrainLogic.isOpened()){
                    terrainLogic.getChessLogic().openChess(new Runnable() {
                        @Override
                        public void run() {
                            effectRunnable.run();
                        }
                    });
                }
                else{
                    effectRunnable.run();
                }

                found = true;
                break;
            }
        }
        if(!found){
            if(onFinish != null) onFinish.run();
        }
    }

    private void mouseEffect(final TerrainLogic terrainLogic, final ChessType mouseChessType, final Runnable onFinish){
        Threadings.delay(500, new Runnable() {
            @Override
            public void run() {
                showAbility(terrainLogic, mouseChessType, false, ChessAnimal.MOUSE, new Runnable() {
                    @Override
                    public void run() {
                        setStatus(terrainLogic, Status.POISON, onFinish == null ? null : onFinish);
                    }
                });
            }
        });
    }

    private void catEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic openedLogic,
                           String random, final Runnable onFinish){
        final ArrayList<TerrainLogic> mouseLogics = getTerrainsByChessType(terrains,
                openedLogic.getChessLogic().getChessModel().getChessColor() == ChessColor.RED ? ChessType.YELLOW_MOUSE : ChessType.RED_MOUSE, true);
        final ArrayList<TerrainLogic> processedMouseLogics = new ArrayList<TerrainLogic>();

        String[] tmp = random.split(",");
        ArrayList<Integer> toGetLogicIndexes = new ArrayList<Integer>();


        for(int q = 0; q < 2; q++){
            int b = 0;
            boolean found = false;
            for(int i = Integer.valueOf(tmp[q]); i<mouseLogics.size(); i++){
                if(!mouseLogics.get(i).isOpened() && !toGetLogicIndexes.contains(i)){
                    toGetLogicIndexes.add(i);
                    found = true;
                    break;
                }
                b++;
            }
            if(b < mouseLogics.size() && !found){
                for(int i = 0; i<(mouseLogics.size() - b); i++){
                    if(!mouseLogics.get(i).isOpened() && !toGetLogicIndexes.contains(i)){
                        toGetLogicIndexes.add(i);
                        break;
                    }
                }
            }
        }

        for(int index : toGetLogicIndexes){
            processedMouseLogics.add(mouseLogics.get(index));
        }

        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                final int[] i = {0};
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        showAbility(openedLogic, openedLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.CAT, new Runnable() {
                            @Override
                            public void run() {
                                for(TerrainLogic logic : processedMouseLogics){
                                    logic.getChessLogic().getChessActor().previewChess(true, new Runnable() {
                                        @Override
                                        public void run() {
                                            i[0]++;
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                while (i[0] < processedMouseLogics.size()){
                    Threadings.sleep(200);
                }
                if(onFinish != null) onFinish.run();
            }
        });

    }

    private void dogEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic openedLogic, ChessColor myChessColor, final Runnable onFinish){
        final ArrayList<TerrainLogic> adjacentLogics = getAdjacentTerrains(terrains, openedLogic);
        final boolean revealChess = myChessColor == openedLogic.getChessLogic().getChessModel().getChessColor();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                final int[] i = {0};
                Threadings.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        _soundsWrapper.playAnimalSound(ChessAnimal.DOG);
                        openedLogic.getChessLogic().getChessActor().showAbilityTriggered(
                                openedLogic.getChessLogic().getChessModel().getChessType(), true, new Runnable() {
                            @Override
                            public void run() {
                                for(TerrainLogic logic : adjacentLogics){
                                    if(logic.isOpened()) i[0]++;
                                    else{
                                        logic.getChessLogic().getChessActor().previewChess(revealChess, new Runnable() {
                                            @Override
                                            public void run() {
                                                i[0]++;
                                            }
                                        });
                                    }
                                }
                            }
                        });
                    }
                });
                while (i[0] < adjacentLogics.size()){
                    Threadings.sleep(200);
                }
                if(onFinish != null) onFinish.run();
            }
        });
    }

    private ArrayList<TerrainLogic> getAdjacentTerrains(final ArrayList<TerrainLogic> terrains, final TerrainLogic openedLogic){
        ArrayList<TerrainLogic> logics = new ArrayList<TerrainLogic>();
        TerrainLogic left = Terrains.getTerrainLogicByPosition(terrains, openedLogic.getTerrainModel().getCol() - 1, openedLogic.getTerrainModel().getRow());
        TerrainLogic top = Terrains.getTerrainLogicByPosition(terrains, openedLogic.getTerrainModel().getCol(), openedLogic.getTerrainModel().getRow() - 1);
        TerrainLogic right = Terrains.getTerrainLogicByPosition(terrains, openedLogic.getTerrainModel().getCol() + 1, openedLogic.getTerrainModel().getRow());
        TerrainLogic bottom = Terrains.getTerrainLogicByPosition(terrains, openedLogic.getTerrainModel().getCol(), openedLogic.getTerrainModel().getRow() + 1);
        if(left != null) logics.add(left);
        if(right != null) logics.add(right);
        if(top != null) logics.add(top);
        if(bottom != null) logics.add(bottom);
        return logics;
    }

    private ArrayList<TerrainLogic> getTerrainsByChessType(final ArrayList<TerrainLogic> terrains, ChessType chessType, boolean closeChessOnly){
        ArrayList<TerrainLogic> logics = new ArrayList<TerrainLogic>();

        for(TerrainLogic logic : terrains){
            if(logic.getChessLogic().getChessModel().getChessType() == chessType){
                if(closeChessOnly && !logic.isOpened()){
                    logics.add(logic);
                }
                else if(!closeChessOnly){
                    logics.add(logic);
                }
            }
        }

        return logics;
    }

    private ArrayList<TerrainLogic> getTerrainsByAnimalType(final ArrayList<TerrainLogic> terrains, ChessAnimal animal, boolean closeChessOnly){
        ArrayList<TerrainLogic> logics = new ArrayList<TerrainLogic>();

        for(TerrainLogic logic : terrains){
            if(logic.getChessLogic().getChessModel().getChessAnimal() == animal){
                if(closeChessOnly && !logic.isOpened()){
                    logics.add(logic);
                }
                else if(!closeChessOnly){
                    logics.add(logic);
                }
            }
        }

        return logics;
    }

    private void setStatus( final TerrainLogic logic, final Status status, final Runnable onFinish){
        Threadings.postRunnable(new Runnable() {
            @Override
            public void run() {
                logic.getChessLogic().getChessActor().setStatusIcon(status, true, new Runnable() {
                    @Override
                    public void run() {
                        logic.getChessLogic().getChessModel().setStatus(status);
                        if (onFinish != null) onFinish.run();
                    }
                });
            }
        });

    }

    private void showAbility(final TerrainLogic terrainLogic, ChessType chessType, boolean hideChessAnimal, ChessAnimal chessAnimal,
                             final Runnable onFinish){

        _soundsWrapper.playAnimalSound(chessAnimal);
        terrainLogic.getChessLogic().getChessActor().showAbilityTriggered(chessType, hideChessAnimal, new Runnable() {
            @Override
            public void run() {
                if(onFinish != null) onFinish.run();
            }
        });
    }

    private boolean isNegativeStatus(Status status){
        return status == Status.POISON || status == Status.PARALYZED || status == Status.INJURED || status == Status.DECREASE;
    }


    private void transformAnimalIfNeeded(TerrainLogic animalLogic, String random, Runnable onFinish){
        if(animalLogic.getChessLogic().getChessModel().canTransform()){
            setStatus(animalLogic, random == "0" ? Status.INJURED : Status.KING, onFinish);
        }
        else{
            if(onFinish != null) onFinish.run();
        }
    }

}
