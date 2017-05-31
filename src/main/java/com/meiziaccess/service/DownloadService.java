package com.meiziaccess.service;

import com.meiziaccess.download.DownloadTool;
import com.meiziaccess.download.DownloadToolInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by user-u1 on 2016/6/17.
 */
@Service
public class DownloadService {

    DownloadToolInterface downloadTool = new DownloadTool();

    //    下载处理好的编目和订单文件
    public boolean downloadXmlAndOrder(String localDir, String remoteDir, String port, String host, String username){
        downloadTool.download(localDir, remoteDir, port, host, username);
        return true;
    }
}
