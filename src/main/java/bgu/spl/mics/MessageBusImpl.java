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
	private Class AttackEventClass = AttackEvent.class;
	private Class shieldEventClass = ShieldEvent.class;
	private Class destroyerEventClass = DestroyerEvent.class;
	private ArrayList<BlockingQueue<Message>> microServiceQueues;
	private ConcurrentHashMap<Class<? extends Event>, ArrayList<Integer>> eventSubscriptionMap;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedList<Integer>> broadcastSubscriptionMap;
	private ArrayList<Integer> robinRoundCounters;
	private ArrayList<Object> lockArray;
	private static class SingletonHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}


	private MessageBusImpl(){
		microServiceQueues = new ArrayList<>(5);
		eventSubscriptionMap = new ConcurrentHashMap<>();
		broadcastSubscriptionMap = new ConcurrentHashMap<>();
		robinRoundCounters = new ArrayList<>(3);
		lockArray = new ArrayList<>(5);
		for(int i=0; i<5; i++) {
			BlockingQueue<Message> tempQueue = null;
			this.microServiceQueues.add(tempQueue);
		}
		for(int i=0; i<4; i++){
			lockArray.add(new Object());
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
			if (!eventSubscriptionMap.containsKey(type)) {
				eventSubscriptionMap.put(type, new ArrayList<>());
			}
			int queueIndex = findQueue(m);
			ArrayList<Integer> eventList = eventSubscriptionMap.get(type);
			eventList.add(queueIndex);
			typeLock.notifyAll(); //wake up leia
		}
	}

	@Override
	public  void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		Object subLock = getLock(3);
		synchronized (subLock){
			if (!broadcastSubscriptionMap.containsKey(type)) {
				broadcastSubscriptionMap.put(type, new LinkedList<>());
			}
			int queueIndex = findQueue(m);
			LinkedList<Integer> broadcastList = broadcastSubscriptionMap.get(type);
			broadcastList.add(queueIndex);
			subLock.notifyAll(); //wake up leia
		}
    }

	@Override @SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		e.getFuture().resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b){
        if (broadcastSubscriptionMap.containsKey(b.getClass())) {
			LinkedList<Integer> broadcastList = broadcastSubscriptionMap.get(b.getClass());
            for (Integer queueIndex : broadcastList) {
                microServiceQueues.get(queueIndex).add(b);
            }
        }
    }

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		int eventIdentity = identifyEvent(e);
		Object lock = getLock(eventIdentity);
		while(!eventSubscriptionMap.containsKey(e.getClass())) {
			try {
				synchronized (lock) {
					lock.wait(); //wait for someone to subscribe
				}
			} catch (InterruptedException I) {
				I.printStackTrace();
			}
		}
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
			event = microServiceQueues.get(identity).take();
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
		return -1;
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
		return -1;
	}


	private <T> void addEvent(Event<T> e, int identity) {
		ArrayList<Integer> subscriptionList = eventSubscriptionMap.get(e.getClass());
		int robinRoundCounter = robinRoundCounters.get(identity);
		int size = subscriptionList.size();
		int queueIndex = subscriptionList.get(robinRoundCounter % size);
		BlockingQueue<Message> tempMicroQueue = microServiceQueues.get(queueIndex);
		tempMicroQueue.add(e);
		robinRoundCounters.set(identity, robinRoundCounter + 1);
	}

	private Object getLock(int identity){
		return lockArray.get(identity);
	}






}
