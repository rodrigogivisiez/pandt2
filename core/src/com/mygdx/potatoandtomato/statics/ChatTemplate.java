package com.mygdx.potatoandtomato.statics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by SiongLeng on 11/5/2016.
 */
public class ChatTemplate {

    public static ConcurrentHashMap<Integer, String> templateMap;

    public static ArrayList<String> getTemplatesByIds(ArrayList<Integer> ids){
        ConcurrentHashMap<Integer, String> map = getMap();
        ArrayList<String> result = new ArrayList();
        for(Integer id : ids){
            if(map.containsKey(id)){
                result.add(map.get(id));
            }
        }
        Collections.sort(result);
        return result;
    }

    public static ArrayList<Integer> getIdsByTemplates(ArrayList<String> templates){
        ConcurrentHashMap<Integer, String> map = getMap();
        ArrayList<Integer> result = new ArrayList();
        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            for(String template : templates){
                if(template.equals(entry.getValue())){
                    result.add(entry.getKey());
                    break;
                }
            }
        }
        return result;
    }

    public static ArrayList<String> getAllTemplates(){
        ArrayList<String> result = populateTemplates();
        Collections.sort(result);
        return result;
    }

    private static ConcurrentHashMap<Integer, String> getMap(){
        if(templateMap == null){
            ArrayList<String> templates = populateTemplates();
            templateMap = new ConcurrentHashMap();
            for(int i = 0; i < templates.size(); i++){
                templateMap.put(i, templates.get(i));
            }
        }
        return templateMap;
    }


    private static ArrayList<String> populateTemplates(){
        ArrayList<String> templates = new ArrayList();
        templates.add("Good game well played.");
        templates.add("Relax, you are doing fine, I think.");
        templates.add("Good day, monsieur!");
        templates.add("Bye");
        templates.add("Be right back...");
        templates.add("This is getting very interesting...");
        templates.add("Such is life :(");
        templates.add("I am just conserving my energy for the next game...");
        templates.add("Don't give up!");
        templates.add("See you again.");
        templates.add("Not my proudest game.");
        templates.add("So close!!!");
        templates.add("Roarrrr!");
        templates.add("Now this is between a rock and a hard place...");
        templates.add("Victory");
        templates.add("No such luck my young apprentice!");
        templates.add("Go go go~");
        templates.add("Focus, my friends");
        templates.add("??? but why ???");
        templates.add("Here comes my trump card...");
        templates.add("Here goes nothing...");
        templates.add("Here comes my ulti...");
        templates.add("I immediately regret my decision.");
        templates.add("Defend");
        templates.add("Coin for the coinless please...");
        templates.add("Help me I am poor");
        templates.add("It's a disaster!");
        return templates;
    }

}
