package com.shop.dto;

import com.shop.constant.GiftStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
public class GiftDto {

    @NotNull(message = "상품 아이디는 필수 입력 값입니다.")
    private Long itemId;

    @Min(value = 1, message = "최소 주문 수량은 1개 입니다.")
    @Max(value = 999, message = "최대 주문 수량은 999개 입니다.")
    private int count;

    @Min(value = 0, message = "포인트는 0 이상의 숫자를 입력해주세요.")
    private int usedPoint = 0;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;

    @NotEmpty(message = "상세 주소는 필수 입력 값입니다.")
    private String addressDetail;

    private GiftStatus giftStatus = GiftStatus.GIFT;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    public OrderDto toOrderDto() {
        OrderDto orderDto = new OrderDto();
        orderDto.setItemId(this.itemId);
        orderDto.setCount(this.count);
        orderDto.setAddress(this.address);
        orderDto.setAddressDetail(this.addressDetail);
        orderDto.setGiftStatus(GiftStatus.GIFT);

        return orderDto;
    }

    public AddressDto toAddressDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setName(this.name);
        addressDto.setPhone(this.phone);
        addressDto.setAddress(this.address);
        addressDto.setAddressDetail(this.addressDetail);

        return addressDto;
    }

}
