package com.shop.entity;

import com.shop.ItemSellStatus;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="item")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Item {
    @Id
    @Column(name = "itme_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

    @Column(nullable = false, length = 50)
   private String itemNm;

    @Column(name = "price", nullable = false)
   private int price;

    @Column(nullable = false)
   private int stockNumber;

    @Lob
    @Column(nullable = false)
   private String itemDetail;

    @Enumerated(EnumType.STRING)
   private ItemSellStatus itemSellStatus;

   private LocalDateTime regTime;
   private LocalDateTime updateTime;
}
