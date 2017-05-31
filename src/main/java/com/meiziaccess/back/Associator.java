package com.meiziaccess.back;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sun on 2016/11/27.
 */
public class Associator {
    public List<Addresses> getAddresses(String orginalDir, String lowVideoDir, String highVideoDir, String frameDir, String xmlDir) {
        List<Addresses> rs=new ArrayList<>();
        File root = new File(orginalDir);
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                rs.addAll(getAddresses(file.getAbsolutePath(),lowVideoDir,highVideoDir,frameDir,xmlDir));
            } else if (isXml(file)) {
                Addresses curAddress=new Addresses();
                String name=getName(file);
                curAddress.setLowCodeVideoPath(associateVideo(name,lowVideoDir));
                curAddress.setHighCodeVideoPath(associateVideo(name,highVideoDir));
                curAddress.setUnCatalgedXmlPath(file.getAbsolutePath());
                curAddress.setCatalgedXmlPath("");
                curAddress.setName(name);
                StringBuilder sb=new StringBuilder();
                associatetKeyFrames(name,frameDir,sb);
                curAddress.setKeyFramePath(sb.toString());
                rs.add(curAddress);
            }
        }
        return rs;
    }

    private boolean isXml(File file) {
        boolean flag = false;
        String str = file.getName();
        String type = str.substring(str.lastIndexOf('.') + 1, str.length());
        if (type.toLowerCase().equals("xml"))
            flag = true;
        return flag;
    }
    private String getName(File file) {
        String name = new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            name=root.getChildText("Name");
        } catch (JDOMException | IOException e) {
            // TODO Auto-generated catch block
            System.out.println("file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
        return name;
    }
    private String getID(File file) {
        String id=new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            id=root.getChildText("ID");
        } catch (JDOMException | IOException e) {
            // TODO Auto-generated catch block
            System.out.println("file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
        return id;
    }
    private String associateVideo(String name,String videoDir){
        String rs=new String();
        File root = new File(videoDir);
        File[] files = root.listFiles();
        for (File file : files) {
            if(!rs.equals("")) return rs;
            if (file.isDirectory()) {
               rs=associateVideo(name,file.getAbsolutePath());
            } else {
                String filename = file.getName().toLowerCase();
                if (filename.contains(name)) {
                   return file.getAbsolutePath();
                }
            }
        }
        return "";
    }
    private void associatetKeyFrames(String name,String xmlDir,StringBuilder stringBuilder){
        File root = new File(xmlDir);
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                associatetKeyFrames(name,file.getAbsolutePath(),stringBuilder);
            } else {
                String filename = file.getName().toLowerCase();
                if (filename.contains(name)) {
                    stringBuilder.append(file.getAbsoluteFile());
                    stringBuilder.append(";");
                }
            }
        }
    }
}
