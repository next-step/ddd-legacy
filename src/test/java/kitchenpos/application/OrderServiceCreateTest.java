package kitchenpos.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;

import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.domain.OrderType.TAKEOUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DisplayName("주문 생성")
class OrderServiceCreateTest extends OrderServiceTestSupport {
    @DisplayName("주문 종류가 지정되어야 한다.")
    @ParameterizedTest(name = "주문 종류가 null이 아니어야 한다.")
    @NullSource
    void nullType(OrderType type) {
        // given
        final var request = new Order();
        request.setType(type);

        // when
        assertThatThrownBy(() -> testService.create(request))
                // then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문세부항목이 비어 있지 않아야 한다.")
    @ParameterizedTest(name = "주문세부항목이 [{0}]이 아니어야 한다.")
    @NullAndEmptySource
    void nullOrEmptyOrderLines(List<OrderLineItem> orderLineItemRequests) {
        // given
        final var request = new Order();
        request.setType(DELIVERY);
        request.setOrderLineItems(orderLineItemRequests);

        // when
        assertThatThrownBy(() -> testService.create(request))
                // then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("중복된 메뉴가 없어야 한다.")
    @Test
    void duplicatedMenuRequests() {
        // given
        final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var duplicatedOrderLineItemRequest1 = new OrderLineItem();
        final var duplicatedOrderLineItemRequest2 = new OrderLineItem();
        duplicatedOrderLineItemRequest1.setMenuId(menuId);
        duplicatedOrderLineItemRequest2.setMenuId(menuId);

        final var request = new Order();
        request.setType(DELIVERY);
        request.setOrderLineItems(List.of(duplicatedOrderLineItemRequest1, duplicatedOrderLineItemRequest2));

        final var menuInRepo = new Menu();
        menuInRepo.setId(menuId);
        when(menuRepository.findAllByIdIn(List.of(menuId, menuId))).thenReturn(List.of(menuInRepo));

        // when
        assertThatThrownBy(() -> testService.create(request))
                // then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("등록된 메뉴를 요청해야 한다.")
    @Test
    void menuNotFound() {
        // given
        final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var orderLineItemRequest = new OrderLineItem();
        orderLineItemRequest.setMenuId(menuId);

        final var request = new Order();
        request.setType(DELIVERY);
        request.setOrderLineItems(List.of(orderLineItemRequest));

        final var menuInRepo = new Menu();
        menuInRepo.setId(menuId);
        when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> testService.create(request))
                // then
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("비공개 메뉴가 포함되지 않아야 한다.")
    @Test
    void menuShouldBeDisplayed() {
        // given
        final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var orderLineItemRequest = new OrderLineItem();
        orderLineItemRequest.setMenuId(menuId);
        orderLineItemRequest.setQuantity(1);

        final var request = new Order();
        request.setType(DELIVERY);
        request.setOrderLineItems(List.of(orderLineItemRequest));

        final var menuInRepo = new Menu();
        menuInRepo.setId(menuId);
        menuInRepo.setDisplayed(false);
        when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuInRepo));

        // when
        assertThatThrownBy(() -> testService.create(request))
                // then
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("요청한 메뉴 금액과 등록된 메뉴 금액이 같아야 한다.")
    @Test
    void diffBetweenRequestPriceAndMenuPrice() {
        // given
        final var requestPrice = new BigDecimal(10000);
        final var menuPrice = new BigDecimal(20000);

        final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        final var orderLineItemRequest = new OrderLineItem();
        orderLineItemRequest.setMenuId(menuId);
        orderLineItemRequest.setQuantity(1);
        orderLineItemRequest.setPrice(requestPrice);

        final var request = new Order();
        request.setType(DELIVERY);
        request.setOrderLineItems(List.of(orderLineItemRequest));

        final var menuInRepo = new Menu();
        menuInRepo.setId(menuId);
        menuInRepo.setDisplayed(true);
        menuInRepo.setPrice(menuPrice);
        when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
        when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuInRepo));

        // when
        assertThatThrownBy(() -> testService.create(request))
                // then
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달")
    @Nested
    class Delivery {
        @DisplayName("주문세부항목 개수는 음수가 아니어야 한다.")
        @Test
        void negativeOrderLineItemQuantity() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var orderLineItemRequest = new OrderLineItem();
            orderLineItemRequest.setMenuId(menuId);
            orderLineItemRequest.setQuantity(-1);

            final var request = new Order();
            request.setType(DELIVERY);
            request.setOrderLineItems(List.of(orderLineItemRequest));

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("배달 타입은 배달 주소가 입력되어야 한다.")
        @ParameterizedTest(name = "입력한 배달주소가 [{0}]이 아니어야 한다.")
        @NullAndEmptySource
        void nullOrEmptyDeliveryAddress(String deliveryAddress) {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var orderLineItemRequest = new OrderLineItem();
            orderLineItemRequest.setMenuId(menuId);
            orderLineItemRequest.setQuantity(1);
            orderLineItemRequest.setPrice(new BigDecimal(10000));

            final var request = new Order();
            request.setType(DELIVERY);
            request.setOrderLineItems(List.of(orderLineItemRequest));
            request.setDeliveryAddress(deliveryAddress);

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            menuInRepo.setDisplayed(true);
            menuInRepo.setPrice(new BigDecimal(10000));
            when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
            when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuInRepo));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문을 생성할 수 있다.")
        @Test
        void create() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            final var orderLineItemRequest = new OrderLineItem();
            orderLineItemRequest.setMenuId(menuId);
            orderLineItemRequest.setQuantity(1);
            orderLineItemRequest.setPrice(new BigDecimal(10000));

            final var request = new Order();
            request.setType(DELIVERY);
            request.setOrderLineItems(List.of(orderLineItemRequest));
            request.setDeliveryAddress("서울시 송파구");

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            menuInRepo.setDisplayed(true);
            menuInRepo.setPrice(new BigDecimal(10000));

            when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
            when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuInRepo));
            when(orderRepository.save(any())).thenAnswer((invocation -> invocation.getArgument(0)));

            // when
            final var result = testService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getType()).isEqualTo(DELIVERY),
                    () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(result.getOrderDateTime()).isNotNull(),
                    () -> assertThat(result.getOrderLineItems())
                            .extracting("menu.id", "quantity")
                            .containsExactly(tuple(UUID.fromString("11111111-1111-1111-1111-111111111111"), 1L)),
                    () -> assertThat(result.getDeliveryAddress())
                            .isEqualTo("서울시 송파구")
            );
        }
    }

    @DisplayName("매장식사")
    @Nested
    class EatIn {
        @DisplayName("등록된 매장테이블을 선택해야 한다.")
        @Test
        void orderTableNotFound() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var orderTableId = UUID.fromString("22222222-2222-2222-2222-222222222222");

            final var orderLineItemRequest = new OrderLineItem();
            orderLineItemRequest.setMenuId(menuId);
            orderLineItemRequest.setQuantity(1);
            orderLineItemRequest.setPrice(new BigDecimal(10000));

            final var request = new Order();
            request.setType(EAT_IN);
            request.setOrderLineItems(List.of(orderLineItemRequest));
            request.setOrderTableId(orderTableId);

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            menuInRepo.setDisplayed(true);
            menuInRepo.setPrice(new BigDecimal(10000));

            when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
            when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuInRepo));
            when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.empty());

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("선택한 매장테이블은 차지된 상태여야 한다.")
        @Test
        void orderTableShouldBeOccupied() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var orderTableId = UUID.fromString("22222222-2222-2222-2222-222222222222");

            final var orderLineItemRequest = new OrderLineItem();
            orderLineItemRequest.setMenuId(menuId);
            orderLineItemRequest.setQuantity(1);
            orderLineItemRequest.setPrice(new BigDecimal(10000));

            final var request = new Order();
            request.setType(EAT_IN);
            request.setOrderLineItems(List.of(orderLineItemRequest));
            request.setOrderTableId(orderTableId);

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            menuInRepo.setDisplayed(true);
            menuInRepo.setPrice(new BigDecimal(10000));

            final var tableInRepo = new OrderTable();
            tableInRepo.setOccupied(false);

            when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
            when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuInRepo));
            when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(tableInRepo));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문을 생성할 수 있다.")
        @Test
        void create() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var orderTableId = UUID.fromString("22222222-2222-2222-2222-222222222222");

            final var orderLineItemRequest = new OrderLineItem();
            orderLineItemRequest.setMenuId(menuId);
            orderLineItemRequest.setQuantity(1);
            orderLineItemRequest.setPrice(new BigDecimal(10000));

            final var request = new Order();
            request.setType(EAT_IN);
            request.setOrderLineItems(List.of(orderLineItemRequest));
            request.setOrderTableId(orderTableId);

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            menuInRepo.setDisplayed(true);
            menuInRepo.setPrice(new BigDecimal(10000));

            final var tableInRepo = new OrderTable();
            tableInRepo.setId(orderTableId);
            tableInRepo.setOccupied(true);

            when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
            when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuInRepo));
            when(orderTableRepository.findById(orderTableId)).thenReturn(Optional.of(tableInRepo));
            when(orderRepository.save(any())).thenAnswer((invocation -> invocation.getArgument(0)));

            // when
            final var result = testService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getType()).isEqualTo(EAT_IN),
                    () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(result.getOrderDateTime()).isNotNull(),
                    () -> assertThat(result.getOrderLineItems())
                            .extracting("menu.id", "quantity")
                            .containsExactly(tuple(UUID.fromString("11111111-1111-1111-1111-111111111111"), 1L)),
                    () -> assertThat(result.getOrderTable().getId())
                            .isEqualTo(UUID.fromString("22222222-2222-2222-2222-222222222222"))
            );
        }
    }

    @DisplayName("포장")
    @Nested
    class Takeout {
        @DisplayName("주문세부항목 개수는 음수가 아니어야 한다.")
        @Test
        void negativeOrderLineItemQuantity() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");
            final var orderLineItemRequest = new OrderLineItem();
            orderLineItemRequest.setMenuId(menuId);
            orderLineItemRequest.setQuantity(-1);

            final var request = new Order();
            request.setType(TAKEOUT);
            request.setOrderLineItems(List.of(orderLineItemRequest));

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));

            // when
            assertThatThrownBy(() -> testService.create(request))
                    // then
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문을 생성할 수 있다.")
        @Test
        void create() {
            // given
            final var menuId = UUID.fromString("11111111-1111-1111-1111-111111111111");

            final var orderLineItemRequest = new OrderLineItem();
            orderLineItemRequest.setMenuId(menuId);
            orderLineItemRequest.setQuantity(1);
            orderLineItemRequest.setPrice(new BigDecimal(10000));

            final var request = new Order();
            request.setType(TAKEOUT);
            request.setOrderLineItems(List.of(orderLineItemRequest));

            final var menuInRepo = new Menu();
            menuInRepo.setId(menuId);
            menuInRepo.setDisplayed(true);
            menuInRepo.setPrice(new BigDecimal(10000));

            when(menuRepository.findAllByIdIn(List.of(menuId))).thenReturn(List.of(menuInRepo));
            when(menuRepository.findById(menuId)).thenReturn(Optional.of(menuInRepo));
            when(orderRepository.save(any())).thenAnswer((invocation -> invocation.getArgument(0)));

            // when
            final var result = testService.create(request);

            // then
            assertAll(
                    () -> assertThat(result.getId()).isNotNull(),
                    () -> assertThat(result.getType()).isEqualTo(TAKEOUT),
                    () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(result.getOrderDateTime()).isNotNull(),
                    () -> assertThat(result.getOrderLineItems())
                            .extracting("menu.id", "quantity")
                            .containsExactly(tuple(UUID.fromString("11111111-1111-1111-1111-111111111111"), 1L))
            );
        }
    }
}
