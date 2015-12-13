package com.mygdx.potatoandtomato.helpers.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by SiongLeng on 13/12/2015.
 */
public class Caches {

    private Array<Cache> _cacheArray;
    private int _cacheSize = 5 * 1024 * 1024; //5mb

    private static Caches _caches;
    public static Caches getInstance(){
        if(_caches == null) _caches = new Caches();
        return _caches;
    }

    public Caches() {
        _cacheArray = new Array<Cache>();;
    }

    public void add(String tag, Object obj, int size){
        if(!exist(tag)){
            Cache cache = new Cache();
            cache.tag = tag;
            cache.obj = obj;
            cache.size = size;
            _cacheArray.add(cache);
            LruDiscardProcess();
        }
    }

    public Object get(String tag){
        int i = 0;
        for(Cache cache : _cacheArray){
            if(cache.tag.equals(tag)){
                _cacheArray.swap(i, _cacheArray.size -1);     //LRU
                return cache.obj;
            }
            i++;
        }
        return null;
    }

    public boolean exist(String tag){
        return (get(tag) != null);
    }

    //LRU model
    private void LruDiscardProcess(){
        int acc = 0;
        for(int i = _cacheArray.size - 1; i >= 0; i--){
            acc += _cacheArray.get(i).size;
            if(acc > _cacheSize){
                int q = i;
                while(q >= 0){
                    if(_cacheArray.get(q) instanceof Disposable){
                        ((Disposable) _cacheArray.get(q)).dispose();
                    }
                    _cacheArray.removeIndex(q);
                    q--;
                }
            }
        }
    }

    public void setCacheSize(int _cacheSize) {
        this._cacheSize = _cacheSize;
    }

    private class Cache {
        public String tag;
        public Object obj;
        public int size;
    }




}
