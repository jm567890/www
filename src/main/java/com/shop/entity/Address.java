package com.shop.entity;

import com.shop.dto.AddressDto;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Address {

    @Id
    @Column(name="address_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // 주소록 엔티티 다대일 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String name;

    private String phone;

    private String address;

    private String addressDetail;

    public static Address createAddress(Member member, AddressDto addressDto) {

        Address address =new Address();
        address.setMember(member);
        address.name = addressDto.getAddress();
        address.phone = addressDto.getPhone();
        address.setAddress(addressDto.getAddress());
        address.setAddressDetail(addressDto.getAddressDetail());

        return address;
    }

}
