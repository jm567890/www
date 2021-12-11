package com.shop.repository;

import com.shop.entity.ItemTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemTagRepository extends JpaRepository<ItemTag,Long> {

    List<ItemTag> findByItemId(@Param(value = "itemId") Long itemId);

}
