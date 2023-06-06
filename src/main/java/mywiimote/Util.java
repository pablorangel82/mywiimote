package mywiimote;

/**
 * This class has the methods to deal with conversions. 
 * 
 * @author Pablo Rangel <pablorangel@gmail.com>
 */
class Util {

    /**
     * Transforms hexadecimal value to a signed binary representation. 
     * 
     * @param hexadecimal value in hexadecimal.
     * @return byte array. Value transformed.
     */
    static byte[] hexToByteArray(String hexadecimal) {
        int len = hexadecimal.length();
        byte[] ans = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            ans[i / 2] = (byte) (((Character.digit(hexadecimal.charAt(i), 16) << 4) + Character.digit(hexadecimal.charAt(i + 1), 16)) & 0xFF);
        }
        return ans;

    }
}
