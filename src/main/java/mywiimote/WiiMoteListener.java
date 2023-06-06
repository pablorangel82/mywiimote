package mywiimote;

/**
 * Mandatory interface clients of the library.
 * 
 * When an event occurs, the appropriate method will be called.
 * 
 * @author Pablo Rangel <pablorangel@gmail.com>
 */
public interface WiiMoteListener {
    
    /**
     * Sensitivity OFF. All data from Accelerometer will be send to the listener.
     */
    public static final double SENSITIVITY_OFF = 0.0;
    /**
     * Sensitivity HIGH. All data from Accelerometer will be send to the listener according to 0.1% of tolerance.
     */
    public static final double SENSITIVITY_HIGH = 0.001;
    /**
     * Sensitivity MEDIUM. All data from Accelerometer will be send to the listener according to 1% of tolerance.
     */
    public static final double SENSITIVITY_MEDIUM = 0.01;
    /**
     * Sensitivity LOW. All data from Accelerometer will be send to the listener according to 10% of tolerance.
     */
    public static final double SENSITIVITY_LOW = 0.1;
    
    /**
     * The listener will be notified if the PLUS button is pressed. 
     */
    public void buttonPlusPressed();
    
    /**
     * The listener will be notified if the MINUS button is pressed. 
     */
    public void buttonMinusPressed();
    
    /**
     * The listener will be notified if the A button is pressed. 
     */
    public void buttonAPressed();
    
    /**
     * The listener will be notified if the B button is pressed. 
     */
    public void buttonBPressed();
    
    /**
     * The listener will be notified if the 1 button is pressed. 
     */
    public void button1Pressed();
    
    /**
     * The listener will be notified if the 2 button is pressed. 
     */
    public void button2Pressed();
    
    /**
     * The listener will be notified if the LEFT button is pressed. 
     */
    public void buttonLeftPressed();
    
    /**
     * The listener will be notified if the RIGHT button is pressed. 
     */
    public void buttonRightPressed();
    
    /**
     * The listener will be notified if the RIGHT button is pressed. 
     */
    public void buttonUpPressed();
    
    /**
     * The listener will be notified if the DOWN button is pressed. 
     */
    public void buttonDownPressed();
    
    /**
     * The listener will be notified if the PLUS button is released. 
     */
    public void buttonPlusReleased();
    
    /**
     * The listener will be notified if the MINUS button is released. 
     */
    public void buttonMinusReleased();
    
    /**
     * The listener will be notified if the A button is released. 
     */
    public void buttonAReleased();
    
    /**
     * The listener will be notified if the B button is released. 
     */
    public void buttonBReleased();
    
    /**
     * The listener will be notified if the 1 button is released. 
     */
    public void button1Released();
    
    /**
     * The listener will be notified if the 2 button is released. 
     */
    public void button2Released();
    
    /**
     * The listener will be notified if the LEFT button is released. 
     */
    public void buttonLeftReleased();
    
    /**
     * The listener will be notified if the RIGHT button is released. 
     */
    public void buttonRightReleased();
    
    /**
     * The listener will be notified if the UP button is released. 
     */
    public void buttonUpReleased();
    
    /**
     * The listener will be notified if the DOWN button is released. 
     */
    public void buttonDownReleased();
    
    /**
     * The listener will be notified with the values of the accelerometer.This method will be called IF ALL VALUES ARE according to sensitivity defined.
     * For Axis X, if the value is higher than X Boundary, the control was moved to the right direction.Otherwise the control was moved to the left direction.
     * For Axis Y, if the value is higher than Y Boundary, the control was moved to the up direction. Otherwise the control was moved to the down direction.
     * For Axis Z, if the value is higher than Z Boundary, the control was moved to the forward direction. Otherwise the control was moved to the backward direction.
     * 
     * @param x value in x axis. Value between 0 and 1. 
     * @param y value in y axis. Value between 0 and 1.
     * @param z value in z axis. Value between 0 and 1.
     * @param xBoundary determines if the value of x should be considered positive acceleration or negative acceleration.
     * @param yBoundary determines if the value of y should be considered positive acceleration or negative acceleration.
     * @param zBoundary determines if the value of z should be considered positive acceleration or negative acceleration.
     * @see WiiMoteDriver
     */
    public void accelerometerValues(double x, double y, double z, double xBoundary, double yBoundary, double zBoundary);
}
