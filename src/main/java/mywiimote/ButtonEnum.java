package mywiimote;

/**
 * Enum to map wiimote buttons.
 * 
 * @author Pablo Rangel <pablorangel@gmail.com>
 */
enum ButtonEnum {
    
    B1("button1"),
    B2("button2"),
    BA("buttonA"),
    BB("buttonB"),
    BM("buttonMinus"),
    BP("buttonPlus"),
    BL("buttonLeft"),
    BR("buttonRight"),
    BU("buttonUp"),
    BD("buttonDown");
    
    final private String label;
    
    ButtonEnum (String label){
        this.label=label;
    }
    
    @Override
    public String toString(){
        return label;
    }
}
