package com.jyb.util;

import com.jyb.config.JstreamConfiguration;
import jyb.test.MockConfiguration;

import java.io.*;

public class ConfigutionUtils {

    public static void configuartionSerializedToFile(JstreamConfiguration configuration,String path){
        try {
            FileOutputStream fos=new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JstreamConfiguration fileSerializedToConfiguration(String path){
        try {
            FileInputStream fis = new FileInputStream(path);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (JstreamConfiguration)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        JstreamConfiguration configuration = MockConfiguration.newInstance();
        ConfigutionUtils.configuartionSerializedToFile(configuration,"/home/zh/Desktop/001.data");
        JstreamConfiguration conf = ConfigutionUtils.fileSerializedToConfiguration("/home/zh/Desktop/001.data");
        System.out.println(conf.getExtConfig().getAppName());
    }
}
