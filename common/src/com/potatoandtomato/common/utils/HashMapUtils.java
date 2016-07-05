package com.potatoandtomato.common.utils;

import java.util.*;

/**
 * Created by SiongLeng on 5/7/2016.
 */
public class HashMapUtils<T> {

    public HashMap<T, Double> sortByValue(HashMap<T, Double> unsortMap, final boolean ascending)
    {
        List<Map.Entry<T, Double>> list = new LinkedList<Map.Entry<T, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Map.Entry<T, Double>>() {
            public int compare(Map.Entry<T, Double> o1,
                               Map.Entry<T, Double> o2) {
                if (ascending) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        HashMap<T, Double> sortedMap = new LinkedHashMap<T, Double>();
        for (Map.Entry<T, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}
