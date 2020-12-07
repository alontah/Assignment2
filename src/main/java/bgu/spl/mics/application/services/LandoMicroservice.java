package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.DestroyerEvent;
import bgu.spl.mics.application.messages.ShieldEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;
    private Message nextMessage;
    private boolean result;// need to figure out what the results are
    Class destroyerEventClass = DestroyerEvent.class;
    private Class terminateBroadcastClass = terminateBroadcast.class;
    private boolean terminate;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        nextMessage = null;
        result = false;
    }

    /**
     * To Do:
     * sub broadcast
     */

    @Override
    protected void initialize() {
        subscribeEvent(destroyerEventClass, new Callback<Event<?>>() {
            @Override
            public void call(Event<?> c) {
                try {
                    Thread.sleep(duration);
                    result = true;
                    complete((Event) c, result);
                }catch (InterruptedException e){}
            }
        });
        subscribeBroadcast(terminateBroadcastClass, new Callback<Broadcast>() {//need to add diary actions
            @Override
            public void call(Broadcast c) {
                terminate = true;
                terminate();
            }
        });
        while (nextMessage == null&& !terminate){
            nextMessage = getNextMessage();
            getCallback(nextMessage.getClass()).call(nextMessage);
            nextMessage = null;//reset
        }
    }
}
