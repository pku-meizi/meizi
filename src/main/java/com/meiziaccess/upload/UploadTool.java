package com.meiziaccess.upload;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.meiziaccess.AssociationTool.Addresses;
import com.meiziaccess.AssociationTool.Associator;
import com.meiziaccess.CommandTool.CommandRunner;
import com.meiziaccess.CommandTool.SftpUtil;
import com.meiziaccess.model.UploadItem;
import com.meiziaccess.uploadModel.UploadLog;
import com.meiziaccess.uploadModel.UploadLogRepository;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by user-u1 on 2016/5/27.
 */
public class UploadTool implements UploadToolInterface {

        /* upload variable*/
    /*****************************************/
//    private  String upload_remote_path;
//
//    private  String upload_vendor_name;
//
//    private  String uploader_name;
//
//    private String vendor_path;

    /****************************************/


    //判断系统
    @Override
    public String getOSName() {
        Properties props = System.getProperties();
        String osName = props.getProperty("os.name").split(" ")[0];
        return osName;
    }


    //检查文件夹是否为空
    @Override
    public boolean checkFolder(String floderPath) {
        //??????
        String osName = getOSName();

        Vector<String> outs;
        if(osName.equals("Windows")){
            outs = CommandRunner.execCmds("cmd /c dir " + floderPath +" /ad /b " );
        }else {
            outs = CommandRunner.execCmds("/bin/ls  " + floderPath );
        }
        if(!outs.isEmpty()){ //?????????
            return true;
        }else{
            return false;
        }
    }

