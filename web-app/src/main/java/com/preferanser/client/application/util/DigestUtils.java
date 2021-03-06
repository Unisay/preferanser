package com.preferanser.client.application.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

public abstract class DigestUtils {

    private static final Logger log = Logger.getLogger("EditorView");

    public static String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte anArray : array)
            sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
        return sb.toString();
    }

    public static String md5Hex(String message) {
        if (message == null)
            return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            log.severe(e.getMessage());
        } catch (UnsupportedEncodingException e) {
            log.severe(e.getMessage());
        }
        return null;
    }

}