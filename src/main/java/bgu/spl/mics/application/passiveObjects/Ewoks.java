package bgu.spl.mics.application.passiveObjects;


import java.util.Vector;

/**
 * Passive object representing the resource manager.
 * <p>
 * This class must be implemented as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class Ewoks {

    private static int ewokNum;
    private Vector<Ewok> ewokVector;

    private static class singletonHolder{
        private static int numOfEwoks;
        private static Ewoks instance = new Ewoks() ;
    }

    private Ewoks(){
        this.ewokVector = new Vector<>();
        for(int i =1;i<=ewokNum;i++){
            ewokVector.add(new Ewok(i));
        }
    }

    public static Ewoks getInstance(int numOfEwoks){
        ewokNum = numOfEwoks;
        return singletonHolder.instance;
    }

    public void acquireEwok (int serialNum) {
        Ewok currentEwok = ewokVector.elementAt(serialNum - 1);
        synchronized (currentEwok) {
            while (!currentEwok.available) {
                try {
                    currentEwok.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            currentEwok.acquire();
        }
    }

    public  void releaseEwok (int serialNum){
        Ewok currentEwok = ewokVector.elementAt(serialNum - 1);
        currentEwok.release();
        synchronized (currentEwok) {
            currentEwok.notifyAll();
        }
    }
}
