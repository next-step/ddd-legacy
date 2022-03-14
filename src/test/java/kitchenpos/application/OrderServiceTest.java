package kitchenpos.application;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuFixture;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderFixture;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderLineItemFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableFixture;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static kitchenpos.domain.OrderFixture.DELIVERY_CHICKEN_ORDER;
import static kitchenpos.domain.OrderFixture.TAKEOUT_CHICKEN_ORDER;
import static kitchenpos.domain.OrderLineItemFixture.CHICKEN_ORDER_LINE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
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
    private OrderService sut;

    @Test
    @DisplayName("주문 생성 시 주문 유형이 누락되면 예외 발생")
    void createFailCase01() {
        // given
        Order order = new Order();

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("주문 생성 시 주문 메뉴를 입력하지 않으면 예외 발생")
    void createFailCase02() {
        // given
        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .build();

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("주문 생성 시 입력한 주문 메뉴가 실제 등록된 주문 메뉴가 아니면 예외 발생")
    void createFailCase03() {
        // given
        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .orderLineItems(Collections.singletonList(CHICKEN_ORDER_LINE))
                                  .build();

        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.emptyList());

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("홀식사가 아닌 주문 생성 시 주문 메뉴의 개수가 0개 미만이면 예외 발생")
    void createFailCase04() {
        // given
        OrderLineItem item = OrderLineItemFixture.builder()
                                                 .quantity(-1)
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .orderLineItems(Collections.singletonList(item))
                                  .build();


        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(new Menu()));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("주문 생성 시 비매 상태인 메뉴를 포함하면 예외 발생")
    void createFailCase05() {
        // given
        Menu menu = MenuFixture.builder()
                               .id(UUID.randomUUID())
                               .displayed(false)
                               .build();

        OrderLineItem item = OrderLineItemFixture.builder()
                                                 .menuId(menu.getId())
                                                 .quantity(0)
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .orderLineItems(Collections.singletonList(item))
                                  .build();


        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(new Menu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("주문 생성 시 입력한 주문 메뉴의 가격이 실제 메뉴 가격과 다르면 예외 발생")
    void createFailCase06() {
        // given
        Menu menu = MenuFixture.builder()
                               .id(UUID.randomUUID())
                               .price(BigDecimal.ZERO)
                               .displayed(true)
                               .build();

        OrderLineItem item = OrderLineItemFixture.builder()
                                                 .menuId(menu.getId())
                                                 .quantity(0)
                                                 .price(BigDecimal.ONE)
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .orderLineItems(Collections.singletonList(item))
                                  .build();


        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(new Menu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("배달 주문은 배달주소가 비어있으면 예외 발생")
    void createFailCase07() {
        // given
        Menu menu = MenuFixture.builder()
                               .id(UUID.randomUUID())
                               .price(BigDecimal.TEN)
                               .displayed(true)
                               .build();

        OrderLineItem item = OrderLineItemFixture.builder()
                                                 .menuId(menu.getId())
                                                 .quantity(0)
                                                 .price(BigDecimal.TEN)
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .orderLineItems(Collections.singletonList(item))
                                  .build();


        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(new Menu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        assertThatIllegalArgumentException().isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("홀식사 주문은 테이블 정보를 잘못 입력하면 예외 발생")
    void createFailCase08() {
        // given
        Menu menu = MenuFixture.builder()
                               .id(UUID.randomUUID())
                               .price(BigDecimal.TEN)
                               .displayed(true)
                               .build();

        OrderLineItem item = OrderLineItemFixture.builder()
                                                 .menuId(menu.getId())
                                                 .quantity(0)
                                                 .price(BigDecimal.TEN)
                                                 .build();

        OrderTable orderTable = OrderTableFixture.builder()
                                                 .id(UUID.randomUUID())
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.EAT_IN)
                                  .orderLineItems(Collections.singletonList(item))
                                  .orderTableId(orderTable.getId())
                                  .build();

        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(new Menu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.empty());

        // when
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("홀식사 주문에 입력한 테이블이 빈 상태면 예외 발생")
    void createFailCase09() {
        // given
        Menu menu = MenuFixture.builder()
                               .id(UUID.randomUUID())
                               .price(BigDecimal.TEN)
                               .displayed(true)
                               .build();

        OrderLineItem item = OrderLineItemFixture.builder()
                                                 .menuId(menu.getId())
                                                 .quantity(0)
                                                 .price(BigDecimal.TEN)
                                                 .build();

        OrderTable orderTable = OrderTableFixture.builder()
                                                 .id(UUID.randomUUID())
                                                 .empty(true)
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.EAT_IN)
                                  .orderLineItems(Collections.singletonList(item))
                                  .orderTableId(orderTable.getId())
                                  .build();

        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(new Menu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.create(order));
    }

    @Test
    @DisplayName("배달 주문 생성 성공")
    void createSuccessCase01() {
        // given
        Menu menu = MenuFixture.builder()
                               .id(UUID.randomUUID())
                               .price(BigDecimal.TEN)
                               .displayed(true)
                               .build();

        OrderLineItem item = OrderLineItemFixture.builder()
                                                 .menuId(menu.getId())
                                                 .quantity(0)
                                                 .price(BigDecimal.TEN)
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .orderLineItems(Collections.singletonList(item))
                                  .deliveryAddress("address")
                                  .build();


        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(new Menu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        // when
        sut.create(order);

        // then
        then(orderRepository).should().save(any());
    }

    @Test
    @DisplayName("홀식사 주문 생성 성공")
    void createSuccessCase02() {
        // given
        Menu menu = MenuFixture.builder()
                               .id(UUID.randomUUID())
                               .price(BigDecimal.TEN)
                               .displayed(true)
                               .build();

        OrderLineItem item = OrderLineItemFixture.builder()
                                                 .menuId(menu.getId())
                                                 .quantity(0)
                                                 .price(BigDecimal.TEN)
                                                 .build();

        OrderTable orderTable = OrderTableFixture.builder()
                                                 .id(UUID.randomUUID())
                                                 .empty(false)
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.EAT_IN)
                                  .orderLineItems(Collections.singletonList(item))
                                  .orderTableId(orderTable.getId())
                                  .build();

        given(menuRepository.findAllByIdIn(any())).willReturn(Collections.singletonList(new Menu()));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));
        given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

        // when
        sut.create(order);

        // then
        then(orderRepository).should().save(any());
    }

    @Test
    @DisplayName("주문 수락 상태로 변경 시 주문 ID가 잘못되어 있으면 오류")
    void acceptFail() {
        // given
        given(orderRepository.findById(any())).willReturn(Optional.empty());

        // when
        assertThatExceptionOfType(NoSuchElementException.class)
            .isThrownBy(() -> sut.accept(UUID.randomUUID()));
    }

    @Test
    @DisplayName("배달 주문을 수락하면 배달대행사에 배달 요청 후 주문 상태 변경")
    void acceptSuccess01() {
        // given
        given(orderRepository.findById(any())).willReturn(Optional.of(DELIVERY_CHICKEN_ORDER));

        // when
        Order actual = sut.accept(UUID.randomUUID());

        // then
        then(kitchenridersClient).should().requestDelivery(any(), any(), any());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("배달이 아니면 배달대행사에 배달 요청을 하지 않고 주문 상태만 변경")
    void acceptSuccess02() {
        // given
        given(orderRepository.findById(any())).willReturn(Optional.of(TAKEOUT_CHICKEN_ORDER));

        // when
        Order actual = sut.accept(UUID.randomUUID());

        // then
        then(kitchenridersClient).should(never()).requestDelivery(any(), any(), any());
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @Test
    @DisplayName("수락 상태가 아닌 주문을 수행하면 오류 발생")
    void serveFail() {
        // given
        given(orderRepository.findById(any())).willReturn(Optional.of(TAKEOUT_CHICKEN_ORDER));

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.serve(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문 수행 성공")
    void serveSuccess() {
        // given
        Order order = OrderFixture.builder()
                                  .status(OrderStatus.ACCEPTED)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        Order actual = sut.serve(UUID.randomUUID());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @ParameterizedTest
    @EnumSource(value = OrderType.class, names = {"TAKEOUT", "EAT_IN"})
    @DisplayName("배달 주문이 아니면 배달 시작 상태로 변경 불가능")
    void startDeliveryFail01(OrderType type) {
        // given
        Order order = OrderFixture.builder()
                                  .type(type)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.startDelivery(UUID.randomUUID()));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    @DisplayName("주문 수행 상태가 아니면 배달 시작 상태로 변경 불가능")
    void startDeliveryFail02(OrderStatus status) {
        // given
        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .status(status)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.startDelivery(UUID.randomUUID()));
    }

    @Test
    @DisplayName("배달 시작 성공")
    void startDeliverySuccess() {
        // given
        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .status(OrderStatus.SERVED)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        Order actual = sut.startDelivery(UUID.randomUUID());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERED", "COMPLETED"})
    @DisplayName("배달중인 주문이 아니면 배달 완료로 변경 불가능")
    void completeDeliveryFail(OrderStatus status) {
        // given
        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .status(status)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.completeDelivery(UUID.randomUUID()));
    }

    @Test
    @DisplayName("배달 종료 성공")
    void completeDeliverySuccess() {
        // given
        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .status(OrderStatus.DELIVERING)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        Order actual = sut.completeDelivery(UUID.randomUUID());

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "SERVED", "DELIVERING", "COMPLETED"})
    @DisplayName("배달 주문은 배달 완료 상태가 아니면 주문 완료 처리 불가능")
    void completeFail01(OrderStatus status) {
        // given
        Order order = OrderFixture.builder()
                                  .type(OrderType.DELIVERY)
                                  .status(status)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.complete(UUID.randomUUID()));
    }

    @ParameterizedTest
    @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "DELIVERING", "DELIVERED", "COMPLETED"})
    @DisplayName("포장 혹은 홀식사 주문은 주문 수행 상태일 때에만 종료 처리 가능")
    void completeFail02(OrderStatus status) {
        // given
        Order order = OrderFixture.builder()
                                  .type(OrderType.TAKEOUT)
                                  .status(status)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));

        // when
        assertThatIllegalStateException().isThrownBy(() -> sut.complete(UUID.randomUUID()));
    }

    @Test
    @DisplayName("홀식사 주문이 완료되었을 때 테이블 상태가 비어있지 않다면 빈 상태로 변경")
    void completeSuccess() {
        // given
        OrderTable orderTable = OrderTableFixture.builder()
                                                 .numberOfGuests(5)
                                                 .empty(false)
                                                 .build();

        Order order = OrderFixture.builder()
                                  .type(OrderType.EAT_IN)
                                  .status(OrderStatus.SERVED)
                                  .orderTable(orderTable)
                                  .build();

        given(orderRepository.findById(any())).willReturn(Optional.of(order));
        given(orderRepository.existsByOrderTableAndStatusNot(any(), eq(OrderStatus.COMPLETED)))
            .willReturn(false);

        // when
        Order actual = sut.complete(UUID.randomUUID());

        // then
        assertThat(actual.getOrderTable()).satisfies(table -> {
            assertThat(table.getNumberOfGuests()).isZero();
            assertThat(table.isEmpty()).isTrue();
        });

        assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }
}
