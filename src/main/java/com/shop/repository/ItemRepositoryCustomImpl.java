package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemComplexSearchSortColumn;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.*;
import com.shop.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{    // ItemRepositoryCustom 상속

    private JPAQueryFactory queryFactory;                    // 동적 쿼리 생성을 위해 사용

    public ItemRepositoryCustomImpl(EntityManager em){       // JPAQueryFactory 생성자로 Em 객체 넣어줌
        this.queryFactory = new JPAQueryFactory(em);
    }

    // 상품 판매 상태 조건 유무에 따라 조회
    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus){
        return searchSellStatus ==
                null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
    }

    // dateTime의 값의 이전 시간 값으로 세팅 후 해당 시간 이후로 조회
    private BooleanExpression regDtsAfter(String searchDateType){
        LocalDateTime dateTime = LocalDateTime.now();

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if(StringUtils.equals("1d", searchDateType)){
            dateTime = dateTime.minusDays(1);
        }else if(StringUtils.equals("1w", searchDateType)){
            dateTime = dateTime.minusWeeks(1);
        }else if(StringUtils.equals("1m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }else if(StringUtils.equals("6m", searchDateType)){
            dateTime = dateTime.minusMonths(1);
        }
        return QItem.item.regTime.after(dateTime);
    }

    // 상품명 또는 상품 생성자 아이디 또는 카테고리 에 검색어 포함 시 조회
    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        if(StringUtils.equals("itemNm", searchBy)) {
            return QItem.item.itemNm.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)) {
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;
    }

    private BooleanExpression searchCategory(ItemComplexSearchDto itemComplexSearchDto) {
        Long cateCode = itemComplexSearchDto.getSearchCategory();

        return cateCode == null ? null : QItem.item.category.cateCode.eq(cateCode);
    }

    private BooleanExpression searchTag(ItemComplexSearchDto itemComplexSearchDto) {
        List<Long> tagIds = itemComplexSearchDto.getSearchTagIds();

        return tagIds == null || tagIds.isEmpty() ? null : QItemTag.itemTag.tag.id.in(tagIds);
    }

    private OrderSpecifier searchOrderBy(ItemComplexSearchDto itemComplexSearchDto) {
        ItemComplexSearchSortColumn sortColumn = itemComplexSearchDto.getSortColumn();
        Sort.Direction sortDirection = itemComplexSearchDto.getSortDirection();

        OrderSpecifier orderSpecifier = null;

        if(sortColumn.equals(ItemComplexSearchSortColumn.REG_TIME)) {
            if(sortDirection.isAscending()) {
                orderSpecifier = QItem.item.regTime.asc();
            } else {
                orderSpecifier = QItem.item.regTime.desc();
            }
        } else if(sortColumn.equals(ItemComplexSearchSortColumn.NAME)) {
            if(sortDirection.isAscending()) {
                orderSpecifier = QItem.item.itemNm.asc();
            } else {
                orderSpecifier = QItem.item.itemNm.desc();
            }
        } else if(sortColumn.equals(ItemComplexSearchSortColumn.PRICE)) {
            if(sortDirection.isAscending()) {
                orderSpecifier = QItem.item.price.asc();
            } else {
                orderSpecifier = QItem.item.price.desc();
            }
        }

        return orderSpecifier;
    }



    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        // queryFactory를 이용해 쿼리 생성
        QueryResults<Item> results = queryFactory
                .selectFrom(QItem.item)            // 상품데이터 조회를 위해 Qitem의 item 지정
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                        itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())     // 시작 인덱스 지정
                .limit(pageable.getPageSize())    // 한번에 가지고 올 최대 개수 지정
                .fetchResults();                  // 조회한 리스트, 전체 개수를 포함하는 QueryResults 반환
                                                  // 2개의 쿼리문이 실행됨

        List<Item> content = results.getResults();
        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    // 검색어가 null이 아닐 때 해당 검색어가 포함되는 상품 조회
    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ?
                null : QItem.item.itemNm.like("%" + searchQuery + "%");
    }


    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price,
                                item.shippingFee
                        )
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();

        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<GiftMainItemDto> getGiftItemPage(ItemSearchDto itemSearchDto, Pageable pageable, Long cateCode) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        QCategory category = QCategory.category;

        QueryResults<GiftMainItemDto> results = queryFactory
                .select(
                        new QGiftMainItemDto(
                                item.id,
                                category.cateCode,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .join(item.category, category)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .where(category.cateCode.eq(cateCode))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<GiftMainItemDto> content = results.getResults();

        long total = results.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
    @Override
    public Page<MainItemDto> getDetailSearchPage(String[] filters, ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        QTag tag = QTag.tag;
        com.shop.entity.QItemTag itemTag = com.shop.entity.QItemTag.itemTag;

        List<String> filterList = Arrays.stream(filters).collect(Collectors.toList());

        JPQLQuery<MainItemDto> query = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price,
                                item.shippingFee
                        )
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .leftJoin(itemTag).on(itemTag.item.eq(itemImg.item))
                .leftJoin(itemTag.tag, tag)
                .where(itemImg.repImgYn.eq("Y"))        //상품 이미지 경우 대표 상품 이미지만 불러옴
                .where(itemNmLike(itemSearchDto.getSearchQuery()))
                .groupBy(itemImg.item)
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if(filterList.size() > 0) {
            query.where(tag.tagNm.in(filterList));
        }

        QueryResults<MainItemDto> results = query.fetchResults();

        List<MainItemDto> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainItemDto> getComplexSearchPage(ItemComplexSearchDto itemComplexSearchDto, Pageable pageable) {
        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;
        QItemTag itemTag = QItemTag.itemTag;
        QTag tag = QTag.tag;

        QueryResults<MainItemDto> results = queryFactory
                .select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price,
                                item.shippingFee
                        )
                )
                .from(itemImg)
                .join(itemImg.item, item)
                .leftJoin(itemTag).on(itemTag.item.eq(itemImg.item))
                .leftJoin(itemTag.tag, tag)
                .where(itemImg.repImgYn.eq("Y"))
                .where(itemNmLike(itemComplexSearchDto.getSearchQuery()))
                .where(searchCategory(itemComplexSearchDto))
                .where(searchTag(itemComplexSearchDto))
                .groupBy(itemImg.item)
                .orderBy(searchOrderBy(itemComplexSearchDto))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<MainItemDto> content = results.getResults();

        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

}
