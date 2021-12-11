package com.shop.entity;

import com.shop.constant.GiftStatus;
import com.shop.constant.OrderStatus;
import com.shop.dto.OrderDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;              // 다대일 단방향 매핑

    private LocalDateTime orderDate;    // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    // 주문 상태

    @Enumerated(EnumType.STRING)
    private GiftStatus giftStatus;      // 구매/선물 상태

    @Column(nullable = false)
    private String address;


    // 영속성 상태 변화를 자식 엔티티에 모두 전이
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,  // 일대다 매핑, mappedBy 속성 : 연관 관계 주인 설정
                orphanRemoval = true, fetch = FetchType.LAZY)
    // 고아 객체 제거 사용
    private List<OrderItem> orderItems = new ArrayList<>();


//    private LocalDateTime regTime;
//    private LocalDateTime updateTime;

    // 주문 상품 정보 - orderItem 객체를 order 객체의 orderItems에 추가
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);        // 양방향 참조이므로 orderItem 객체에도 order 객체 세팅
    }

    public static Order createOrder(Member member, OrderDto orderDto, List<OrderItem> orderItemList) {
        Order order = new Order();
        order.setMember(member);
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        order.setGiftStatus(orderDto.getGiftStatus());

        if(orderDto.getGiftStatus().equals(GiftStatus.BUY)) {
            order.setAddress(member.getAddress() + " " + member.getAddressDetail());
        } else if(orderDto.getGiftStatus().equals(GiftStatus.GIFT)) {
            order.setAddress(orderDto.getAddress() + " " + orderDto.getAddressDetail());
        }

        for(OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }

        return order;
    }


    // 총 주문 금액
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    // 주문 취소
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }
}
