package mywiimote;

/**
 *
 * This class shows how to use the library mywiimote. It is just a simple
 * example :-)
 *
 * @author Pablo Rangel <pablorangel@gmail.com>
 */
public class Sample {

    public static void main(String[] args) {

        WiiMoteDriver wii = new WiiMoteDriver(new WiiMoteListener() {

            @Override
            public void buttonPlusPressed() {
                System.out.println("Button + pressed!");
            }

            @Override
            public void buttonMinusPressed() {
                System.out.println("Button - pressed!");
            }

            @Override
            public void buttonAPressed() {
                System.out.println("Button A pressed!");
            }

            @Override
            public void buttonBPressed() {
                System.out.println("Button B pressed!");
            }

            @Override
            public void button1Pressed() {
                System.out.println("Button 1 pressed!");
            }

            @Override
            public void button2Pressed() {
                System.out.println("Button 2 pressed!");
            }

            @Override
            public void buttonLeftPressed() {
                System.out.println("Button LEFT pressed!");
            }

            @Override
            public void buttonRightPressed() {
                System.out.println("Button RIGHT pressed!");
            }

            @Override
            public void buttonUpPressed() {
                System.out.println("Button UP pressed!");
            }

            @Override
            public void buttonDownPressed() {
                System.out.println("Button DOWN pressed!");
            }

            @Override
            public void accelerometerValues(double x, double y, double z, double xBoundary, double yBoundary, double zBoundary) {
                if (x > xBoundary) {
                    System.out.println("RIGHT Acceleration: " + x);
                }
                if (x < xBoundary) {
                    System.out.println("LEFT Acceleration:" + x);
                }
            }

            @Override
            public void buttonPlusReleased() {
                System.out.println("Button + Released!");
            }

            @Override
            public void buttonMinusReleased() {
                System.out.println("Button - Released!");
            }

            @Override
            public void buttonAReleased() {
                System.out.println("Button A Released!");
            }

            @Override
            public void buttonBReleased() {
                System.out.println("Button B Released!");
            }

            @Override
            public void button1Released() {
                System.out.println("Button 1 Released!");
            }

            @Override
            public void button2Released() {
                System.out.println("Button 2 Released!");
            }

            @Override
            public void buttonLeftReleased() {
                System.out.println("Button LEFT Released!");
            }

            @Override
            public void buttonRightReleased() {
                System.out.println("Button RIGHT Released!");
            }

            @Override
            public void buttonUpReleased() {
                System.out.println("Button UP Released!");
            }

            @Override
            public void buttonDownReleased() {
                System.out.println("Button DOWN Released!");
            }
        },
                WiiMoteListener.SENSITIVITY_MEDIUM);

        if (wii.discover()) {
            if (wii.connect()) {
                wii.turnOnRumble(3000);
                wii.listen();
            }

        }
    }

}
