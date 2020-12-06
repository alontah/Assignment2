package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.ShieldEvent;

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
    private boolean result;// need to figure out what the results are
    private Class shieldEventClass = ShieldEvent.class;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
        nextMessage = null;
        result = false;
    }

    /**
     * To Do:
     * sub broadcast
     */

    @Override
    protected  void initialize() {
        subscribeEvent(shieldEventClass, new Callback<Event<?>>() {
            @Override
            public void call(Event<?> c) {
                try {
                    Thread.sleep(duration);
                }catch (InterruptedException e){}
            }
        });
        while (nextMessage == null){
            nextMessage = getNextMessage();// wait for new message to come
            getCallback(nextMessage.getClass()).call(nextMessage);//get the callback
            if (nextMessage instanceof ShieldEvent){
                result = true;
                complete((Event) nextMessage, result);
            }
            nextMessage = null;//reset
        }
    }

}
