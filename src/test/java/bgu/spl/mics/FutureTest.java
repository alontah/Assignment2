package bgu.spl.mics;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    private Future<String> future;

    @Before
    public void setUp() throws Exception {
        future = new Future<String>();
    }

    @Test
    /**@pre
     *      future.isDone = false;
     *      future.result = null;
     *
     * @post
     *      future.isDone = true;
     *      future.result = "test";
     **/
    public void get() {
        assertFalse(future.isDone());
        future.resolve("test");
        String test = future.get();
        assertEquals("test", test);
        assertTrue(future.isDone());
    }

    @Test
    /**@pre
     *      future.isDone = false;
     *      future.result = null;
     *
     * @post
     *      future.isDone = true;
     *      future.result = "test";
     **/
    public void resolve() {
        String str = "someResult";
        future.resolve(str);
        assertTrue(future.isDone());
        assertEquals(str, future.get());
    }

    @Test
    /**@pre
     *      future.isDone = false;
     *
     * @post
     *      future.isDone = true;
     **/
    public void isDone() {
        assertFalse(future.isDone());
        future.resolve("test");
        assertTrue(future.isDone());
    }

    @Test
    /**@pre
     *      future.isDone = false;
     *      future.result = null;
     *
     * @midTest
     *      future.isDone = false;
     *      future.result = null;
     *
     * @post
     *      future.isDone = true;
     *      future.result = "test";
     **/
    public void getTimeOut() throws InterruptedException {
        assertFalse(future.isDone());
        future.get(200, TimeUnit.MILLISECONDS);
        assertFalse(future.isDone());
        future.resolve("test");
        assertEquals("test", future.get(200,TimeUnit.MILLISECONDS));
    }
}