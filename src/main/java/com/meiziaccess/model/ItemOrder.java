package com.meiziaccess.model;

import javax.persistence.*;

/**
 * Created by Administrator on 2017/3/25.
 */
@Entity
@Table(name = "item_order")
public class ItemOrder {

        @Id
        @GeneratedValue
        private Long id;

        @Column(name = "orderid")
        private Long orderid;

        @Column(name = "url")
        private String url;

        @Column(name = "status")
        private int status;

        @Column(name = "trans_path")
        private String trans_path;

        @Column(name = "pack_path")
        private String pack_path;

        public ItemOrder(){super();}

        public ItemOrder(Long orderid, String url, int status, String trans_path, String pack_path) {
            this.orderid = orderid;
            this.url = url;
            this.status = status;
            this.trans_path = trans_path;
            this.pack_path = pack_path;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getOrderid() {
            return orderid;
        }

        public void setOrderid(Long orderid) {
            this.orderid = orderid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTrans_path() {
            return trans_path;
        }

        public void setTrans_path(String trans_path) {
            this.trans_path = trans_path;
        }

        public String getPack_path() {
            return pack_path;
        }

        public void setPack_path(String pack_path) {
            this.pack_path = pack_path;
        }
    }

