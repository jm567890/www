package com.shop.mapstruct;

import com.shop.dto.ItemDto;
import com.shop.entity.Item;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ItemMapper extends GenericMapper<ItemDto, Item> {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

}
