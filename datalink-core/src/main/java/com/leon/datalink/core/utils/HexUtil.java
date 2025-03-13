package com.leon.datalink.core.utils;


public class HexUtil {

    /**
     * byte转Hex
     */
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if(hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }
    /**
     * byte[]转Hex
     */
    public static String bytesToHex(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if(hex.length() < 2) {
                hex = "0" + hex;
            }
            sb.append(hex.toUpperCase());
        }

        return sb.toString();
    }

    /**
     * Hex转byte,hex只能含两个字符，如果是一个字符byte高位会是0
     */
    public static byte hexToByte(String hex) {
        return (byte)Integer.parseInt(hex, 16);
    }
    /**
     * Hex转byte[]，两种情况，Hex长度为奇数最后一个字符会被舍去
     */
    public static byte[] hexToBytes(String hex) {
        if (hex.length() < 1) {
            return null;
        } else {
            byte[] result = new byte[hex.length() / 2];
            int j = 0;
            for(int i = 0; i < hex.length(); i+=2) {
                result[j++] = (byte)Integer.parseInt(hex.substring(i,i+2), 16);
            }
            return result;
        }
    }

}
