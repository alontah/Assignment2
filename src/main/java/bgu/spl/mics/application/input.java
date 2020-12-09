package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;

public class input {
    private static input instance = null;
    private Attack[] attacks;
    int R2D2;
    int Lando;
    int Ewoks;

    private input(){};

    public synchronized static input getInstance(){
        if (instance==null){
            instance = new input();
        }
        return instance;
    }

    public int getEwoks() {
        return Ewoks;
    }
    public void setEwoks(int ewoks) {
        Ewoks = ewoks;
    }
    public int getLando() {
        return Lando;
    }
    public void setLando(int lando) {
        Lando = lando;
    }
    public int getR2D2() {
        return R2D2;
    }
    public void setR2D2(int r2d2) {
        R2D2 = r2d2;
    }
    public Attack[] getAttacks() {
        return attacks;
    }
    public void setAttacks(Attack[] attacks) {
        this.attacks = attacks;
    }
}
