package com.shop.dto;

import com.shop.constant.ItemComplexSearchSortColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Sort;

import java.util.List;

@Getter
@Setter
@ToString
public class ItemComplexSearchDto {

    private Long searchCategory;

    private List<Long> searchTagIds;

    private String searchQuery = "";

    private ItemComplexSearchSortColumn sortColumn = ItemComplexSearchSortColumn.REG_TIME;

    private Sort.Direction sortDirection = Sort.Direction.DESC;

}
