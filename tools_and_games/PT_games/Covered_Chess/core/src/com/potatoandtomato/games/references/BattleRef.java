package com.potatoandtomato.games.references;

import com.badlogic.gdx.math.MathUtils;
import com.potatoandtomato.common.utils.SafeDouble;
import com.potatoandtomato.games.enums.ChessType;
import com.potatoandtomato.games.enums.Status;
import com.potatoandtomato.games.helpers.Logs;
import com.potatoandtomato.games.models.ChessModel;

import java.util.HashMap;

/**
 * Created by SiongLeng on 1/1/2016.
 */
public class BattleRef {

    private HashMap<String, HashMap<String, SafeDouble>> refs;

    public BattleRef() {
        refs = new HashMap<String, HashMap<String, SafeDouble>>();
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

    private HashMap<String, SafeDouble> mouse(){
        HashMap<String, SafeDouble> result = new HashMap<String, SafeDouble>();
        result.put("MOUSE", new SafeDouble(50));
        result.put("CAT", new SafeDouble(0));
        result.put("DOG", new SafeDouble(4));
        result.put("WOLF", new SafeDouble(1));
        result.put("TIGER", new SafeDouble(1));
        result.put("LION", new SafeDouble(1));
        result.put("ELEPHANT", new SafeDouble(95));
        return result;
    }

    private HashMap<String, SafeDouble> cat(){
        HashMap<String, SafeDouble> result = new HashMap<String, SafeDouble>();
        result.put("MOUSE", new SafeDouble(100));
        result.put("CAT", new SafeDouble(50));
        result.put("DOG", new SafeDouble(40));
        result.put("WOLF", new SafeDouble(10));
        result.put("TIGER", new SafeDouble(1));
        result.put("LION", new SafeDouble(1));
        result.put("ELEPHANT", new SafeDouble(1));
        return result;
    }

    private HashMap<String, SafeDouble> dog(){
        HashMap<String, SafeDouble> result = new HashMap<String, SafeDouble>();
        result.put("MOUSE", new SafeDouble(100));
        result.put("CAT", new SafeDouble(60));
        result.put("DOG", new SafeDouble(52));
        result.put("WOLF", new SafeDouble(20));
        result.put("TIGER", new SafeDouble(3));
        result.put("LION", new SafeDouble(1));
        result.put("ELEPHANT", new SafeDouble(1));
        return result;
    }

    private HashMap<String, SafeDouble> wolf(){
        HashMap<String, SafeDouble> result = new HashMap<String, SafeDouble>();
        result.put("MOUSE", new SafeDouble(100));
        result.put("CAT", new SafeDouble(100));
        result.put("DOG", new SafeDouble(80));
        result.put("WOLF", new SafeDouble(52));
        result.put("TIGER", new SafeDouble(10));
        result.put("LION", new SafeDouble(5));
        result.put("ELEPHANT", new SafeDouble(1));
        return result;
    }

    private HashMap<String, SafeDouble> tiger(){
        HashMap<String, SafeDouble> result = new HashMap<String, SafeDouble>();
        result.put("MOUSE", new SafeDouble(100));
        result.put("CAT", new SafeDouble(100));
        result.put("DOG", new SafeDouble(100));
        result.put("WOLF", new SafeDouble(90));
        result.put("TIGER", new SafeDouble(53));
        result.put("LION", new SafeDouble(30));
        result.put("ELEPHANT", new SafeDouble(1));
        return result;
    }

    private HashMap<String, SafeDouble> lion(){
        HashMap<String, SafeDouble> result = new HashMap<String, SafeDouble>();
        result.put("MOUSE", new SafeDouble(100));
        result.put("CAT", new SafeDouble(100));
        result.put("DOG", new SafeDouble(100));
        result.put("WOLF", new SafeDouble(95));
        result.put("TIGER", new SafeDouble(70));
        result.put("LION", new SafeDouble(53));
        result.put("ELEPHANT", new SafeDouble(3));
        return result;
    }

    private HashMap<String, SafeDouble> elephant(){
        HashMap<String, SafeDouble> result = new HashMap<String, SafeDouble>();
        result.put("MOUSE", new SafeDouble(10));
        result.put("CAT", new SafeDouble(100));
        result.put("DOG", new SafeDouble(100));
        result.put("WOLF", new SafeDouble(100));
        result.put("TIGER", new SafeDouble(94));
        result.put("LION", new SafeDouble(90));
        result.put("ELEPHANT", new SafeDouble(55));
        return result;
    }

    public int getWinPercent(ChessModel from, ChessModel to){
        String fromAnimal = from.getChessType().name().split("_")[1];
        if(to.getChessType() == ChessType.NONE){
            return 100;
        }
        else{
            String toAnimal = to.getChessType().name().split("_")[1];
            return processStatusPoint(refs.get(fromAnimal).get(toAnimal).getIntValue(), from, to);
        }
    }

    //return true(winner is from) or false(winner is to)
    public boolean getFromIsWinner(ChessModel from, ChessModel to){
        int percent = getWinPercent(from, to);
        int random = MathUtils.random(0, 100);

        Logs.show("Winning percent is: " + percent);
        Logs.show("Calculated random is: " + random);

        if(percent == 0){
            return false;
        }
        else if(percent == 100){
            return true;
        }
        else{
            if(random <= percent){
                return true;
            }
            else{
                return false;
            }
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
