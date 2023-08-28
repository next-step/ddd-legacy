package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.MENU;
import static kitchenpos.fixture.MenuGroupFixture.MENU_GROUP;
import static kitchenpos.fixture.OrderFixture.*;
import static kitchenpos.fixture.OrderTableFixture.ORDER_TABLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

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
    @DisplayName("주문 종류가 있어야 한다.")
    void menuTypeExists() {
        // given
        Order expected = ORDER_EAT_IN(OrderStatus.WAITING);

        // when
        expected.setType(null);

        // then
        assertThatThrownBy(() -> orderService.create(expected))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 리스트가 있어야 한다.")
    void menuListNotEmpty() {
        // given
        Order expected = ORDER_EAT_IN(OrderStatus.WAITING);

        // when
        expected.setOrderLineItems(null);

        // then
        assertThatThrownBy(() -> orderService.create(expected))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"takeout", "delivery"})
    @DisplayName("매장 식사가 아닌 경우 메뉴 개수는 0 이상이다.")
    void takeOutAndDeliveryMenuLineGreaterThanOrEqualToZero(String typeValue) {
        // given
        Order expected = typeValue.equals("takeout") ?
                ORDER_TAKEOUT(OrderStatus.WAITING) : ORDER_DELIVERY(OrderStatus.WAITING);
        expected.setType(typeValue.equals("takeout") ? OrderType.TAKEOUT : OrderType.DELIVERY);
        Menu menu = MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));

        // when
        expected.getOrderLineItems().forEach(item -> item.setQuantity(-1));

        // then
        assertThatThrownBy(() -> orderService.create(expected))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문과 메뉴 금액이 같아야 한다.")
    void menuAndMenuLinePriceIsSame() {
        // given
        Order expected = ORDER_EAT_IN(OrderStatus.WAITING);
        Menu menu = MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        menu.setPrice(new BigDecimal(30000));

        // then
        assertThatThrownBy(() -> orderService.create(expected))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("배달인 경우 배달 주소가 있어야 한다.")
    void deliveryHasAddress() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.WAITING);
        Menu menu = MENU();
        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        expected.setDeliveryAddress(null);

        // then
        assertThatThrownBy(() -> orderService.create(expected))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("매장 식사인 경우 빈 매장 테이블이 있어야 한다.")
    void eatInHasEmptyOrderTable() {
        // given
        Order expected = ORDER_EAT_IN(OrderStatus.WAITING);
        Menu menu = MENU();
        OrderTable orderTable = ORDER_TABLE();

        given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(expected.getOrderTableId())).willReturn(Optional.of(orderTable));

        // when
        orderTable.setOccupied(false);

        // then
        assertThatThrownBy(() -> orderService.create(expected))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 내역이 있어야 한다.")
    void orderExists() {
        // given
        Order expected = new Order();

        // when & then
        assertThatThrownBy(() -> orderService.create(expected))
                .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("주문 상태가 '대기'여야 한다.")
    void orderStatusIsWaiting() {
        // given
        Order expected = ORDER_EAT_IN(OrderStatus.COMPLETED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when & then
        assertThatThrownBy(() -> orderService.accept(expected.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("'배달' 주문인 경우 딜리버리를 호출한다.")
    void callDeliveryForDelivery() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.WAITING);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when
        Order actual = orderService.accept(expected.getId());

        // then
        verify(kitchenridersClient, times(1))
                .requestDelivery(eq(expected.getId()),any(BigDecimal.class), anyString());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문이 들어온 걸 확인하면 주문 상태를 '주문 접수'로 처리한다.")
    void changeStatusToAccepted(String typeName) {
        // given
        Order expected = ORDER_EAT_IN(OrderStatus.WAITING);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when
        Order actual = orderService.accept(expected.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("주문 상태가 '주문 접수'여야 한다.")
    void statusIsAccepted() {
        // given
        Order expected = ORDER_EAT_IN(OrderStatus.COMPLETED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when && then
        assertThatThrownBy(() -> orderService.serve(expected.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("완료되면 주문 상태를 '요리 완료'로 처리한다.")
    void changeStatusToServed() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.ACCEPTED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when
        Order actual = orderService.serve(expected.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @Test
    @DisplayName("주문 배달을 시작한다.")
    void startDelivery() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.SERVED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when
        Order actual = orderService.startDelivery(expected.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @Test
    @DisplayName("주문 타입이 주문 배달이어야 한다.")
    void typeShouldDeliveryTest(String typeName) {
        // given
        Order takeout = ORDER_TAKEOUT(OrderStatus.SERVED);
        Order eatIn = ORDER_EAT_IN(OrderStatus.SERVED);

        // when
        given(orderRepository.findById(takeout.getId())).willReturn(Optional.of(takeout));
        given(orderRepository.findById(eatIn.getId())).willReturn(Optional.of(eatIn));

        // then
        assertThatThrownBy(() -> orderService.startDelivery(takeout.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> orderService.startDelivery(eatIn.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 상태가 '요리 완료'여야 한다.")
    void statusShouldServed() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.COMPLETED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when && then
        assertThatThrownBy(() -> orderService.startDelivery(expected.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 배달을 완료한다.")
    void completeDelivery() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.DELIVERING);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when
        Order actual = orderService.completeDelivery(expected.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @Test
    @DisplayName("주문 상태가 '배달 중'이어야 한다.")
    void statusIsDelivering() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.COMPLETED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when && then
        assertThatThrownBy(() -> orderService.completeDelivery(expected.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("완료되면 주문 상태를 '배달 완료'로 처리한다.")
    void changeStatusToCompleted() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.DELIVERED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when
        Order actual = orderService.complete(expected.getId());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("주문 타입이 '배달'이면서 배달 상태가 '배달 완료'여야 한다.")
    void checkDeliveryStatus() {
        // given
        Order expected = ORDER_DELIVERY(OrderStatus.COMPLETED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when && then
        assertThatThrownBy(() -> orderService.complete(expected.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 타입이 테이크아웃이나 매장 식사인 경우 주문 상태가 '요리 완료'여야 한다.")
    void checkTakeOutAndEatInStatus() {
        // given
        Order expected1 = ORDER_TAKEOUT(OrderStatus.COMPLETED);
        given(orderRepository.findById(expected1.getId())).willReturn(Optional.of(expected1));
        Order expected2 = ORDER_EAT_IN(OrderStatus.COMPLETED);
        given(orderRepository.findById(expected2.getId())).willReturn(Optional.of(expected2));

        // when && then
        assertThatThrownBy(() -> orderService.complete(expected1.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> orderService.complete(expected2.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("주문 타입이 매장 식사 경우 해당 테이블에 추가 주문이 없는 경우 해당 테이블을 비운다.")
    void eatInClearTable() {
        // given
        Order expected = ORDER_EAT_IN(OrderStatus.SERVED);
        given(orderRepository.findById(expected.getId())).willReturn(Optional.of(expected));

        // when
        Order actual = orderService.complete(expected.getId());

        // then
        verify(orderRepository, times(1))
                .existsByOrderTableAndStatusNot(expected.getOrderTable(), OrderStatus.COMPLETED);
        assertThat(actual.getOrderTable().isOccupied()).isFalse();
        assertThat(actual.getOrderTable().getNumberOfGuests()).isZero();
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("모든 주문 내역을 가져온다.")
    void findAllOrder() {
        // given
        Order delivery = ORDER_DELIVERY(OrderStatus.DELIVERING);
        Order takeout = ORDER_TAKEOUT(OrderStatus.WAITING);
        Order eatIn = ORDER_EAT_IN(OrderStatus.SERVED);
        given(orderRepository.findAll()).willReturn(List.of(delivery, takeout, eatIn));

        // when
        List<Order> actual = orderService.findAll();

        // then
        verify(orderRepository, times(1)).findAll();
        assertThat(actual).containsExactly(delivery, takeout, eatIn);
    }
}
