package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SiongLeng on 24/12/2015.
 */
public class MultiHashMap<S, T> {

    private HashMap<S, ArrayList<T>> _hashMap;


    public MultiHashMap() {
        _hashMap = new HashMap<>();
    }

    public void put(S s, T t){
        ArrayList<T> tArrayList;
        if(_hashMap.containsKey(s)){
            tArrayList = _hashMap.get(s);
        }
        else{
            tArrayList = new ArrayList<>();
        }
        tArrayList.add(t);
        _hashMap.put(s, tArrayList);
    }

    public ArrayList<T> get(S s){
        return _hashMap.get(s);
    }

    public void remove(S s){
        _hashMap.remove(s);
    }

    public void clear(){
        _hashMap.clear();
    }

    public int size(){
        return _hashMap.size();
    }



}
