package com.meiziaccess.controller;


import com.meiziaccess.model.ItemMedia;
import com.meiziaccess.model.ItemMediaRepository;
import com.meiziaccess.model.ItemOrder;
import com.meiziaccess.model.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by user-u1 on 2016/4/12.
 */

@Controller
public class OrderController {

    @Value("${configure.IPAdderss}")
    private String IPAddress;           //当前服务器ip

    @Value("${configure.download.video_path}")
    private String download_path;       //订单项高码视频转码后的路径

    @Autowired
    private ItemMediaRepository itemMediaRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${configure.order.path}")
    private String order_path;      //   order/orderid

     //订单项API
     @RequestMapping(value = "/order", method = RequestMethod.POST, produces = "application/json;charset-UTF-8")
     @ResponseBody
     public Map<String, Object> orderItem(@RequestBody ItemMedia item){
         Map<String, Object> order_return = new HashMap<String, Object>();

         //查找order表中的是否存在orderid
         List<ItemOrder> o_list = orderRepository.findOrderByOrderid(item.getOrderid());
         ItemMedia itemMedia ;
         ItemOrder order = new ItemOrder();
         if(o_list.isEmpty()){
             //生成order表中的URL
             String o_url = "http://" + IPAddress + "/get_order?orderid=" + item.getOrderid();
             //添加order表中的记录
             order.setOrderid(item.getOrderid());
             order.setUrl(o_url);
             order.setStatus(0);

             String path = order_path+"/order" + order.getOrderid();      //   order\\orderid
             File file = new File(path);
             file.mkdir();
             order.setTrans_path(path);

             orderRepository.save(order);

             //生成item表中的URL
             String i_url = "http://" + IPAddress + "/get_item?orderid=" + item.getOrderid()+"&&uuid="+item.getUuid();
             item.setUrl(i_url);
             //设置成0，表示还未完成转码
             item.setStatus(0);
             item.setOrder_video_path(order.getTrans_path()+"\\"+item.getUuid()+"."+item.getFormat());// order\\orderid\\uuid.mp4
             itemMedia = itemMediaRepository.save(item);

             order_return.put("uuid", itemMedia.getUuid());
             order_return.put("status", itemMedia.getStatus());
             order_return.put("url", itemMedia.getUrl());

         }else{
             List<ItemMedia> i_o_list = itemMediaRepository.findMediaByOrderid(item.getOrderid());
             int mag = 1;
             for(int j=0; j<i_o_list.size(); j++){

                if(i_o_list.get(j).getUuid()==item.getUuid()){
                    mag = 0;
                    order_return.put("uuid", i_o_list.get(j).getUuid());
                    order_return.put("status", i_o_list.get(j).getStatus());
                    order_return.put("url", i_o_list.get(j).getUrl());
                    break;
                }
             }
             if(mag == 1){
                 String i_url = "http://" + IPAddress + "/get_item?orderid=" + item.getOrderid()+"&&uuid="+item.getUuid();
                 item.setUrl(i_url);
                 //设置成0，表示还未完成转码
                 item.setStatus(0);
                 String path = order_path + item.getOrderid();
                 item.setOrder_video_path(path+"\\"+item.getUuid()+"."+item.getFormat());// order\\orderid\\uuid.mp4
                 itemMedia = itemMediaRepository.save(item);
                 order_return.put("uuid", itemMedia.getUuid());
                 order_return.put("status", itemMedia.getStatus());
                 order_return.put("url", itemMedia.getUrl());
             }

         }

        return order_return;

     }

