package jpabook.service;

import jpabook.domain.Delivery;
import jpabook.domain.Member;
import jpabook.domain.Order;
import jpabook.domain.OrderItem;
import jpabook.domain.item.Item;
import jpabook.repository.ItemRepository;
import jpabook.repository.MemberRepository;
import jpabook.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * 주문
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 엔티티 조회
        Item item = itemRepository.findOne(itemId);
        Member member = memberRepository.findOne(memberId);

        // 배송정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .orderPrice(item.getPrice())
                .count(count)
                .build();

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        // 주문 저장
        orderRepository.save(order);

        return order.getId();
    }

    // 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        order.cancel();
    }

    // 검색
//    public List<Order> orderSearch(OrderSearch orderSearch) {
//        return orderRepository.findAll(orderSearch);
//    }
}
