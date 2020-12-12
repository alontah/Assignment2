package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;


/**
 * C3POMicroservices is in charge of the handling {@link bgu.spl.mics.application.messages.AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link bgu.spl.mics.application.messages.AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class C3POMicroservice extends MicroService {
    private Message nextMessage;
    private Class AttackEventClass = AttackEvent.class;
    private Ewoks myEwoks;
    private Class terminateBroadcastClass = terminateBroadcast.class;
    private boolean terminate;

    public C3POMicroservice() {
        super("C3PO");
        nextMessage = null;
        terminate = false;
        myEwoks = Ewoks.getInstance(0);
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEventClass,c-> {
            List<Integer> attackList = ((AttackEvent) c).getSerials();
            attackList.sort(Integer::compareTo);
            for (Integer serial: attackList){
                myEwoks.acquireEwok(serial);
            }
            try {
                Thread.sleep(((AttackEvent) c).getDuration());
                for (Integer serial: attackList){
                    myEwoks.releaseEwok(serial);
                }
                complete((Event) c, true);
                myDiary.raiseAttackBy1();
                myDiary.setC3POFinish(System.currentTimeMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        subscribeBroadcast(terminateBroadcastClass, c-> {
            terminate = true;
            terminate();
            myDiary.setC3POTerminate(System.currentTimeMillis());
        });

        while (nextMessage == null&&!terminate) {
            nextMessage = getNextMessage();
            getCallback(nextMessage.getClass()).call(nextMessage);
            nextMessage = null;
        }
    }
}

