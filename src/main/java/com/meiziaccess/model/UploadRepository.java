package com.meiziaccess.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by user-u1 on 2016/11/20.
 */
public interface UploadRepository extends JpaRepository<UploadItem, Long> {
    List<UploadItem> findUploadItemByMd5(String md5);

    //List<UploadItem> findByStatusAndVendor_type(int status, int vendor_type);

    List<UploadItem> findByTitle(String title);

    List<UploadItem> findByMd5(String Md5);

    @Query("select u from UploadItem u where u.status=?1 and u.vendor_type=?2 and u.token=?3")
    List<UploadItem> findByStatusAndVendor_typeAndToken(int status, int vendor_type, int token);

    @Query("delete from UploadItem u where u.md5=?1")
    List<UploadItem> delMd5(String md5);

//    最好不用更新语句 因为不识别 具体原因不清楚
//    @Query("update UploadItem u set u.inform=null , u.price=0, u.price_type=0, u.status=0, u.token=0 where u.md5=?1")
//    List<UploadItem> setMd5(String md5);

}
