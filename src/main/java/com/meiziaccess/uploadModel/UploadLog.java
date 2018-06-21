package com.meiziaccess.uploadModel;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by user-u1 on 2016/5/27.
 */
@Entity
@Table(name = "upload_log")
public class UploadLog {

    public UploadLog() {super();}

    public UploadLog(String vendor_name, Date upload_time, String uploader_name,
                     String xml_upload_path, String video_upload_path, String vendor_path,
                     double video_price, String video_copyright, int video_price_type, int video_copyright_duration,
                     String frame_extract_path, String material_type, int status, boolean on_shelf, String md5, String title, int play_status) {
        this.vendor_name = vendor_name;
        this.upload_time = upload_time;
        this.uploader_name = uploader_name;
        this.xml_upload_path = xml_upload_path;
        this.video_upload_path = video_upload_path;
        this.vendor_path = vendor_path;
        this.video_price = video_price;
        this.video_copyright = video_copyright;
        this.video_price_type = video_price_type;
        this.video_copyright_duration = video_copyright_duration;
        this.frame_extract_path = frame_extract_path;
        this.material_type = material_type;
        this.status = status;
        this.on_shelf = on_shelf;
        this.md5 = md5;
        this.title = title;
        this.play_status = play_status;
    }



    public String toString(){
        return getUploader_name() + " " + getUpload_time() + "file for " + getVideo_price() + " with " + getVideo_copyright();
    }

    @Id
    @GeneratedValue
    @Column(name = "log_id")
    private int log_id ;

    @Column(name = "vendor_name")
    private String vendor_name;

    @Column(name = "upload_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date upload_time;

    @Column(name = "uploader_name")
    private String uploader_name;

    @Column(name = "xml_upload_path")
    private String xml_upload_path;

    @Column(name = "xml_trans_path")
    private String xml_trans_path;

    @Column(name = "video_play_path")
    private String video_play_path;

    @Column(name = "video_upload_path")
    private String video_upload_path;

    @Column(name = "video_cut_path")
    private String video_cut_path;

    @Column(name = "frame_extract_path")
    private String frame_extract_path;

    @Column(name = "vendor_path")
    private String vendor_path;

    @Column(name = "video_price")
    private double video_price;

    @Column(name = "video_copyright")
    private String video_copyright;

    @Column(name = "video_price_type")
    private int video_price_type;

    @Column(name = "video_copyright_duration")
    private int video_copyright_duration;

    @Column(name = "material_type")
    private String material_type;

    @Column(name ="status")
    private int status;

    @Column(name ="on_shelf")
    private boolean on_shelf;

    @Column(name = "md5")
    private String md5;

    @Column(name = "title")
    private String title;

    @Column(name = "play_status")
    private int play_status;

    public int getPlay_status() {
        return play_status;
    }

    public void setPlay_status(int play_status) {
        this.play_status = play_status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLog_id() {
        return log_id;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isOn_shelf() {
        return on_shelf;
    }

    public void setOn_shelf(boolean on_shelf) {
        this.on_shelf = on_shelf;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getVendor_name() {
        return vendor_name;
    }

    public void setVendor_name(String vendor_name) {
        this.vendor_name = vendor_name;
    }

    public Date getUpload_time() {
        return upload_time;
    }

    public void setUpload_time(Date upload_time) {
        this.upload_time = upload_time;
    }

    public String getUploader_name() {
        return uploader_name;
    }

    public void setUploader_name(String uploader_name) {
        this.uploader_name = uploader_name;
    }

    public String getXml_upload_path() {
        return xml_upload_path;
    }

    public void setXml_upload_path(String xml_upload_path) {
        this.xml_upload_path = xml_upload_path;
    }

    public String getXml_trans_path() {
        return xml_trans_path;
    }

    public void setXml_trans_path(String xml_trans_path) {
        this.xml_trans_path = xml_trans_path;
    }

    public String getVideo_upload_path() {
        return video_upload_path;
    }

    public void setVideo_upload_path(String video_upload_path) {
        this.video_upload_path = video_upload_path;
    }

    public String getVideo_cut_path() {
        return video_cut_path;
    }

    public void setVideo_cut_path(String video_cut_path) {
        this.video_cut_path = video_cut_path;
    }

    public String getFrame_extract_path() {
        return frame_extract_path;
    }

    public void setFrame_extract_path(String frame_extract_path) {
        this.frame_extract_path = frame_extract_path;
    }

    public String getVendor_path() {
        return vendor_path;
    }

    public void setVendor_path(String vendor_path) {
        this.vendor_path = vendor_path;
    }

    public double getVideo_price() {
        return video_price;
    }

    public void setVideo_price(double video_price) {
        this.video_price = video_price;
    }

    public String getVideo_copyright() {
        return video_copyright;
    }

    public void setVideo_copyright(String video_copyright) {
        this.video_copyright = video_copyright;
    }

    public String getVideo_play_path() {
        return video_play_path;
    }

    public void setVideo_play_path(String video_play_path) {
        this.video_play_path = video_play_path;
    }

    public int getvideo_price_type() {
        return video_price_type;
    }

    public void setVideo_price_type(int video_price_type) {
        this.video_price_type = video_price_type;
    }

    public int getVideo_copyright_duration() {
        return video_copyright_duration;
    }

    public void setVideo_copyright_duration(int video_copyright_duration) {
        this.video_copyright_duration = video_copyright_duration;
    }

    public int getVideo_price_type() {
        return video_price_type;
    }

    public String getMaterial_type() {
        return material_type;
    }

    public void setMaterial_type(String material_type) {
        this.material_type = material_type;
    }
}
