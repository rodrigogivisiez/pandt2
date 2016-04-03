package com.potatoandtomato.games.references;

import com.badlogic.gdx.math.MathUtils;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.ChessModel;

import java.util.HashMap;

/**
 * Created by SiongLeng on 1/1/2016.
 */
public class BattleRef {

    private HashMap<String, HashMap<String, Integer>> refs;

    public BattleRef() {
        refs = new HashMap<String, HashMap<String, Integer>>();
        populateAll();
    }

    private void populateAll(){
        refs.put("MOUSE", mouse());
        refs.put("CAT", cat());
        refs.put("DOG", dog());
        refs.put("WOLF", wolf());
        refs.put("TIGER", tiger());
        refs.put("LION", lion());
        refs.put("ELEPHANT", elephant());
    }

    private HashMap<String, Integer> mouse(){
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("MOUSE", 50);
        result.put("CAT", 0);
        result.put("DOG", 4);
        result.put("WOLF", 1);
        result.put("TIGER", 1);
        result.put("LION", 1);
        result.put("ELEPHANT", 95);
        return result;
    }

    private HashMap<String, Integer> cat(){
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("MOUSE", 100);
        result.put("CAT", 50);
        result.put("DOG", 40);
        result.put("WOLF", 10);
        result.put("TIGER", 1);
        result.put("LION", 1);
        result.put("ELEPHANT", 1);
        return result;
    }

    private HashMap<String, Integer> dog(){
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("MOUSE", 100);
        result.put("CAT", 60);
        result.put("DOG", 52);
        result.put("WOLF", 20);
        result.put("TIGER", 3);
        result.put("LION", 1);
        result.put("ELEPHANT", 1);
        return result;
    }

    private HashMap<String, Integer> wolf(){
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("MOUSE", 100);
        result.put("CAT", 100);
        result.put("DOG", 80);
        result.put("WOLF", 52);
        result.put("TIGER", 10);
        result.put("LION", 5);
        result.put("ELEPHANT", 1);
        return result;
    }

    private HashMap<String, Integer> tiger(){
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("MOUSE", 100);
        result.put("CAT", 100);
        result.put("DOG", 100);
        result.put("WOLF", 90);
        result.put("TIGER", 53);
        result.put("LION", 30);
        result.put("ELEPHANT", 1);
        return result;
    }

    private HashMap<String, Integer> lion(){
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("MOUSE", 100);
        result.put("CAT", 100);
        result.put("DOG", 100);
        result.put("WOLF", 95);
        result.put("TIGER", 70);
        result.put("LION", 53);
        result.put("ELEPHANT", 3);
        return result;
    }

    private HashMap<String, Integer> elephant(){
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        result.put("MOUSE", 10);
        result.put("CAT", 100);
        result.put("DOG", 100);
        result.put("WOLF", 100);
        result.put("TIGER", 94);
        result.put("LION", 90);
        result.put("ELEPHANT", 55);
        return result;
    }

    public int getWinPercent(ChessModel from, ChessModel to){
        String fromAnimal = from.getChessType().name().split("_")[1];
        if(to.getChessType() == ChessType.NONE){
            return 100;
        }
        else{
            String toAnimal = to.getChessType().name().split("_")[1];
            return processStatusPoint(refs.get(fromAnimal).get(toAnimal), from, to);
        }
    }

    //return true(winner is from) or false(winner is to)
    public boolean getFromIsWinner(ChessModel from, ChessModel to){
        int percent = getWinPercent(from, to);
        int random = MathUtils.random(0, 100);

        Logs.show("Winning percent is: " + percent);
        Logs.show("Calculated random is: " + random);

        if(random < percent){
            return true;
        }
        else{
            return false;
        }
    }


    public int processStatusPoint(int original, ChessModel attacker, ChessModel defender){
        Status attackerStatus = attacker.getStatus();
        Status defenderStatus = defender.getStatus();

        int atkAdd = 0;
        int atkMinus = 0;

        if(attackerStatus == Status.ANGRY){
            atkAdd = 10;
        }
        else if(attackerStatus == Status.POISON){
            atkAdd = -30;
        }
        else if(attackerStatus == Status.VENGEFUL){
            atkAdd = 35;
        }
        else if(attackerStatus == Status.KING){
            atkAdd = 20;
        }
        else if(attackerStatus == Status.DECREASE){
            atkAdd = -12;
        }
        else if(attackerStatus == Status.INJURED){
            atkAdd = -40;
        }


        if(defenderStatus == Status.ANGRY){
            atkMinus = 0;
        }
        else if(defenderStatus == Status.POISON){
            atkMinus = 30;
        }
        else if(defenderStatus == Status.VENGEFUL){
            atkMinus = -30;
        }
        else if(defenderStatus == Status.KING){
            atkMinus = -20;
        }
        else if(defenderStatus == Status.DECREASE){
            atkMinus = 5;
        }
        else if(defenderStatus == Status.INJURED){
            atkMinus = 40;
        }


        original = original + atkAdd + atkMinus;
        if(original < 0) original = 0;
        if(original > 100) original = 100;
        return original;
    }


}
