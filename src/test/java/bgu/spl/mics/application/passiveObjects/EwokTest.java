package bgu.spl.mics.application.passiveObjects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EwokTest {
    private Ewok E1;
    private Ewok E2;

    @org.junit.Before
    public void setUp() throws Exception {
        E1 = new Ewok(1);
        E2 = new Ewok(2);
        E2.acquire();
    }

    @org.junit.Test
    /**@pre
     *      Ewok.available = true;
     *
     * @post
     *      Ewok.avaolable = false;
     *
     **/
    public void acquire() {
        E1.acquire();
        assertFalse(E1.isAvailable());
    }

    @org.junit.Test
    /**@pre
     *      Ewok.available = false;
     *
     * @post
     *      Ewok.avaolable = true;
     *
     **/
    public void release() {
        E2.release();
        assertTrue(E2.isAvailable());
    }
}