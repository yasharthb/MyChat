package com.mychat;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Random;

public class Encryption {

    private static String decode(String message) {
        byte[] data = Base64.decode(message, Base64.DEFAULT);
        try {
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
    private static String encode(String input) {
        try {
            return Base64.encodeToString(input.getBytes("UTF-8"),Base64.DEFAULT);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String key()
    {   String k="";
    Random r=new Random();
    for(int i=0;i<4;i++)
        k=k+(char)(r.nextInt(25)+65);
    return k;
    }
public static String encrypt(String strClearText,String strKey){
    String strData="";
    String encData=encode(strClearText);
    for(int i=0,j=0;i<encData.length();i++)
    {
        char ch=encData.charAt(i);
        if(Character.isUpperCase(ch))
        {
            strData=strData+(char)((ch + strKey.charAt(j) - 130) % 26 + 'A');
            j=++j%4;
        }
        else if(Character.isLowerCase(ch))
        {
            strData=strData+(char)((ch + strKey.charAt(j) - 162) % 26 + 'a');
            j=++j%4;
        }
        else
            strData=strData+ch;
    }
    return strData;

 }
    public static String decrypt(String strEncrypted, String strKey) {
        String strData = "";
        for (int i = 0, j = 0; i < strEncrypted.length(); i++)
        {
            char ch = strEncrypted.charAt(i);

            if (Character.isUpperCase(ch)) {
                strData += (char) ((ch - strKey.charAt(j) + 26) % 26 + 'A');
                j = ++j % 4;
            }
            else if(Character.isLowerCase(ch))
            {
                strData += (char) ((ch - strKey.charAt(j) + 20) % 26 + 'a');
                j = ++j % 4;
            }
            else
                strData+=ch;
        }

        return decode(strData);

    }
}