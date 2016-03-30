package com.potatoandtomato.games.references;

import com.potatoandtomato.common.Threadings;
import com.potatoandtomato.games.assets.Sounds;
import com.potatoandtomato.games.enums.ChessAnimal;
import com.potatoandtomato.games.enums.ChessColor;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;
import com.potatoandtomato.games.services.SoundsWrapper;
import com.potatoandtomato.games.helpers.Terrains;
import com.potatoandtomato.games.screens.TerrainLogic;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 25/2/2016.
 */
public class StatusRef {

    private SoundsWrapper _soundsWrapper;
    private long _abilityTriggeredAnimateTime = 1000;

    public StatusRef(SoundsWrapper _soundsWrapper) {
        this._soundsWrapper = _soundsWrapper;
    }

    public void chessOpened(final ArrayList<TerrainLogic> terrains, final TerrainLogic openedLogic, ChessColor myChessColor, String random){
        switch (openedLogic.getChessLogic().getChessModel().getChessAnimal()){
            case DOG:
                dogEffect(terrains, openedLogic, myChessColor);
                break;
            case CAT:
                catEffect(terrains, openedLogic, random);
                break;
            case TIGER:
                tigerEffect(terrains, openedLogic);
                break;
            case LION:
                lionEffect(terrains, openedLogic);
                break;
            case ELEPHANT:
                elephantEffect(terrains, openedLogic);
                break;
            default:
                break;
        }
    }

    public void chessMoved(final ArrayList<TerrainLogic> terrains, final TerrainLogic winnerLogic, ChessType winnerChessType,
                           final ChessType loserChessType, final String random){

        transformAnimalIfNeeded(winnerLogic, random);

//        final Runnable transformRunnable = new Runnable() {
//            @Override
//            public void run() {
//                if(loserChessType != ChessType.NONE){
//                    winnerLogic.getChessLogic().getChessModel().addKillCount();
//                    transformAnimalIfNeeded(winnerLogic, random);
//                }
//                else{
//                }
//            }
//        };

        switch (loserChessType.toChessAnimal()){
            case MOUSE:
                mouseEffect(winnerLogic, loserChessType);
                break;
            case WOLF:
                wolfEffect(terrains, loserChessType);
                break;
            default:
                break;
        }
    }

    public void suddenDeathStatus(final ArrayList<TerrainLogic> terrains){
        for(TerrainLogic terrainLogic : terrains){
            if(terrainLogic.getChessLogic().getChessModel().getStatus() != Status.ANGRY && !terrainLogic.isEmpty()){
                setStatus(terrainLogic, Status.ANGRY);
            }
            _soundsWrapper.playSounds(Sounds.Name.ANGRY);
        }
    }

    public void turnOver(final ArrayList<TerrainLogic> terrains){
        for(TerrainLogic terrainLogic : terrains){
            terrainLogic.getChessLogic().getChessModel().addStatusTurn();
            if(terrainLogic.getChessLogic().getChessModel().getStatus() == Status.POISON){
                if(terrainLogic.getChessLogic().getChessModel().getStatusTurn() > 8){
                    setStatus(terrainLogic, Status.NONE);
                }
            }
            if(terrainLogic.getChessLogic().getChessModel().getStatus() == Status.PARALYZED){
                if(terrainLogic.getChessLogic().getChessModel().getStatusTurn() > 4){
                    setStatus(terrainLogic, Status.NONE);
                }
            }
            if(terrainLogic.getChessLogic().getChessModel().getStatus() == Status.DECREASE){
                if(terrainLogic.getChessLogic().getChessModel().getStatusTurn() > 4){
                    setStatus(terrainLogic, Status.NONE);
                }
            }
        }
    }

