package helpers;

/**
 * Created by SiongLeng on 9/12/2015.
 */
public class T_Threadings {

    public static int waitingTaskCount = 0;


    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitTasks(int expectedTask){
        waitingTaskCount = 0;
        while (expectedTask > waitingTaskCount){
            sleep(300);
        }
        waitingTaskCount = 0;
    }

    public static void oneTaskFinish(){
        waitingTaskCount++;
    }


}
