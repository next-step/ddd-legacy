package kitchenpos.application;

import kitchenpos.Fixtures;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static kitchenpos.Fixtures.주문_요청;
import static kitchenpos.Fixtures.주문_요청_매장;
import static kitchenpos.Fixtures.주문_요청_배달;
import static kitchenpos.Fixtures.주문_요청_포장;
import static kitchenpos.Fixtures.주문테이블_착석;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 서비스 테스트")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;


    @Test
    @DisplayName("메뉴에 없는 항목은 주문할 수 없다.")
    void orderLineItemMustExistInMenu() {
        // given
        Order order = 주문_요청_배달();
        // when
        when(menuRepository.findAllByIdIn(any()))
                .thenReturn(Collections.emptyList());
        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("노출되지 않은 메뉴는 주문할 수 없다.")
    void orderLineItemMustBeDisplayed() {
        // given
        Order order = 주문_요청();
        // when
        when(menuRepository.findAllByIdIn(any()))
                .thenReturn(Collections.singletonList(Fixtures.메뉴_생성_두마리_매콤_치킨_시험중()));
        when(menuRepository.findById(any()))
                .thenReturn(Optional.of(Fixtures.메뉴_생성_두마리_매콤_치킨_시험중()));
        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalStateException.class);
    }


    @Test
    @DisplayName("메뉴 가격이 제품 가격 * 수량의 합보다 작거나 같은 경우 주문할 수 없다.")
    void orderPriceMustBeGreaterThanOrEqualToSumOfMenuPriceAndQuantity() {
        // given
        Order order = 주문_요청();
        // when
        when(menuRepository.findAllByIdIn(any()))
                .thenReturn(Collections.singletonList(Fixtures.메뉴_생성_두마리_반_치킨()));
        when(menuRepository.findById(any()))
                .thenReturn(Optional.of(Fixtures.메뉴_생성_두마리_반_치킨()));
        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 유형과 주문 제품 값이 존재해야 한다.")
    void orderMustHaveOrderTypeAndOrderLineItems() {
        // given
        Order order = 주문_요청_배달();
        order.setType(null);
        order.setOrderLineItems(null);

        // when & then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("매장 식사가 아닌 경우, 주문 수량은 0 이상이어야 한다.")
    void deliveryOrderQuantityMustBeGreaterThanZero() {
        // given
        Order order = 주문_요청_배달();
        order.getOrderLineItems().get(0).setQuantity(-1);

        // when
        when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(Fixtures.메뉴_생성_두마리_치킨()));

        // then
        assertThatThrownBy(() -> orderService.create(order))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Nested
    @DisplayName("주문 - 매장식사")
    class EatIn {

        @Test
        @DisplayName("주문을 등록한다")
        void eatInOrderCreate() {
            // given
            Order order = 주문_요청_매장(주문테이블_착석());

            // when
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(Fixtures.메뉴_생성_두마리_치킨()));
            when(menuRepository.findById(any())).thenReturn(Optional.of(Fixtures.메뉴_생성_두마리_치킨()));
            when(orderTableRepository.findById(any())).thenReturn(Optional.of(주문테이블_착석()));
            when(orderRepository.save(any())).thenReturn(order);

            // then
            order = orderService.create(order);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        @DisplayName("매장 주문시 테이블이 존재하지 않으면 주문할 수 없다.")
        void eatInOrderMustHaveOrderTable() {
            Order order = 주문_요청_매장(주문테이블_착석());

            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(Fixtures.메뉴_생성_두마리_치킨()));
            when(menuRepository.findById(any())).thenReturn(Optional.of(Fixtures.메뉴_생성_두마리_치킨()));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문 상태를 완료 처리 할 경우 테이블을 비운다.")
        void eatInOrderStatusChangeToComplete() {
            // given
            Order order = 주문_요청_매장(주문테이블_착석());
            order.setStatus(OrderStatus.SERVED);

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));
            when(orderRepository.existsByOrderTableAndStatusNot(any(), any())).thenReturn(false);

            orderService.complete(order.getId());

            // then
            assertAll(
                    () -> assertThat(order.getOrderTable().isOccupied()).isFalse(),
                    () -> assertThat(order.getOrderTable().getNumberOfGuests()).isZero()
            );
        }

    }

    @Nested
    @DisplayName("주문 - 포장")
    class TakeOut {

        @Test
        @DisplayName("주문을 등록한다")
        void takeOutOrderCreate() {
            // given
            Order order = 주문_요청_포장();

            // when
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(Fixtures.메뉴_생성_두마리_치킨()));
            when(menuRepository.findById(any())).thenReturn(Optional.of(Fixtures.메뉴_생성_두마리_치킨()));
            when(orderRepository.save(any())).thenReturn(order);

            // then
            order = orderService.create(order);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        @DisplayName("포장 주문시 음식이 제공된 경우 주문 상태를 완료 처리한다.")
        void takeOutOrderStatusChangeToComplete() {
            // given
            Order order = 주문_요청_포장();
            order.setStatus(OrderStatus.SERVED);

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(order));

            orderService.complete(order.getId());

            // then
            assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

    }

    @Nested
    @DisplayName("주문 - 배달")
    class Delivery {

        @Test
        @DisplayName("주문을 등록한다")
        void deliveryOrderCreate() {
            // given
            Order order = 주문_요청_배달();

            // when
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(Fixtures.메뉴_생성_두마리_치킨()));
            when(menuRepository.findById(any())).thenReturn(Optional.of(Fixtures.메뉴_생성_두마리_치킨()));
            when(orderRepository.save(any())).thenReturn(order);

            // then
            order = orderService.create(order);
            assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        @DisplayName("배달 주문시 배달 주소가 존재해야 한다.")
        void deliveryOrderMustHaveDeliveryAddress() {
            // given
            Order order = 주문_요청_배달();
            order.setDeliveryAddress(null);

            // when
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(Fixtures.메뉴_생성_두마리_치킨()));
            when(menuRepository.findById(any())).thenReturn(Optional.of(Fixtures.메뉴_생성_두마리_치킨()));

            // then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }

    @Nested
    @DisplayName("주문 상태를 변경")
    class ChangeOrderStatusServe {

        @Test
        @DisplayName("변경하고자 하는 주문은 존재하는 주문이어야한다.")
        void orderMustExist() {
            // given
            Order 주문_요청 = 주문_요청();
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            // then
            assertThatThrownBy(() -> orderService.accept(주문_요청.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("주문 상태를 서빙 상태로 변경 할 수 있다.")
        void orderStatusChangeToServed() {
            // given
            Order 주문_요청 = 주문_요청();
            주문_요청.setStatus(OrderStatus.ACCEPTED);
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));
            // then
            주문_요청 = orderService.serve(주문_요청.getId());
            assertThat(주문_요청.getStatus()).isEqualTo(OrderStatus.SERVED);

        }

        @Test
        @DisplayName("주문 상태가 대기 중 (WAITING) 인 경우 접수상태로 변경 할 수 없다.")
        void orderStatusCannotChangeToAcceptedWhenStatusIsWaiting() {
            // given
            Order 주문_요청 = 주문_요청();
            주문_요청.setStatus(OrderStatus.WAITING);

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));

            // then
            assertThatThrownBy(() -> orderService.serve(주문_요청.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("주문 상태를 배달 중 (DELIVERING) 상태로 변경 할 수 있다.")
        void orderStatusChangeToDelivering() {
            // given
            Order 주문_요청 = 주문_요청_배달();
            주문_요청.setStatus(OrderStatus.SERVED);

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));

            // then
            주문_요청 = orderService.startDelivery(주문_요청.getId());
            assertThat(주문_요청.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        @DisplayName("주문 상태가 수락 상태가 아니라면 배달 상태로 변경할 수 없다.")
        void orderStatusMustBeAcceptedWhenChangeStatusToDelivery() {
            // given
            Order 주문_요청 = 주문_요청_배달();

            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));

            // then
            assertThatThrownBy(() -> orderService.startDelivery(주문_요청.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    @Nested
    @DisplayName("주문 상태를 배달 완료 상태로 변경")
    class ChangeOrderStatusDelivering {

        @Test
        @DisplayName("주문 상태를 배달 상태로 변경 할 수 있다.")
        void orderStatusChangeToDeliveryWhenStatusIsAccepted() {
            // given
            Order 주문_요청 = 주문_요청_배달();
            주문_요청.setStatus(OrderStatus.DELIVERING);
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));
            // then
            주문_요청 = orderService.completeDelivery(주문_요청.getId());
            assertThat(주문_요청.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }


        @Test
        @DisplayName("주문 유형이 배달 (DELIVERING) 상태 가 아니면 배달 완료 상태로 변경 할 수 없다.")
        void orderStatusMustBeAcceptedWhenChangeStatusToDelivery() {
            // given
            Order 주문_요청 = 주문_요청_배달();
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));
            // then
            assertThatThrownBy(() -> orderService.completeDelivery(주문_요청.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    @Nested
    @DisplayName("배달 주문")
    class DeliveryOrder {

        @Test
        @DisplayName("주문 상태를 배달 완료 상태로 변경 할 수 있다.")
        void orderStatusChangeToDelivered() {
            // given
            Order 주문_요청 = 주문_요청_배달();
            주문_요청.setStatus(OrderStatus.DELIVERED);
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));
            orderService.complete(주문_요청.getId());
            // then
            assertThat(주문_요청.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("주문 상태가 배달 중인 경우 배달 완료 상태로 변경 할 수 있다.")
        void orderStatusChangeToDeliveredWhenStatusIsDelivering() {
            // given
            Order 주문_요청 = 주문_요청_배달();
            주문_요청.setStatus(OrderStatus.DELIVERING);
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));

            // then
            assertThatThrownBy(() -> orderService.complete(주문_요청.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    @Nested
    @DisplayName("포장 주문")
    class TakeoutOrder {

        @Test
        @DisplayName("주문 유형이 포장 (TAKEOUT) 상태 인 경우, 주문 상태가 서빙 완료 (SERVED) 상태 이여야 한다. - 포장")
        void orderMustExistWhenChangeStatusToDelivered() {
            // given
            Order 주문_요청 = 주문_요청_포장();
            주문_요청.setStatus(OrderStatus.SERVED);
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));
            orderService.complete(주문_요청.getId());
            // then
            assertThat(주문_요청.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("주문 유형이 포장 (TAKEOUT) 상태 인 경우, 주문 상태가 서빙 완료 (SERVED) 상태 가 아닐때 예외가 발생한다 - 포장")
        void orderTypeMustBeTakeoutWhenChangeStatusToDelivered() {
            // given
            Order 주문_요청 = 주문_요청_포장();
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));
            // then
            assertThatThrownBy(() -> orderService.complete(주문_요청.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

    }

    @Nested
    @DisplayName("매장 식사")
    class EatinOrder {

        @Test
        @DisplayName("매장 식사 (EATIN) 일 경우 주문 상태를 완료로 변경시 테이블을 정리한다 - 매장")
        void orderTypeMustBeDeliveryWhenChangeStatusToDelivering() {
            // given
            Order 주문_요청 = 주문_요청_매장();
            주문_요청.setStatus(OrderStatus.SERVED);
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));
            // then
            주문_요청 = orderService.complete(주문_요청.getId());

            assertThat(주문_요청.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            assertThat(주문_요청.getOrderTable().getNumberOfGuests()).isZero();
            assertThat(주문_요청.getOrderTable().isOccupied()).isFalse();
        }

        @Test
        @DisplayName("주문 유형이 매장 식사 (EAT_IN) 상태 일 경우 주문 상태가 서빙 완료 (SERVED) 상태 가 아닐때 예외가 발생한다. - 매장")
        void orderStatusMustBeServedWhenChangeStatusToDelivered() {
            // given
            Order 주문_요청 = 주문_요청_매장();
            // when
            when(orderRepository.findById(any())).thenReturn(Optional.of(주문_요청));
            // then
            assertThatThrownBy(() -> orderService.complete(주문_요청.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Test
    @DisplayName("주문을 전체 조회 할 수 있다")
    void findAll() {
        // given
        List<Order> 주문_요청_목록 = 주문_목록_조회();
        // when
        when(orderRepository.findAll()).thenReturn(주문_요청_목록);
        List<Order> 응답_결과 = orderService.findAll();
        // then
        assertThat(응답_결과).hasSize(3);
    }

    public List<Order> 주문_목록_조회() {
        return new ArrayList<>(List.of(주문_요청_매장(), 주문_요청_배달(), 주문_요청_포장()));
    }

}