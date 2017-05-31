package com.meiziaccess.controller;


import com.meiziaccess.model.ItemMedia;
import com.meiziaccess.model.ItemMediaRepository;
import com.meiziaccess.model.ItemOrder;
import com.meiziaccess.model.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
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

    @Value("${configure.download.video_path}")
    private String download_path;       //订单项高码视频转码后的路径

    @Autowired
    private ItemMediaRepository itemMediaRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${configure.order.path}")
    private String order_path;      //   order/orderid

     //订单项API
     @RequestMapping(value = "/post_media", method = RequestMethod.POST, produces = "application/json;charset-UTF-8")
     @ResponseBody
     public Map<String, Object> orderItem(@RequestBody ItemMedia item){
         Map<String, Object> order_return = new HashMap<String, Object>();

         //查找order表中的是否存在orderid
         List<ItemOrder> o_list = orderRepository.findOrderByOrderid(item.getOrderid());
         ItemMedia itemMedia ;
         ItemOrder order = new ItemOrder();
         if(o_list.isEmpty()){
             //生成order表中的URL
             String o_url = "http://" + IPAddress + "/mediaAll?orderid=" + item.getOrderid();
             //添加order表中的记录
             order.setOrderid(item.getOrderid());
             order.setUrl(o_url);
             order.setStatus(0);

             String path = order_path + order.getOrderid();      //   order\\orderid
             File file = new File(path);
             file.mkdir();
             order.setTrans_path(path);

             orderRepository.save(order);

             //生成item表中的URL
             String i_url = "http://" + IPAddress + "/media?uuid=" + item.getUuid();
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
                    order_return.put("uuid", item.getUuid());
                    order_return.put("status", item.getStatus());
                    order_return.put("url", item.getUrl());
                    break;
                }
             }
             if(mag == 1){
                 String i_url = "http://" + IPAddress + "/media?uuid=" + item.getUuid();
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
//             List<ItemMedia> i_list = itemMediaRepository.findMediaByUuid(ord.getUuid());
//             if (i_list.isEmpty()){
//                 String i_url = "http://" + IPAddress + "/media?uuid=" + ord.getUuid();
//                 ord.setUrl(i_url);
//                 //设置成0，表示还未完成转码
//                 ord.setStatus(0);
//                itemMedia = itemMediaRepository.save(ord);
//             }else {
//                 itemMedia =i_list.get(0);
//             }

         }

        return order_return;

     }

//    //订单PostAPI
//    @RequestMapping(value = "/order", method = RequestMethod.POST, produces = "application/json;charset-UTF-8")
//    @ResponseBody
//    public Map<String, Object> order(@RequestBody ItemMedia ord){
//        Map<String, Object> order_return = new HashMap<String, Object>();
//
//        //查询该订单项是否已经处理过，已处理就直接返回url
//        List<ItemMedia> list =  itemMediaRepository.findMediaByUuid(ord.getUuid());
//        ItemMedia itemMedia;
//        if(list.isEmpty()){
//
//            //设置生成的url
//            String url = "http://" + IPAddress + "/media?uuid=" + ord.getUuid();
//            ord.setUrl(url);
//
//            //设置成0，表示还未完成转码
//            ord.setStatus(0);
//
//            itemMedia = itemMediaRepository.save(ord);
//        }else{
//            itemMedia = list.get(0);
//        }
//
//        //返回字段
//        order_return.put("uuid", itemMedia.getUuid());
//        order_return.put("status", itemMedia.getStatus());
//        order_return.put("url", itemMedia.getUrl());
//
//        return order_return;
//    }

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
            System.out.println("@"+order+"@");
            //查看order表中的status状态
            int o_status = order.getStatus();
            System.out.println("@"+o_status+"@");
            switch (o_status) {
                //0:订单未完成  1：订单完成  2：压缩完成  3：无订单

                case 0:
                    System.out.println("执行###########");
                    List<ItemMedia> i_list = itemMediaRepository.findMediaByOrderid(post_order.getOrderid());
                    System.out.println("上岛咖啡"+i_list);
                    int flag = 1;
                    for (int j = 0; j < i_list.size(); j++) {
                        //判断item中的status的状态是否全部为1
                        int i_status = i_list.get(j).getStatus();
//                        System.out.println("1执行###########"+i_status);
                        System.out.println(i_list.get(j).getOrderid() + " " + i_list.get(j).getUuid() + " " + i_status);
                        if (i_status == 0) {
                            flag = 0;
                            break;
                        }
                    }
                    if (flag == 1) {
                       order.setStatus(flag);
                        System.out.println("执行###########"+order.getStatus());

                        orderRepository.save(post_order);
                    }
                    order_return.put("orderid", order.getOrderid());
                    order_return.put("status", order.getStatus());
                    order_return.put("url", order.getUrl());
                    break;
                case 1:
                    order_return.put("orderid", order.getOrderid());
                    order_return.put("status", order.getStatus());
                    order_return.put("url", order.getUrl());
                    break;
                case 2:
                    String path = order.getTrans_path() + ".zip";
                    System.out.println("*&*&*&*&&*&*&*&&*&");
                    System.out.println(order.getTrans_path());
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
    public ResponseEntity downloadFile( Long uuid)
            throws IOException {
        //生成相应的文件下载链接

//        通过uuid查找高码视频路径
        List<ItemMedia> i_list = itemMediaRepository.findMediaByUuid(uuid);

        //如果没有uuid，则提示没有该订单项
        if(i_list.isEmpty()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        ItemMedia itemMedia = i_list.get(0);
        String filePath = "";

        //查看转码状态，如果完成就返回，否则提示转码未完成
        if(itemMedia.getStatus()==1){
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
   }
