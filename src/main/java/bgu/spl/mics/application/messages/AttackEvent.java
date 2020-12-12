package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import java.util.List;

public class AttackEvent implements Event<Boolean> {
    private Future<Boolean> myFuture;
    private int duration;
    private List<Integer> serials;

    public AttackEvent(int duration, List<Integer> serials){
        this.myFuture = new Future<>();
        this.duration = duration;
        this.serials=serials;
    }

    public Future getFuture(){
        return this.myFuture;
    }

    public List<Integer> getSerials(){
        return this.serials;
    }
    public int getDuration(){
        return this.duration;
    }
}
