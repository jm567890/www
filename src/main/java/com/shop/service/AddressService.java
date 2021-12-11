package com.shop.service;

import com.shop.dto.AddressDto;
import com.shop.entity.Address;
import com.shop.entity.Member;
import com.shop.repository.AddressRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor    // final, @Nonnull이 붙은 필드에 생성자 생성
public class AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    // 주소록 추가
    public Long saveAddress(AddressDto addressDto, String email) {
        Member member = memberRepository.findByEmail(email);
        Address address = Address.createAddress(member, addressDto);

        addressRepository.save(address);

        return address.getId();
    }
}
