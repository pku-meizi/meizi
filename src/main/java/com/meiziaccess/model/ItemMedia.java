package com.meiziaccess.model;



import javax.annotation.sql.DataSourceDefinition;
import javax.persistence.*;
import javax.persistence.Entity;


/**
 * Created by user-u1 on 2016/4/14.
 */

//获取Post过来的订单数据
@Entity
@Table(name = "item_media")
public class ItemMedia {

    @Id
    @GeneratedValue
    private Long id;

    //订单项ID
    @Column(name = "uuid")
    private Long uuid;

    @Column(name = "isEntire")
    private Boolean isEntire;

    @Column(name = "starttime")
    private int starttime;

    @Column(name = "endtime")
    private int endtime;

    @Column(name = "highdef_video_path")
    private String highdef_video_path;

    @Column(name = "order_video_path")
    private String order_video_path;

    @Column(name = "format")
    private String format;

    @Column(name = "status")
    private int status;

    @Column(name = "url")
    private String url;

    @Column(name = "orderid")
    private Long orderid;

/*    @ManyToOne(targetEntity = Order.class,fetch = FetchType.EAGER) // Manager表和Role表关系多对一
    @JoinColumn(name = "orderid",nullable = false) //外键
    //cascade--设置级联方式，
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    private Order order;*/


    public ItemMedia() {super();}

    public ItemMedia(Long uuid, Boolean isEntire, int starttime, int endtime, String highdef_video_path, String order_video_path, String format, int status, String url, Long orderid) {
        this.uuid = uuid;
        this.isEntire = isEntire;
        this.starttime = starttime;
        this.endtime = endtime;
        this.highdef_video_path = highdef_video_path;
        this.order_video_path = order_video_path;
        this.format = format;
        this.status = status;
        this.url = url;
        this.orderid = orderid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public Boolean getEntire() {
        return isEntire;
    }

    public void setEntire(Boolean entire) {
        isEntire = entire;
    }

    public int getStarttime() {
        return starttime;
    }

    public void setStarttime(int starttime) {
        this.starttime = starttime;
    }

    public int getEndtime() {
        return endtime;
    }

    public void setEndtime(int endtime) {
        this.endtime = endtime;
    }

    public String getHighdef_video_path() {
        return highdef_video_path;
    }

    public void setHighdef_video_path(String highdef_video_path) {
        this.highdef_video_path = highdef_video_path;
    }

    public String getOrder_video_path() {
        return order_video_path;
    }

    public void setOrder_video_path(String order_video_path) {
        this.order_video_path = order_video_path;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }
}