    private void lionEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic lionLogic){
        final ArrayList<TerrainLogic> targetLogics = getLionEffectTargets(terrains, lionLogic);

        if(targetLogics.size() > 0){
            showAbility(lionLogic, lionLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.LION);
            Threadings.delay(_abilityTriggeredAnimateTime, new Runnable() {
                @Override
                public void run() {
                    ChessColor lionChessColor = lionLogic.getChessLogic().getChessModel().getChessColor();
                    boolean found = false;
                    for(TerrainLogic terrainLogic : targetLogics){
                          setStatus(terrainLogic, Status.DECREASE);
                    }
                    _soundsWrapper.playSounds(Sounds.Name.DECREASE);
                }
            });
        }

    }

    private ArrayList<TerrainLogic> getLionEffectTargets(final ArrayList<TerrainLogic> terrains, final TerrainLogic lionLogic){
        ArrayList<TerrainLogic> terrainLogics = new ArrayList<TerrainLogic>();
        ChessColor lionChessColor = lionLogic.getChessLogic().getChessModel().getChessColor();
        for (TerrainLogic terrainLogic : terrains) {
            if(terrainLogic.getChessLogic().getChessModel().getChessColor() != lionChessColor && terrainLogic.isOpened() && !terrainLogic.isEmpty()){
                terrainLogics.add(terrainLogic);
            }
        }
        return terrainLogics;
    }

    private void tigerEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic tigerLogic){
        final ArrayList<TerrainLogic> targetLogics = getTigerEffectTargets(terrains, tigerLogic);

        if(targetLogics.size() > 0){
            showAbility(tigerLogic, tigerLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.TIGER);
            Threadings.delay(_abilityTriggeredAnimateTime, new Runnable() {
                @Override
                public void run() {
                    ChessColor tigerChessColor = tigerLogic.getChessLogic().getChessModel().getChessColor();
                    boolean found = false;
                    for (TerrainLogic terrainLogic : targetLogics) {
                           setStatus(terrainLogic, Status.PARALYZED);
                    }

                    _soundsWrapper.playSounds(Sounds.Name.PARALYZED);
                }
            });
        }
    }

    private ArrayList<TerrainLogic> getTigerEffectTargets(final ArrayList<TerrainLogic> terrains, final TerrainLogic tigerLogic){
        ArrayList<TerrainLogic> terrainLogics = new ArrayList<TerrainLogic>();
        ChessColor tigerChessColor = tigerLogic.getChessLogic().getChessModel().getChessColor();
        for (TerrainLogic terrainLogic : terrains) {
            if (terrainLogic.getChessLogic().getChessModel().getChessColor() != tigerChessColor && terrainLogic.isOpened() && !terrainLogic.isEmpty()){
                terrainLogics.add(terrainLogic);
            }
        }
        return terrainLogics;
    }

    private void elephantEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic elephantLogic){
        final ArrayList<TerrainLogic> targetLogics = getElephantEffectTargets(terrains, elephantLogic);

        if(targetLogics.size() > 0){
            showAbility(elephantLogic, elephantLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.ELEPHANT);
            Threadings.delay(_abilityTriggeredAnimateTime, new Runnable() {
                @Override
                public void run() {
                    boolean found = false;
                    for (final TerrainLogic terrainLogic : targetLogics) {
                          setStatus(terrainLogic, Status.NONE);
                    }

                    _soundsWrapper.playSounds(Sounds.Name.HEAL);
                }
            });
        }
    }

    private ArrayList<TerrainLogic> getElephantEffectTargets(final ArrayList<TerrainLogic> terrains, final TerrainLogic elephantLogic){
        ArrayList<TerrainLogic> terrainLogics = new ArrayList<TerrainLogic>();
        for (final TerrainLogic terrainLogic : terrains) {
            if (isNegativeStatus(terrainLogic.getChessLogic().getChessModel().getStatus()) &&
                    terrainLogic.getChessLogic().getChessModel().getChessColor() == elephantLogic.getChessLogic().getChessModel().getChessColor()) {
                terrainLogics.add(terrainLogic);
            }
        }
        return terrainLogics;
    }

    private void wolfEffect(final ArrayList<TerrainLogic> terrains, final ChessType wolfChessType){
        boolean found = false;
        for(final TerrainLogic terrainLogic : terrains){
            if(terrainLogic.getChessLogic().getChessModel().getChessType() == wolfChessType){
                final Runnable effectRunnable = new Runnable() {
                    @Override
                    public void run() {
                        showAbility(terrainLogic, wolfChessType, true, ChessAnimal.WOLF);
                        Threadings.delay(_abilityTriggeredAnimateTime, new Runnable() {
                            @Override
                            public void run() {
                                setStatus(terrainLogic, Status.INCREASE);
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
    }

    private void mouseEffect(final TerrainLogic terrainLogic, final ChessType mouseChessType){
        Threadings.delay(500, new Runnable() {
            @Override
            public void run() {
                showAbility(terrainLogic, mouseChessType, false, ChessAnimal.MOUSE);
                Threadings.delay(_abilityTriggeredAnimateTime, new Runnable() {
                    @Override
                    public void run() {
                        if(setStatus(terrainLogic, Status.POISON)) _soundsWrapper.playSounds(Sounds.Name.POISON);
                    }
                });
            }
        });
    }

    private void catEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic openedLogic,
                           String random){
        final ArrayList<TerrainLogic> targetLogics = getCatEffectTargets(terrains, openedLogic, random);

        if(targetLogics.size() > 0){
            Threadings.runInBackground(new Runnable() {
                @Override
                public void run() {
                    final int[] i = {0};
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            showAbility(openedLogic, openedLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.CAT);

                            Threadings.delay(_abilityTriggeredAnimateTime, new Runnable() {
                                @Override
                                public void run() {
                                    for(TerrainLogic logic : targetLogics){
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
                }
            });
        }
    }

    private ArrayList<TerrainLogic> getCatEffectTargets(final ArrayList<TerrainLogic> terrains,  final TerrainLogic openedLogic,
                                                        String random){
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
        return processedMouseLogics;
    }

    private void dogEffect(final ArrayList<TerrainLogic> terrains, final TerrainLogic openedLogic, ChessColor myChessColor){
        final ArrayList<TerrainLogic> targetLogics = getDogEffectTargets(terrains, openedLogic);
        final boolean revealChess = myChessColor == openedLogic.getChessLogic().getChessModel().getChessColor();

        if(targetLogics.size() > 0){
            Threadings.postRunnable(new Runnable() {
                @Override
                public void run() {

                    showAbility(openedLogic, openedLogic.getChessLogic().getChessModel().getChessType(), true, ChessAnimal.DOG);

                    Threadings.delay(_abilityTriggeredAnimateTime, new Runnable() {
                        @Override
                        public void run() {
                            for(TerrainLogic logic : targetLogics){
                                logic.getChessLogic().getChessActor().previewChess(revealChess, null);
                            }
                        }
                    });
                }
            });
        }
    }

    private ArrayList<TerrainLogic> getDogEffectTargets(final ArrayList<TerrainLogic> terrains, final TerrainLogic openedLogic){
        final ArrayList<TerrainLogic> targetLogics = new ArrayList<TerrainLogic>();
        final ArrayList<TerrainLogic> adjacentLogics = getAdjacentTerrains(terrains, openedLogic);
        for(TerrainLogic logic : adjacentLogics) {
            if (!logic.isOpened() && !logic.isEmpty()) {
                targetLogics.add(logic);
            }
        }

        return targetLogics;
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

    private boolean setStatus( final TerrainLogic logic, final Status status){
        if( logic.getChessLogic().getChessModel().getStatus() == Status.ANGRY ||  logic.getChessLogic().getChessModel().getStatus() == Status.KING){
            return false;
        }
        else{
            Threadings.delay(1000, new Runnable() {
                @Override
                public void run() {
                    logic.getChessLogic().getChessModel().setStatus(status);
                    logic.getChessLogic().invalidate();
                }
            });
            logic.getChessLogic().getChessActor().setStatusIcon(status, true);
            return true;
        }
    }

    private void showAbility(final TerrainLogic terrainLogic, ChessType chessType, boolean hideChessAnimal, ChessAnimal chessAnimal){
        _soundsWrapper.playAnimalSound(chessAnimal);
        terrainLogic.getChessLogic().getChessActor().showAbilityTriggered(chessType, hideChessAnimal);
    }

    private boolean isNegativeStatus(Status status){
        return status == Status.POISON || status == Status.PARALYZED || status == Status.INJURED || status == Status.DECREASE;
    }


    private void transformAnimalIfNeeded(TerrainLogic animalLogic, String random){
        if(animalLogic.getChessLogic().getChessModel().canTransform() && random.equals("1")){
            setStatus(animalLogic, Status.KING);
            _soundsWrapper.playSounds(Sounds.Name.KING);
        }
    }

}
