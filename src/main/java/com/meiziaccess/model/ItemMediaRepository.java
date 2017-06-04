package com.meiziaccess.model;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by user-u1 on 2016/5/7.
 */
public interface ItemMediaRepository extends JpaRepository<ItemMedia, Long> {
    List<ItemMedia> findMediaByUuidAndOrderid(Long orderid, Long  uuid);


    List<ItemMedia> findMediaByOrderid(Long orderid);
}
