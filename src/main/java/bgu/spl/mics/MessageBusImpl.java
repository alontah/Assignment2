package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DestroyerEvent;
import bgu.spl.mics.application.messages.ShieldEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private ArrayList<Queue<Message>> microServiceQueues;
	private Hashtable<Class<? extends Event>, ArrayList<Integer>> subscriptionTable;
	private ArrayList<Integer> robinRoundCounters;
	private final Object leiaLock = new Object();
	private final Object hanLock = new Object();
	private final Object C3POLock = new Object();
	private final Object R2D2Lock = new Object();
	private final Object landoLock = new Object();




	private MessageBusImpl(){
		this.microServiceQueues = new ArrayList<>(5);
		this.subscriptionTable = new Hashtable<>();
		this.robinRoundCounters = new ArrayList<>(3); // might change when we add events
		for(int i=0; i<=2; i++){
			robinRoundCounters.set(i,0);
		}
		for(int i=0; i<5; i++) {
			Queue<Message> tempQueue = null;
			this.microServiceQueues.add(tempQueue);
		}
	}

	public static synchronized MessageBusImpl getInstance(){
		if(instance==null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}
	
	@Override
	public synchronized <T> void  subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		int queueIndex = findQueue(m);// find proper index
		ArrayList<Integer> eventList = subscriptionTable.get(type);//find event ArrayList within the map
		if(eventList == null){//if null create
			subscriptionTable.put(type, new ArrayList<Integer>());
			subscribeEvent(type, m);
		} else {
			eventList.add(queueIndex);
			notifyAll();
		}
		/***
		 * think if locks / sync are needed.
		 */
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
		try {
			addEvent(e, eventIdentity); // inserts the event to the proper Q
		} catch (InterruptedException I){}
		Future eventFuture = e.getFuture();
        return eventFuture;
	}

	@Override
	public void register(MicroService m) {
		int microServiceIndex = findQueue(m);
		microServiceQueues.set(microServiceIndex, new LinkedList<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		int microServiceIndex = findQueue(m);
		microServiceQueues.set(microServiceIndex, null);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message event;
		int identity = findQueue(m);//find correct queue
		while(microServiceQueues.get(identity).isEmpty()) {//waiting for leia to add to queue
			wait();
		}
		synchronized (getLock(identity)){//locking queue so leia doesn't add while he removes
			event = microServiceQueues.get(identity).remove();
		}
//		notifyAll(); // might NOT be needed...
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
		if(e instanceof  AttackEvent){
			return 0;
		}
		if(e instanceof ShieldEvent){
			return 1;
		}if(e instanceof DestroyerEvent){
			return 2;
		}
		return -1; // might add events later
	}


	private <T> void addEvent(Event<T> e, int identity) throws InterruptedException {
		while(!subscriptionTable.containsKey(e.getClass())) {//get event list from map
			wait();//while map doesn't contain list wait
		}

		ArrayList<Integer> temp = subscriptionTable.get(e.getClass());//send event to correct one with count array and modulu
		int size = temp.size();
		int queueCounter = robinRoundCounters.get(identity);
		int queue = temp.get(queueCounter%size); //gets next queue for this type of message MIGHT NEED SYNC CUS OF SIZE!!!!!!
		Queue<Message> tempMicroQueue = microServiceQueues.get(queue);

		synchronized (getLock(queue)) { // returns correct lock
			tempMicroQueue.add(e); // adds the event to the correct Queue
			robinRoundCounters.set(identity, queueCounter + 1);
		}
		notifyAll();//so events will try to check their queues
	}

	private Object getLock(int identity){
		switch (identity){
			case(0):
		}

		if(identity == 0){
			return leiaLock;
		}
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
