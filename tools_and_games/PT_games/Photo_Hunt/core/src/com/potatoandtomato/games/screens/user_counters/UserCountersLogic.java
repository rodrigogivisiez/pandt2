package com.potatoandtomato.games.screens.user_counters;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Disposable;
import com.potatoandtomato.common.GameCoordinator;
import com.potatoandtomato.common.models.Player;
import com.potatoandtomato.common.utils.SafeThread;
import com.potatoandtomato.common.utils.Threadings;
import com.potatoandtomato.games.absintf.GameModelListener;
import com.potatoandtomato.games.models.GameModel;
import com.potatoandtomato.games.models.Services;

import java.util.*;

/**
 * Created by SiongLeng on 12/4/2016.
 */
public class UserCountersLogic implements Disposable {

    private GameModel gameModel;
    private Services services;
    private GameCoordinator gameCoordinator;
    private UserCountersActor userCountersActor;
    private ArrayList<String> showingUsernameUserIds;
    private int sortCd;
    private SafeThread safeThread;

    public UserCountersLogic(GameModel gameModel, Services services, GameCoordinator gameCoordinator) {
        this.gameModel = gameModel;
        this.services = services;
        this.gameCoordinator = gameCoordinator;
        this.showingUsernameUserIds = new ArrayList();
        this.sortCd = 999999;

        this.userCountersActor = new UserCountersActor(services, gameCoordinator);
        setListenersAndThreads();

    }

    public void userCounterChanged(String userId){
        this.getUserCountersActor().updateCounter(userId, gameModel.getUserClickedCount(userId));

        sortCd = 30;
    }

    public void sort(){
        HashMap<String, Integer> userRecords = gameModel.getUserRecords();
        Map<String, Integer> sortedUserRecords = sortByComparator(userRecords, false);

        ArrayList<String> userIdsSortedByRecord = new ArrayList();
        for (Map.Entry<String, Integer> entry : sortedUserRecords.entrySet()) {
            String userId = entry.getKey();
            userIdsSortedByRecord.add(userId);
        }

        for(Player player : gameCoordinator.getIndexToPlayersMap().values()){
            if(!userIdsSortedByRecord.contains(player.getUserId())){
                userIdsSortedByRecord.add(player.getUserId());
            }
        }

        userCountersActor.updateSorting(userIdsSortedByRecord);
    }

    public void setListenersAndThreads(){

        gameModel.addGameModelListener(new GameModelListener() {
            @Override
            public void onAddedClickCount(String userId, int newCount) {
                userCounterChanged(userId);
            }
        });

        for(Map.Entry<String, Table> entry  : userCountersActor.getUserIdToTableMap().entrySet()){
            final String userId = entry.getKey();
            final Table counterTable = entry.getValue().findActor("counterTable");
            counterTable.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    userCountersActor.setUserNameVisibility(userId, true);
                    Threadings.runInBackground(new Runnable() {
                        @Override
                        public void run() {
                            Threadings.sleep(3000);
                            userCountersActor.setUserNameVisibility(userId, false);
                        }
                    });
                }
            });
        }

        final SafeThread safeThread = new SafeThread();
        Threadings.runInBackground(new Runnable() {
            @Override
            public void run() {
                while (true){
                    while (sortCd >= 0){
                        Threadings.sleep(100);
                        sortCd--;
                        if(safeThread.isKilled()) return;
                    }

                    sortCd = 999999;
                    Threadings.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            sort();
                        }
                    });
                }
            }
        });
    }

    private Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean ascOrder)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                if (ascOrder) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public UserCountersActor getUserCountersActor() {
        return userCountersActor;
    }


    @Override
    public void dispose() {
        if(safeThread != null) safeThread.kill();
    }
}
