package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DestroyerEvent;
import bgu.spl.mics.application.messages.ShieldEvent;
import bgu.spl.mics.application.messages.terminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Attack;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

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
	private Vector<Future> futures;
	private AtomicInteger counter = new AtomicInteger(0);

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
            Future nextFuture = sendEvent(nextAttack);
            futures.add(nextFuture);//save vector of future objects
        }
        for (Future curr : futures){//wait for all attacks to be  completed
            curr.get();//get() waits for current future to be done
        }

        ShieldEvent shield = new ShieldEvent();
        Future shieldFuture = sendEvent(shield);//send deactivation event to r2d2
        shieldFuture.get();//wait for future to finish

        DestroyerEvent destroy = new DestroyerEvent();
        Future destroyFuture = sendEvent(destroy);//send destroyer event to lando
        destroyFuture.get(); //wait for future to finish

        try {
            sendBroadcast(new terminateBroadcast());
            terminate();
            myDiary.setLeiaTerminate(System.currentTimeMillis());
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        System.out.println("Leia Done");
    }
}
