package com.shop.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartDetailDto {

    private Long cartItemId;

    private String itemNm;

    private int price;

    private int shippingFee;

    private int count;

    private String imgUrl;

    public CartDetailDto(Long cartItemId, String itemNm, int price, int shippingFee, int count, String imgUrl) {
        this.cartItemId = cartItemId;
        this.itemNm = itemNm;
        this.price = price;
        this.shippingFee = shippingFee;
        this.count = count;
        this.imgUrl = imgUrl;
    }

}
