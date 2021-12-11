package com.shop.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name="tag")
@Getter
@Setter
@ToString
public class Tag {

    @Id
    @Column(name="tag_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, length = 50)
    private String tagNm;

    @Column(length = 50)
    private String tagContent;

    private int totalSell;

    public void addTotalSell(){
        this.totalSell += 1;
    }

}
