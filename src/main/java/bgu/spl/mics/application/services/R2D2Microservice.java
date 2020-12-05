package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.ShieldEvent;

import java.util.ArrayList;
import java.util.Queue;

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
    private Class c = ShieldEvent.class;

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
       nextMessage = null;
        result = false;
    }

    @Override
    protected  void initialize() {
        Callback call = c -> {//probablly not good. need to figure out how to do this
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) { }
            };
        subscribeEvent(c, call);
        while (nextMessage == null){
            nextMessage = getNextMessage();// wait for new message to come
            if (nextMessage instanceof ShieldEvent){
                call.call(nextMessage);
                result = true;
                complete((Event) nextMessage, result);
            }
            else { //broadcast message - need to figure out what to do with it
            }
            nextMessage = null;//reset
        }
    }

}
