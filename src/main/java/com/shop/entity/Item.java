package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.exception.OutOfStockException;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="item")
@Getter
@Setter
@ToString
@Builder @NoArgsConstructor @AllArgsConstructor
//@IdClass(ItemID.class)
public class Item extends BaseEntity{

    @Id   // 기본키 지정
    @Column(name="item_id")
    @GeneratedValue(strategy = GenerationType.AUTO)   // 기본키 생성 전략 자동으로 설정
    private Long id;      // 상품 코드

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cateCode")
    private Category category;           // 카테고리 코드

    @Column(nullable = false, length = 50)          // not null 설정
    private String itemNm;    // 상품명

    @Column(name="price", nullable = false)
    private int price;     // 가격

    @Column(nullable = false)
    private int stockNumber;     // 재고 수량

    @Lob
    @Column(nullable = false)
    private String itemDetail;        // 상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;    // 상품 판매 상태

    @Column(nullable = false)
    private Integer shippingFee; // 배송비

    public static Item createItem(ItemFormDto itemFormDto, Category category) {
        Item item = new Item();
        item.setItemNm(itemFormDto.getItemNm());
        item.setCategory(category);
        item.setPrice(itemFormDto.getPrice());
        item.setStockNumber(itemFormDto.getStockNumber());
        item.setItemDetail(itemFormDto.getItemDetail());
        item.setItemSellStatus(itemFormDto.getItemSellStatus());
        item.setShippingFee(itemFormDto.getShippingFee());

        return item;
    }

    public void updateItem(ItemFormDto itemFormDto, Category category){
        this.setCategory(category);
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.shippingFee = itemFormDto.getShippingFee();

    }

    // 상품 재고 감소 로직
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock<0){
            throw new OutOfStockException("상품의 재고가 부족 합니다. " +
                    "(현재 재고 수량 : "+ this.stockNumber+")");
        }
        this.stockNumber = restStock;
    }

    // 상품 재고 증가
    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }

}
