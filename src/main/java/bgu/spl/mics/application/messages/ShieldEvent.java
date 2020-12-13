package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class ShieldEvent implements Event<Boolean> {
    private Future <Boolean> future;

    public ShieldEvent()
    {
        this.future = new Future<>();
    }

    public Future getFuture(){
        return this.future;
    }
}
