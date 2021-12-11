package com.shop.repository;

import com.shop.dto.GiftMainItemDto;
import com.shop.dto.ItemComplexSearchDto;
import com.shop.dto.ItemSearchDto;

import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<GiftMainItemDto> getGiftItemPage(ItemSearchDto itemSearchDto, Pageable pageable, Long cateCode);

    Page<MainItemDto> getDetailSearchPage(String[] filters, ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getComplexSearchPage(ItemComplexSearchDto itemComplexSearchDto, Pageable pageable);

}
