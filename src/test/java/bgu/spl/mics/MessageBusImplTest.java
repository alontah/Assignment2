//package bgu.spl.mics;
//
//import bgu.spl.mics.application.messages.AttackEvent;
//import bgu.spl.mics.application.services.C3POMicroservice;
//import bgu.spl.mics.application.services.HanSoloMicroservice;
//
//import static org.junit.Assert.*;
//
//public class MessageBusImplTest {
//    private MessageBusImpl magicBus;
//    private MicroService firstMicroService;
//    private MicroService secondMicroService;
//    private AttackEvent attack1;
//    private AttackEvent attack2;
//    private Broadcast b1;
//
//    @org.junit.Before
//    public void setUp() throws Exception {
//        this.magicBus = MessageBusImpl.getInstance();
//        this.firstMicroService = new HanSoloMicroservice();
//        this.secondMicroService = new C3POMicroservice();
//        this.attack1 = new AttackEvent();
//        this.attack2 = new AttackEvent();
//        this.b1 = new bgu.spl.mics.application.messages.Broadcast();
//    }
//
//    @org.junit.Test
//    /**
//     * @pre:
//     *      hanQueue = null;
//     *
//     * tests the following methods:
//     *      register - initializes the microService Queue
//     *      subscribeEvent - subscribe's the microService to a type of events
//     *      sendEvent - enqueues the event to the subscribed microServices.
//     *      complete - updates the Future result
//     *
//     * @post:
//     *      hanQueue != null;
//     *      firstMicroService is subscribed to event type
//     *      future isDone();
//     *      future.result == true;
//     *
//     **/
//    public void firstTestSendEvent(){
//        this.magicBus.register(this.firstMicroService);
//        this.magicBus.subscribeEvent( attack1.getClass(), this.firstMicroService);
//        Future future1 = this.magicBus.sendEvent(attack1);
//        this.magicBus.complete(attack1, true);
//        assertTrue(future1.isDone());
//        assertEquals(true, future1.get());
//    }
//
//    @org.junit.Test
//    /**
//     * @pre:
//     *      C3POQueue = null;
//     *
//     * tests the following methods:
//     *      register - initializes the microService Queue
//     *      subscribeBroadcast - subscribe's the microService to receive broadcasts.
//     *      sendBroadcast - enqueues the Broadcast to the subscribed microServices.
//     *
//     * @post:
//     *      C3POQueue != null;
//     *      secondMicroService is subscribed to broadcasts
//     *
//     **/
//    public void secondTestSendBroadcast(){
//        this.magicBus.register(this.secondMicroService);
//        this.magicBus.subscribeBroadcast(b1.getClass(), this.secondMicroService);
//        this.magicBus.sendBroadcast(b1);
//    }
//
//
//
//    @org.junit.Test
//    /**
//     * @pre:
//     *      C3POQueue = null;
//     *
//     * tests the following methods:
//     *      register - initializes the microService Queue
//     *      subscribeEvent - subscribe's the microService to a type of events
//     *      sendEvent - enqueues the event to the subscribed microServices.
//     *      awaitMessage - returns the next event in the Queue
//     *
//     * @post:
//     *      C3POQueue != null;
//     *      attack1 == attack2;
//     *
//     **/
//    public void awaitMessage() throws InterruptedException {
//        this.magicBus.register(this.secondMicroService);
//        magicBus.subscribeEvent(attack2.getClass(), secondMicroService); // subscribes with an empty callback
//        magicBus.sendEvent(attack2);
//        try {
//            attack1 =(AttackEvent) magicBus.awaitMessage(secondMicroService);
//        } catch (InterruptedException e){}
//
//        assertEquals(attack1, attack2);
//
//    }
//
//    @org.junit.Test //tested in firstTestSendEvent
//    public void subscribeEvent() {
//    }
//
//    @org.junit.Test //tested in secondTestSendBroadcast
//    public void subscribeBroadcast() {
//    }
//
//    @org.junit.Test //tested in firstTestSendEvent
//    public void complete() {
//    }
//
//    @org.junit.Test //tested in secondTestSendBroadcast
//    public void sendBroadcast() {
//    }
//
//    @org.junit.Test //tested in firstTestSendEvent
//    public void sendEvent() {
//    }
//
//    @org.junit.Test //tested in firstTestSendEvent
//    public void register() {
//    }
//
//
//}