package jpabook.service;

import jpabook.domain.Order;
import jpabook.domain.OrderStatus;
import jpabook.domain.item.Book;
import jpabook.domain.item.Item;
import jpabook.exception.NotEnoughStockException;
import jpabook.domain.Address;
import jpabook.domain.Member;
import jpabook.repository.OrderRepository;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    @DisplayName("상품 주문 테스트")
    void 상품_주문_테스트() throws Exception {
        // given
        Member member = getMember();
        Item item = getBook("JPA", 10000, 10);

        int orderCount = 2;

        // when
        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);
        Order order = orderRepository.findOne(orderId);

        // then
        assertThat(order.getStatus(), CoreMatchers.is(OrderStatus.ORDER));
        assertThat(order.getMember().getId(), is(member.getId()));
        assertThat(order.getOrderItems().size(), is(1));
        assertThat(order.getTotalPrice(), is(10000 * orderCount));
        assertThat(item.getStockQuantity(), is(8));
    }



    @Test
    @DisplayName("주문 취소 테스트")
    void 주문_취소_테스트() throws Exception {
        // given
        Member member = getMember();
        Item item = getBook("JPA", 10000, 10);

        int orderCount = 3;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        // when
        orderService.cancelOrder(orderId);

        // then
        Order order = orderRepository.findOne(orderId);

        assertThat(order.getStatus(), is(OrderStatus.CANCEL));
        assertThat(item.getStockQuantity(), is(10));
    }

    @Test
    @DisplayName("상품 주문 재고수량 초과")
    void 상품_주문_재고수량_초과() throws Exception {
        // given
        Member member = getMember();
        Item item = getBook("JPA", 10000, 10);

        int orderCount = 11;

        // when
        NotEnoughStockException ex = assertThrows(NotEnoughStockException.class, () -> {
            orderService.order(member.getId(), item.getId(), orderCount);
        });

        // then
        assertThat(ex.getMessage(), is("need more stock"));
    }

    private Book getBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member getMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "광성로", "11"));
        em.persist(member);
        return member;
    }
}