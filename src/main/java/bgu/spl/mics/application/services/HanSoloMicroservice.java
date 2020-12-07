package bgu.spl.mics.application.services;


import bgu.spl.mics.Callback;
import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.passiveObjects.Ewoks;

import java.util.List;

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
    private Class AttackEventClass = AttackEvent.class;
    private boolean result = true;
    Ewoks myEwoks = Ewoks.getInstance(0);

    public HanSoloMicroservice() {
        super("Han");
        nextMessage = null;
    }


    @Override
    protected void initialize() {
        subscribeEvent(AttackEventClass, new Callback<Event<?>>() {//subs to attack event
            @Override
            public void call(Event<?> c) {
                //Ewoks myEwoks = Ewoks.getInstance(((AttackEvent)c).sizeOfAttack());
                List<Integer> attackList = ((AttackEvent)c).getSerials();
                attackList.sort(Integer::compareTo);
                for (int i = 0; i< attackList.size(); i++){
                    myEwoks.acquireEwok(attackList.get(i));
                }
                try {
                    Thread.sleep(((AttackEvent) c).getDuration());
                    for (int i = 0; i< attackList.size(); i++){
                        myEwoks.releaseEwok(attackList.get(i));
                    }
                }catch (InterruptedException e){}
            }
        });
        while (nextMessage == null){//gets next message
            nextMessage = getNextMessage();// wait for new message to come
            getCallback(nextMessage.getClass()).call(nextMessage);//get the callback
            if (nextMessage instanceof AttackEvent){
                result = true;
                complete((Event) nextMessage, result);
            }
            nextMessage = null;//reset
        }
    }
}
