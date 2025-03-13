

package com.leon.datalink.core.utils;


import com.leon.datalink.core.common.Constants;

import java.nio.charset.Charset;

/**
 * ByteUtils.
 *
 * @author Leon
 */
public final class ByteUtils {
    
    public static final byte[] EMPTY = new byte[0];
    
    /**
     * String to byte array.
     *
     * @param input input string
     * @return byte array of string
     */
    public static byte[] toBytes(String input) {
        if (input == null) {
            return EMPTY;
        }
        return input.getBytes(Charset.forName(Constants.ENCODE));
    }
    
    /**
     * Object to byte array.
     *
     * @param obj input obj
     * @return byte array of object
     */
    public static byte[] toBytes(Object obj) {
        if (obj == null) {
            return EMPTY;
        }
        return toBytes(String.valueOf(obj));
    }
    
    /**
     * Byte array to string.
     *
     * @param bytes byte array
     * @return string
     */
    public static String toString(byte[] bytes) {
        if (bytes == null) {
            return StringUtils.EMPTY;
        }
        return new String(bytes, Charset.forName(Constants.ENCODE));
    }

    public static int compare(byte[] buffer1, byte[] buffer2) {
        return ByteUtils.compare(buffer1, 0, buffer1.length, buffer2, 0, buffer2.length);
    }

    public static int compare(byte[] buffer1, int offset1, int length1, byte[] buffer2, int offset2, int length2) {
        if (buffer1 == buffer2 && offset1 == offset2 && length1 == length2) {
            return 0;
        } else {
            int end1 = offset1 + length1;
            int end2 = offset2 + length2;
            int i = offset1;

            for(int j = offset2; i < end1 && j < end2; ++j) {
                int a = buffer1[i] & 255;
                int b = buffer2[j] & 255;
                if (a != b) {
                    return a - b;
                }

                ++i;
            }

            return length1 - length2;
        }
    }

    public static boolean isEmpty(byte[] data) {
        return data == null || data.length == 0;
    }
    
    public static boolean isNotEmpty(byte[] data) {
        return !isEmpty(data);
    }
    
}
