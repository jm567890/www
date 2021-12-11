package com.shop.service;

import com.shop.entity.Tag;
import com.shop.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    @Transactional(readOnly = true)
    public List<Tag> getTagList() {
        return tagRepository.findAllByOrderByTotalSellDesc();
    }

}
