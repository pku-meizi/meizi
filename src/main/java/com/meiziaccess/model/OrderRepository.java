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
    @Query("update ItemOrder o set o.status=?1 where o.orderid=?2")
    int setStatus (Integer status, Long orderid);

}
