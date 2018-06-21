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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

//<<<<<<< HEAD
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Value("${configure.order.path}")
//    private String order_path;      //   order/orderid
//
//     //订单项API
//     @RequestMapping(value = "/order", method = RequestMethod.POST, produces = "application/json;charset-UTF-8")
//     @ResponseBody
//     public Map<String, Object> orderItem(@RequestBody ItemMedia item){
//         Map<String, Object> order_return = new HashMap<String, Object>();
//         int mag = 1;
//         //查找order表中的是否存在orderid
//         List<ItemOrder> o_list = orderRepository.findOrderByOrderid(item.getOrderid());
//         ItemMedia itemMedia ;
//         ItemOrder order = new ItemOrder();
//         if(o_list.isEmpty()){
//             //生成order表中的URL
//             String o_url = "http://" + IPAddress + "/get_order?orderid=" + item.getOrderid();
//             //添加order表中的记录
//             order.setOrderid(item.getOrderid());
//             order.setUrl(o_url);
//             order.setStatus(0);
//
//             String path = order_path+ order.getOrderid();      //   order\\orderid
////             File file = new File(path);
////             file.mkdir();
//             order.setTrans_path(path);
//
//             orderRepository.save(order);
//
//             //生成item表中的URL
//             String i_url = "http://" + IPAddress + "/get_item?orderid=" + item.getOrderid()+"&&uuid="+item.getUuid();
//             item.setUrl(i_url);
//             //设置成0，表示还未完成转码
//             item.setStatus(0);
//            // item.setOrder_video_path(order.getTrans_path()+"\\"+item.getUuid()+"."+item.getFormat());// order\\orderid\\uuid.mp4
//             itemMedia = itemMediaRepository.save(item);
//
//             order_return.put("uuid", itemMedia.getUuid());
//             order_return.put("status", itemMedia.getStatus());
//             order_return.put("url", itemMedia.getUrl());
//
//         }else{
//             List<ItemMedia> i_o_list = itemMediaRepository.findMediaByOrderid(item.getOrderid());
//
//             for(int j=0; j<i_o_list.size(); j++){
//
//                if(i_o_list.get(j).getUuid().equals(item.getUuid())){
//                    mag = 0;
//                    order_return.put("uuid", i_o_list.get(j).getUuid());
//                    order_return.put("status", i_o_list.get(j).getStatus());
//                    order_return.put("url", i_o_list.get(j).getUrl());
//                    break;
//                }
//             }
//             if(mag == 1){
//                 String i_url = "http://" + IPAddress + "/get_item?orderid=" + item.getOrderid()+"&&uuid="+item.getUuid();
//                 item.setUrl(i_url);
//                 //设置成0，表示还未完成转码
//                 item.setStatus(0);
//                 String path = order_path + item.getOrderid();
////                 item.setOrder_video_path(path+File.separator+item.getUuid()+"."+item.getFormat());// order\\orderid\\uuid.mp4
////                 System.out.println(item.getOrder_video_path()+"###"); 转码写入
//                 itemMedia = itemMediaRepository.save(item);
//                 order_return.put("uuid", itemMedia.getUuid());
//                 order_return.put("status", itemMedia.getStatus());
//                 order_return.put("url", itemMedia.getUrl());
//             }
//
//         }
//
//        return order_return;
//
//     }
//
//    //视频订单下载链接
//    @RequestMapping(value = "/post_order", method = RequestMethod.POST, produces = "application/json;charset-UTF-8")
//=======
    //订单PostAPI
    @RequestMapping(value = "/order", method = RequestMethod.POST, produces = "application/json;charset-UTF-8")
    @ResponseBody
    public Map<String, Object> order(@RequestBody ItemMedia ord){
        Map<String, Object> order_return = new HashMap<String, Object>();
        System.out.println(ord.getUuid() + ", " + ord.getEntire()+ ", " + ord.getStarttime() + ", " + ord.getEndtime() + ", " + ord.getHighdef_video_path());
        String url = "http://" + IPAddress + "/media?uuid=" + ord.getUuid();

        //处理视频，修改链接和地址
        ord.setStatus(0);
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
        String filePath = itemMedia.getOrder_video_path();
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