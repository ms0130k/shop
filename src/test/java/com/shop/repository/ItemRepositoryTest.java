package com.shop.repository;

import com.shop.ItemSellStatus;
import com.shop.entity.Item;
import lombok.ToString;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.shop.entity.QItem;

import javax.persistence.PersistenceContext;
import javax.persistence.EntityManager;

import com.querydsl.core.BooleanBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @PersistenceContext
    EntityManager entityManager;

    @Test
    @DisplayName("상품 저장")
    public void createItem() {
        Item item = Item.builder()
                .itemNm("테스트상품")
                .price(10000)
                .itemDetail("테스트상품 상세 설명")
                .itemSellStatus(ItemSellStatus.SELL)
                .stockNumber(100)
                .regTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();
        Item savedItem = itemRepository.save(item);
        System.out.println(savedItem);
    }

    @Test
    @DisplayName("상품명 조회")
    public void findByItemNm() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNm("테스트상품1");
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    private void createItemList() {
        for (int i = 0; i < 10; i++) {
            ItemSellStatus itemSellStatus = i < 5 ? ItemSellStatus.SELL : ItemSellStatus.SOLD_OUT;
            Item item = Item.builder()
                    .itemNm("테스트상품" + i)
                    .price(10000 + i)
                    .itemDetail("테스트상품 상세 설명" + i)
                    .itemSellStatus(itemSellStatus)
                    .stockNumber(100)
                    .regTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 or 상품상세설명")
    public void findByItemNmOrItemDetail() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNmOrItemDetail("테스트상품1", "테스트상품 상세 설명5");
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("가격 LessThan")
    public void findByPriceLessThan() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10005);
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("가격 LessThan 가격 내림차순 정렬")
    public void findByPriceLessThanOrderByPriceDesc() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("@Query를 이용한 상품 조회")
    public void findByItemDetail() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("nativeQuery 속성을 이용한 상품 조회 테스트")
    public void findByItemByNative() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트상품 상세 설명");
        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회1")
    public void queryDsl() {
        this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        QItem qitem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qitem)
                .where(qitem.itemSellStatus.eq(ItemSellStatus.SELL))
                .where(qitem.itemDetail.like("%" + "테스트상품 상세 설명" + "%"))
                .orderBy(qitem.price.desc());

        List<Item> itemList = query.fetch();

        for (Item item : itemList) {
            System.out.println(item);
        }
    }

    @Test
    @DisplayName("상품 Querydsl 조회2")
    public void queryDsl2() {
        this.createItemList();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "테스트상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.lt(price));

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)) {
            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 7);
        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements: " + itemPagingResult.getTotalElements());
        List<Item> resultItemList = itemPagingResult.getContent();
        for (Item resultItem : resultItemList) {
            System.out.println(resultItem);
        }
    }
}