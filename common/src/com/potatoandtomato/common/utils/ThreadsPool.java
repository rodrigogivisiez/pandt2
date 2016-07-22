package com.potatoandtomato.common.utils;

import java.util.ArrayList;

/**
 * Created by SiongLeng on 7/3/2016.
 */
public class ThreadsPool{

    private ArrayList<Threadings.ThreadFragment> threadFragments;

    public ThreadsPool() {
        this.threadFragments = new ArrayList<Threadings.ThreadFragment>();
    }

    public void addFragment(Threadings.ThreadFragment threadFragment){
        threadFragments.add(threadFragment);
    }

    public boolean allFinished(){
        for(Threadings.ThreadFragment threadFragment : threadFragments){
            if(!threadFragment.isFinished()) return false;
        }
        return true;
    }

    public int getUnfinishedFragmentSize(){
        int i = 0;
        for(Threadings.ThreadFragment threadFragment : threadFragments){
            if(!threadFragment.isFinished()) i++;
        }
        return i;
    }

}
