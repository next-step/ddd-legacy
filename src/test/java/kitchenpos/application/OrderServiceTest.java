package kitchenpos.application;

import kitchenpos.ApplicationTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderLineItemFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("주문")
class OrderServiceTest extends ApplicationTest {

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

    private OrderTable orderTable;
    private Menu menu;
    private OrderLineItem orderLineItem;
    private String deliveryAddress;

    @BeforeEach
    void setUp() {
        orderTable = OrderTableFixture.create("주문테이블", true, 1);
        menu = MenuFixture.createDefaultWithNameAndPrice("메뉴", BigDecimal.valueOf(2000));
        orderLineItem = OrderLineItemFixture.create(menu, BigDecimal.valueOf(2000), 1);
        deliveryAddress = "배달주소";
    }

    @DisplayName("만들기")
    @Nested
    class Create {

        @DisplayName("매장 주문")
        @Nested
        class EatIn {

            @DisplayName("[성공] 매장 주문을 만든다. 주문은 대기상태다. ")
            @Test
            void createEatInTest1() {
                //given
                Order eatIn = OrderFixture.createEatIn(Optional.ofNullable(orderTable), List.of(orderLineItem));
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));
                when(orderTableRepository.findById(any())).thenReturn(Optional.ofNullable(orderTable));
                when(orderRepository.save(any())).thenReturn(eatIn);
                //when
                Order created = orderService.create(eatIn);
                //then
                verify(orderRepository, times(1)).save(any());
                assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
            }

