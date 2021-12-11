package com.shop.mapstruct;

import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemFormMapper extends GenericMapper<ItemFormDto, Item> {

    ItemFormMapper INSTANCE = Mappers.getMapper(ItemFormMapper.class);

}
