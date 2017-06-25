package com.meiziaccess.controller;


import com.meiziaccess.model.ItemMedia;
import com.meiziaccess.model.ItemMediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user-u1 on 2016/4/12.
 */

@Controller
public class OrderController {

    @Value("${configure.IPAdderss}")
    private String IPAddress;           //当前服务器ip

    @Autowired
    private ItemMediaRepository itemMediaRepository;

    //订单PostAPI
    @RequestMapping(value = "/order", method = RequestMethod.POST, produces = "application/json;charset-UTF-8")
    @ResponseBody
    public Map<String, Object> order(@RequestBody ItemMedia ord){
        Map<String, Object> order_return = new HashMap<String, Object>();
        System.out.println(ord.getUuid() + ", " + ord.getEntire()+ ", " + ord.getStarttime() + ", " + ord.getEndtime() + ", " + ord.getHighdef_video_path());
        String url = "http://" + IPAddress + "/media?uuid=" + ord.getUuid();

        //处理视频，修改链接和地址
        ord.setStatus(1);
        ord.setUrl(url);
        List<ItemMedia> list =  itemMediaRepository.findMediaByUuid(ord.getUuid());
        ItemMedia itemMedia;
        if(list.isEmpty()){
            itemMedia = itemMediaRepository.save(ord);
        }else{
            itemMedia = list.get(0);
        }

        //返回字段
        order_return.put("uuid", itemMedia.getUuid());
        order_return.put("status", itemMedia.getStatus());
        order_return.put("url", itemMedia.getUrl());

        return order_return;
    }

    //视频下载链接
    @RequestMapping(value = "/media", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadFile( Long uuid)
            throws IOException {
        //生成相应的文件下载链接
//        String filePath = "E:/" + 1 + ".rmvb";
//        通过uuid查找高码视频路径
        System.out.println("uuid = " + uuid);
        List<ItemMedia> list = itemMediaRepository.findMediaByUuid(uuid);
        if(list.isEmpty()) return null;
        ItemMedia itemMedia = list.get(0);
        System.out.println(itemMedia.getHighdef_video_path()); //        String filePath = "/home/derc/video/" + id + ".rmvb";
        String filePath = itemMedia.getHighdef_video_path();
        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getFilename()));
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(file.contentLength())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }

}