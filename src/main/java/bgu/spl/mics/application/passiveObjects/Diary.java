package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a Diary - in which the flow of the battle is recorded.
 * We are going to compare your recordings with the expected recordings, and make sure that your output makes sense.
 * <p>
 * Do not add to this class nothing but a single constructor, getters and setters.
 */
public class Diary {

    private AtomicInteger threadFinishCounter;
    private AtomicInteger totalAttack;
    private final AtomicBoolean isFinished;
    private long HanSoloFinish;
    private long C3POFinish;
    private long R2D2Deactivate;
    private long LeiaTerminate;
    private long HanSoloTerminate;
    private long C3POTerminate;
    private long R2D2Terminate;
    private long LandoTerminate;

    private static class SingletonHolder{
        private static Diary instance = new Diary();
    }

    private Diary(){
        totalAttack = new AtomicInteger(0);
        threadFinishCounter = new AtomicInteger(0);
        isFinished = new AtomicBoolean(false);
    }

    public static Diary getInstance(){
        return SingletonHolder.instance;
    }

    public void raiseThreadFinishCounterBy1(){
        int currThreadsFinished;
        do{
            currThreadsFinished = threadFinishCounter.intValue();
        }while (!threadFinishCounter.compareAndSet(currThreadsFinished,currThreadsFinished+1));
        isFinished();//check if all threads finished
    }

    public void raiseAttackBy1() {
        int currAttackNumber;
        do {
            currAttackNumber = totalAttack.intValue();
        } while (!totalAttack.compareAndSet(currAttackNumber, currAttackNumber + 1));
    }

    public void setHanSoloFinish(long hanSoloFinish) {
        HanSoloFinish = hanSoloFinish;
    }

    public void setC3POFinish(long c3POFinish) {
        C3POFinish = c3POFinish;
    }

    public void setR2D2Deactivate(long r2D2Deactivate) {
        R2D2Deactivate = r2D2Deactivate;
    }

    public void setLeiaTerminate(long leiaTerminate) {
        LeiaTerminate = leiaTerminate;
    }

    public void setHanSoloTerminate(long hanSoloTerminate) {
        HanSoloTerminate = hanSoloTerminate;
    }

    public void setC3POTerminate(long c3POTerminate) {
        C3POTerminate = c3POTerminate;
    }

    public void setR2D2Terminate(long r2D2Terminate) {
        R2D2Terminate = r2D2Terminate;
    }

    public void setLandoTerminate(long landoTerminate) {
        LandoTerminate = landoTerminate;
    }

    public AtomicInteger getThreadFinishCounter() {
        return threadFinishCounter;
    }

    public AtomicInteger getTotalAttack() {
        return totalAttack;
    }

    public long getHanSoloFinish() {
        return HanSoloFinish;
    }

    public long getC3POFinish() {
        return C3POFinish;
    }

    public long getR2D2Deactivate() {
        return R2D2Deactivate;
    }

    public long getLeiaTerminate() {
        return LeiaTerminate;
    }

    public long getHanSoloTerminate() {
        return HanSoloTerminate;
    }

    public long getC3POTerminate() {
        return C3POTerminate;
    }

    public long getR2D2Terminate() {
        return R2D2Terminate;
    }

    public long getLandoTerminate() {
        return LandoTerminate;
    }

    public AtomicBoolean getIsFinished() {
        return isFinished;
    }

    private void isFinished(){
        if (threadFinishCounter.get()==5){
            isFinished.compareAndSet(false,true);
            synchronized (isFinished){
                isFinished.notifyAll();//wake up main
            }
        }
    }
}
