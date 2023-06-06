package mywiimote;

import static mywiimote.Util.hexToByteArray;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.L2CAPConnection;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.microedition.io.Connector;

/**
 * The main class for library clients.
 *
 * This class is responsible for connection with the wiimote. To connect to the
 * wiimote, this class uses L2CAP connection, according to wiimote protocol.
 *
 * The protocol to communicate with the wiimote is based on what was described
 * at: https://wiibrew.org.
 *
 * This library requires bluecove api. BlueCove is a Java library for Bluetooth
 * (JSR-82 implementation). Bluecove requires BlueZ stack on linux (an official
 * Linux Bluetooth protocol stack).
 *
 * @author Pablo Rangel <pablorangel@gmail.com>
 */
public class WiiMoteDriver implements DiscoveryListener {
    
    /**
     * The object that represents the physical device of wiimote.
     */
    private RemoteDevice control = null;

    /**
     * Controls the thread of inquiring process.
     */
    final Object inquiryCompletedEvent = new Object();

    /**
     * L2CAP Connection for controlling. Still figuring out what for... Useless
     * for now.
     */
    private L2CAPConnection controlPipe = null;

    /**
     * L2CAP Connection for sending and receiving data.
     */
    private L2CAPConnection dataPipe = null;

    /**
     * Represents the client of the library. Every event occurred will be
     * notified to this listener.
     */
    private WiiMoteListener listener = null;

    /**
     * Map of the buttons for the first byte of data sent by the wiimote. Map
     * for: Pad Left, Pad Right, Pad Down, Pad Up, Button Plus. The id
     * (hexadecimal transformed to decimal) of the button refers to value sent
     * by the wiimote.
     */
    private HashMap<Integer, Button> buttonsByte1 = new HashMap();

    /**
     * Map of the buttons for the second byte of data sent by the wiimote. Map
     * for: Button 2, Button 1, Button B, Button A, Button Minus. The id
     * (hexadecimal transformed to decimal) of the button refers to value sent
     * by the wiimote.
     */
    private HashMap<Integer, Button> buttonsByte2 = new HashMap();

    /**
     * Indicates the sensitivity of the accelerometer.
     *
     * @see WiiMoteListener
     *
     */
    private double sensitivity;

    /**
     * The follow attribute specifies the value that determines the boundary of
     * positive or negative acceleration in x axis.
     */
    private double xBoundary = 0.0; //will store the value of wiimote in x axis without movement. 
    /**
     * The follow attribute specifies the value that determines the boundary of
     * positive or negative acceleration in y axis.
     */
    private double yBoundary = 0.0; //will store the value of wiimote in y axis without movement.
    /**
     * The follow attribute specifies the value that determines the boundary of
     * positive or negative acceleration in z axis.
     */
    private double zBoundary = 0.0; //will store the value of wiimote in z axis without movement.

    /**
     * Default constructor. Expects the client of the library as an argument.
     * The events will be notified to this listener.
     *
     * @param listener client of the library.
     * @param sensitivity sensitivity of the accelerometer.
     */
    public WiiMoteDriver(WiiMoteListener listener, double sensitivity) {
        System.setProperty("bluecove.jsr82.psm_minimum_off", "true");
        this.listener = listener;
        this.sensitivity = sensitivity;
        buttonsByte1.put(1, new Button(listener, ButtonEnum.BL));
        buttonsByte1.put(2, new Button(listener, ButtonEnum.BR));
        buttonsByte1.put(4, new Button(listener, ButtonEnum.BD));
        buttonsByte1.put(8, new Button(listener, ButtonEnum.BU));
        buttonsByte1.put(16, new Button(listener, ButtonEnum.BP));
        buttonsByte2.put(1, new Button(listener, ButtonEnum.B2));
        buttonsByte2.put(2, new Button(listener, ButtonEnum.B1));
        buttonsByte2.put(4, new Button(listener, ButtonEnum.BB));
        buttonsByte2.put(8, new Button(listener, ButtonEnum.BA));
        buttonsByte2.put(16, new Button(listener, ButtonEnum.BM));
    }

