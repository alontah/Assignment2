package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DestroyerEvent;
import bgu.spl.mics.application.messages.ShieldEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private ArrayList<Queue<Message>> microServiceQueues;
	private AtomicInteger AttackCounter;
	private final Object hanLock = new Object();
	private final Object C3POLock = new Object();
	private final Object R2D2Lock = new Object();
	private final Object landoLock = new Object();
//	private Queue<Message> leiaQueue;
//	private Queue<Message> hanQueue;
//	private Queue<Message> C3POQueue;
//	private Queue<Message> R2D2Queue;
//	private Queue<Message> landoQueue;



	private MessageBusImpl(){
		this.microServiceQueues = new ArrayList<>(5);
		for(int i=0; i<5; i++) {
			Queue<Message> tempQueue = null;
			this.microServiceQueues.add(tempQueue);
		}
		this.AttackCounter = new AtomicInteger(0);
	}

	public static synchronized MessageBusImpl getInstance(){
		if(instance==null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}
	
	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//find MicroService queue
		// initilaize it
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		int eventIdentity = identifyEvent(e);
		addEvent(e, eventIdentity);
		// while future(!isDone()){ wait()} need to make sure events notifyall when future isDone (complete)
		//return future
        return null;
	}

	@Override
	public void register(MicroService m) {
		int microServiceIndex = findQueue(m);
		microServiceQueues.set(microServiceIndex, new LinkedList<Message>());
		m.initialize();
	}

	@Override
	public void unregister(MicroService m) {
		int microServiceIndex = findQueue(m);
		microServiceQueues.set(microServiceIndex, null);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message event = null;
		int identity = findQueue(m);//find currect queue
		while(microServiceQueues.get(identity).isEmpty()) {//waiting for leia to add to queue
			wait();
		}
		synchronized (getLock(identity)){//locking queue so leia doesn't add while he removes
			event = microServiceQueues.get(identity).remove();
		}
		notifyAll(); // might NOT be needed...
		return event;
	}

	private int findQueue(MicroService m){
		if(m instanceof LeiaMicroservice){
			return 0;
		}
		if(m instanceof HanSoloMicroservice){
			return 1;
		}
		if(m instanceof C3POMicroservice){
			return 2;
		}
		if(m instanceof R2D2Microservice){
			return 3;
		}
		return 4; // lando microservice
	}

	private <T> int identifyEvent(Event<T> e){
		if(e instanceof AttackEvent){
			return 0;
		}
		if(e instanceof ShieldEvent){
			return 1;
		}if(e instanceof DestroyerEvent){
			return 2;
		}
		return -1; // might add events later
	}


	private <T> void addEvent(Event<T> e, int identity){
		if(identity == 0){
			if(AttackCounter.get()%2 == 0){
				synchronized (hanLock){
					microServiceQueues.get(1).add(e);
					int expected = AttackCounter.get()+1;
					AttackCounter.compareAndSet(expected,AttackCounter.get()+1);
				}
			} else {
				synchronized (C3POLock){
					microServiceQueues.get(2).add(e);
					int expected = AttackCounter.get()+1;
					AttackCounter.compareAndSet(expected,AttackCounter.get()+1);
				}
			}
		} else if(identity == 1){
			synchronized (R2D2Lock) {
				microServiceQueues.get(3).add(e);//adds shield to r2d2
			}
		} else if(identity == 2){
			synchronized (landoLock) {
				microServiceQueues.get(4).add(e);//adds destroyer to lando
			}
		}
		notifyAll();//so events will try to check their queues
	}

	private Object getLock(int identity){
		if(identity == 1){
			return hanLock;
		}
		if(identity == 2){
			return C3POLock;
		}
		if(identity == 3){
			return R2D2Lock;
		}
		return landoLock;
	}





}
