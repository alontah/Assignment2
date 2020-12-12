package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.DestroyerEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;

/**
 * LandoMicroservice
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LandoMicroservice  extends MicroService {
    private long duration;
    private Message nextMessage;
    private Class destroyerEventClass = DestroyerEvent.class;
    private Class terminateBroadcastClass = terminateBroadcast.class;
    private boolean terminate;

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        nextMessage = null;
        terminate=false;
    }


    @Override
    protected void initialize() {
        subscribeEvent(destroyerEventClass, c -> {
            try {
                Thread.sleep(duration);
                complete((Event) c, true);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });

        subscribeBroadcast(terminateBroadcastClass, c -> {
            terminate = true;
            terminate();
            myDiary.setLandoTerminate(System.currentTimeMillis());
        });


        while (nextMessage == null && !terminate){
            nextMessage = getNextMessage();
            getCallback(nextMessage.getClass()).call(nextMessage);
            nextMessage = null;
        }
    }
}
