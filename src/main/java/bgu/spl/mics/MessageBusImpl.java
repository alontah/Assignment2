package bgu.spl.mics;

import bgu.spl.mics.application.messages.AttackEvent;
import bgu.spl.mics.application.messages.DestroyerEvent;
import bgu.spl.mics.application.messages.ShieldEvent;
import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private Class AttackEventClass = AttackEvent.class;
	private Class shieldEventClass = ShieldEvent.class;
	private Class destroyerEventClass = DestroyerEvent.class;
	private ArrayList<BlockingQueue<Message>> microServiceQueues;
	private ConcurrentHashMap<Class<? extends Event>, ArrayList<Integer>> subscriptionMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, ArrayList<Integer>> subscriptionBroadcastMap;
	private ArrayList<Integer> robinRoundCounters;
	private ArrayList<Object> lockArray;
	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}




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
			robinRoundCounters.add(0);
		}
	}

	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}
	
	@Override
	public  <T> void  subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		Object typeLock = getLock(identifyEventByType(type));
		synchronized (typeLock) {
			if (!subscriptionMap.containsKey(type)) {
				subscriptionMap.put(type, new ArrayList<>());
			}
			int queueIndex = findQueue(m);// find proper index
			ArrayList<Integer> eventList = subscriptionMap.get(type);
			eventList.add(queueIndex);
			typeLock.notifyAll(); // ask YEHIEL
		}
	}

	@Override
	public  void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		Object subLock = getLock(4);
		synchronized (subLock){
			if (!subscriptionBroadcastMap.containsKey(type)) {
				subscriptionBroadcastMap.put(type, new ArrayList<>());
			}
			int queueIndex = findQueue(m);//find queue index
			ArrayList<Integer> broadcastList = subscriptionBroadcastMap.get(type);
			broadcastList.add(queueIndex);//add microService to list

			subLock.notifyAll(); // ask YEHIEL

		}
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		e.getFuture().resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b){
        if (subscriptionBroadcastMap.containsKey(b.getClass())) { // make sure all microservices subscribe first thing!!!!
            ArrayList<Integer> broadcastList = subscriptionBroadcastMap.get(b.getClass());//get list
            for (Integer queueIndex : broadcastList) {// go through the list
                microServiceQueues.get(queueIndex).add(b);
            }
        }
    }

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Object lock = getLock(identifyEvent(e));
		while(!subscriptionMap.containsKey(e.getClass())) {
			try {
				synchronized (lock) {
					lock.wait();
				}
			} catch (InterruptedException I) {
				I.printStackTrace();
			}
		}
		int eventIdentity = identifyEvent(e);
		addEvent(e, eventIdentity);
        return e.getFuture();
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
	public Message awaitMessage(MicroService m){
		Message event = null;
		int identity = findQueue(m);//find correct queue
		try {
			while(event == null) {
				event = microServiceQueues.get(identity).take();
			}
		} catch (InterruptedException ex){
			ex.printStackTrace();
		}
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

	private <T> int identifyEventByType(Class<? extends Event<T>> type){
		if(type.equals(AttackEventClass)){
			return 0;
		}
		if(type.equals(shieldEventClass)){
			return 1;
		}if(type.equals(destroyerEventClass)){
			return 2;
		}
		return -1; // might add events later
	}


	private <T> void addEvent(Event<T> e, int identity) {
		ArrayList<Integer> temp = subscriptionMap.get(e.getClass());//getting the subscription list
		int queueCounter = robinRoundCounters.get(identity);
		int size = temp.size();
		int queue = temp.get(queueCounter % size); //gets next queue for this type of message MIGHT NEED SYNC CUS OF SIZE!!!!!!

		BlockingQueue<Message> tempMicroQueue = microServiceQueues.get(queue);
		tempMicroQueue.add(e); // adds the event to the correct Queue
		robinRoundCounters.set(identity, queueCounter + 1);
	}

	private Object getLock(int identity){
		return lockArray.get(identity);
	}






}
