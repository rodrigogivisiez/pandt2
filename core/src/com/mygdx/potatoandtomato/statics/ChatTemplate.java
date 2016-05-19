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
        templates.add("Good Game.");
        templates.add("Relax, you are doing fine.");
        templates.add("Luis suarez scores!.");
        templates.add("????????");
        templates.add("Ni hao");
        templates.add("Omg");
        templates.add("Holy Shit");
        templates.add("Gogogogogogo");
        templates.add("Don't Give up");
        templates.add("See you again");

        return templates;
    }

}
