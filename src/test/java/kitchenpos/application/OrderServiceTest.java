package kitchenpos.application;

import static kitchenpos.fixture.OrderFixture.createDeliveryOrder;
import static kitchenpos.fixture.OrderFixture.createEatInOrder;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderFixture.createTakeOutOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("주문 테스트")
public class OrderServiceTest extends OrderServiceFixture {

    @DisplayName("주문 등록시")
    @Nested
    class Order_create_of {
        Order request = createOrder();

        @DisplayName("주문 타입은 필수 이다.")
        @Test
        void orderTypeIsEssential() {
            request.setType(null);
            assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(request)
            );
        }

        @DisplayName("주문 항목은")
        @Nested
        class OrderItems_of {
            OrderLineItem orderLineItem = createOrderLineItem();

            @ParameterizedTest(name = "{0}이면 안된다.")
            @MethodSource("kitchenpos.application.OrderServiceTest#nullAndEmptyOrderLienItemList")
            void orderLineIsEssential(List<OrderLineItem> orderLineItems) {
                request.setOrderLineItems(orderLineItems);
                assertThatIllegalArgumentException().isThrownBy(() ->
                        orderService.create(request)
                );
            }

            @Test
            @DisplayName("주문 항목에 포함된 메뉴는 등록되어 있어야한다.")
            void orderItem_has_registMenu() {

                orderLineItem.setMenuId(null);

                request.setOrderLineItems(List.of(orderLineItem));

                assertThatExceptionOfType(NoSuchElementException.class)
                        .isThrownBy(() -> orderService.create(request));
            }

            @Test
            @DisplayName("주문 항목에 포함된 메뉴만 가능하다.")
            void createdMenuRegisteredOrder() {
                request.setOrderLineItems(List.of(orderLineItem));

                assertThatExceptionOfType(NoSuchElementException.class)
                        .isThrownBy(() -> orderService.create(request));
            }

            @Test
            @DisplayName("주문 항목에 포함된 메뉴의 메뉴가 숨겨져 있으면 안된다.")
            void orderItem_has_a_no_displayed_menu() {
                OrderLineItem orderLineItem = createOrderLineItem();
                orderLineItem.getMenu().setDisplayed(false);
                request.setOrderLineItems(Collections.singletonList(orderLineItem));

                assertThatIllegalStateException().isThrownBy(() ->
                        orderService.create(request)
                );
            }

            @ParameterizedTest(name = "주문 유형이 매장 식사가 아닌 경우 주문 항목의 수량은 0개 이상 이어야 한다.")
            @MethodSource("kitchenpos.application.OrderServiceTest#notEatInOrder")
            void not_EatInOrder_Is_Quantity_Not_LessThen_Zero(Order request) {
                orderLineItem.setQuantity(-1);
                List<OrderLineItem> orderLineItems = List.of(orderLineItem);
                request.setOrderLineItems(orderLineItems);

                assertThatIllegalArgumentException()
                        .isThrownBy(() -> orderService.create(request));
            }

            @ParameterizedTest(name= "주문항목의 메뉴의 가격은 주문항목의 가격과 같아야 등록이 가능하다.")
            @ValueSource(ints = {-1, 2})
            void orderItemPrice_IsEqual_MenuPrice(int price) {
                OrderLineItem orderLineItem = createOrderLineItem();
                orderLineItem.getMenu().setPrice(BigDecimal.valueOf(1));
                orderLineItem.setPrice(BigDecimal.valueOf(price));
                request.setOrderLineItems(Collections.singletonList(orderLineItem));

                assertThatIllegalArgumentException().isThrownBy(() ->
                        orderService.create(request)
                );
            }
        }

        @Nested
        @DisplayName("주문 유형이 배달 주문 일때")
        class orderType_of_delivery {
            @ParameterizedTest(name = "배달주소가 필수여야 한다.")
            @NullAndEmptySource
            void deliveryOrder_is_deliveryAddress_is_essential(String deliveryAddress) {
                Order request = createDeliveryOrder();
                request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
                request.setDeliveryAddress(deliveryAddress);

                assertThatIllegalArgumentException().isThrownBy(() ->
                        orderService.create(request)
                );
            }
        }

        @Nested
        @DisplayName("매장 식사 주문 일때")
        class orderType_of_eat_in {

            @DisplayName("주문 테이블이 반드시 필요하다.")
            @Test
            void eat_in_order_has_orderTable_essential() {
                Order request  = createEatInOrder();
                request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));

