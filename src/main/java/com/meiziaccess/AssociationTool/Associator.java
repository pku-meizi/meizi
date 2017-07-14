package com.meiziaccess.AssociationTool;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.internal.SystemProperty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sun on 2016/11/27.
 */
public class Associator {

    //orginalDir 编目前xml
    //xmlDir 编目后xml
    public List<Addresses> getAddresses(String orginalDir,String lowVideoDir,String highVideoDir,String frameDir,String xmlDir, int type) {

        List<Addresses> rs=new ArrayList<>();
        File root = new File(orginalDir);
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                rs.addAll(getAddresses(file.getAbsolutePath(),lowVideoDir,highVideoDir,frameDir,xmlDir, type));
            } else if (isXml(file)) {
                Addresses curAddress=new Addresses();
                String name="";
                String keyFramePath="";
                //解析xml, 获取名字
                try {
                    switch (type) {
                        case 1:
                            name = getName(file);       //网络台
                            StringBuilder sb = new StringBuilder();
                            associatetKeyFrames(name, frameDir, sb);
                            keyFramePath = sb.toString();
                            break;
                        case 2:
                            name = getName2(file);      //BTV
                            keyFramePath = getKeyFrameBTVorNanfang(file, frameDir);
                            // System.out.println(keyFramePath+"-----------------------------------------------------------------------------------------");
                            break;
                        case 3:
                            name = getName2(file);      //南方素材
                            keyFramePath = getKeyFrameBTVorNanfang(file, frameDir);
                            break;
                        case 4:
                            name = getName3(file);      //海外素材
                            keyFramePath = getKeyFrameHaiWai(file, frameDir);
                            break;
                        case 5:
                            name = getName4(file);      //电视剧
                            System.out.println(name);
                            keyFramePath = getKeyFrameDianshiju(file, frameDir);
                            break;
                        default:
                    }
                }catch (Exception e){
                    continue;
                }

                curAddress.setLowCodeVideoPath(associateVideo(name,lowVideoDir));
                curAddress.setHighCodeVideoPath(associateVideo(name,highVideoDir));

                curAddress.setUnCatalgedXmlPath(file.getAbsolutePath());
                curAddress.setCatalgedXmlPath("");
                curAddress.setName(name);
                curAddress.setKeyFramePath(keyFramePath);

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

    private String associateVideo(String name,String videoDir){
        while(name.contains(" ")) name=name.replaceAll(" ","");
        name=name.toLowerCase();
        String rs=new String();
        File root = new File(videoDir);
        File[] files = root.listFiles();
        for (File file : files) {
            if(!rs.equals("")) return rs;
            if (file.isDirectory()) {
               rs=associateVideo(name,file.getAbsolutePath());
            } else {
                String filename = file.getName().replace("（一）", "1/2").replace("（二）", "2/2").toLowerCase();
                while (filename.contains(" ")) filename=filename.replaceAll(" ","");
                if (filename.contains(name)) {
                   return file.getAbsolutePath();
                }
            }
        }
        return "";
    }



    //函数用途：知道节目的 Name 找到与之相关的关键帧，这要用做处理网络节目的那些编目
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



    //针对网络台
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
    //针对BTV 和 南方素材
    public String getName2(File file){
        String name=new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            Namespace ns = root.getNamespace();
            Element nameElemnet=root.getChild("List",ns).getChild("CatalogueMetaData",ns).getChild("TopUnit",ns).getChild("Name",ns);
            name=nameElemnet.getText();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
    //针对海外素材
    public String getName3(File file){
        String name=new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            Namespace ns = root.getNamespace();
            Element nameElemnet=root.getChild("ImportContents",ns).getChild("CatalogueMetaData",ns).getChild("TopUnit",ns).getChild("Name",ns);
            name=nameElemnet.getText();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
    //针对电视剧
    public String getName4(File file){
        String name=new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            //Namespace ns = root.getNamespace();
            Element nameElemnet=root.getChild("MetaData").getChild("Clip").getChild("ClipFiles").getChild("ClipFile");
            name=nameElemnet.getAttributeValue("filename");
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
    //针对标准XML
    private String getName5(File file) {
        String result = new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            if(root.getAttributeValue("VideoPath").equals("")){
                result = root.getChild("Program").getChild("Title").getChild("ProperTitle").getText();
            }
            else
                result=root.getAttributeValue("VideoPath");
        } catch (JDOMException | IOException e) {
            // TODO Auto-generated catch block
            System.out.println("file: " + file.getAbsolutePath());
            e.printStackTrace();
        }
        return result;
    }

    //注意，提取出来的是文件名，如关键帧路径为 /temp/keyframe/a.jpg  则返回的为"a.jpg;"  注意后面有个分号
    //抽取关键帧路径 适用于BTV 和 南方素材,不同的关键帧用';'分割
    public String getKeyFrameBTVorNanfang(File file,String frameDir){
        String name=new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            Namespace ns = root.getNamespace();
            List<Element> le=root.getChild("List",ns).getChild("MetaData",ns).getChildren("Attributes",ns);
            for (Element e:le) {
                List<Element> attrElementList=e.getChildren("AttributeItem",ns);
                for (Element ae : attrElementList){
                    if(ae.getChildText("ItemCode",ns).equals("STRKEYFRAMEFILE")){
                        String[] ss=ae.getChildText("Value",ns).split("/");
                        if(ss.length>0){
                            String desPath=findFilePath(ss[ss.length-1],frameDir);
                            if(desPath.length()>0) {
                                name += desPath;
                                name += ";";
                            }
                        }
                    }

                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
    //抽取关键帧路径 适用于海外素材,不同的关键帧用';'分割
    public String getKeyFrameHaiWai(File file,String frameDir){
        String name=new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            Namespace ns = root.getNamespace();
            List<Element> le=root.getChild("ImportContents",ns).getChild("MetaData",ns).getChildren("Attributes",ns);
            for (Element e:le) {
                List<Element> attrElementList=e.getChildren("AttributeItem",ns);
                for (Element ae : attrElementList){
                    if(ae.getChildText("ItemCode",ns).equals("关键帧文件")){
                        String[] ss=ae.getChildText("Value",ns).split("/");
                        if(ss.length>0){
                            String desPath=findFilePath(ss[ss.length-1],frameDir);
                            if(desPath.length()>0) {
                                name += desPath;
                                name += ";";
                            }
                        }
                    }

                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }
    //抽取关键帧路径 适用于电视剧,不同的关键帧用';'分割
    public String getKeyFrameDianshiju(File file,String frameDir){
        String name=new String();
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;
        try {
            doc = builder.build(file);
            Element root = doc.getRootElement();
            //Namespace ns = root.getNamespace();
            List<Element> keyframeEl = root.getChild("MetaData").getChild("KeyFrames").getChildren("KeyFrame");
            for (Element e:keyframeEl) {
                String desPath=findFilePath( e.getChildText("FileName"),frameDir);
                if(desPath.length()>0) {
                    name += desPath;
                    name += ";";
                }
            }
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    //函数用途：当知道关键帧的文件名时，找到他的绝对路径
    public String findFilePath(String name,String Dir){
        String rs=new String();
        File root = new File(Dir);
        File[] files = root.listFiles();
        for (File file : files) {
            if(!rs.equals("")) return rs;
            if (file.isDirectory()) {
                rs=findFilePath(name,file.getAbsolutePath());
            } else {
                String filename = file.getName().toLowerCase().replaceAll(" ","");
                if (filename.equals(name.toLowerCase().replaceAll(" ",""))) {
                    rs= file.getAbsolutePath();
                }
            }
        }
        return rs;
    }
}
