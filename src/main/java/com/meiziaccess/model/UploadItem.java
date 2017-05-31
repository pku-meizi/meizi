package com.meiziaccess.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by user-u1 on 2016/11/18.
 */

@Entity
@Table(name = "upload_item")
public class UploadItem {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "upload")
    boolean upload;

    @Column(name = "title")
    String title;

    @Column(name = "upload_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date upload_time;

    @Column(name = "duration")
    int duration;

    @Column(name = "price_type")
    int price_type;     //0:单价  1:一口价

    @Column(name = "price")
    double price;

    @Column(name = "copyright_type")
    String copyright_type;     //RF  RM

    @Column(name = "copyright_duration")
    int copyright_duration;   //0:6个月  1:一年  2:两年    3:三年   4::五年   5:长期  装换成天数

    @Column(name = "md5")
    String md5;

    @Column(name = "path")
    String path;

    @Column(name = "material_type")
    String material_type;           //成片 素材

    @Column(name = "status")
    int status;

    @Column(name = "inform")
    private String inform;

    @Column(name = "token")
    private int token;

    @Column(name = "vendor_type")
    private int vendor_type;

    public UploadItem() {
        this.upload = true;
        this.title = "";
        this.upload_time = new Date();
        this.duration = 0;
        this.price = 0;
        this.price_type = 0;
        this.copyright_type = "RM";
        this.copyright_duration = 0;
        this.md5 = "";
        this.path = "";
        this.material_type = "成片";
        this.status = 0;
        this.inform = "";
        this.token = 0;
        this.vendor_type =0;
    }

    public UploadItem(String title, String md5, String path) {
        this.title = title;
        this.md5 = md5;
        this.path = path;

        this.upload = true;
        this.upload_time = new Date();
        this.duration = 0;
        this.price = 0;
        this.price_type = 0;
        this.copyright_type = "RM";
        this.copyright_duration = 0;
        this.material_type = "成片";
        this.status =0;
    }


    public UploadItem(boolean upload, String title, Date upload_time, int duration, int price_type,
                      double price, String copyright_type, int copyright_duration, String md5, String path,
                      String material_type,  int status) {
        this.upload = upload;
        this.title = title;
        this.upload_time = upload_time;
        this.duration = duration;
        this.price_type = price_type;
        this.price = price;
        this.copyright_type = copyright_type;
        this.copyright_duration = copyright_duration;
        this.md5 = md5;
        this.path = path;
        this.material_type = material_type;
        this.status = status;
    }

    public int getVendor_type() {
        return vendor_type;
    }

    public void setVendor_type(int vendor_type) {
        this.vendor_type = vendor_type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(Date upload_time) {
        this.upload_time = upload_time;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPrice_type() {
        return price_type;
    }

    public void setPrice_type(int price_type) {
        this.price_type = price_type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCopyright_type() {
        return copyright_type;
    }

    public void setCopyright_type(String copyright_type) {
        this.copyright_type = copyright_type;
    }

    public int getCopyright_duration() {
        return copyright_duration;
    }

    public void setCopyright_duration(int copyright_duration) {
        this.copyright_duration = copyright_duration;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMaterial_type() {
        return material_type;
    }

    public void setMaterial_type(String material_type) {
        this.material_type = material_type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getInform() {
        return inform;
    }

    public void setInform(String inform) {
        this.inform = inform;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    @Override
    public boolean equals(Object anObject){
        if(this==anObject)  // 引用相等
            return true;
        if(anObject==null)  // 对象为null
            return false;
        if(getClass() !=anObject.getClass()) //类是否相同
            return false;
        UploadItem another = (UploadItem)anObject;
        return md5.equals(another.getMd5());  // 域属性相等
    }
}
