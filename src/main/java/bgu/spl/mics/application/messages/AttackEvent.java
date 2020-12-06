package bgu.spl.mics.application.messages;
import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

import java.util.List;

public class AttackEvent implements Event<Boolean> {
    private Future myFuture;
    private long duration;
    private List<Integer> serials;

    public AttackEvent(long duration, List<Integer> serials){
        this.myFuture = new Future();
        this.duration = duration;
        this.serials=serials;
    }

    public Future getFuture(){
        return this.myFuture;
    }

    public int sizeOfAttack(){
        return this.serials.size();
    }
    public List<Integer> getSerials(){
        return this.serials;
    }
    public long getDuration(){
        return this.duration;
    }
}
