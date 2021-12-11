package com.shop.repository;


import com.shop.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

     @Query("select t from Tag t order by t.totalSell desc")
     List<Tag> findAllByOrderByTotalSellDesc();

     Tag findByTagNm(String tagNm);

}