    /**
     * Method to find the bluetooth devices.
     *
     * @return true if the wiimote was found, false otherwise.
     */
    public boolean discover() {

        synchronized (inquiryCompletedEvent) {
            try {
                DiscoveryAgent agent = LocalDevice.getLocalDevice().getDiscoveryAgent();
                boolean started = agent.startInquiry(DiscoveryAgent.GIAC, this);
                while (control == null) {
                    if (started) {
                        System.out.println("wait for device inquiry to complete...");
                        inquiryCompletedEvent.wait();
                        if (control != null) {
                            System.out.println("Wii Remote found: " + control.getFriendlyName(false));
                            return true;
                        }
                        started = agent.startInquiry(DiscoveryAgent.GIAC, this);
                    }
                }
            } catch (BluetoothStateException ex) {
                Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return false;
    }

    /**
     * This method send the bluetooth device discovered.
     *
     * @param btDevice bluetooth device discovered.
     * @param cod class of bluetooth device.
     */
    @Override
    public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
        try {
            // If the device is not a wiimote (and a valid model), there is nothing to do.
            if (isValid(btDevice.getFriendlyName(false))) {
                control = btDevice;
            } else {
                System.out.println("Discarting: " + btDevice.getFriendlyName(false));
            }
        } catch (IOException ex) {
            System.out.println("Discarting unknown device...");
        }
    }

    /**
     * Just informs if the inquiring process is completed.
     */
    @Override
    public void inquiryCompleted(int discType) {
        System.out.println("Device Inquiry completed!");
        synchronized (inquiryCompletedEvent) {
            inquiryCompletedEvent.notifyAll();
        }
    }

    /**
     * Not implemented.
     */
    @Override
    public void serviceSearchCompleted(int transID, int respCode) {
        //Nothing to do so far.
    }

    /**
     * Not implemented.
     */
    @Override
    public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
        //Nothing to do so far.
    }

    /**
     * Indicate if the model of bluetooth device is valid. To be valid, the
     * device must comply with the list of the models listed in @class Model.
     *
     * @param model name of the model
     * @return true if valid. False otherwise.
     */
    private boolean isValid(String model) {
        var ret = Arrays.stream(Model.values()).anyMatch((t) -> t.getModelName().equals(model));
        return ret;
    }

