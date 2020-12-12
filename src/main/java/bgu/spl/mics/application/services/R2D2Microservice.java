package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.ShieldEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;

/**
 * R2D2Microservices is in charge of the handling {@link bgu.spl.mics.application.messages.ShieldEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link bgu.spl.mics.application.messages.ShieldEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class R2D2Microservice extends MicroService {
    private long duration;
    private Message nextMessage;
    private Class shieldEventClass = ShieldEvent.class;
    private Class terminateBroadcastClass = terminateBroadcast.class;
    private boolean terminate;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
        nextMessage = null;
        terminate = false;
    }


    @Override
    protected  void initialize() {
        subscribeEvent(shieldEventClass, c -> {
            try {
                Thread.sleep(duration);
                complete((Event) c, true);
                myDiary.setR2D2Deactivate(System.currentTimeMillis());
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        });

        subscribeBroadcast(terminateBroadcastClass, c -> {
            terminate = true;
            terminate();
            myDiary.setR2D2Terminate(System.currentTimeMillis());
        });

        while (nextMessage == null && !terminate){
            nextMessage = getNextMessage();
            getCallback(nextMessage.getClass()).call(nextMessage);
            nextMessage = null;
        }
    }

}
