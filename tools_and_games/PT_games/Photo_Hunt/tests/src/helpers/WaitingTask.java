package helpers;

import com.potatoandtomato.common.Threadings;

/**
 * Created by SiongLeng on 3/2/2016.
 */
public class WaitingTask {

    boolean waiting = false;
    boolean killed = false;

    public WaitingTask() {

    }

    public void kill(){
        killed = true;
    }

    public void stop(){
        waiting = false;
    }

    public void start(long waitingMiliSec){
        waiting = true;
        while (true){
            if(killed) break;

            Threadings.sleep(waitingMiliSec);
            if(!waiting){
                break;
            }
        }
    }

}
