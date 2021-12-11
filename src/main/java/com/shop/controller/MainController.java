package com.shop.controller;

import com.shop.config.auth.LoginUser;
import com.shop.config.auth.dto.SessionUser;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Category;
import com.shop.service.CategoryService;
import com.shop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final CategoryService categoryService;
    private final ItemService itemService;

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Model model, Optional<Integer> page){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0,6);
        List<Category> categoryList = categoryService.getCategoryList();

        Page<MainItemDto> items =
                itemService.getMainItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("maxPage", 5);


        return "main";
    }
}
