package bgu.spl.mics.application.services;

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

    public LandoMicroservice(long duration) {
        super("Lando");
        this.duration = duration;
        nextMessage = null;
        result = false;
    }

    @Override
    protected void initialize() {
        //wait until destroyer event received
        //destroy
        while (nextMessage == null){
            nextMessage = getNextMessage();
            if (nextMessage instanceof DestroyerEvent){
                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) { }
                result = true;
                complete((Event) nextMessage, result);
            }
            else { //broadcast message - need to figure out what to do with it
            }
            nextMessage = null;//reset
        }
    }
}
