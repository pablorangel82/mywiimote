package mywiimote;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents a Wiimote button. It is for implementation sake. Does not matter
 * for library users.
 *
 * This class maps a generic button, with the methods that implement the
 * behavior of events.
 *
 * @author Pablo Rangel <pablorangel@gmail.com>
 */
class Button {

    /**
     * This constant indicates the minimal interval that indicates if the button
     * is hold or not.
     */
    private static final long INTERVAL = 300;

    /**
     * Enum value of mapped button.
     */
    private final ButtonEnum button;

    /**
     * Library client. When the button is fired, the listener will be notified.
     */
    private WiiMoteListener listener;

    /**
     * Last time when the button was activated.
     */
    private long timestamp;

    /**
     * Default constructor.
     *
     * @param listener client of the library. When a event occurs, the listener
     * is notified according to the button.
     * @param button button object of wiimote which has the event was fired.
     */
    Button(WiiMoteListener listener, ButtonEnum button) {
        this.listener = listener;
        this.button = button;
        timestamp = System.currentTimeMillis();
    }

    /**
     * This method is called when the button is pressed or released.
     */
    void fire() {
        long time = System.currentTimeMillis();
        long dif = time - timestamp;
        timestamp = System.currentTimeMillis();

        /* if the button is already activated according to the interval, the event is discarted.*/
        if (dif < INTERVAL) {
            return;
        }

        /*
         * This thread will be started when the button is fired. 
         * The method will be invocated according to reflection technique.    
         */
        Thread thread = new Thread(() -> {
            try {
                Method m1 = listener.getClass().getMethod(button.toString() + "Pressed", null);
                m1.invoke(listener, null);

                long t;
                long d;

                do {
                    Thread.sleep(100);
                    t = System.currentTimeMillis();
                    d = t - timestamp;

                } while (d < INTERVAL); //while the button is activated, the client will not be notified about release event.   

                Method m2 = listener.getClass().getMethod(button.toString() + "Released", null);
                m2.invoke(listener, null);

            } catch (IllegalAccessException ex) {
                Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Button.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        thread.start();

    }

}