    /**
     * 读取上架文件
     */
    public  Map<String, String> readFile(String fileName)  {
        Map<String, String> map = new HashMap<String, String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("??????????????????????????????");
            reader = new BufferedReader(new FileReader(file));

            String tempString = null;
            int line = 1;
            // ?????????????????null????????
            while ((tempString = reader.readLine()) != null) {
                // ?????????
                System.out.println(tempString);
                String [] tmp = tempString.split(" ");
                if(tmp.length != 2) continue;
                map.put(tmp[0], tmp[1]);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return map;
    }

    //读取文件夹中的编目,素材文件列表和上架信息，修改数据库内容
    public boolean updateDatabase_total(String folderPath, String xmlName, String videoName, UploadLogRepository uploadLogRepository,
                                  String upload_remote_path,String upload_vendor_name, String uploader_name, String vendor_path,
                                  String trans_path, String play_path, List<String> frames) {

        Map<String, String> map = new HashMap<String, String>();
        //?????????:price copyright
        try {
             map = readFile(folderPath + "/upload.txt");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        if(uploadLogRepository == null){
            System.out.println("uploadLogRepository is null");
        }

        //????????????
        String higeCodeVideoName = "";
        String[] cmdsArray = new String[]{"/bin/ls", vendor_path};
        System.out.println("/bin/ls "+vendor_path);
        Vector<String> lists = CommandRunner.execCmdsArray(cmdsArray);
        System.out.println(lists.toString());
        String videoNameWithFormat = videoName.substring(0, videoName.lastIndexOf("."));
        for(String s : lists){
            String fileName = s.substring(0, s.lastIndexOf("."));
            System.out.println(fileName + "   " + videoNameWithFormat);
            if(fileName.equals(videoNameWithFormat)){
                System.out.println(s);
                higeCodeVideoName = s;
                break;
            }
        }

        //???????????
        UploadLog log = new UploadLog(upload_vendor_name,
                                    new Date(),
                                    uploader_name,
                                    upload_remote_path +"/"+ xmlName ,          //xml??????
                                    upload_remote_path +"/"+ videoName,         //vedio??????
                                    vendor_path + "/"+higeCodeVideoName,        //?????????
                                    Double.parseDouble(map.get("price")),   //???
                                    "RF",
                                    1,
                                    5,
                                    "",
                                    "",
                                    0,
                                    false,
                                    "",
                                    "",
                                    0
        );
        //xml??????
        log.setXml_trans_path(trans_path + "/" +"trans_"+new Date().getTime()+"_"+xmlName);

        //????????p4??????
        String videoTransName = videoName.split("\\.")[0] + ".mp4";
        log.setVideo_play_path(play_path+"/"+videoTransName);



        uploadLogRepository.save(log);
        return true;
    }

    public static String removeBlank(String s){
        String ans="";
        for(int i=0; i<s.length(); i++){
            if(s.charAt(i) != ' '){
                ans += s.charAt(i);
            }
        }
        return ans;
    }

    public String getFullName(String s){
        String ans="";
        for(int i=0; i<s.length(); i++){
            if(s.charAt(i) == ' '){
                ans += "\" \"";
//                int j=i+1;
//                while(s.charAt(j)==' ') j++;
//                i = j-1;
            }else{
                ans += s.charAt(i);
            }

        }
        return ans;
    }

    public String getFullNameWithZhunayi(String s){
        String ans="";
        for(int i=0; i<s.length(); i++){
            if(s.charAt(i) == ' ') {
                ans += '\\';
            }
            ans += s.charAt(i);
        }
        return ans;
    }

    public boolean uploadFile(String folderPath, UploadLogRepository uploadLogRepository, String upload_remote_path,
                              String upload_vendor_name, String uploader_name, String vendor_path, String trans_path,
                              String play_path) {

        if(uploadLogRepository == null){
            System.out.println("UploadLogRepository is null.");
            return false;
        }

        //??????
        String osName = getOSName();
        //?????????
        Vector<String> outs;
        if(osName.equals("Windows")){               //????????indows????????scp?????????path??
            //??????
//            outs = CommandRunner.execCmds("cmd /c dir " + folderPath +" /a-d /b " );
//            String xmlName="", videoName="";
//            for(int i=0; i<outs.size(); i++){
//                String[] file = outs.get(i).split("\\.");
//                //????????ml???
//                if(file[file.length-1].equals("xml")){
//                    xmlName = outs.get(i);
//
//                    //???xml???
//                    CommandRunner.execCmds("pscp -P 10722 -pw pkulky201 " + folderPath + "\\" + outs.get(i) + " derc@162.105.180.15:" + upload_remote_path);
//                }else{
//                    if(outs.get(i).equals("upload.txt")){
//                        continue;
//                    }else{
//                        videoName = outs.get(i);
//                        //??????
//                        CommandRunner.execCmds("pscp -P 10722 -pw pkulky201 " + folderPath + "\\" + outs.get(i) + " derc@162.105.180.15:" + upload_remote_path );
//                    }
//                }
//                //??????
//                System.out.println("cmd /c del " + folderPath + "\\" + outs.get(i));
//                CommandRunner.execCmds("cmd /c del " + folderPath + "\\" + outs.get(i));
//            }
//
//            //????????
//            updateDatabase(folderPath, xmlName, videoName, uploadLogRepository,  upload_remote_path,
//                    upload_vendor_name,  uploader_name,  vendor_path);
//
//            //???upload.txt???
//            CommandRunner.execCmds("cmd /c del " + folderPath + "\\" + "upload.txt");

        }else {
            outs = CommandRunner.execCmds("/bin/ls " + folderPath);
            String xmlName="", videoName="";
            for(int i=0; i<outs.size(); i++){
                String[] file = outs.get(i).split("\\.");
                System.out.println(outs.get(i) + " " +file.length);

                if(outs.get(i).contains(" ")){
                    String[] cmdsArray;
                    if(!file[file.length-1].equals("txt") && !file[file.length-1].equals("xml")){
                        cmdsArray = new String[]{"/bin/mv", vendor_path + "/" + outs.get(i),  vendor_path + "/" + removeBlank(outs.get(i))};
                        System.out.println("/bin/mv " + vendor_path + "/" + outs.get(i) + " " + vendor_path + "/" + removeBlank(outs.get(i)));
                        CommandRunner.execCmdsArray(cmdsArray);
                    }
                    cmdsArray = new String[]{"/bin/mv", folderPath+"/"+outs.get(i), folderPath+"/"+removeBlank(outs.get(i))};
                    System.out.println("/bin/mv "+folderPath+"/"+outs.get(i)+" "+folderPath+"/"+removeBlank(outs.get(i)));
                    CommandRunner.execCmdsArray(cmdsArray);
                    outs.set(i, removeBlank(outs.get(i)));
                }

                //????????ml???
                if(file[file.length-1].equals("xml")){
                    xmlName = outs.get(i);
                    //???xml???
//                    CommandRunner.execCmds("scp -P 10722 " + folderPath + "/" + outs.get(i) + " derc@162.105.180.15:" + upload_remote_path );
                }else{
                    if(outs.get(i).equals("upload.txt")){
                        continue;
                    }else{
                        //??????
//                        CommandRunner.execCmds("scp -P 10722 " + folderPath + "/" + outs.get(i) + " derc@162.105.180.15:" + upload_remote_path );
                        videoName = outs.get(i);
                    }
                }
                try {
                    System.out.println(folderPath + "/" + outs.get(i) + " " + upload_remote_path);
                    CommandRunner.scpPut(folderPath + "/" + outs.get(i), upload_remote_path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //??????
                System.out.println("rm " + folderPath + "/" + outs.get(i));
//                CommandRunner.execCmds("rm " + folderPath + "/" + outs.get(i));
            }

            List<String> keyFrames = new ArrayList<>();

            //????????
            updateDatabase_total(folderPath, xmlName , videoName, uploadLogRepository,  upload_remote_path,
                    upload_vendor_name,  uploader_name,  vendor_path, trans_path, play_path,  keyFrames);
            //???upload.txt???
//            CommandRunner.execCmds("rm " + folderPath + "/" + "upload.txt");
        }
        return true;
    }

    public boolean uploadFiles(String folderPath, UploadLogRepository uploadLogRepository, String upload_remote_path,
                               String upload_vendor_name, String uploader_name, String vendor_path, String trans_path,
                               String play_path){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String day = dateFormat.format(date);
        String remote_full_path = upload_remote_path + "/" + day;

        //??????
        String osName = getOSName();
        //?????????
        Vector<String> outs;
        if(osName.equals("Windows")){               //????????indows????????scp?????????path??
            //???????????????????????????????????????????????????????
//            System.out.println("/bin/mkdir " + remote_full_path );
//            try {
//                CommandRunner.runSSH( "/bin/mkdir " + remote_full_path );
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
////            CommandRunner.execCmds("pscp -P 10722 -pw pkulky201 -r " + folderPath  + " derc@162.105.180.15:" + remote_full_path);
//            //????????????????????
//            outs = CommandRunner.execCmds("cmd /c dir " + folderPath +" /ad /b " );
//            for(int i=0; i<outs.size(); i++){
//                //????????
//                CommandRunner.execCmds("pscp -P 10722 -pw pkulky201 -r " + folderPath + "\\" + outs.get(i) + " derc@162.105.180.15:" + remote_full_path);
//                //????????
//                System.out.println("cmd /c rd /s/q  " + folderPath + "\\" + outs.get(i));
//                CommandRunner.execCmds("cmd /c rd /s/q " + folderPath + "\\" + outs.get(i));
//            }
//            //???upload.txt???
//            CommandRunner.execCmds("cmd /c del " + folderPath + "\\" + "upload.txt");
        }else {
            //???????????
            System.out.println("/bin/mkdir " + remote_full_path );
            try {
                CommandRunner.runSSH( "/bin/mkdir " + remote_full_path );
            } catch (IOException e) {
                e.printStackTrace();
            }
            //???????????
            outs = CommandRunner.execCmds("/bin/ls -F " + folderPath + " | grep '/$' ");
            System.out.println("/bin/ls -F " + folderPath + " | grep '/$' ");
            for(int i=0; i<outs.size(); i++){
                if(outs.get(i).charAt(outs.get(i).length()-1)==':') continue;
                System.out.println(""+i+": "+outs.get(i));

                //???????????
                String folderName = outs.get(i).substring(0, outs.get(i).length()-1);
                if(folderName.contains(" ")){

                    System.out.println("/bin/mv "+folderPath+"/"+folderName+" "+folderPath+"/"+removeBlank(folderName));
                    String[] cmdsArray = new String [] {"/bin/mv", folderPath+"/"+folderName, folderPath+"/"+removeBlank(folderName)};
                    Vector<String> vecstrs = CommandRunner.execCmdsArray(cmdsArray);
                    System.out.println(vecstrs.toString());
                    folderName = removeBlank(folderName);
                }
                System.out.println("/bin/mkdir " + remote_full_path + "/" + folderName );
                try {
                    CommandRunner.runSSH("/bin/mkdir " + remote_full_path + "/" + folderName);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //???????????
                System.out.println("localPath="+folderPath+"/"+folderName);
                System.out.println("remotePath="+remote_full_path + "/" + folderName);
                uploadFile(folderPath+"/"+folderName, uploadLogRepository, remote_full_path + "/" + folderName,
                        upload_vendor_name, uploader_name,  vendor_path, trans_path, play_path);

                //???????????
                System.out.println("rm -rf " + folderPath + "/" + folderName);
                CommandRunner.execCmds("rm -rf " + folderPath + "/" + folderName);
            }
        }
        return true;
    }

    public  static List<UploadItem> getUploadItems(String path){
        List<UploadItem> list = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isDirectory()) {
                list.add(new UploadItem(tempList[i].getName(), getMD5(tempList[i].getName()), tempList[i].getPath()));
            }
        }
        return list;
    }

    public static boolean isNullOrBlank(String s){
        if (s == null || s.equals("")){
            return true;
        }else{
            return false;
        }
    }

    public  static List<UploadItem> getUploadItemsAssociation(String path, int type){
        List<UploadItem> list = new ArrayList<>();
        Associator associator=new Associator();
//        System.out.println(path);
        List<Addresses> rs=associator.getAddresses(path + "/xml",path+"/video",path+File.separator+"highCodeVideo",
                path+"/keyFrame",path+"/xml", type);
//        List<Addresses> rs=associator.getAddresses(path + "\\xml",path+"\\视频",path+"\\视频",
//                path+"\\视频截图",path+"\\xml");
        for(Addresses addresses:rs){
            String xmlPath = addresses.getUnCatalgedXmlPath();
            String highCodeVideoPath = addresses.getHighCodeVideoPath();
            String lowCdeVideoPath = addresses.getLowCodeVideoPath();
            String keyFramePath = addresses.getKeyFramePath();
            String name = addresses.getName();

            List<String> pathList = new ArrayList<>();
            pathList.add(xmlPath);
            pathList.add(lowCdeVideoPath);
            pathList.add(highCodeVideoPath);
            pathList.add(keyFramePath);
            String pathCombination = StringUtils.join(pathList, ',');

//            System.out.println(pathCombination);
            boolean flag = false;
            if(isNullOrBlank(lowCdeVideoPath) || isNullOrBlank(highCodeVideoPath)){
                flag = true;
            }
//            for(String s: pathList){
//                if(isNullOrBlank(s)){
//                    flag = true;
//                    System.out.println(name);
//                    break;
//                }
//            }
            if(flag){
                continue;
            }


            list.add(new UploadItem(name, getMD5(name), pathCombination));
        }
        return list;
    }

    public static String getMD5(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());

            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            System.out.println("MD5");
        }
        return "";
    }


//    public static List<UploadItem> getTestData(){
//        List<UploadItem> list = new ArrayList<>();
//        UploadItem item0 = new UploadItem(false, "??? ??1??", new Date(), 600, 0, 100, "RM", 0, UploadTool.getMD5("??? ??1??"), "");
//        UploadItem item1 = new UploadItem(false, "??? ??2??",  new Date(), 600, 1, 10, "RM", 0, UploadTool.getMD5("??? ??2??"), "");
//        UploadItem item2 = new UploadItem(false, "??? ??3??",  new Date(), 600, 0, 100, "RM", 0, UploadTool.getMD5("??? ??3??"), "");
//        UploadItem item3 = new UploadItem(false, "??? ??4??",  new Date(), 600, 0, 100, "RM", 0, UploadTool.getMD5("??? ??4??"), "");
//        UploadItem item4 = new UploadItem(false, "??? ??5??",  new Date(), 600, 0, 100, "RM", 0, UploadTool.getMD5("??? ??5??"), "");
//        list.add(item0);
//        list.add(item1);
//        list.add(item2);
//        list.add(item3);
//        list.add(item4);
//        return list;
//    }

    public  boolean uploadItems( String fileDir,  String remotePath,  ChannelSftp sftp,
                                UploadLogRepository uploadLogRepository ,String upload_vendor_name,
                                String vendorPath, String uploader_name,  String trans_path,
                                String play_path, UploadItem item){

        //???????????
        File file = new File(fileDir);
        File[] tempList = file.listFiles();

        //??????????
        List<String> xmlName = new ArrayList<>();            //xml,???,??????
        String videoName = "";          //???
        List<String> keyFrames = new ArrayList<>();          //?????,???,??????

        String remoteKeyFramesPath = remotePath + "/keyFrames";

        int status = 0;

        int play_status = 0;

        boolean on_shelf = false;

        String md5 = item.getMd5();

        String title = item.getTitle();

        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                try {

                    //??????????
                    //xml
                    if(tempList[i].getName().endsWith(".xml")){
                        xmlName.add(tempList[i].getName());
                    }else if(tempList[i].getName().endsWith(".jpg")){
                        keyFrames.add(tempList[i].getName());
                    }else{
                        videoName = tempList[i].getName();
                        vendorPath = tempList[i].getPath();     //??????
                    }

                    //?????
                    SftpUtil.uploadFile(tempList[i].getPath(), remoteKeyFramesPath, tempList[i].getName(), sftp);

                } catch (SftpException e) {
                    e.printStackTrace();
                }
            }
        }

