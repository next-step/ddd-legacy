package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.helper.InMemoryMenuRepository;
import kitchenpos.helper.InMemoryOrderRepository;
import kitchenpos.helper.InMemoryOrderTableRepository;
import kitchenpos.helper.MenuFixture;
import kitchenpos.helper.OrderFixture;
import kitchenpos.helper.OrderLineItemFixture;
import kitchenpos.helper.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderService testTarget;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        menuRepository = new InMemoryMenuRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        testTarget = new OrderService(
            orderRepository,
            menuRepository,
            orderTableRepository,
            kitchenridersClient
        );
    }

    @DisplayName("매장 주문 테스트")
    @Nested
    class EatInOrderTest {

        @DisplayName("주문 등록 테스트")
        @Nested
        class CreateTest {

            @DisplayName("매장 주문을 등록 할 수 있다.")
            @Test
            void test01() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE);
                Order request = OrderFixture.request(
                    OrderType.EAT_IN,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    orderTable.getId()
                );

                // when
                Order actual = testTarget.create(request);

                // then
                assertAll(
                    () -> assertThat(actual.getId()).isNotNull(),
                    () -> assertThat(actual.getType()).isEqualTo(OrderType.EAT_IN),
                    () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(actual.getOrderTable().getId()).isEqualTo(orderTable.getId()),
                    () -> assertThat(actual.getOrderTable().isOccupied()).isTrue(),
                    () -> assertThat(actual.getOrderLineItems())
                        .singleElement()
                        .matches(
                            orderLineItem -> orderLineItem.getMenu().getId().equals(menu.getId()))
                        .matches(orderLineItem -> orderLineItem.getQuantity() == 1L)
                );
            }

            @DisplayName("주문 타입없이 주문을 등록 할 수 없다.")
            @Test
            void test02() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE);
                Order request = OrderFixture.request(
                    null,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    orderTable.getId()
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("주문 아이템없이 주문을 등록 할 수 없다.")
            @Test
            void test03() {
                // given
                Order request = OrderFixture.request(
                    OrderType.EAT_IN,
                    Collections.emptyList()
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("존재하지 않는 메뉴를 주문 할 수 없다.")
            @Test
            void test04() {
                // given
                OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE);
                Order request = OrderFixture.request(
                    OrderType.EAT_IN,
                    List.of(OrderLineItemFixture.request(UUID.randomUUID(), 1,
                        BigDecimal.valueOf(6000))),
                    orderTable.getId()
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("감춰진 메뉴를 주문 할 수 없다.")
            @Test
            void test05() {
                // given
                Menu menu = menuRepository.save(MenuFixture.NO_DISPLAYED_MENU);
                OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE);
                Order request = OrderFixture.request(
                    OrderType.EAT_IN,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    orderTable.getId()
                );

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("매장 주문의 경우, 존재하지 않는 주문 테이블에 주문을 등록 할 수 없다.")
            @Test
            void test06() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                Order request = OrderFixture.request(
                    OrderType.EAT_IN,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    UUID.randomUUID()
                );

                // when & then
                assertThatExceptionOfType(NoSuchElementException.class)
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("매장 주문의 경우, 빈 테이블이 아닌 경우 주문을 등록 할 수 없다.")
            @Test
            void test07() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                OrderTable orderTable = orderTableRepository.save(OrderTableFixture.EMPTY_TABLE);
                Order request = OrderFixture.request(
                    OrderType.EAT_IN,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    orderTable.getId()
                );

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("메뉴의 가격과 주문 아이템의 가격이 다른 경우, 주문 할 수 없다.")
            @Test
            void test08() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                OrderTable orderTable = orderTableRepository.save(OrderTableFixture.OCCUPIED_TABLE);
                BigDecimal orderItemPrice = menu.getPrice().add(BigDecimal.valueOf(1000));
                Order request = OrderFixture.request(
                    OrderType.EAT_IN,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, orderItemPrice)),
                    orderTable.getId()
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }
        }

        @DisplayName("주문 수락 테스트")
        @Nested
        class AcceptTest {

            @DisplayName("주문을 수락 할 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.eatInOrder(
                    OrderStatus.WAITING,
                    OrderTableFixture.OCCUPIED_TABLE
                ));

                // when
                Order actual = testTarget.accept(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.ACCEPTED);
            }

            @DisplayName("주문이 대기 상태가 아닌 경우, 수락 할 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.eatInOrder(
                    OrderStatus.ACCEPTED,
                    OrderTableFixture.OCCUPIED_TABLE
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.accept(order.getId()));
            }
        }

        @DisplayName("주문 서빙 테스트")
        @Nested
        class ServeTest {

            @DisplayName("주문을 서빙 할 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.eatInOrder(
                    OrderStatus.ACCEPTED,
                    OrderTableFixture.OCCUPIED_TABLE
                ));

                // when
                Order actual = testTarget.serve(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.SERVED);
            }

            @DisplayName("수락 상태가 아닌 주문을 서빙 할 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.eatInOrder(
                    OrderStatus.WAITING,
                    OrderTableFixture.OCCUPIED_TABLE
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.serve(order.getId()));
            }
        }

        @DisplayName("배달 시작 테스트")
        @Nested
        class StartDeliveryTest {

            @DisplayName("매장 주문은 배달을 시작 할 수 없다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.eatInOrder(
                    OrderStatus.SERVED,
                    OrderTableFixture.OCCUPIED_TABLE
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.startDelivery(order.getId()));
            }
        }

        @DisplayName("주문 완료 테스트")
        @Nested
        class CompleteTest {

            @DisplayName("서빙된 주문을 완료 시킬 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.eatInOrder(
                    OrderStatus.SERVED,
                    OrderTableFixture.OCCUPIED_TABLE
                ));

                // when
                Order actual = testTarget.complete(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.COMPLETED);
                assertThat(actual.getOrderTable())
                    .matches(table -> table.getNumberOfGuests() == 0)
                    .matches(table -> !table.isOccupied());
            }

            @DisplayName("서빙되지 않은 주문을 완료 시킬 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.eatInOrder(
                    OrderStatus.ACCEPTED,
                    OrderTableFixture.OCCUPIED_TABLE
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.complete(order.getId()));
            }
        }
    }

    @DisplayName("배달 주문 테스트")
    @Nested
    class DeliveryOrderTest {

        @DisplayName("주문 등록 테스트")
        @Nested
        class CreatTest {

            @DisplayName("배달 주문을 등록 할 수 있다.")
            @Test
            void test01() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                Order request = OrderFixture.request(
                    OrderType.DELIVERY,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    "delivery address"
                );

                // when
                Order actual = testTarget.create(request);

                // then
                assertAll(
                    () -> assertThat(actual.getId()).isNotNull(),
                    () -> assertThat(actual.getType()).isEqualTo(OrderType.DELIVERY),
                    () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(actual.getDeliveryAddress()).isEqualTo("delivery address"),
                    () -> assertThat(actual.getOrderLineItems())
                        .singleElement()
                        .matches(
                            orderLineItem -> orderLineItem.getMenu().getId().equals(menu.getId()))
                        .matches(orderLineItem -> orderLineItem.getQuantity() == 1L)
                );
            }

            @DisplayName("주문 타입없이 주문을 등록 할 수 없다.")
            @Test
            void test02() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                Order request = OrderFixture.request(
                    null,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    "delivery address"
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("주문 아이템없이 주문을 등록 할 수 없다.")
            @Test
            void test03() {
                // given
                Order request = OrderFixture.request(
                    OrderType.DELIVERY,
                    Collections.emptyList(),
                    "delivery address"
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("존재하지 않는 메뉴를 주문 할 수 없다.")
            @Test
            void test04() {
                // given
                Order request = OrderFixture.request(
                    OrderType.DELIVERY,
                    List.of(OrderLineItemFixture.request(UUID.randomUUID(), 1,
                        BigDecimal.valueOf(6000))),
                    "delivery address"
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("주문 아이템의 수량은 0 이상이어야 한다.")
            @Test
            void test05() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                Order request = OrderFixture.request(
                    OrderType.DELIVERY,
                    List.of(OrderLineItemFixture.request(menu.getId(), -1, menu.getPrice())),
                    "delivery address"
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("감춰진 메뉴를 주문 할 수 없다.")
            @Test
            void test06() {
                // given
                Menu menu = menuRepository.save(MenuFixture.NO_DISPLAYED_MENU);
                Order request = OrderFixture.request(
                    OrderType.DELIVERY,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    "delivery address"
                );

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("배달 주문의 경우, 배달 주소가 없는 경우 주문 할 수 없다.")
            @ParameterizedTest
            @NullAndEmptySource
            void test07(String deliveryAddress) {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                Order request = OrderFixture.request(
                    OrderType.DELIVERY,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice())),
                    deliveryAddress
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("메뉴의 가격과 주문 아이템의 가격이 다른 경우, 주문 할 수 없다.")
            @Test
            void test08() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                BigDecimal orderItemPrice = menu.getPrice().add(BigDecimal.valueOf(1000));
                Order request = OrderFixture.request(
                    OrderType.DELIVERY,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, orderItemPrice)),
                    "delivery address"
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }
        }

        @DisplayName("주문 수락 테스트")
        @Nested
        class AcceptTest {

            @DisplayName("주문을 수락 할 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.WAITING,
                    "delivery address"
                ));

                // when
                Order actual = testTarget.accept(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.ACCEPTED);
                verify(kitchenridersClient)
                    .requestDelivery(order.getId(), BigDecimal.valueOf(6000), "delivery address");
            }

            @DisplayName("주문이 대기 상태가 아닌 경우, 수락 할 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.ACCEPTED,
                    "delivery address"
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.accept(order.getId()));
            }
        }


        @DisplayName("주문 서빙 테스트")
        @Nested
        class ServeTest {

            @DisplayName("주문을 서빙 할 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.ACCEPTED,
                    "delivery address"
                ));

                // when
                Order actual = testTarget.serve(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.SERVED);
            }

            @DisplayName("수락 상태가 아닌 주문을 서빙 할 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.WAITING,
                    "delivery address"
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.serve(order.getId()));
            }
        }

        @DisplayName("배달 시작 테스트")
        @Nested
        class StartDeliveryTest {

            @DisplayName("서빙된 주문을 배달 시작 할 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.SERVED,
                    "delivery address"
                ));

                // when
                Order actual = testTarget.startDelivery(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.DELIVERING);
            }

            @DisplayName("서빙된 주문이 아닌 경우, 배달 시작 할 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.ACCEPTED,
                    "delivery address"
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.startDelivery(order.getId()));
            }
        }

        @DisplayName("배달 완료 테스트")
        @Nested
        class CompleteDeliveryTest {

            @DisplayName("배달중인 주문을 배달 완료 할 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.DELIVERING,
                    "delivery address"
                ));

                // when
                Order actual = testTarget.completeDelivery(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.DELIVERED);
            }

            @DisplayName("배달중이 아닌 주문을 배달 완료 할 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.SERVED,
                    "delivery address"
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.completeDelivery(order.getId()));
            }
        }

        @DisplayName("주문 완료 테스트")
        @Nested
        class CompleteTest {

            @DisplayName("배달 완료된 주문을 완료 시킬 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.DELIVERED,
                    "delivery address"
                ));

                // when
                Order actual = testTarget.complete(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("배달 완료되지 않은 주문을 완료 시킬 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.deliveryOrder(
                    OrderStatus.DELIVERING,
                    "delivery address"
                ));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.complete(order.getId()));
            }
        }
    }

    @DisplayName("포장 주문 테스트")
    @Nested
    class TakeOutOrderTest {

        @DisplayName("주문 등록 테스트")
        @Nested
        class CreateTest {

            @DisplayName("포장 주문을 등록 할 수 있다.")
            @Test
            void test01() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                Order request = OrderFixture.request(
                    OrderType.TAKEOUT,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()))
                );

                // when
                Order actual = testTarget.create(request);

                // then
                assertAll(
                    () -> assertThat(actual.getId()).isNotNull(),
                    () -> assertThat(actual.getType()).isEqualTo(OrderType.TAKEOUT),
                    () -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(actual.getOrderLineItems())
                        .singleElement()
                        .matches(
                            orderLineItem -> orderLineItem.getMenu().getId().equals(menu.getId()))
                        .matches(orderLineItem -> orderLineItem.getQuantity() == 1L)
                );
            }

            @DisplayName("주문 타입없이 주문을 등록 할 수 없다.")
            @Test
            void test02() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                Order request = OrderFixture.request(
                    null,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()))
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("주문 아이템없이 주문을 등록 할 수 없다.")
            @Test
            void test03() {
                // given
                Order request = OrderFixture.request(
                    OrderType.TAKEOUT,
                    Collections.emptyList()
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("존재하지 않는 메뉴를 주문 할 수 없다.")
            @Test
            void test04() {
                // given
                Order request = OrderFixture.request(
                    OrderType.TAKEOUT,
                    List.of(OrderLineItemFixture.request(UUID.randomUUID(), 1, BigDecimal.valueOf(6000)))
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("주문 아이템의 수량은 0 이상이어야 한다.")
            @Test
            void test05() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                Order request = OrderFixture.request(
                    OrderType.TAKEOUT,
                    List.of(OrderLineItemFixture.request(menu.getId(), -1, menu.getPrice()))
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("감춰진 메뉴를 주문 할 수 없다.")
            @Test
            void test06() {
                // given
                Menu menu = menuRepository.save(MenuFixture.NO_DISPLAYED_MENU);
                Order request = OrderFixture.request(
                    OrderType.TAKEOUT,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, menu.getPrice()))
                );

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.create(request));
            }

            @DisplayName("메뉴의 가격과 주문 아이템의 가격이 다른 경우, 주문 할 수 없다.")
            @Test
            void test07() {
                // given
                Menu menu = menuRepository.save(MenuFixture.ONE_FRIED_CHICKEN);
                BigDecimal orderItemPrice = menu.getPrice().add(BigDecimal.valueOf(1000));
                Order request = OrderFixture.request(
                    OrderType.TAKEOUT,
                    List.of(OrderLineItemFixture.request(menu.getId(), 1, orderItemPrice))
                );

                // when & then
                assertThatIllegalArgumentException()
                    .isThrownBy(() -> testTarget.create(request));
            }
        }


        @DisplayName("주문 수락 테스트")
        @Nested
        class AcceptTest {

            @DisplayName("주문을 수락 할 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.takeoutOrder(OrderStatus.WAITING));

                // when
                Order actual = testTarget.accept(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.ACCEPTED);
            }

            @DisplayName("주문이 대기 상태가 아닌 경우, 수락 할 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.takeoutOrder(OrderStatus.ACCEPTED));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.accept(order.getId()));
            }
        }

        @DisplayName("주문 서빙 테스트")
        @Nested
        class ServeTest {

            @DisplayName("주문을 서빙 할 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.takeoutOrder(OrderStatus.ACCEPTED));

                // when
                Order actual = testTarget.serve(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.SERVED);
            }

            @DisplayName("수락 상태가 아닌 주문을 서빙 할 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.takeoutOrder(OrderStatus.WAITING));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.serve(order.getId()));
            }
        }

        @DisplayName("배달 시작 테스트")
        @Nested
        class StartDeliveryTest {

            @DisplayName("포장 주문은 배달을 시작 할 수 없다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.takeoutOrder(OrderStatus.SERVED));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.startDelivery(order.getId()));
            }
        }

        @DisplayName("주문 완료 테스트")
        @Nested
        class CompleteTest {

            @DisplayName("서빙된 주문을 완료 시킬 수 있다.")
            @Test
            void test01() {
                // given
                Order order = orderRepository.save(OrderFixture.takeoutOrder(OrderStatus.SERVED));

                // when
                Order actual = testTarget.complete(order.getId());

                // then
                assertThat(actual.getStatus())
                    .isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("서빙되지 않은 주문을 완료 시킬 수 없다.")
            @Test
            void test02() {
                // given
                Order order = orderRepository.save(OrderFixture.takeoutOrder(OrderStatus.ACCEPTED));

                // when & then
                assertThatIllegalStateException()
                    .isThrownBy(() -> testTarget.complete(order.getId()));
            }
        }
    }

    @DisplayName("주문 목록 조회 테스트")
    @Nested
    class FindAllTest {

        @DisplayName("주문 목록을 조회 할 수 있다.")
        @Test
        void test01() {
            // given
            Order order1 = orderRepository.save(OrderFixture.eatInOrder(
                OrderStatus.SERVED,
                OrderTableFixture.OCCUPIED_TABLE
            ));
            Order order2 = orderRepository.save(OrderFixture.deliveryOrder(
                OrderStatus.DELIVERING,
                "delivery address"
            ));
            Order order3 = orderRepository.save(OrderFixture.takeoutOrder(OrderStatus.ACCEPTED));

            // when
            List<Order> actual = testTarget.findAll();

            // then
            assertThat(actual)
                .anyMatch(o -> o.getId() == order1.getId())
                .anyMatch(o -> o.getId() == order2.getId())
                .anyMatch(o -> o.getId() == order3.getId());
        }
    }

}