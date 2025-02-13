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
        @DisplayName("실패: 메뉴 상태가 표시중이지 않은 메뉴가 포함되어 있으면 MenuNotDisplayedException이 발생한다.")
        void createOrder_fail_whenMenuNotDisplayed() {
            // given
            Menu 비표시_메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            비표시_메뉴.setDisplayed(false);

            // 주문 항목에 해당 메뉴를 포함시킴
            List<OrderLineItem> 주문메뉴 = List.of(
                    OrderFixture.주문상품_Request(비표시_메뉴, 1L)
            );
            Order request = OrderFixture.주문_Request(OrderType.EAT_IN, 주문메뉴);

            // when & then: 주문 생성 시 MenuNotDisplayedException 예외가 발생하는지 확인
            assertThrows(MenuNotDisplayedException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 주문 항목 각각의 가격이 시스템에 등록된 메뉴와 동일하지 않으면 OrderLineItemPriceMismatchException 발생한다.")
        void createOrder_fail_whenPriceMismatch() {
            // given
            Menu 후라이드_치킨_메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());

            OrderLineItem 주문상품 = OrderFixture.주문상품_Request(후라이드_치킨_메뉴, 1L);
            주문상품.setPrice(후라이드_치킨_메뉴.getPrice().add(new BigDecimal("1000"))); // 가격 불일치 발생

            Order request = OrderFixture.주문_Request(OrderType.EAT_IN, List.of(주문상품));

            // when & then
            assertThrows(OrderLineItemPriceMismatchException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("성공: 주문 최초 생성시 상태는 대기여야 한다.")
        void createOrder_success_withWaitingStatus() {
            // given: 시스템에 등록된 메뉴 생성 (표시 중인 메뉴)
            Menu 후라이드치킨메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            OrderLineItem 주문상품 = OrderFixture.주문상품_Request(후라이드치킨메뉴, 1L);
            OrderTable 주문테이블 = orderTableRepository.save(OrderTableFixture.주문테이블_사용중_Request());
            Order request = OrderFixture.주문_Request(주문테이블, OrderType.EAT_IN, List.of(주문상품));

            // when: 주문 생성 호출
            Order createdOrder = sut.create(request);

            // then: 생성된 주문의 상태가 대기(WAITING) 상태인지 검증
            assertEquals(OrderStatus.WAITING, createdOrder.getStatus());
        }
        @Test
        @DisplayName("실패: 배달 주문일 때, 배달 주소가 입력되지 않으면 OrderDeliveryAddressNotEnteredException이 발생한다.")
        void createOrder_fail_whenDeliveryOrderWithoutAddress() {
            // given
            Menu 후라이드치킨메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            OrderTable 주문테이블 = orderTableRepository.save(OrderTableFixture.주문테이블_사용중_Request());
            OrderLineItem 주문상품 = OrderFixture.주문상품_Request(후라이드치킨메뉴, 1L);
            Order request = OrderFixture.주문_Request(주문테이블, OrderType.DELIVERY, List.of(주문상품));
            request.setDeliveryAddress(null);

            // when & then
            assertThrows(OrderDeliveryAddressNotEnteredException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 매장 주문일 때, 사용중이 아닌 테이블에 주문을 생성하면 OrderTableNotOccupiedException이 발생한다.")
        void createOrder_fail_whenDineInWithoutAvailableTable() {
            // given
            Menu 후라이드치킨메뉴 = menuRepository.save(MenuFixture.후라이드_치킨_메뉴_Request());
            OrderTable 주문테이블 = orderTableRepository.save(OrderTableFixture.주문테이블_생성_Request());
            OrderLineItem 주문상품 = OrderFixture.주문상품_Request(후라이드치킨메뉴, 1L);
            Order request = OrderFixture.주문_Request(주문테이블, OrderType.EAT_IN, List.of(주문상품));

            // when & then
            assertThrows(OrderTableNotOccupiedException.class, () -> sut.create(request));
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