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
    private static Ewoks instance = null;
    private Vector<Ewok> ewokVector;

    private Ewoks(int numOfEwoks){
        this.ewokVector = new Vector<>();
        for(int i =1;i<=numOfEwoks;i++){
            ewokVector.add(new Ewok(i));
        }
    }

    public static synchronized Ewoks getInstance(int numOfEwoks){
        if(instance==null) {
            instance = new Ewoks(numOfEwoks);
        }
        return instance;
    }

    public synchronized boolean acquireEwok (int serialNum)  {
        Ewok currentEwok = ewokVector.elementAt(serialNum-1);
        while (!currentEwok.available) {//wait until ewok is available
            try {
                wait();
            } catch (InterruptedException e){};
        }
        currentEwok.acquire();
        return true;
    }

    public void releaseEwok (int serialNum){
        ewokVector.elementAt(serialNum-1).release();
        notifyAll();
    }
}
