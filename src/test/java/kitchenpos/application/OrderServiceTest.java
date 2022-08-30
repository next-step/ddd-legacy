package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import kitchenpos.constant.Fixtures;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        Fixtures.initialize();
    }

    @DisplayName("주문 등록")
    @Nested
    public class CreateTest {
        @DisplayName("성공 테스트")
        @Nested
        public class SuccessTest {
            @Captor
            private ArgumentCaptor<Order> argumentCaptor;

            @DisplayName("매장식사 정상 동작")
            @Test
            void createWithEatIn() {
                // given
                Order request = new Order();
                request.setType(OrderType.EAT_IN);
                request.setOrderTable(Fixtures.ORDER_TABLE);
                request.setOrderLineItems(List.of(Fixtures.ORDER_LINE_ITEM));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.MENU));
                given(menuRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU));
                given(orderTableRepository.findById(any()))
                    .willReturn(Optional.of(Fixtures.ORDER_TABLE));

                // when
                orderService.create(request);

                // then
                then(orderRepository).should().save(argumentCaptor.capture());
                Order result = argumentCaptor.getValue();

                assertAll(
                    () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.WAITING),
                    () -> assertThat(result.getOrderDateTime()).isNotNull()
                );
            }

            @DisplayName("배달 정상 동작")
            @Test
            void createWithEatDelivery() {
                // given
                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setOrderLineItems(List.of(Fixtures.ORDER_LINE_ITEM));
                request.setDeliveryAddress("SampleDeliveryAddress");

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.MENU));
                given(menuRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU));

                // when
                orderService.create(request);

                // then
                then(orderRepository).should().save(any());
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        public class FailTest {
            @DisplayName("주문유형이 null 일 수 없음")
            @Test
            void createWithInvalidType() {
                // given
                Order request = new Order();
                request.setType(null);

                // when then
                assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(request));
            }

            @DisplayName("메뉴 1개 이상 등록 해야함")
            @Test
            void createWithEmptyMenu() {
                // given
                Order request = new Order();
                request.setType(OrderType.EAT_IN);

                // when then
                assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(request));
            }

            @DisplayName("반드시 이미 등록된 메뉴만 등록 해야함")
            @Test
            void createWithNotExistsMenu() {
                // given
                Order request = new Order();
                request.setType(OrderType.EAT_IN);
                request.setOrderLineItems(List.of(Fixtures.ORDER_LINE_ITEM));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of());

                // when then
                assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(request));
            }

            @DisplayName("매장식사가 아닐경우 수량은 0개 이상 이어야함")
            @Test
            void createWithInvalidQuantity() {
                // given
                OrderLineItem orderLineItemRequest = new OrderLineItem();
                orderLineItemRequest.setMenu(Fixtures.MENU);
                orderLineItemRequest.setQuantity(-1L);
                orderLineItemRequest.setPrice(BigDecimal.valueOf(5000));

                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setOrderLineItems(List.of(orderLineItemRequest));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.MENU));

                // when then
                assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(request));
            }

            @DisplayName("등록하려는 메뉴는 숨김처리 되어있지 않아야 한다")
            @Test
            void createWithInvalidDisplayed() {
                // given
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setName("SampleMenu");
                menu.setPrice(BigDecimal.valueOf(5000));
                menu.setMenuGroup(Fixtures.MENU_GROUP);
                menu.setDisplayed(false);
                menu.setMenuProducts(List.of(Fixtures.MENU_PRODUCT));

                OrderLineItem orderLineItemRequest = new OrderLineItem();
                orderLineItemRequest.setMenu(Fixtures.MENU);
                orderLineItemRequest.setQuantity(1L);
                orderLineItemRequest.setPrice(BigDecimal.valueOf(5000));

                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setOrderLineItems(List.of(orderLineItemRequest));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));

                // when then
                assertThatIllegalStateException().isThrownBy(() -> orderService.create(request));
            }

            @DisplayName("등록시 입력한 메뉴의 가격과 실제 해당 메뉴의 가격은 같아야 함")
            @Test
            void createWithInvalidPrice() {
                // given
                Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setName("SampleMenu");
                menu.setPrice(BigDecimal.valueOf(10_000));
                menu.setMenuGroup(Fixtures.MENU_GROUP);
                menu.setDisplayed(true);
                menu.setMenuProducts(List.of(Fixtures.MENU_PRODUCT));

                OrderLineItem orderLineItemRequest = new OrderLineItem();
                orderLineItemRequest.setMenu(Fixtures.MENU);
                orderLineItemRequest.setQuantity(1L);
                orderLineItemRequest.setPrice(BigDecimal.valueOf(5000));

                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setOrderLineItems(List.of(orderLineItemRequest));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(menu));
                given(menuRepository.findById(any())).willReturn(Optional.of(menu));

                // when then
                assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(request));
            }

            @DisplayName("배달의 경우 배달주소를 입력 해야 함")
            @Test
            void createWithInvalidDeliveryAddress() {
                // given
                Order request = new Order();
                request.setType(OrderType.DELIVERY);
                request.setOrderTable(Fixtures.ORDER_TABLE);
                request.setOrderLineItems(List.of(Fixtures.ORDER_LINE_ITEM));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.MENU));
                given(menuRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU));

                // when then
                assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(request));
            }

            @DisplayName("매장식사의 경우 배달주소를 입력 해야 함")
            @Test
            void createWithInvalidField() {
                // given
                OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTable.setName("SampleOrderTable");
                orderTable.setNumberOfGuests(0);
                orderTable.setOccupied(false);

                Order request = new Order();
                request.setType(OrderType.EAT_IN);
                request.setOrderTable(orderTable);
                request.setOrderLineItems(List.of(Fixtures.ORDER_LINE_ITEM));

                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(Fixtures.MENU));
                given(menuRepository.findById(any())).willReturn(Optional.of(Fixtures.MENU));
                given(orderTableRepository.findById(any())).willReturn(Optional.of(orderTable));

                // when then
                assertThatIllegalStateException().isThrownBy(() -> orderService.create(request));
            }
        }
    }

    @DisplayName("주문 수락")
    @Nested
    public class AcceptTest {
        @DisplayName("성공 테스트")
        @Nested
        public class SuccessTest {
            @DisplayName("매장식사 정상 동작")
            @Test
            void accept() {
                // given
                UUID orderId = Fixtures.ORDER.getId();

                given(orderRepository.findById(orderId)).willReturn(Optional.of(Fixtures.ORDER));

                // when
                Order result = orderService.accept(orderId);

                // then
                assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }

            @DisplayName("배달 정상 동작")
            @Test
            void acceptDelivery() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY, OrderStatus.WAITING);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when
                Order result = orderService.accept(order.getId());

                // then
                then(kitchenridersClient).should().requestDelivery(eq(order.getId()), any(), any());

                assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        public class FailTest {
            @DisplayName("주문대기 상태에서만 가능해야 함")
            @Test
            void acceptWithInvalidStatus() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.EAT_IN, OrderStatus.ACCEPTED);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when then
                assertThatIllegalStateException().isThrownBy(
                    () -> orderService.accept(order.getId())
                );
            }
        }
    }

    @DisplayName("주문제공")
    @Nested
    public class ServeTest {
        @DisplayName("성공 테스트")
        @Nested
        public class SuccessTest {
            @DisplayName("정상 동작")
            @Test
            void serve() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY, OrderStatus.ACCEPTED);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when
                Order result = orderService.serve(order.getId());

                // then
                assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        public class FailTest {
            @DisplayName("주문수락 상태에서만 가능해야 함")
            @Test
            void serveWithInvalidStatus() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY, OrderStatus.WAITING);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when then
                assertThatIllegalStateException().isThrownBy(
                    () -> orderService.serve(order.getId())
                );
            }
        }
    }

    @DisplayName("배달중")
    @Nested
    public class StartDeliveryTest {
        @DisplayName("성공 테스트")
        @Nested
        public class SuccessTest {
            @DisplayName("정상 동작")
            @Test
            void startDelivery() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY, OrderStatus.SERVED);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when
                Order result = orderService.startDelivery(order.getId());

                // then
                assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        public class FailTest {
            @DisplayName("배달의 경우에만 가능해야 함")
            @Test
            void startDeliveryWithNotDeliveryType() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.EAT_IN, OrderStatus.SERVED);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when then
                assertThatIllegalStateException().isThrownBy(
                    () -> orderService.startDelivery(order.getId())
                );
            }

            @DisplayName("주문제공 상태에서만 가능해야 함")
            @Test
            void startDeliveryWithInvalidStatus() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY, OrderStatus.WAITING);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when then
                assertThatIllegalStateException().isThrownBy(
                    () -> orderService.startDelivery(order.getId())
                );
            }
        }
    }

    @DisplayName("배달완료")
    @Nested
    public class CompleteDeliveryTest {
        @DisplayName("성공 테스트")
        @Nested
        public class SuccessTest {
            @DisplayName("정상 동작")
            @Test
            void completeDelivery() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY,
                    OrderStatus.DELIVERING);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when
                Order result = orderService.completeDelivery(order.getId());

                // then
                assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        public class FailTest {
            @DisplayName("배달중 상태에서만 가능해야 함")
            @Test
            void completeDeliveryWithInvalidStatus() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY, OrderStatus.WAITING);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when then
                assertThatIllegalStateException().isThrownBy(
                    () -> orderService.completeDelivery(order.getId())
                );
            }
        }
    }

    @DisplayName("주문 완료")
    @Nested
    public class CompleteTest {
        @DisplayName("성공 테스트")
        @Nested
        public class SuccessTest {
            @DisplayName("배달 정상 동작")
            @Test
            void completeDelivery() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY, OrderStatus.DELIVERED);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when
                Order result = orderService.complete(order.getId());

                // then
                assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("포장 정상 동작")
            @Test
            void completeTakeout() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.TAKEOUT, OrderStatus.SERVED);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when
                Order result = orderService.complete(order.getId());

                // then
                assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("매장식사 정상 동작")
            @Test
            void completeEatIn() {
                // given
                OrderTable orderTable = Fixtures.createSampleOrderTable(5, true);
                Order order = Fixtures.createSampleOrder(OrderType.EAT_IN, OrderStatus.SERVED);

                order.setOrderTable(orderTable);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));
                given(orderRepository.existsByOrderTableAndStatusNot(orderTable,
                    OrderStatus.COMPLETED)).willReturn(Boolean.FALSE);

                // when
                Order result = orderService.complete(order.getId());

                // then
                assertAll(
                    () -> assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                    () -> assertThat(result.getOrderTable().getNumberOfGuests()).isZero(),
                    () -> assertThat(result.getOrderTable().isOccupied()).isFalse()
                );
            }
        }

        @DisplayName("실패 테스트")
        @Nested
        public class FailTest {
            @DisplayName("배달의 경우 배달완료 상태에서만 가능해야 함")
            @Test
            void completeWithInvalidStatus() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.DELIVERY, OrderStatus.SERVED);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when then
                assertThatIllegalStateException().isThrownBy(
                    () -> orderService.complete(order.getId())
                );
            }

            @DisplayName("포장의 경우 주문제공 상태에서만 가능해야 함")
            @Test
            void completeWithInvalidStatus2() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.TAKEOUT, OrderStatus.WAITING);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when then
                assertThatIllegalStateException().isThrownBy(
                    () -> orderService.complete(order.getId())
                );
            }

            @DisplayName("매장식사의 경우 주문제공 상태에서만 가능해야 함")
            @Test
            void completeWithInvalidStatus3() {
                // given
                Order order = Fixtures.createSampleOrder(OrderType.EAT_IN, OrderStatus.WAITING);

                given(orderRepository.findById(order.getId())).willReturn(Optional.of(order));

                // when then
                assertThatIllegalStateException().isThrownBy(
                    () -> orderService.complete(order.getId())
                );
            }
        }
    }

    @DisplayName("모든 주문 조회")
    @Test
    void findAll() {
        // given
        given(orderRepository.findAll()).willReturn(List.of(Fixtures.ORDER));

        // when
        List<Order> results = orderService.findAll();

        // then
        assertThat(results).containsExactly(Fixtures.ORDER);
    }
}