    /**
     * This method is responsible for connection with the wiimote. The method
     * estabilish two connections: one for control and another for data.
     *
     * This method only can be called if the @method discover was successfull
     * called.
     *
     * @return true if connection was a successfull. False otherwise.
     */
    public boolean connect() {
        System.out.println("Connecting...");
        String controlAddress = "btl2cap://" + control.getBluetoothAddress() + ":11;authenticate=false;encrypt=false;master=false";
        String dataAddress = "btl2cap://" + control.getBluetoothAddress() + ":13;authenticate=false;encrypt=false;master=false";
        try {
            controlPipe = (L2CAPConnection) Connector.open(controlAddress);
            dataPipe = (L2CAPConnection) Connector.open(dataAddress);
            pairing();
            turnOnLed(1);
            setMode();
        } catch (IOException e) {
            try {
                controlPipe.close();
                dataPipe.close();
            } catch (IOException ex) {
                Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
            }

            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Disconnects the wiimote device.
     *
     * @return true if succeed. False otherwise.
     */
    public boolean disconnect() {
        if (controlPipe != null && dataPipe != null) {
            try {
                controlPipe.close();
                dataPipe.close();
            } catch (IOException ex) {
                Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
            }
            control = null;
        }
        return true;
    }

    /**
     * Method for setting mode of protocol. This mode returns data from the
     * buttons and the accelerometer in the Wii Remote. This library only
     * supports the mode 0x31. Should be called before receiving any data.
     */
    private void setMode() {
        byte[] report = hexToByteArray("a2120031");

        try {
            System.out.println("Changing mode...");
            dataPipe.send(report);

        } catch (IOException ex) {
            Logger.getLogger(WiiMoteDriver.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method for paring process. Should be called just after the connection.
     *
     * The message includes the bluetooth address of wiimote backwards.
     *
     * @return true if the paring is performed. False otherwise.
     */
    private boolean pairing() {
        String val6 = control.getBluetoothAddress().substring(10, 12);
        String val5 = control.getBluetoothAddress().substring(8, 10);
        String val4 = control.getBluetoothAddress().substring(6, 8);
        String val3 = control.getBluetoothAddress().substring(4, 6);
        String val2 = control.getBluetoothAddress().substring(2, 4);
        String val1 = control.getBluetoothAddress().substring(0, 2);

        String regularAddress = val1 + val2 + val3 + val4 + val5 + val6;
        String inverseAddress = val6 + val5 + val4 + val3 + val2 + val1;

//        try {
//            val6 = LocalDevice.getLocalDevice().getBluetoothAddress().substring(10, 12);
//            val5 = LocalDevice.getLocalDevice().getBluetoothAddress().substring(8, 10);
//            val4 = LocalDevice.getLocalDevice().getBluetoothAddress().substring(6, 8);
//            val3 = LocalDevice.getLocalDevice().getBluetoothAddress().substring(4, 6);
//            val2 = LocalDevice.getLocalDevice().getBluetoothAddress().substring(2, 4);
//            val1 = LocalDevice.getLocalDevice().getBluetoothAddress().substring(0, 2);
//
//        } catch (BluetoothStateException ex) {
//            Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        regularAddress = val1 + val2 + val3 + val4 + val5 + val6;
//        inverseAddress = val6 + val5 + val4 + val3 + val2 + val1;
        byte[] data = hexToByteArray(inverseAddress);
        try {
            controlPipe.send(data);
        } catch (IOException ex) {
            Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /**
     * Method for turn on the rumble feature.
     * 
     * @param time indicates time in milliseconds to turn off rumble. 
     * If the value is 0, rumble will not turned off automatically. 
     */
    public void turnOnRumble(long time) {
        byte[] report = hexToByteArray("a210" + "01");
        
        try {
            dataPipe.send(report);
            if (time > 0){
                Thread.sleep(time);
                turnOfRumble();
            }
        } catch (IOException ex) {
            Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method for turn off the rumble feature.
     */
    public void turnOfRumble() {
        byte[] report = hexToByteArray("a210" + "00");
        
        try {
            dataPipe.send(report);
        } catch (IOException ex) {
            Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method for turn on one of four leds.
     *
     * @param led led should be turned on. Options available: 1, 2, 3 ,4.
     */
    private void turnOnLed(int led) {
        System.out.println("Turning on led: " + led);
        String op = "10";
        switch (led) {
            case 1: {
                op = "10";
                break;
            }
            case 2: {
                op = "20";
                break;
            }
            case 3: {
                op = "40";
                break;
            }
            case 4: {
                op = "80";
                break;
            }
        }

        byte[] report = hexToByteArray("a211" + op);
        try {
            dataPipe.send(report);
        } catch (IOException ex) {
            Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Method for listening data from wiimote.
     *
     * This method can only be called if the connection is performed.
     * 
     * The listener will be notified according to the data received and
     * interpreted.
     *
     */
    public void listen() {
        int cont = 0;

        double lastValueOfX = 0.0; //will store the last value of wiimote in x axis. 
        double lastValueOfY = 0.0; //will store the value of wiimote in y axis.
        double lastValueOfZ = 0.0; //will store the value of wiimote in z axis.

        byte[] report = new byte[7];//expecting 7 bytes from wiimote. setMode method should be called first. 
        try {
            System.out.println("Receiving...");
            while (true) {
                dataPipe.receive(report);

                //Discovering the value for wiimote without movement.
                if (cont < 10) {
                    xBoundary = report[4] < 0 ? 256 + report[4] : report[4];
                    yBoundary = report[5] < 0 ? 256 + report[5] : report[5];
                    zBoundary = report[6] < 0 ? 256 + report[6] : report[6];

                    xBoundary = xBoundary / 256;
                    yBoundary = yBoundary / 256;
                    zBoundary = zBoundary / 256;
                    cont++;
                    continue;
                }

                int value = report[2];
                Button b1 = buttonsByte1.get(value); //if one button of the first byte was fired (LEFT, RIGHT, DOWN, UP OR PLUS), the library client will be notified. 
                if (b1 != null) {
                    b1.fire();
                }
                value = report[3];
                Button b2 = buttonsByte2.get(value); //if one button of the second byte was fired (TWO, ONE, B, A OR MINUS), the library client will be notified.
                if (b2 != null) {
                    b2.fire();
                }

                //Converting data from accelerometer: signed to unsigned.
                double x, y, z;

                x = report[4] < 0 ? 256 + report[4] : report[4];
                y = report[5] < 0 ? 256 + report[5] : report[5];
                z = report[6] < 0 ? 256 + report[6] : report[6];

                x = x / 256;
                y = y / 256;
                z = z / 256;

                //if the value is according to the setted sensitivity, then the client of the library will be notified. 
                if (sensitivity == WiiMoteListener.SENSITIVITY_OFF) {
                    listener.accelerometerValues(x, y, z, xBoundary, yBoundary, zBoundary);
                } else {
                    if ((x > (lastValueOfX * (1.0 + sensitivity)) || x < (lastValueOfX * (1.0 - sensitivity)))
                            && (y > (lastValueOfY * (1.0 + sensitivity)) || y < (lastValueOfY * (1.0 - sensitivity)))
                            && (z > (lastValueOfZ * (1.0 + sensitivity)) || z < (lastValueOfZ * (1.0 - sensitivity)))) {
                        listener.accelerometerValues(x, y, z,xBoundary, yBoundary, zBoundary);
                    }
                }
                lastValueOfX = x;
                lastValueOfY = y;
                lastValueOfZ = z;
            }
        } catch (IOException ex) {
            Logger.getLogger(WiiMoteDriver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public double getxBoundary() {
        return xBoundary;
    }

    public double getyBoundary() {
        return yBoundary;
    }

    public double getzBoundary() {
        return zBoundary;
    }

}
