package bgu.spl.mics.application.services;

import bgu.spl.mics.Event;
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
	private Vector<Future> futures;

    public LeiaMicroservice(Attack[] attacks) {
        super("Leia");
		this.attacks = attacks;
		this.futures = new Vector<>();
    }

    @Override
    protected void initialize() {
        for(int i = 0; i< attacks.length;i++){//send the attack events to the message bus
            AttackEvent nextAttack = new AttackEvent(attacks[i].getDuration(),attacks[i].getSerials());
            Future nextFuture = sendEvent(nextAttack);
            futures.set(i, nextFuture);//save vector of future objects
        }
        boolean isFinish = false;
        while (!isFinish){//wait until all future objects are completed
            try {
                for (int i = 0; i<futures.size();i++){
                    if(!(futures.get(i).isDone()))
                        wait();
                }
                isFinish = true;
            }catch (InterruptedException e){}
        }
        ShieldEvent shield = new ShieldEvent();
        Future shieldFuture = sendEvent(shield);//send deactivation event to r2d2
        while (!shieldFuture.isDone()){//wait for the result of shield event
            try {
                wait();
            }catch (InterruptedException e) {}
        }
        DestroyerEvent destroy = new DestroyerEvent();
        Future destroyFuture = sendEvent(destroy);//send destroyer event to lando
        while (!destroyFuture.isDone()){//wait for the result of destroyer event
            try {
                wait();
            }catch (InterruptedException e) {}
        }
        sendBroadcast(new terminateBroadcast());
        terminate();
    }
}