            @DisplayName("[예외] 매장 주문에는 등록된 주문테이블이 필요하다.")
            @Test
            void createEatInTest2() {
                //given
                Order eatIn = OrderFixture.createEatIn(Optional.ofNullable(orderTable), List.of(orderLineItem));

                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));
                //when
                when(orderTableRepository.findById(any())).thenReturn(Optional.empty());
                //then
                assertThatThrownBy(() -> orderService.create(eatIn))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("[예외] 매장 주문을 만든다. 주문 테이블은 사용 중이어야 한다.")
            @Test
            void createEatInTest3() {
                //given
                orderTable.setOccupied(false);
                Order eatIn = OrderFixture.createEatIn(Optional.ofNullable(orderTable), List.of(orderLineItem));

                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));
                when(orderTableRepository.findById(any())).thenReturn(Optional.ofNullable(orderTable));
                //when
                assertThat(orderTable.isOccupied()).isFalse();
                //then
                assertThatThrownBy(() -> orderService.create(eatIn))
                        .isInstanceOf(IllegalStateException.class);
            }

        }

        @DisplayName("배달 주문")
        @Nested
        class Delivery {

            @DisplayName("[성공] 배달 주문을 만든다. 주문은 대기상태다. ")
            @Test
            void createDeliveryTest1() {
                //given
                Order delivery = OrderFixture.createDelivery(Optional.ofNullable(deliveryAddress), List.of(orderLineItem));
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));
                when(orderRepository.save(any())).thenReturn(delivery);
                //when
                Order created = orderService.create(delivery);
                //then
                verify(orderRepository, times(1)).save(any());
                assertThat(created.getStatus()).isEqualTo(OrderStatus.WAITING);
            }

            @DisplayName("[예외] 배달 주문에는 주소가 필요하다.")
            @ParameterizedTest
            @NullAndEmptySource
            void createDeliveryTest2(String address) {
                //given
                Order delivery = OrderFixture.createDelivery(Optional.ofNullable(address), List.of(orderLineItem));
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
                when(menuRepository.findById(any())).thenReturn(Optional.ofNullable(menu));
                //then
                assertThatThrownBy(() -> orderService.create(delivery))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("[예외] 배달 주문을 만든다. 주문 메뉴 1항목의 수량은 0개 이상이다.")
            @Test
            void createDeliveryTest3() {
                //given
                orderLineItem.setQuantity(-1);
                Order delivery = OrderFixture.createDelivery(
                        Optional.ofNullable(deliveryAddress)
                        , List.of(orderLineItem));

                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

                //then
                assertThatThrownBy(() -> orderService.create(delivery))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("포장 주문")
        @Nested
        class TakeOut {
            @DisplayName("[예외] 배달 주문을 만든다. 주문 메뉴 1항목의 수량은 0개 이상이다.")
            @Test
            void createTakeoutTest1() {
                //given
                orderLineItem.setQuantity(-1);
                Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
                when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

                //then
                assertThatThrownBy(() -> orderService.create(takeOut))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("[예외] 주문 타입이 필요하다.")
        @Test
        void createTest2() {
            //given
            Order order = OrderFixture.create(null
                    , Optional.of(orderTable)
                    , Optional.of(deliveryAddress)
                    , List.of(orderLineItem));
            //then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);


        }

        @DisplayName("[예외] 주문 메뉴는 1개 이상이어야 한다.")
        @Test
        void createTest3() {
            //given
            Order order = OrderFixture.createTakeOut(Collections.emptyList());
            //then
            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 주문 메뉴는 등록된 메뉴여야 한다.")
        @Test
        void createTest4() {
            //given
            Menu menu2 = MenuFixture.createDefaultWithNameAndPrice("메뉴2", BigDecimal.valueOf(2000));
            OrderLineItem orderLineItem2 = OrderLineItemFixture.create(menu2, BigDecimal.valueOf(2000), 1);

            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem, orderLineItem2));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));

            //then
            assertThatThrownBy(() -> orderService.create(takeOut))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("[예외] 주문 메뉴는 표기 상태여야 한다.")
        @Test
        void createTest5() {
            //given
            menu.setDisplayed(false);
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            //then
            assertThatThrownBy(() -> orderService.create(takeOut))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[예외] 주문 메뉴의 가격은 원래 메뉴의 가격과 같아야 한다.")
        @Test
        void createTest6() {
            //given
            menu.setPrice(BigDecimal.valueOf(3000));
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            when(menuRepository.findAllByIdIn(any())).thenReturn(List.of(menu));
            when(menuRepository.findById(menu.getId())).thenReturn(Optional.of(menu));

            //then
            assertThatThrownBy(() -> orderService.create(takeOut))
                    .isInstanceOf(IllegalArgumentException.class);
        }

    }


    @DisplayName("접수")
    @Nested
    class Accept {

        @DisplayName("[성공] 주문을 접수한다. 상태가 접수로 바뀐다.")
        @Test
        void acceptTest1() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            when(orderRepository.findById(any())).thenReturn(Optional.of(takeOut));
            assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.WAITING);
            //when
            Order accept = orderService.accept(takeOut.getId());
            //then
            assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("[예외] 주문을 접수한다. 상태가 대기여야 한다.")
        @Test
        void acceptTest2() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            takeOut.setStatus(OrderStatus.ACCEPTED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(takeOut));
            //then
            assertThatThrownBy(() -> orderService.accept(takeOut.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[성공] 배달 주문을 접수한다. 라이더를 부른다.")
        @Test
        void acceptTest3() {
            //given
            Order delivery = OrderFixture.createDelivery(Optional.of(deliveryAddress), List.of(orderLineItem));
            when(orderRepository.findById(any())).thenReturn(Optional.of(delivery));
            //when
            Order accept = orderService.accept(delivery.getId());
            //then
            verify(kitchenridersClient, times(1))
                    .requestDelivery(any(), any(), any());
            assertThat(accept.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("[예외] 등록되지 않은 주문은 접수할 수 없다.")
        @Test
        void acceptTest4() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            takeOut.setStatus(OrderStatus.ACCEPTED);
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> orderService.accept(takeOut.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

    }

    @DisplayName("서빙")
    @Nested
    class Serve {

        @DisplayName("[성공] 주문을 서빙한다. 서빙 후 주문의 상태는 서빙완료다.")
        @Test
        void serveTest1() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            when(orderRepository.findById(any())).thenReturn(Optional.of(takeOut));
            takeOut.setStatus(OrderStatus.ACCEPTED);
            //when
            Order served = orderService.serve(takeOut.getId());
            //then
            assertThat(served.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @DisplayName("[예외] 주문을 서빙한다. 서빙 전 주문의 상태는 접수여야 한다.")
        @Test
        void serveTest2() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            when(orderRepository.findById(any())).thenReturn(Optional.of(takeOut));
            //then
            assertThatThrownBy(() -> orderService.serve(takeOut.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[예외] 등록되지 않은 주문은 서빙할 수 없다.")
        @Test
        void serveTest3() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            takeOut.setStatus(OrderStatus.ACCEPTED);
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> orderService.serve(takeOut.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("배달요청")
    @Nested
    class StartDelivery {

        @DisplayName("[성공] 배달 주문을 배달한다. 배달 후 주문의 상태는 배달중이다.")
        @Test
        void deliveryTest1() {
            //given
            Order delivery = OrderFixture.createDelivery(Optional.ofNullable(deliveryAddress), List.of(orderLineItem));
            delivery.setStatus(OrderStatus.SERVED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(delivery));
            //when
            Order startDelivery = orderService.startDelivery(delivery.getId());
            //then
            assertThat(startDelivery.getStatus())
                    .isEqualTo(OrderStatus.DELIVERING);

        }

        @DisplayName("[예외] 매장 주문은 배달할 수 없다.")
        @Test
        void deliveryTest2() {
            //given
            Order eatIn = OrderFixture.createEatIn(Optional.of(orderTable), List.of(orderLineItem));
            eatIn.setStatus(OrderStatus.SERVED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(eatIn));
            //when
            //then
            assertThatThrownBy(() -> orderService.startDelivery(eatIn.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[예외] 포장 주문은 배달할 수 없다.")
        @Test
        void deliveryTest3() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            takeOut.setStatus(OrderStatus.SERVED);
            when(orderRepository.findById(any())).thenReturn(Optional.of(takeOut));
            //when
            //then
            assertThatThrownBy(() -> orderService.startDelivery(takeOut.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[예외] 배달 주문의 상태가 서빙완료일때만 배달할 수 있다.")
        @Test
        void deliveryTest4() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            when(orderRepository.findById(any())).thenReturn(Optional.of(takeOut));
            //when
            assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.WAITING);
            //then
            assertThatThrownBy(() -> orderService.startDelivery(takeOut.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[예외] 등록된 주문만 배달할 수 있다.")
        @Test
        void deliveryTest5() {
            //given
            Order delivery = OrderFixture.createDelivery(Optional.ofNullable(deliveryAddress), List.of(orderLineItem));
            delivery.setStatus(OrderStatus.SERVED);
            //when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> orderService.startDelivery(delivery.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @DisplayName("배달완료")
    @Nested
    class CompleteDelivery {

        @DisplayName("[예외] 등록된 주문만 배달할 수 있다.")
        @Test
        void deliveryTest6() {
            //given
            Order delivery = OrderFixture.createDelivery(Optional.ofNullable(deliveryAddress), List.of(orderLineItem));
            delivery.setStatus(OrderStatus.DELIVERING);
            //when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> orderService.completeDelivery(delivery.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("[예외] 배달 주문의 상태가 배달 중일때만 배달완료할 수 있다.")
        @Test
        void deliveryTest7() {
            //given
            Order delivery = OrderFixture.createDelivery(Optional.ofNullable(deliveryAddress), List.of(orderLineItem));
            when(orderRepository.findById(any())).thenReturn(Optional.of(delivery));
            //when
            assertThat(delivery.getStatus()).isEqualTo(OrderStatus.WAITING);
            //then
            assertThatThrownBy(() -> orderService.completeDelivery(delivery.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("[성공] 배달 완료한다. 배달완료 후 주문 상태는 '배달완료'다")
        @Test
        void deliveryTest8() {
            //given
            Order delivery = OrderFixture.createDelivery(Optional.ofNullable(deliveryAddress), List.of(orderLineItem));
            delivery.setStatus(OrderStatus.DELIVERING);
            when(orderRepository.findById(any())).thenReturn(Optional.of(delivery));
            assertThat(delivery.getStatus()).isEqualTo(OrderStatus.DELIVERING);
            //when
            Order completeDelivery = orderService.completeDelivery(delivery.getId());
            //then
            assertThat(completeDelivery.getStatus()).isEqualTo(OrderStatus.DELIVERED);

        }
    }

    @DisplayName("주문완료")
    @Nested
    class Complete {

        @DisplayName("[예외] 등록된 주문만 완료할 수 있다.")
        @Test
        void completeTest1() {
            //given
            Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
            takeOut.setStatus(OrderStatus.SERVED);
            //when
            when(orderRepository.findById(any())).thenReturn(Optional.empty());
            //then
            assertThatThrownBy(() -> orderService.completeDelivery(takeOut.getId()))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("배달")
        @Nested
        class Delivery {

            @DisplayName("[성공] 배달 주문을 완료한다.")
            @Test
            void completeDeliveryTest1() {
                //given
                Order delivery = OrderFixture.createDelivery(Optional.ofNullable(deliveryAddress), List.of(orderLineItem));
                delivery.setStatus(OrderStatus.DELIVERED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(delivery));
                assertThat(delivery.getStatus()).isEqualTo(OrderStatus.DELIVERED);
                //when
                Order completeDelivery = orderService.complete(delivery.getId());
                //then
                assertThat(completeDelivery.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("[예외] 배달 주문 상태가 '배달완료'가 아니면 완료할 수 없다.")
            @Test
            void completeDeliveryTest2() {
                //given
                Order delivery = OrderFixture.createDelivery(Optional.ofNullable(deliveryAddress), List.of(orderLineItem));
                when(orderRepository.findById(any())).thenReturn(Optional.of(delivery));
                //when
                assertThat(delivery.getStatus()).isEqualTo(OrderStatus.WAITING);
                //then
                assertThatThrownBy(() -> orderService.complete(delivery.getId()))
                        .isInstanceOf(IllegalStateException.class);
            }


        }

        @DisplayName("매장")
        @Nested
        class EatIn {

            @DisplayName("[성공] 매장 주문을 완료한다. 주문 상태는 '주문완료'가 된다. 주문테이블을 치운다.")
            @Test
            void completeEatInTest1() {
                //given
                Order eatIn = OrderFixture.createEatIn(Optional.ofNullable(orderTable), List.of(orderLineItem));
                eatIn.setStatus(OrderStatus.SERVED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(eatIn));
                assertThat(eatIn.getStatus()).isEqualTo(OrderStatus.SERVED);
                //when
                Order completed = orderService.complete(eatIn.getId());
                //then
                assertAll(
                        () -> assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED)
                        , () -> assertThat(completed.getOrderTable().isOccupied()).isFalse()
                        , () -> assertThat(completed.getOrderTable().getNumberOfGuests()).isZero()
                );
            }

            @DisplayName("[예외] 매장 주문 상태가 '서빙완료'가 아니면 완료할 수 없다.")
            @Test
            void completeDeliveryTest2() {
                //given
                Order eatIn = OrderFixture.createEatIn(Optional.ofNullable(orderTable), List.of(orderLineItem));
                when(orderRepository.findById(any())).thenReturn(Optional.of(eatIn));
                //when
                assertThat(eatIn.getStatus()).isEqualTo(OrderStatus.WAITING);
                //then
                assertThatThrownBy(() -> orderService.complete(eatIn.getId()))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("포장")
        @Nested
        class TakeOut {

            @DisplayName("[성공] 포장 주문을 완료한다. 주문 상태는 '주문완료'가 된다. ")
            @Test
            void completeTakeoutTest1() {
                //given
                Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
                takeOut.setStatus(OrderStatus.SERVED);
                when(orderRepository.findById(any())).thenReturn(Optional.of(takeOut));
                assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.SERVED);
                //when
                Order completed = orderService.complete(takeOut.getId());
                //then
                assertThat(completed.getStatus()).isEqualTo(OrderStatus.COMPLETED);

            }

            @DisplayName("[예외] 포장 주문 상태가 '서빙완료'가 아니면 완료할 수 없다.")
            @Test
            void completeTakeoutTest2() {
                //given
                Order takeOut = OrderFixture.createTakeOut(List.of(orderLineItem));
                when(orderRepository.findById(any())).thenReturn(Optional.of(takeOut));
                //when
                assertThat(takeOut.getStatus()).isEqualTo(OrderStatus.WAITING);
                //then
                assertThatThrownBy(() -> orderService.complete(takeOut.getId()))
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }


}

