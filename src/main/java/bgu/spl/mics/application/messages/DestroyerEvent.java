package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Future;

public class DestroyerEvent implements Event<Boolean> {
    private Future<Boolean> future;

    public DestroyerEvent()
    {
        this.future = new Future<>();
    }

    public Future getFuture(){
        return this.future;
    }
}
