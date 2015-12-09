package helpers;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class T_Threadings {

    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
