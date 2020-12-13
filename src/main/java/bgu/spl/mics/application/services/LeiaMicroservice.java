package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DestroyerEvent;
import bgu.spl.mics.application.messages.ShieldEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.Vector;

/**
 * LeiaMicroservices Initialized with Attack objects, and sends them as  {@link AttackEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link AttackEvent}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LeiaMicroservice extends MicroService {
	private Attack[] attacks;
	private Vector<Future<Boolean>> futures;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		this.futures = new Vector<>();
    }

    @Override
    protected void initialize() {
        for (Attack myAttack: attacks)
        {
            AttackEvent nextAttack = new AttackEvent(myAttack.getDuration(),myAttack.getSerials());
            Future <Boolean> nextFuture = sendEvent(nextAttack);
            futures.add(nextFuture);
        }
        for (Future curr : futures){
            curr.get();//get() waits for current future to be done
        }

        ShieldEvent shield = new ShieldEvent();
        Future <Boolean> shieldFuture = sendEvent(shield);
        shieldFuture.get();

        DestroyerEvent destroy = new DestroyerEvent();
        Future <Boolean> destroyFuture = sendEvent(destroy);
        destroyFuture.get();

        try {
            sendBroadcast(new terminateBroadcast());
            terminate();
            myDiary.setLeiaTerminate(System.currentTimeMillis());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
