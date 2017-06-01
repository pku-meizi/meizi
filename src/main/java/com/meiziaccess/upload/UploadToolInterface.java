package com.meiziaccess.upload;

import com.jcraft.jsch.ChannelSftp;
import com.meiziaccess.model.UploadItem;
import com.meiziaccess.uploadModel.UploadLogRepository;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by user-u1 on 2016/5/27.
 */
//上传编目和素材文件
public interface UploadToolInterface  {

    //判断系统
    public String getOSName();

    //检查文件夹是否为空
    public boolean checkFolder(String floderPath);

    //读取文件夹中的编目,素材文件列表和上架信息，修改数据库内容
    public boolean updateDatabase_total(String folderPath, String xmlName, String videoName, UploadLogRepository uploadLogRepository,
                                        String upload_remote_path, String upload_vendor_name, String uploader_name, String vendor_path,
                                        String trans_path, String play_path, List<String> frames);


//    //执行shell脚本，上传文件，删除文件
    public boolean uploadFile(String folderPath, UploadLogRepository uploadLogRepository, String upload_remote_path,
                              String upload_vendor_name, String uploader_name, String vendor_path, String trans_path, String play_path);
//
//    //执行shell脚本，上传文件夹，删除文件夹
    public boolean uploadFiles(String folderPath, UploadLogRepository uploadLogRepository, String upload_remote_path,
                               String upload_vendor_name, String uploader_name, String vendor_path, String trans_path, String play_path);

    //读取上架文件
    public  Map<String, String> readFile(String fileName);

    //上传所有视频文件夹列表
    public boolean uploadItemDirs(String upload_remote_path, List<UploadItem> list,
                                  UploadLogRepository uploadLogRepository, String upload_vendor_name,
                                  String vendorPath, String uploader_name, String trans_path,
                                  String play_path);

    //更新远程数据库
    public boolean updateDatabase(List<String> xmlName, String videoName, UploadLogRepository uploadLogRepository,
                                  String upload_remote_path, String upload_vendor_name, String uploader_name, String vendor_path,
                                  String trans_path, String play_path, UploadItem item, String remoteKeyFramesPath, int status,
                                  boolean on_shelf, String md5, String title);

    //上传每一个视频文件夹
    public  boolean uploadItems(final String fileDir, final String remotePath, final ChannelSftp sftp,
                                UploadLogRepository uploadLogRepository, String upload_vendor_name,
                                String vendorPath, String uploader_name, String trans_path,
                                String play_path, UploadItem item);

    //针对xml，低码视频，关键帧在不同文件的情况
    public boolean uploadItemDirsAssociation(String upload_remote_path, List<UploadItem> list,
                                             UploadLogRepository uploadLogRepository, String upload_vendor_name,
                                             String vendorPath, String uploader_name, String trans_path,
                                             String play_path);
    //删除文件
    public boolean deleteItemDirsAssociation(UploadItem item);
}
