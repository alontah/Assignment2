package bgu.spl.mics;

import bgu.spl.mics.application.services.C3POMicroservice;
import bgu.spl.mics.application.services.HanSoloMicroservice;
import bgu.spl.mics.application.services.LeiaMicroservice;
import bgu.spl.mics.application.services.R2D2Microservice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private Vector<Queue<Message>> microServiceQueues;
//	private Queue<Message> leiaQueue;
//	private Queue<Message> hanQueue;
//	private Queue<Message> C3POQueue;
//	private Queue<Message> R2D2Queue;
//	private Queue<Message> landoQueue;



	private MessageBusImpl(){
		this.microServiceQueues = new Vector<>(5);
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
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

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
		
		return null;
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
		return 4;
	}
}
