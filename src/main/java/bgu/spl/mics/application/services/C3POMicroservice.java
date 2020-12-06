package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AttackEvent;
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
    private boolean result = true;

    public C3POMicroservice() {
        super("C3PO");
    }

    @Override
    protected void initialize() {
        subscribeEvent(AttackEventClass, new Callback<Event<?>>() {//subs to attack event
            @Override
            public void call(Event<?> c) {
                Ewoks myEwoks = Ewoks.getInstance(((AttackEvent) c).sizeOfAttack());
                List<Integer> attackList = ((AttackEvent) c).getSerials();
                attackList.sort(Integer::compareTo);
                for (int i = 0; i < attackList.size(); i++) {
                    myEwoks.acquireEwok(attackList.get(i));
                }
                try {
                    Thread.sleep(((AttackEvent) c).getDuration());
                    for (int i = 0; i< attackList.size(); i++) {
                        myEwoks.releaseEwok(attackList.get(i));
                    }
                } catch (InterruptedException e) { }
            }
        });
        while (nextMessage == null) {//gets next message
            nextMessage = getNextMessage();// wait for new message to come
            getCallback(nextMessage.getClass()).call(nextMessage);//get the callback
            if (nextMessage instanceof AttackEvent) {
                result = true;
                complete((Event) nextMessage, result);
            }
            nextMessage = null;//reset
        }
    }
}

