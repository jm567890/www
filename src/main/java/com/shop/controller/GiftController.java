package com.shop.controller;

import com.shop.dto.*;
import com.shop.service.AddressService;
import com.shop.service.ItemService;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.java_sdk.api.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
public class GiftController {

    private final AddressService addressService;
    private final OrderService orderService;
    private final ItemService itemService;

    @GetMapping(value = "/giftMain/{cateCode}")
    public String giftMain(@PathVariable("cateCode") Long cateCode, ItemSearchDto itemSearchDto,
                           Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,6);
        Page<GiftMainItemDto> items =
                itemService.getGiftItemPage(itemSearchDto, pageable, cateCode);


        log.info(String.valueOf(cateCode));

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);

        return "/gift/giftMain" ;
    }



    // 문자 전송
    @ResponseBody
    @PostMapping(value = "/sendSms")
    public ResponseEntity sendSms(@RequestBody OrderDto orderDto,
                                  Principal principal) throws Exception { // 휴대폰 문자보내기

        String api_key = "api_key";
        String api_secret = "api_secret";
        Message coolsms = new Message(api_key, api_secret);

        HashMap<String, String> params = new HashMap<String, String>();

        String email = principal.getName();           // principal 객체에서 현재 로그인한 회원의 이메일 정보 조회
        Long orderId;

        params.put("to", "내번호"); // 수신번호
        params.put("from", orderDto.getFrom()); // 발신번호
        params.put("text", orderDto.getText()); // 문자내용
        params.put("type", "sms"); // 문자 타입
        params.put("app_version", "JAVA SDK v2.2"); // application name and version

        System.out.println(params);

//        JSONObject result = coolsms.send(params); // 보내기&전송결과받기

        System.out.println("===============================");
//        System.out.println("result : "+result);
        System.out.println("result : OK");

        System.out.println("===============================");

        return new ResponseEntity<Long>(HttpStatus.OK);    // HTTP 응답 상태 코드 반환
    }


    // 선물하기 폼
    @GetMapping(value ="/giftForm/{itemId}")
    public String giftForm(@PathVariable("itemId") Long itemId, @RequestParam("count") Integer count, Model model) {
        GiftDto giftDto = new GiftDto();
        giftDto.setItemId(itemId);
        giftDto.setCount(count);

        model.addAttribute("giftDto", giftDto);

        return "gift/giftForm";
    }

    // 선물
    @PostMapping(value = "/gift")
    public String gift(@Valid GiftDto giftDto, BindingResult bindingResult, Principal principal, Model model) {
        if(bindingResult.hasErrors()) {
            return "gift/giftForm";
        }

        String email = principal.getName();

        try {
            orderService.order(giftDto.toOrderDto(), email);
            addressService.saveAddress(giftDto.toAddressDto(), email);
        } catch(Exception e) {
            log.error(e.getMessage(), e);

            return "gift/giftForm";
        }

        model.addAttribute("message", "선물하기가 완료되었습니다.");

        return "redirect:/orders";
    }
}