                assertThatExceptionOfType(NoSuchElementException.class)
                        .isThrownBy(() -> orderService.create(request));
            }

            @DisplayName("사용중인 오더 테이블이 아닌경우 등록이 불가능하다.")
            @Test
            void eat_in_order_has_orderTable_is_no_occupied() {
                Order request  = createEatInOrder();
                request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
                OrderTable orderTable = createOrderTable();
                orderTable.setOccupied(false);
                request.setOrderTable(orderTable);
                request.setOrderTableId(orderTable.getId());

                assertThatIllegalStateException().isThrownBy(
                        () -> orderService.create(request)
                );
            }
        }
    }

    @Test
    @DisplayName("주문을 등록 한다.")
    void create() {
        Order request = createEatInOrder();
        request.setOrderLineItems(Collections.singletonList(createOrderLineItem()));
        OrderTable orderTable = createOrderTable();
        request.setOrderTable(orderTable);
        request.setOrderTableId(orderTable.getId());

        Order createOrder = orderService.create(request);

        assertAll(
                () -> assertThat(createOrder.getId()).isNotNull(),
                () -> assertThat(createOrder.getOrderTable().getId()).isEqualTo(orderTable.getId()),
                () -> assertThat(createOrder.getType()).isEqualTo(OrderType.EAT_IN),
                () -> assertThat(createOrder.getStatus()).isEqualTo(OrderStatus.WAITING)
        );
    }

    @DisplayName("주문 수락은(을)")
    @Nested
    class save_with_orderAccept {
        @Test
        @DisplayName("주문 상태가 대기 상태가 아니어야 한다.")
        void accept_before_status_is_waiting() {
            final Order request = createOrder();
            Order createdOrder = orderRepository.save(request);
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderService.accept(createdOrder.getId()));
        }

        @Test
        @DisplayName("존재하지 않는 주문을 수락 할수 없다.")
        void accept_is_existOrder() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.accept(UUID.randomUUID()));
        }

        @Test
        @DisplayName("성공 한다.")
        void accept() {
            final Order createdOrder = 매장주문이_등록되어_있음();

            final Order acceptOrder = orderService.accept(createdOrder.getId());

            assertThat(acceptOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }
    }


    @DisplayName("주문 제공은(을)")
    @Nested
    class save_with_orderServe {
        @Test
        @DisplayName("주문 상태가 접수상태 이어야 한다.")
        void serve_before_status_is_accepted() {
            final Order createdOrder = 매장주문이_등록되어_있음();

            assertThatIllegalStateException().isThrownBy(() ->
                    orderService.serve(createdOrder.getId())
            );
        }

        @Test
        @DisplayName("성공 한다.")
        void serve() {
            Order createdOrder = 주문접수된_배달주문();

            final Order serve = orderService.serve(createdOrder.getId());

            assertThat(serve.getStatus()).isEqualTo(OrderStatus.SERVED);
        }
    }

    @DisplayName("주문 배달은(을)")
    @Nested
    class save_with_orderStartDelivery {
        @Test
        @DisplayName("주문이 등록 되어 있는 주문만 가능 하다.")
        void startDelivery_is_not_noSearchOrder() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.startDelivery(UUID.randomUUID()));
        }

        @Test
        @DisplayName("배달주문만 가능하다.")
        void startDelivery_is_orderType_is_not_delivered() {
            final Order createdOrder = 배달주문이_등록되어_있음();
            assertThatIllegalStateException().isThrownBy(() ->
                    orderService.startDelivery(createdOrder.getId())
            );
        }

        @Test
        @DisplayName("주문 제공 상태만 가능하다.")
        void startDelivery_is_beforeState_is_served() {
            final Order createdOrder = 배달주문이_등록되어_있음();
            assertThatIllegalStateException().isThrownBy(() ->
                    orderService.startDelivery(createdOrder.getId())
            );
        }

        @Test
        @DisplayName("성공 한다.")
        void startDelivery() {
            Order createdOrder = 주문제공된_배달주문();

            final Order order = orderService.startDelivery(createdOrder.getId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }
    }

    @DisplayName("배달 완료은(을)")
    @Nested
    class save_with_completeDelivery {
        @Test
        @DisplayName("주문이 등록 되어 있는 주문만 배달완료가 가능 하다.")
        void completeDelivery_is_not_noSearchOrder() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.completeDelivery(UUID.randomUUID()));
        }


        @Test
        @DisplayName("주문 유형이 배달이 아니면 배달완료 상태로 변경이 불가능 하다.")
        void completeDelivery_is_orderType_is_not_delivered() {
            Order createdOrder = 매장주문이_등록되어_있음();

            assertThatIllegalStateException().isThrownBy(() ->
                    orderService.completeDelivery(createdOrder.getId())
            );
        }

        @Test
        @DisplayName("배달중 상태가 아니면 배달 완료 상태로 변경이 불가능하다.")
        void completeDelivery_is_beforeState_is_served() {
            Order createdOrder = 배달주문이_등록되어_있음();

            assertThatIllegalStateException().isThrownBy(() ->
                    orderService.completeDelivery(createdOrder.getId())
            );
        }

        @Test
        @DisplayName("성공 한다.")
        void completeDelivery() {
            Order createdOrder = 배달시작된_배달주문();

            final Order order = orderService.completeDelivery(createdOrder.getId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

    }

    @DisplayName("주문 완료은(을)")
    @Nested
    class save_with_OrderComplete {
        @Test
        @DisplayName("등록 되어 있는 주문만 가능 하다.")
        void complete_is_not_noSearchOrder() {
            assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> orderService.complete(UUID.randomUUID()));
        }

        @DisplayName("배달 주문은(울)")
        @Nested
        class deliveryOrder_Complete {

            @Test
            @DisplayName("배달완료 상태에서만 가능하다.")
            void deliveryOrder_complete_is_beforeState_is_delivered() {
                Order deliveryOrder = 배달주문이_등록되어_있음();
                assertThatIllegalStateException()
                        .isThrownBy(() -> orderService.complete(deliveryOrder.getId()));
            }

            @Test
            @DisplayName("완료한다.")
            void deliveryOrderComplete() {
                Order deliveryOrder = 배달완료된_배달주문();

                final Order completeOrder = orderService.complete(deliveryOrder.getId());

                assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

        }

        @DisplayName("포장 주문은")
        @Nested
        class takeOutOrder_Complete {

            @Test
            @DisplayName("주문 제공 상태에서만 가능하다.")
            void takeOutOrder_is_beforeState_is_delivered() {
                Order takeOutOrder = 포장주문이_등록되어_있음();
                assertThatIllegalStateException()
                        .isThrownBy(() -> orderService.complete(takeOutOrder.getId()));
            }

            @Test
            @DisplayName("완료한다.")
            void takeOutOrderComplete() {
                Order takeOutOrder = 포장주문이_등록되어_있음();
                주문을_주문제공_상태까지_진행(takeOutOrder);

                final Order completeOrder = orderService.complete(takeOutOrder.getId());

                assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }
        }

        @DisplayName("매장 주문은")
        @Nested
        class eatInOrder_Complete {
            @Test
            @DisplayName("주문 제공 상태에서만 가능하다.")
            void eatInOrder_is_beforeState_is_delivered() {
                Order eatInOrder = 매장주문이_등록되어_있음();
                assertThatIllegalStateException()
                        .isThrownBy(() -> orderService.complete(eatInOrder.getId()));
            }


            @Test
            @DisplayName("매장주문을 완료한다.")
            void eatInOrderComplete() {
                Order eatInOrder = 매장주문이_등록되어_있음();
                주문을_주문제공_상태까지_진행(eatInOrder);

                final Order completeOrder = orderService.complete(eatInOrder.getId());

                assertAll(
                        () -> assertThat(completeOrder.getOrderTable().getNumberOfGuests()).isEqualTo(0),
                        () -> assertThat(completeOrder.getOrderTable().isOccupied()).isFalse(),
                        () -> assertThat(completeOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                );
            }
        }
    }


    @Test
    @DisplayName("주문이 조회됨")
    void findAll() {
        포장주문이_등록되어_있음();
        배달주문이_등록되어_있음();
        매장주문이_등록되어_있음();

        final List<Order> orders = orderService.findAll();

        assertAll(
                () -> assertThat(orders).hasSize(3),
                () -> assertThat(orders)
                        .extracting("type")
                        .contains(OrderType.EAT_IN, OrderType.DELIVERY, OrderType.TAKEOUT)
        );
    }

    public static Stream<Arguments> nullAndEmptyOrderLienItemList() {
       return Stream.of(
                Arguments.of(Named.of("null인 주문항목" ,null))
                , Arguments.of(Named.of("비어있는 주문항목", new ArrayList<>()))
        );
    }

    private static Stream<Order> notEatInOrder() {
        return Stream.of(createDeliveryOrder(), createTakeOutOrder());
    }

}
