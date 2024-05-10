package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @Nested
    @DisplayName("주문 생성")
    class OrderCreation {
        @Test
        @DisplayName("주문의 유형이 null이면 예외가 발생한다.")
        void shouldThrowExceptionIfOrderTypeIsNull() {
            // given
            Order 주문_유형_없는_주문 = OrderFixture.주문_생성(null);

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(주문_유형_없는_주문))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문내역 항목이 null이거나 비어있으면 예외가 발생한다.")
        @ParameterizedTest(name = "orderLineItems = {0}")
        @NullAndEmptySource
        void shouldThrowExceptionIfOrderItemsAreNullOrAbsent(List<OrderLineItem> orderLineItems) {
            // given
            Order 주문내역_항목_없는_주문 = OrderFixture.주문_생성(OrderType.EAT_IN, orderLineItems);

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(주문내역_항목_없는_주문))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목에 속한 메뉴 갯수와 실제 존재하는 메뉴 갯수가 다르면 예외가 발생한다.")
        @Test
        void shouldThrowExceptionIfOrderItemsCountMismatch() {
            // given
            Menu 기본_메뉴 = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN, List.of(
                OrderFixture.주문_항목_생성(기본_메뉴.getId()),
                OrderFixture.주문_항목_생성(기본_메뉴.getId())
            ));

            given(menuRepository.findAllByIdIn(any()))
                .willReturn(List.of(기본_메뉴));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("매장 내 식사가 아닐 때 주문 항목의 수량이 0보다 작으면 예외가 발생한다.")
        @Test
        void shouldThrowExceptionIfOrderItemCountLessThanZeroForNonEatIn() {
            // given
            Menu 기본_메뉴 = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.DELIVERY, List.of(
                OrderFixture.주문_항목_생성(기본_메뉴.getId(), -1L)
            ));

            given(menuRepository.findAllByIdIn(any()))
                .willReturn(List.of(기본_메뉴));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문항목에 해당하는 메뉴가 존재하지 않다면 예외가 발생한다.")
        @Test
        void shouldThrowExceptionWhenOrderItemMenuDoesNotExist() {
            // given
            Menu 기본_메뉴 = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN, List.of(
                OrderFixture.주문_항목_생성(기본_메뉴.getId())
            ));

            given(menuRepository.findAllByIdIn(any()))
                .willReturn(List.of(기본_메뉴));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(request))
                      .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("메뉴가 숨김처리 되어있다면 있다면 예외가 발생한다.")
        @Test
        void shouldThrowExceptionIfOrderingHiddenMenu() {
            // given
            Menu 기본_메뉴 = MenuFixture.기본_메뉴();
            기본_메뉴.setDisplayed(false);
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN, List.of(
                OrderFixture.주문_항목_생성(기본_메뉴.getId())
            ));

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(기본_메뉴));
            given(menuRepository.findById(기본_메뉴.getId())).willReturn(Optional.of(기본_메뉴));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(request))
                      .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("메뉴의 가격과 주문항목의 가격이 다르다면 예외가 발생한다.")
        @Test
        void shouldThrowExceptionIfOrderPriceMismatch() {
            // given
            Menu 만원짜리_메뉴 = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN, List.of(
                OrderFixture.주문_항목_생성(만원짜리_메뉴.getId(), 1L, new BigDecimal(20_000L))
            ));

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(만원짜리_메뉴));
            given(menuRepository.findById(any())).willReturn(Optional.of(만원짜리_메뉴));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("배달일때 배달주소가 비어있거나 null이면 예외가 발생한다.")
        @NullAndEmptySource
        @ParameterizedTest
        void shouldThrowExceptionIfDeliveryAddressIsEmptyOrNull(String deliveryAddress) {
            // given
            Menu 기본_메뉴 = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.DELIVERY, List.of(
                OrderFixture.주문_항목_생성(기본_메뉴.getId(), 1L, new BigDecimal(10_000L))
            ));
            request.setDeliveryAddress(deliveryAddress);

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(기본_메뉴));
            given(menuRepository.findById(any())).willReturn(Optional.of(기본_메뉴));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(request))
                      .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("매장 내 식사일 때 주문 테이블이 없으면 예외가 발생한다.")
        @Test
        void shouldThrowExceptionIfNoOrderTableForEatIn() {
            // given
            Menu 기본_메뉴 = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN, List.of(
                OrderFixture.주문_항목_생성(기본_메뉴.getId(), 1L, new BigDecimal(10_000L))
            ));

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(기본_메뉴));
            given(menuRepository.findById(any())).willReturn(Optional.of(기본_메뉴));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(request))
                      .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("매장 내 식사일 때 주문 테이블이 사용 중이 아니라면 예외가 발생한다.")
        @Test
        void shouldThrowExceptionIfOrderTableNotInUseForEatIn() {
            // given
            Menu 기본_메뉴 = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN, List.of(
                OrderFixture.주문_항목_생성(기본_메뉴.getId(), 1L, new BigDecimal(10_000L))
            ));
            OrderTable 미사용_테이블 = OrderFixture.주문_테이블_생성();

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(기본_메뉴));
            given(menuRepository.findById(any())).willReturn(Optional.of(기본_메뉴));
            given(orderTableRepository.findById(any())).willReturn(Optional.of(미사용_테이블));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.create(request))
                      .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("매장 내 식사 주문을 생성할 수 있다.")
        @Test
        void shouldSuccessfullyCreateEatInOrder() {
            // given
            Menu menu = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN, List.of(
                OrderFixture.주문_항목_생성(menu.getId(), 1L, new BigDecimal(10_000L))
            ));
            OrderTable orderTable = OrderFixture.주문_테이블_생성();
            orderTable.setOccupied(true);

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));
            given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

            // when
            when(orderRepository.save(any())).thenReturn(request);
            Order result = orderService.create(request);

            // then
            Assertions.assertThat(result.getId()).isNotNull();
            Assertions.assertThat(result.getType()).isEqualTo(OrderType.EAT_IN);
            Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @DisplayName("테이크 아웃 주문을 생성할 수 있다.")
        @Test
        void shouldSuccessfullyCreateTakeoutOrder() {
            // given
            Menu menu = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.TAKEOUT, List.of(
                OrderFixture.주문_항목_생성(menu.getId(), 1L, new BigDecimal(10_000L))
            ));

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
            given(menuRepository.findById(any())).willReturn(Optional.of(menu));

            // when
            when(orderRepository.save(any())).thenReturn(request);
            Order result = orderService.create(request);

            // then
            Assertions.assertThat(result.getId()).isNotNull();
            Assertions.assertThat(result.getType()).isEqualTo(OrderType.TAKEOUT);
            Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @DisplayName("배달 주문을 생성할 수 있다.")
        @Test
        void shouldSuccessfullyCreateDeliveryOrder() {
            // given
            Menu 기본_메뉴 = MenuFixture.기본_메뉴();
            Order request = OrderFixture.주문_생성(OrderType.DELIVERY, List.of(
                OrderFixture.주문_항목_생성(기본_메뉴.getId(), 1L, new BigDecimal(10_000L))
            ));
            request.setDeliveryAddress("주소");

            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(기본_메뉴));
            given(menuRepository.findById(any())).willReturn(Optional.of(기본_메뉴));

            // when
            when(orderRepository.save(any())).thenReturn(request);
            Order result = orderService.create(request);

            // then
            Assertions.assertThat(result.getId()).isNotNull();
            Assertions.assertThat(result.getType()).isEqualTo(OrderType.DELIVERY);
            Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING);
        }
    }

    @Nested
    @DisplayName("주문 수락")
    class OrderAcceptance {
        @DisplayName("주문이 존재하지 않으면 에러가 발생한다.")
        @Test
        void shouldThrowExceptionWhenAcceptingNonexistentOrder() {
            // given
            Order order = OrderFixture.주문_생성(OrderType.EAT_IN);
            given(orderRepository.findById(order.getId())).willReturn(Optional.empty());

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.accept(order.getId()))
                      .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("현재 주문 상태가 대기 상태가 아니면 예외가 발생한다.")
        @Test
        void shouldThrowExceptionWhenAcceptingNonWaitingOrder() {
            // given
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN);
            request.setStatus(OrderStatus.COMPLETED);
            given(orderRepository.findById(request.getId())).willReturn(Optional.of(request));

            // when & then
            Assertions.assertThatThrownBy(() -> orderService.accept(request.getId()))
                      .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("배달 주문일 때는 라이더를 호출해야 한다.")
        @Test
        void shouldCallRiderWhenAcceptingDeliveryOrder() {
            // given
            Menu menu = MenuFixture.기본_메뉴();
            OrderLineItem orderLineItem = OrderFixture.주문_항목_생성(menu.getId());
            Order order = OrderFixture.주문_생성(OrderType.DELIVERY, List.of(orderLineItem));
            order.setStatus(OrderStatus.WAITING);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            orderService.accept(order.getId());

            // then
            verify(kitchenridersClient, times(1)).requestDelivery(order.getId(), menu.getPrice(), order.getDeliveryAddress());
        }

        @DisplayName("매장 주문을 수락할 수 있다.")
        @Test
        void shouldAcceptEatInOrder() {
            // given
            Order request = OrderFixture.주문_생성(OrderType.EAT_IN);
            request.setStatus(OrderStatus.WAITING);
            given(orderRepository.findById(request.getId())).willReturn(Optional.of(request));

            // when
            Order result = orderService.accept(request.getId());

            // then
            Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("테이크아웃 주문을 수락할 수 있다.")
        @Test
        void shouldAcceptTakeoutOrder() {
            // given
            Order request = OrderFixture.주문_생성(OrderType.TAKEOUT);
            request.setStatus(OrderStatus.WAITING);
            given(orderRepository.findById(request.getId())).willReturn(Optional.of(request));

            // when
            Order result = orderService.accept(request.getId());

            // then
            Assertions.assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("배달 주문을 수락할 수 있다.")
        @Test
        void shouldAcceptDeliveryOrder() {
            // given
            Menu menu = MenuFixture.기본_메뉴();
            OrderLineItem orderLineItem = OrderFixture.주문_항목_생성(menu.getId());
            Order order = OrderFixture.주문_생성(OrderType.DELIVERY, List.of(orderLineItem));
            order.setStatus(OrderStatus.WAITING);

            given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

            // when
            orderService.accept(order.getId());

            // then
            Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }
    }

}