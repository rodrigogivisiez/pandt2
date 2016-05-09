package com.mygdx.potatoandtomato.miscs.comparators;

import com.mygdx.potatoandtomato.models.RoomUser;

import java.util.Comparator;

/**
 * Created by SiongLeng on 29/4/2016.
 */
public class RoomUserSlotIndexComparator implements Comparator<RoomUser> {

    @Override
    public int compare(RoomUser roomUser1, RoomUser roomUser2) {
        return roomUser1.getSlotIndex().compareTo(roomUser2.getSlotIndex());
    }
}
