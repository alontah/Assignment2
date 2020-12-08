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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private ArrayList<BlockingQueue<Message>> microServiceQueues;
	private ConcurrentHashMap<Class<? extends Event>, ArrayList<Integer>> subscriptionMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedList<Integer>> subscriptionBroadcastMap;
	private ArrayList<Integer> robinRoundCounters;
	private ArrayList<Object> lockArray;




	private MessageBusImpl(){
		this.microServiceQueues = new ArrayList<>(5);
		this.subscriptionMap = new ConcurrentHashMap<>();
		this.subscriptionBroadcastMap = new ConcurrentHashMap<>();
		this.robinRoundCounters = new ArrayList<>(3); // might change when we add events
		this.lockArray = new ArrayList<>(5);
		for(int i=0; i<5; i++) {
			lockArray.add(new Object());// might DELETE LOCKS!!!!!!
			BlockingQueue<Message> tempQueue = null;
			this.microServiceQueues.add(tempQueue);
		}
		for(int i=0; i<=2; i++){
			robinRoundCounters.set(i,0);
		}
	}

	public static synchronized MessageBusImpl getInstance(){
		if(instance==null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}
	
	@Override
	public  <T> void  subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		int queueIndex = findQueue(m);// find proper index
		ArrayList<Integer> eventList = subscriptionMap.get(type);//find event ArrayList within the map
		if(eventList == null){//if null create
			subscriptionMap.put(type, new ArrayList<>());
			subscribeEvent(type, m);
		} else {
			// might need lock for list...
			eventList.add(queueIndex);
			//notifyAll();
		}
		/***
		 * think if locks / sync are needed.
		 * 
		 */
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		int queueIndex = findQueue(m);//find queue index
		LinkedList<Integer> broadcastList = subscriptionBroadcastMap.get(type);// get broadcast list
		if(broadcastList == null) {//if null create list
			subscriptionBroadcastMap.put(type, new LinkedList<>());
			subscribeBroadcast(type, m);
		} else {
			broadcastList.add(queueIndex);//add microService to list
			//notifyAll();
		}
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		e.getFuture().resolve(result);
		notifyAll();
	}

	@Override
	public void sendBroadcast(Broadcast b) throws InterruptedException {
		while(!subscriptionBroadcastMap.containsKey(b.getClass())){//while list isn't contained wait,
			wait();
		}
		LinkedList<Integer> broadcastList = subscriptionBroadcastMap.get(b.getClass());//get list
		for(Integer queueIndex: broadcastList) {// go through the list
			//synchronized (getLock(queueIndex)) {//get locks
				microServiceQueues.get(queueIndex).add(b);
				//notifyAll();
			//}
		}
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
		microServiceQueues.set(microServiceIndex, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		int microServiceIndex = findQueue(m);
		microServiceQueues.set(microServiceIndex, null);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message event = null;
		int identity = findQueue(m);//find correct queue
		try {
			while(event == null) {
				event = microServiceQueues.get(identity).take();
			}
		} catch (InterruptedException ex){}
//		while(microServiceQueues.get(identity).isEmpty()) {//waiting for leia to add to queue
//			wait();
//		}
//		synchronized (getLock(identity)){//locking queue so leia doesn't add while he removes
//			event = microServiceQueues.get(identity).remove();
//		}
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
		return 4; // lando microService
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
		while(!subscriptionMap.containsKey(e.getClass())) { //waiting for someone to subscribe  to event type
			wait();
		}

		ArrayList<Integer> temp = subscriptionMap.get(e.getClass());//getting the subscription list
		int size = temp.size();
		int queueCounter = robinRoundCounters.get(identity);
		int queue = temp.get(queueCounter%size); //gets next queue for this type of message MIGHT NEED SYNC CUS OF SIZE!!!!!!
		BlockingQueue<Message> tempMicroQueue = microServiceQueues.get(queue);

//		synchronized (getLock(queue)) { // returns correct lock
			tempMicroQueue.add(e); // adds the event to the correct Queue
			robinRoundCounters.set(identity, queueCounter + 1);
//		}
//		notifyAll();//so events will try to check their queues
	}

	private Object getLock(int identity){
		return lockArray.get(identity);
	}






}
