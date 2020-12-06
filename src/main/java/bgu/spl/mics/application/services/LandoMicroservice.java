package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DestroyerEvent;
import bgu.spl.mics.application.messages.ShieldEvent;

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
                }catch (InterruptedException e){}
            }
        });
        while (nextMessage == null){
            nextMessage = getNextMessage();
            getCallback(nextMessage.getClass()).call(nextMessage);
            if (nextMessage instanceof DestroyerEvent){
                result = true;
                complete((Event) nextMessage, result);
            }
            nextMessage = null;//reset
        }
    }
}
