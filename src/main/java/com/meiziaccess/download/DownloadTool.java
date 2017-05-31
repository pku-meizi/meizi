package com.meiziaccess.download;

import com.meiziaccess.CommandTool.CommandRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Vector;


/**
 * Created by user-u1 on 2016/6/17.
 */

@Component
public class DownloadTool implements DownloadToolInterface {

    //Linux和Mac指令版本
    public void download(String localDir, String remoteDir, String port, String host, String username){

        try {
            //获取本地文件列表
            Vector<String> localList = CommandRunner.execCmds("/bin/ls " + localDir);

            //获取远程文件列表
            Vector<String> remoteList = CommandRunner.runSSHAndGetString("/bin/ls " + remoteDir);

            remoteList.removeAll(localList);

            System.out.println(port + "  " + host + "  " + username);
            for(int i=0; i<remoteList.size(); i++){
                System.out.println(remoteList.get(i));
                CommandRunner.execCmds("scp -P "+port+" -r  "+username+"@"+host+":"+remoteDir+"/"+remoteList.get(i) +" "+localDir);
//                CommandRunner.scpGet(remoteDir+"/"+remoteList.get(i), localDir);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
