package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.ShieldEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Diary;

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
    private Class terminateBroadcastClass = terminateBroadcast.class;
    private boolean terminate;
    private Diary myDiary = Diary.getInstance();

    public R2D2Microservice(long duration) {
        super("R2D2");
        this.duration = duration;
        nextMessage = null;
        result = false;
        terminate = false;
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
                    result = true;
                    complete((Event)c, result);
                    myDiary.setR2D2Deactivate(System.currentTimeMillis());
                }catch (InterruptedException e){}
            }
        });
        subscribeBroadcast(terminateBroadcastClass, new Callback<Broadcast>() {//need to add diary actions
            @Override
            public void call(Broadcast c) {
                terminate = true;
                terminate();
                myDiary.setR2D2Terminate(System.currentTimeMillis());
            }
        });
        while (nextMessage == null && !terminate){
            nextMessage = getNextMessage();// wait for new message to come
            getCallback(nextMessage.getClass()).call(nextMessage);//get the callback
            nextMessage = null;//reset
        }
    }

}
