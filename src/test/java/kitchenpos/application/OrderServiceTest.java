package kitchenpos.application;

import config.UnitTest;
import kitchenpos.MenuFixture;
import kitchenpos.OrderFixture;
import kitchenpos.OrderTableFixture;
import kitchenpos.domain.*;
import kitchenpos.domain.Order;
import kitchenpos.infra.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
class OrderServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;

    private OrderService sut;

    @BeforeEach
    void setUp() {
        orderRepository = new InmemoryOrderRepository();
        menuRepository = new InmemoryMenuRepository();
        orderTableRepository = new InmemoryOrderTableRepository();
        kitchenridersClient = new FakeKitchenridersClient();
        sut = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrderTests {

        @Test
        @DisplayName("성공: 고객은 메뉴를 선택해 주문할 수 있다.")
        void createOrder_success() {
            // given
        }

        @Test
        @DisplayName("실패: 주문 타입을 선택하지 않으면 OrderTypeNotSelectedException이 발생한다.")
        void createOrder_fail_whenOrderTypeNotSelected() {
            //given
            OrderType type = null;
            Order request = OrderFixture.주문_Request(type);

            //when & then
            assertThrows(OrderTypeNotSelectedException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 주문 항목을 선택하지 않으면 OrderLineItemNotSelectedException이 발생한다.")
        void createOrder_fail_whenNoMenuSelected() {
            //given
            Order request = OrderFixture.주문_Request(OrderType.EAT_IN);

            //when & then
            assertThrows(OrderLineItemNotSelectedException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 주문 항목에 존재하지 않는 메뉴가 포함되어 있으면 OrderLineItemNotMatchedMenuException이 발생한다.")
        void createOrder_fail_whenTakeoutOrDeliveryWithZeroQuantity() {
            // given
            Menu 후라이드_치킨_메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            Menu 존재하지_않는_메뉴 = MenuFixture.양념_치킨_메뉴_Request();
            List<OrderLineItem> 존재하지_않는_메뉴가_포함된_주문메뉴 = List.of(
                    OrderFixture.주문상품_Request(후라이드_치킨_메뉴, 1L),
                    OrderFixture.주문상품_Request(존재하지_않는_메뉴, 1L)
            );
            Order request = OrderFixture.주문_Request(OrderType.EAT_IN, 존재하지_않는_메뉴가_포함된_주문메뉴);

            // when & then
            assertThrows(OrderLineItemNotMatchedMenuException.class, () -> sut.create(request));
        }

        @ParameterizedTest
        @DisplayName("실패: 매장 주문을 제외한 주문 항목의 수량은 0보다 크지 않으면 OrderLineQuantityNegativeException이 발생한다.")
        @MethodSource("kitchenpos.OrderFixture#orderTypeNotEatIn")
        void createOrder_fail_whenTakeoutOrDeliveryWithZeroQuantity(OrderType type) {
            //given
            Menu 후라이드_치킨_메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            List<OrderLineItem> 주문_메뉴 = List.of(
                    OrderFixture.주문상품_Request(후라이드_치킨_메뉴, -1L)
            );
            Order request = OrderFixture.주문_Request(type, 주문_메뉴);


            //when & then
            assertThrows(OrderLineQuantityNegativeException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 메뉴 상태가 표시중인 메뉴만 주문할 수 있다.")
        void createOrder_fail_whenMenuNotDisplayed() {

        }

        @Test
        @DisplayName("실패: 주문 항목 각각의 가격은 시스템에 등록된 메뉴와 동일해야 한다.")
        void createOrder_fail_whenPriceMismatch() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("성공: 주문 최초 생성시 상태는 대기여야 한다.")
        void createOrder_success_withWaitingStatus() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("실패: 배달 주문이면 배달 주소를 입력해야 한다.")
        void createOrder_fail_whenDeliveryOrderWithoutAddress() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("실패: 매장 주문이면 미사용 상태인 매장 테이블을 선택해야 한다.")
        void createOrder_fail_whenDineInWithoutAvailableTable() {
            throw new UnsupportedOperationException("Not Implemented");
        }
    }


    @Nested
    @DisplayName("주문을 접수")
    class AcceptOrderTests {
        @Test
        @DisplayName("성공: 대기 상태의 주문을 접수할 수 있다.")
        void acceptOrder_success_whenWaiting() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("실패: 대기 상태가 아닌 주문은 접수할 수 없다.")
        void acceptOrder_fail_whenNotWaiting() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("성공: 배달 주문을 접수하면 배달 요청을 보낸다.")
        void acceptOrder_success_whenDeliveryOrder_requestsDelivery() {
            throw new UnsupportedOperationException("Not Implemented");
        }
    }

    @Nested
    @DisplayName("조리가 완료된 주문을 제공")
    class ServeOrderTests {
        @Test
        @DisplayName("성공: 접수 상태의 주문만 제공할 수 있다.")
        void serveOrder_success_whenAccepted() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("실패: 접수 상태가 아닌 주문은 제공할 수 없다.")
        void serveOrder_fail_whenNotAccepted() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("성공: 배달 주문이 제공되면 배달이 시작되었음을 기록한다.")
        void serveOrder_success_whenDeliveryOrder_startDelivery() {
            throw new UnsupportedOperationException("Not Implemented");
        }
    }

    @Nested
    @DisplayName("배달 주문을 완료")
    class CompleteDeliveryTests {
        @Test
        @DisplayName("성공: 배달 중 상태의 주문만 배달 완료할 수 있다.")
        void completeDelivery_success_whenInDelivery() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("실패: 배달 중이 아닌 상태의 주문은 배달 완료할 수 없다.")
        void completeDelivery_fail_whenNotInDelivery() {
            throw new UnsupportedOperationException("Not Implemented");
        }
    }

    @Nested
    @DisplayName("주문을 최종 완료")
    class CompleteOrderTests {
        @Test
        @DisplayName("성공: 매장 주문과 포장 주문은 제공된 상태에서만 완료할 수 있다.")
        void completeOrder_success_whenDineInOrTakeOut() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("성공: 배달 주문은 배달 완료 상태에서만 완료할 수 있다.")
        void completeOrder_success_whenDeliveryCompleted() {
            throw new UnsupportedOperationException("Not Implemented");
        }

        @Test
        @DisplayName("성공: 매장 주문이 완료되면 테이블을 미사용 상태로 변경하고 인원 수를 0으로 설정한다.")
        void completeOrder_success_whenDineIn_updatesTable() {
            throw new UnsupportedOperationException("Not Implemented");
        }
    }

    @Nested
    @DisplayName("모든 주문을 조회")
    class GetAllOrdersTests {
        @Test
        @DisplayName("성공: 전체 주문 목록을 반환한다.")
        void getAllOrders_success() {
            throw new UnsupportedOperationException("Not Implemented");
        }
    }

}