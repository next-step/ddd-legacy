package kitchenpos.application;

import static kitchenpos.application.MenuServiceFixture.menu;
import static kitchenpos.application.MenuServiceFixture.menus;
import static kitchenpos.application.OrderServiceFixture.order;
import static kitchenpos.application.OrderServiceFixture.orderLineItems;
import static kitchenpos.application.OrderServiceFixture.orders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderType;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    MenuRepository menuRepository;

    @InjectMocks
    OrderService orderService;

    @DisplayName("주문을 등록할 수 있다.")
    @Test
    void create() {

        //given
        Order order = order();
        order.setType(OrderType.DELIVERY);
        order.setOrderLineItems(orderLineItems());
        order.setDeliveryAddress("성동구 성수동");

        given(menuRepository.findAllByIdIn(any())).willReturn(menus());
        Menu menu = menu();
        menu.setDisplayed(true);
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        //when
        orderService.create(order);

        //then
        assertAll(
            () -> verify(menuRepository).findAllByIdIn(any()),
            () -> verify(menuRepository).findById(any()),
            () -> verify(orderRepository).save(any())
        );
    }

    @DisplayName("주문의 상태를 수락(ACCEPTED)으로 변경할 수 있다.")
    @Test
    void accept() {

        //given
        Order order = order();
        order.setStatus(OrderStatus.WAITING);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        //when
        Order result = orderService.accept(order.getId());

        //then
        assertAll(
            () -> verify(orderRepository).findById(any()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED)
        );
    }

    @DisplayName("주문의 상태를 제공(SERVED)됨으로 변경할 수 있다.")
    @Test
    void serve() {

        //given
        Order order = order();
        order.setStatus(OrderStatus.ACCEPTED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        //when
        Order result = orderService.serve(order.getId());

        //then
        assertAll(
            () -> verify(orderRepository).findById(any()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED)
        );
    }

    @DisplayName("주문의 상태를 배달시작(DELIVERING)으로 변경할 수 있다.")
    @Test
    void startDelivery() {

        //given
        Order order = order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.SERVED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        //when
        Order result = orderService.startDelivery(order.getId());

        //then
        assertAll(
            () -> verify(orderRepository).findById(any()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING)
        );
    }

    @DisplayName("주문의 상태를 배달완료(DELIVERED)으로 변경할 수 있다.")
    @Test
    void completeDelivery() {

        //given
        Order order = order();
        order.setStatus(OrderStatus.DELIVERING);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        //when
        Order result = orderService.completeDelivery(order.getId());

        //then
        assertAll(
            () -> verify(orderRepository).findById(any()),
            () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED)
        );
    }

    @DisplayName("주문의 상태를 완료(COMPLETED)으로 변경할 수 있다.")
    @Test
    void complete() {

        //given
        Order order = order();
        order.setType(OrderType.DELIVERY);
        order.setStatus(OrderStatus.DELIVERED);
        given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

        //when
        Order result = orderService.complete(order.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문의 목록을 조회할 수 있다.")
    @Test
    void findAll() {

        //given
        List<Order> orders = orders();
        given(orderRepository.findAll()).willReturn(orders);

        //when
        List<Order> result = orderService.findAll();

        //then
        assertThat(result).hasSize(orders.size());

    }
}