    //视频订单下载链接
    @RequestMapping(value = "/post_order", method = RequestMethod.POST, produces = "application/json;charset-UTF-8")
    @ResponseBody
    public Map<String, Object> downloadAll( @RequestBody ItemOrder post_order){
        Map<String, Object> order_return = new HashMap<String, Object>();
        //查看order表中是否有orderid
        System.out.println(post_order.getOrderid());
        List<ItemOrder> o_list = orderRepository.findOrderByOrderid(post_order.getOrderid());
        //如果没有返回没有该订单
        if(o_list.isEmpty()){
            order_return.put("status", 3);
            return order_return;
        }else {
            //如果有订单
            ItemOrder order = o_list.get(0);
            //查看order表中的status状态
            int o_status = order.getStatus();
            //查看订单中有对少订单项
            List<ItemMedia> i_list = itemMediaRepository.findMediaByOrderid(post_order.getOrderid());
            //存放订单项的文件夹
            File file = new File(order.getTrans_path());
            File[] files = file.listFiles();

            System.out.println("@"+o_status+"@");
            switch (o_status) {
                //0:订单未完成  1：订单完成(转码完成)  2：压缩完成  3：无订单

                case 0:
                    System.out.println("执行###########");

                    if(!file.exists()){
                        System.out.print("没有订单文件夹");
                    }else{

                        int fileSize = files.length;
                        if(fileSize == i_list.size()){
                            order.setStatus(1);
                            orderRepository.setStatus(order.getStatus(), order.getOrderid());
                        }else {
                            System.out.print("状态不动");
                        }
                    }
//                    int flag = 1;
//                    for (int j = 0; j < i_list.size(); j++) {
//                        //判断item中的status的状态是否全部为1
//                        int i_status = i_list.get(j).getStatus();
//                        if (i_status == 0) {
//                            flag = 0;
//                            break;
//                        }
//                    }
//                    if (flag == 1) {
//                       order.setStatus(flag);
//                        System.out.println("执行###########"+order.getStatus());
//                        //跟新status
//                        orderRepository.setStatus(order.getStatus(), order.getOrderid());
//                    }
                    order_return.put("orderid", order.getOrderid());
                    order_return.put("status", order.getStatus());
                    order_return.put("url", order.getUrl());
                    break;
                case 1:
                    System.out.print("*****做压缩操作******");

                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    FileOutputStream fos = null;
                    ZipOutputStream zos = null;
                    File zipFile = new File(order.getTrans_path() + ".zip");
                    if(zipFile.exists()){
                        System.out.print(order.getTrans_path() + ".zip"+"已存在");
                    }else {
                        try {

                            fos = new FileOutputStream(zipFile);
                            zos = new ZipOutputStream(new BufferedOutputStream(fos));
                            byte[] bufs = new byte[1024*10];
                            for(int i=0; i<files.length;i++){
                                ZipEntry zipEntry = new ZipEntry(files[i].getName());
                                zos.putNextEntry(zipEntry);

                                fis = new FileInputStream(files[i]);
                                bis = new BufferedInputStream(fis, 1024*10);
                                int read = 0;
                                while((read=bis.read(bufs, 0, 1024*10)) != -1){
                                    zos.write(bufs, 0, read);
                                }
                            }
                            order.setStatus(2);
                            orderRepository.setStatus(order.getStatus(), order.getOrderid());

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }catch(IOException e){
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }finally{
                            try {
                                if(null != bis) bis.close();
                                if(null != zos) zos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }


                    }

                    order_return.put("orderid", order.getOrderid());
                    order_return.put("status", order.getStatus());
                    order_return.put("url", order.getUrl());
                    break;
                case 2:
                    String path = order.getTrans_path() + ".zip";
                    order.setPack_path(path);
                    orderRepository.save(order);

                    order_return.put("orderid", order.getOrderid());
                    order_return.put("status", order.getStatus());
                    order_return.put("url", order.getUrl());

                    break;

                default:
                    System.out.print("没有多余情况");
            }
        }
        return order_return;

    }


    //视频订单项下载链接
    @RequestMapping(value = "/get_item", method = RequestMethod.GET)
    public ResponseEntity downloadFile( Long orderid, Long uuid)
            throws IOException {
        //生成相应的文件下载链接

//        通过uuid查找高码视频路径
        List<ItemMedia> i_list = itemMediaRepository.findByOrderidAndUuid(orderid, uuid);
        System.out.println("*****取出来了*****");
        //如果没有uuid，则提示没有该订单项
        if(i_list.isEmpty()){
            System.out.println("*****没有值*****");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        ItemMedia itemMedia = i_list.get(0);
        String filePath = "";
        List<ItemOrder> o_list = orderRepository.findOrderByOrderid(orderid);
        ItemOrder itemOrder = o_list.get(0);
        //查看转码状态，如果完成就返回，否则提示转码未完成
        if(itemOrder.getStatus()==2){
            filePath = itemMedia.getOrder_video_path();
            System.out.println(filePath);

        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-sto     re, must-revalidate");
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

    //视频订单全部下载
    @RequestMapping(value = "/get_order", method = RequestMethod.GET)
    public ResponseEntity downloadFileAll( Long orderid) throws IOException{

        //        通过uuid查找高码视频路径
        List<ItemOrder> o_list = orderRepository.findOrderByOrderid(orderid);

        //如果没有uuid，则提示没有该订单项
        if(o_list.isEmpty()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        ItemOrder order = o_list.get(0);
        String filePath = "";

        //查看转码状态，如果完成就返回，否则提示转码未完成
        if(order.getStatus()==2){
            filePath = order.getPack_path();
        }else{
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

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
/*
*trans_path  要压缩文件夹的路径
*
* pack_path   压缩完成后压缩文件的路径
*
* order_video_path  订单高码文件存放路径
*
* orderName  压缩后文件的名称
*
*将放在trans_path路径下的文件，打包成orderID名称的zip文件，并存放在order_path中
*
* */

//    public static boolean fileToZip(String trans_path, String order_path, String orderName){
//        boolean flag = false;
//        FileInputStream fis = null;
//        BufferedInputStream bis = null;
//        FileOutputStream fos = null;
//        ZipOutputStream zos = null;
//
//        if (trans_path.isEmpty()){
//            System.out.println("高码订单文件不存在");
//        }else{
//        }
//
//
//        return flag;
//    }
   }
