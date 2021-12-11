package com.shop.service;

import com.shop.constant.GiftStatus;
import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.shop.constant.GiftStatus.BUY;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemTagRepository itemTagRepository;
    private final ItemImgRepository itemImgRepository;
    private final OrderItemRepository orderItemRepository;

    public void processTagTotalSell(Item item) {
        List<ItemTag> itemTag = itemTagRepository.findByItemId(item.getId());

        for(ItemTag itemtag : itemTag) {
            itemtag.getTag().addTotalSell();
        }
    }

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId())              // 주문 상품 조회
                .orElseThrow(EntityNotFoundException::new);

        Member member = memberRepository.findByEmail(email);                  // 이메일 정보를 이용해 회원 정보 조회

        List<OrderItem> orderItemList = new ArrayList<>();

        // 주문할 상품 엔티티와 주문 수량을 이용하여 주문 상품 엔티티 생성
        OrderItem orderItem =
                OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        // 회원 정보와 주문할 상품 리스트 정보를 이용하여 주문 엔티티 생성 (상태 : 구매)
        Order order = Order.createOrder(member, orderDto, orderItemList);

        this.processTagTotalSell(item);

        orderRepository.save(order);                                 // 생성한 주문 엔티티 저장
        return order.getId();
    }

    // 전체 주문 조회
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        return this.getPaginatedOrderList(orders, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderListStatus(String email, Pageable pageable, GiftStatus giftStatus) {
        List<Order> orders = orderRepository.findOrdersStatus(email, pageable, giftStatus);
        Long totalCount = orderRepository.countOrder(email);

        return this.getPaginatedOrderList(orders, pageable, totalCount);
    }

    private Page<OrderHistDto> getPaginatedOrderList(List<Order> orders, Pageable pageable, Long totalCount) {
        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);

            List<OrderItem> orderItems = order.getOrderItems();

            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepImgYn(orderItem.getItem().getId(), "Y");

                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());

                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){   // 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사사
       Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }
        return true;
    }

    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();      // 변경 감지 기능에 의해 트랜잭션이 끝날 때 update 쿼리 실행
    }

    public Long orders(List<OrderDto> orderDtoList, String email){

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        // 주문할 상품 리스트
        for(OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem =
                    OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        OrderDto orderDto = new OrderDto();
        orderDto.setAddress(member.getAddress());
        orderDto.setAddressDetail(member.getAddressDetail());
        orderDto.setGiftStatus(GiftStatus.BUY);

        // 현재 로그인한 회원과 주문 상품 목록을 이용하여 주문 엔티티 생성
        Order order = Order.createOrder(member, orderDto, orderItemList);
        orderRepository.save(order);       // 주문 데이터 저장

        return order.getId();
    }

}
