package com.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.constant.ItemComplexSearchSortColumn;
import com.shop.dto.ItemComplexSearchDto;
import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.repository.CategoryRepository;
import com.shop.service.CategoryService;
import com.shop.service.ItemService;
import com.shop.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final TagService tagService;
    private final CategoryService categoryService;
    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("tagList", tagService.getTagList());
        model.addAttribute("itemFormDto", new ItemFormDto());

        return "item/itemForm" ;
    }

    // 상품 등록
    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("tagList", tagService.getTagList());

        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");

            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList);

        } catch(Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");

            return "item/itemForm";
        }

        return "redirect:/admin/items";
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemFormUpdate(@PathVariable("itemId") Long itemId, Model model) {
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("tagList", tagService.getTagList());

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);

            model.addAttribute("itemFormDto", itemFormDto);
        } catch(EntityNotFoundException e) {
            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());

            return "item/itemForm";
        }

        return "item/itemForm";
    }


    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model) {
        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("tagList", tagService.getTagList());

        if(bindingResult.hasErrors()) {
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null) {
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");

            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);
        } catch(Exception e) {
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");

            return "item/itemForm";
        }

        return "redirect:/admin/items";
    }

    // 상품 관리 페이지
    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page")Optional<Integer> page, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);

            model.addAttribute("items", items);
            model.addAttribute("itemSearchDto", itemSearchDto);
            model.addAttribute("maxPage", 5);
            return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model) {
        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        log.info("itemFormDto : " + itemFormDto.toString());
        model.addAttribute("item", itemFormDto);

        return "item/itemDtl";
    }

    @GetMapping(value = { "/items", "/items/{page}" })
    public String itemList(ItemComplexSearchDto itemComplexSearchDto, Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        Page<MainItemDto> items = itemService.getComplexSearchPage(itemComplexSearchDto, pageable);

        model.addAttribute("categoryList", categoryService.getCategoryList());
        model.addAttribute("tagList", tagService.getTagList());
        model.addAttribute("itemComplexSearchDto", itemComplexSearchDto);
        model.addAttribute("items", items);
        model.addAttribute("maxPage", 5);

        return "item/itemList";
    }


}
