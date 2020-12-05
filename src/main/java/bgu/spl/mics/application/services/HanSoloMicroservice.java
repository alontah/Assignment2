package bgu.spl.mics.application.services;


import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;

/**
 * HanSoloMicroservices is in charge of the handling {@link bgu.spl.mics.application.messages.AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link bgu.spl.mics.application.messages.AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class HanSoloMicroservice extends MicroService {
    private Message nextMessage;

    public HanSoloMicroservice() {
        super("Han");
        nextMessage = null;
    }


    @Override
    protected void initialize() {
        //get next message
        // figure out if attack or broadcast
        // solve the event
        // complete
        // wait for the next message

    }
}
