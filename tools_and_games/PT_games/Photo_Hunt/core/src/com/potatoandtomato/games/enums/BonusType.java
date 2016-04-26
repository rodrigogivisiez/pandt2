package com.potatoandtomato.games.enums;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by SiongLeng on 18/4/2016.
 */
public enum BonusType {
    NONE,
    INVERTED,
    LOOPING,
    ONE_PERSON,
    LIGHTING,
    MEMORY,
    TORCH_LIGHT,
    DISTRACTION,
    WRINKLE,
    COVERED,
    EGG;

    private static final List<BonusType> VALUES =
            Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static BonusType random()  {
        BonusType randomType = VALUES.get(RANDOM.nextInt(SIZE));
        while (randomType == BonusType.NONE){
            randomType = VALUES.get(RANDOM.nextInt(SIZE));
        }
        return randomType;
    }

}
