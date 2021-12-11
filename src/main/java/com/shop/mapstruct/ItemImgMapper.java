package com.shop.mapstruct;

import com.shop.dto.ItemImgDto;
import com.shop.entity.ItemImg;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ItemImgMapper extends GenericMapper<ItemImgDto, ItemImg> {

    ItemImgMapper INSTANCE = Mappers.getMapper(ItemImgMapper.class);

}