        //????????
        updateDatabase( xmlName, videoName, uploadLogRepository,  remotePath,
                upload_vendor_name,  uploader_name,  vendorPath, trans_path, play_path, item,  remoteKeyFramesPath, status, on_shelf, md5, title, play_status);
        return true;
    }

    /**
     * 将以分号隔开的字符串拆成列表
     * @param names
     * @return
     */
    private List<String> getNameList(String names){
        List<String> nameList = new ArrayList<>();
        if(names == null || names.equals("")){
            return nameList;
        }
        String[] paths = names.split(";");
        for(int i=0; i<paths.length; i++){
            nameList.add(paths[i]);
        }
        return nameList;
    }

    String getExtension(String name){
        String[] names = name.split(".");
        return names[names.length-1];
    }


    //删除实体文件
    public boolean deleteItemDirsAssociation(UploadItem item){

        String[] paths = item.getPath().split(",");

        String xmlPath = paths[0];
        String lowCodeVideoPath = paths[1];
        String highCodeVideoPath = paths[2];

        String keyFramePath = "";
        if (paths.length == 4){
            keyFramePath = paths[3];
        }

        System.out.println("keyFramePath = " + keyFramePath);

        //xml
        List<String> xmlName = getNameList(xmlPath);
        for(int i=0; i<xmlName.size(); i++){
            System.out.println("xml = " + xmlName.get(i));
                File xmlf = new File(xmlName.get(i));
                xmlf.delete();
            System.out.println("xml =删除成功 ");
        }

        //lowCodeVideo
        System.out.println("lowCodeVideoPath = " +lowCodeVideoPath);
        File lowVideof = new File(lowCodeVideoPath);
        lowVideof.delete();
        System.out.println("lowCodeVideoPath = 删除成功" );
        //keyFrames
        List<String> keyFrames = getNameList(keyFramePath);

        for(int i=0; i<keyFrames.size(); i++){

            File file = new File(keyFrames.get(i));
            file.delete();
            System.out.println("keyFramePath =删除成功 ");
        }


        return true;
    }

    public  boolean uploadItemsAssociation( String fileDir,  String remotePath,  ChannelSftp sftp,
                                 UploadLogRepository uploadLogRepository ,String upload_vendor_name,
                                 String vendorPath, String uploader_name,  String trans_path,
                                 String play_path, UploadItem item){

        System.out.println("path = " + item.getPath());
        String[] paths = item.getPath().split(",");

        String xmlPath = paths[0];
        String lowCodeVideoPath = paths[1];
        String highCodeVideoPath = paths[2];

        int status = 0;

        int play_status = 0;

        boolean on_shelf = false;

        String md5 = item.getMd5();

        String title = item.getTitle();

        String keyFramePath = "";
        if (paths.length == 4){
            keyFramePath = paths[3];
        }

        System.out.println("keyFramePath = " + keyFramePath);

        //xml   替换xml文件名中的空格
        List<String> xmlName = getNameList(xmlPath);
        for(int i=0; i<xmlName.size(); i++){
            try {
                File xmlFile = new File(xmlName.get(i));
                String xml =  replaceBlankToLine(xmlFile.getName());
                SftpUtil.uploadFile(xmlName.get(i), remotePath, xml, sftp);
                xmlName.set(i, xml);
                System.out.println("上传 " + xmlName.get(i));
            } catch (SftpException e) {
                e.printStackTrace();
            }
        }

        //lowCodeVideo
        File videoFile = new File(lowCodeVideoPath);
        String videoName = replaceBlankToLine(videoFile.getName());
        try {

            SftpUtil.uploadFile(lowCodeVideoPath, remotePath, videoName, sftp);
            System.out.println("上传 "+videoName);
        } catch (SftpException e) {
            e.printStackTrace();
        }

        //highCodeVideo =

        //keyFrames
        List<String> keyFrames = getNameList(keyFramePath);
        String remoteKeyFramesPath = remotePath+"/keyFrames";
        for(int i=0; i<keyFrames.size(); i++){
            try {
                File frameFile = new File(keyFrames.get(i));
                String frame = replaceBlankToLine(frameFile.getName());
                SftpUtil.uploadFile(keyFrames.get(i), remoteKeyFramesPath, frame, sftp);
                keyFrames.set(i, frame);
                System.out.println("上传 "+keyFrames.get(i));
            } catch (SftpException e) {
                e.printStackTrace();
            }
        }

        //只支持单个xml，关键帧，低码文件对应
        updateDatabase( xmlName, videoName, uploadLogRepository,  remotePath,
                upload_vendor_name,  uploader_name,  highCodeVideoPath, trans_path, play_path, item,  remoteKeyFramesPath, status, on_shelf, md5, title, play_status);
        return true;
    }

    /**
     *
     * @param xmlName               xml名字列表
     * @param videoName             低码video名字
     * @param uploadLogRepository   写媒资数据库的类
     * @param upload_remote_path    远程上传的文件夹路径
     * @param upload_vendor_name    上传电视台名
     * @param uploader_name         上传人名
     * @param vendor_path           高码video路径
     * @param trans_path            媒资平台转码路径
     * @param play_path             媒资平台播放路径
     * @param item                  视频类
     * @param remoteKeyFramesPath                关键帧文件夹路径
     * @param status                审核状态
     * @param on_shelf              上架状态
     * @param md5
     * @param title
     * @param play_status
     * @return
     */
    public boolean updateDatabase(List<String> xmlName, String videoName, UploadLogRepository uploadLogRepository,
                                  String upload_remote_path,String upload_vendor_name, String uploader_name, String vendor_path,
                                  String trans_path, String play_path, UploadItem item, String remoteKeyFramesPath, int status,
                                  boolean on_shelf, String md5, String title, int play_status) {

        String xmlOriginName = xmlName.get(0);
        //xml
        for(int i=0; i<xmlName.size(); i++){
            xmlName.set(i, upload_remote_path + "/" + xmlName.get(i));
        }
        String xmlPath = StringUtils.join(xmlName, ',');

        //frames 关键帧文件夹路径
//        for(int i=0; i<frames.size(); i++){
//            frames.set(i, upload_remote_path + "/" + frames.get(i));
//        }
//        String framesPath = StringUtils.join(frames, ',');
        String framesPath = remoteKeyFramesPath;

        //版权时间转换成天
        int duration = 0;
        switch (item.getCopyright_duration()){
            case 0:
                duration = 182;
                break;
            case 1:
                duration = 365;
                break;
            case 2:
                duration = 365*2;
                break;
            case 3:
                duration = 365*3;
                break;
            case 4:
                duration = 365*5;
                break;
            case 5:
                duration = -1;
                break;
            default:
                duration = 0;
        }

        String material_type ="";
        switch (item.getMaterial_type()){
            case "成片":
                material_type = "full";
                break;
            case "素材":
                material_type = "material";
                break;
            default:
                material_type = "full";
        }
        UploadLog log = new UploadLog(
                upload_vendor_name,
                new Date(),
                uploader_name,
                xmlPath ,          //xml
                upload_remote_path +"/"+ videoName,         //video
                vendor_path,        //电视台路径
                item.getPrice(),   //价格
                item.getCopyright_type(),   //版权类型
                item.getPrice_type(),   //价格类型
                duration,    //版权时间
                framesPath,
                material_type,
                status,
                on_shelf,
                md5,
                title,
                play_status
        );
        log.setXml_trans_path(trans_path + "/" +"trans_"+new Date().getTime()+"_"+xmlOriginName);

        String videoTransName = videoName.split("\\.")[0] + ".mp4";
        log.setVideo_play_path(play_path+"/"+videoTransName);
        uploadLogRepository.save(log);
        return true;
    }

    public boolean uploadItemDirs(String upload_remote_path, List<UploadItem> list,
                                   UploadLogRepository uploadLogRepository ,String upload_vendor_name,
                                   String vendorPath, String uploader_name,  String trans_path,
                                   String play_path){

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String day = dateFormat.format(date);
        String remote_full_path = upload_remote_path + "/" + day;

        try {

            ChannelSftp sftp = SftpUtil.getSftpConnect("192.168.1.8",22, "luyj", "pkulky201");
            SftpUtil.mkdir(remote_full_path, sftp);

            for(int i=0; i<list.size(); i++){

                UploadItem item = list.get(i);
                String remoteFileDir = remote_full_path + "/" + item.getTitle();
                SftpUtil.mkdir(remoteFileDir, sftp);

                uploadItems(item.getPath(), remoteFileDir, sftp, uploadLogRepository , upload_vendor_name,
                        vendorPath, uploader_name,  trans_path, play_path, item);

            }
//            SftpUtil.exit(sftp);

        }  catch (SftpException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * 将名字中的空格替换成下划线
     * @param name
     * @return
     */
    public String replaceBlankToLine(String name){
        return name.replace(" ", "_");
    }

    public boolean uploadItemDirsAssociation(String upload_remote_path, List<UploadItem> list,
                                  UploadLogRepository uploadLogRepository ,String upload_vendor_name,
                                  String vendorPath, String uploader_name,  String trans_path,
                                  String play_path){

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String day = dateFormat.format(date);
        String remote_full_path = upload_remote_path + "/" + day;

        try {

            ChannelSftp sftp = SftpUtil.getSftpConnect("192.168.1.8",22, "luyj", "pkulky201");
            SftpUtil.mkdir(remote_full_path, sftp);

            for(int i=0; i<list.size(); i++){

                UploadItem item = list.get(i);
                String remoteFileDir = remote_full_path + "/" + replaceBlankToLine(item.getMd5());
//                String remoteFileDir = remote_full_path + "/" + replaceBlankToLine(item.getTitle());
                SftpUtil.mkdir(remoteFileDir, sftp);

                uploadItemsAssociation(item.getPath(), remoteFileDir, sftp, uploadLogRepository , upload_vendor_name,
                        vendorPath, uploader_name,  trans_path, play_path, item);

            }

            SftpUtil.exit(sftp);

        }  catch (SftpException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        } finally {

        }

        return true;
    }



//    public static void main(String[] args) {
//        String path = ",,,";
//        String[] paths = path.split(",");
//        for(int i=0; i<paths.length; i++){
//            System.out.println(paths[i]);
//        }
//    }
}
