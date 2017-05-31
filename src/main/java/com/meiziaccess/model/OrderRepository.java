package com.meiziaccess.model;

import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/3/17/017.
 */
public interface OrderRepository extends JpaRepository<ItemOrder,Long>{

    List<ItemOrder> findOrderByOrderid(Long orderid);


    @Modifying
    @Transactional
    @Query("update ItemOrder o set o.trans_path=?1")
    String upTans_path (String trans_path);

}
