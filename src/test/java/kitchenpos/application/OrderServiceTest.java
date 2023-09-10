package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.*;

import static kitchenpos.domain.OrderStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class OrderServiceTest extends IntegrationTest {


    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuRepository menuRepository;
    private final OrderTableRepository orderTableRepository;

    @MockBean
    private KitchenridersClient kitchenridersClient;

    OrderServiceTest(OrderService orderService,
                     OrderRepository orderRepository,
                     ProductRepository productRepository,
                     MenuGroupRepository menuGroupRepository,
                     MenuRepository menuRepository,
                     OrderTableRepository orderTableRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.menuRepository = menuRepository;
        this.orderTableRepository = orderTableRepository;
    }

    private List<Product> 상품_치킨_콜라;
    private List<MenuProduct> 메뉴구성상품_치킨_콜라;
    private MenuGroup 메뉴그룹_기본;
    private Menu 메뉴_후라이드치킨세트;
    private Menu 메뉴_후라이드치킨세트_미전시;


    private OrderTable 테이블_4명_점유중;
    private OrderTable 테이블_0명_빈테이블;
    private List<OrderLineItem> 주문품목_후라이드치킨세트;
    private List<OrderLineItem> 주문품목_후라이드치킨세트_마이너스_주문;
    private List<OrderLineItem> 주문품목_후라이드치킨세트_미전시;

    @BeforeEach
    void setUp() {
        상품_치킨_콜라 = Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
        );

        메뉴구성상품_치킨_콜라 = MenuProductFixture.create(상품_치킨_콜라 , 1);
        메뉴그룹_기본 = MenuGroupFixture.create();
        메뉴_후라이드치킨세트 = MenuFixture.create(
                UUID.randomUUID(), "후라이드 치킨 세트", BigDecimal.valueOf(19_000L),
                메뉴구성상품_치킨_콜라, 메뉴그룹_기본, true
        );
        메뉴_후라이드치킨세트_미전시 = MenuFixture.create(
                UUID.randomUUID(), "후라이드 치킨 세트", BigDecimal.valueOf(19_000L),
                메뉴구성상품_치킨_콜라, 메뉴그룹_기본, false
        );

        테이블_4명_점유중 = OrderTableFixture.create(UUID.randomUUID(), "테이블_4명_점유중", 4, true);
        테이블_0명_빈테이블 = OrderTableFixture.create(UUID.randomUUID(), "테이블", 0, false);
        주문품목_후라이드치킨세트 = List.of(OrderLineItemFixture.create(메뉴_후라이드치킨세트, 1L));
        주문품목_후라이드치킨세트_마이너스_주문 = List.of(OrderLineItemFixture.create(메뉴_후라이드치킨세트, -1L));
        주문품목_후라이드치킨세트_미전시 = List.of(OrderLineItemFixture.create(메뉴_후라이드치킨세트_미전시, 1L));
    }

    @DisplayName("주문을 등록합니다.")
    @Nested
    class create {


        @DisplayName("[정상] 배달 주문 유형인 주문을 등록합니다.")
        @Test
        void create_success_delivery_order_type_one_order_line_item() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = OrderFixture.create(null, OrderType.DELIVERY, null, 주문품목_후라이드치킨세트);

            Order savedOrder = orderService.create(givenOrder);

            assertNotNull(savedOrder.getId());
            assertEquals(givenOrder.getType(), savedOrder.getType());
            assertEquals(WAITING, savedOrder.getStatus());
            assertEquals(givenOrder.getDeliveryAddress(), savedOrder.getDeliveryAddress());
            assertEquals(givenOrder.getOrderLineItems().size(), savedOrder.getOrderLineItems().size());
        }

        @DisplayName("[정상] 매장식사 주문 유형인 주문을 등록합니다.")
        @Test
        void create_success_eat_in_order_type_one_order_line_item() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = OrderFixture.create(null, OrderType.EAT_IN, null, 테이블_4명_점유중, 주문품목_후라이드치킨세트);

            Order savedOrder = orderService.create(givenOrder);

            assertNotNull(savedOrder.getId());
            assertEquals(givenOrder.getType(), savedOrder.getType());
            assertEquals(WAITING, savedOrder.getStatus());
            assertNull(savedOrder.getDeliveryAddress());
            assertNotNull(savedOrder.getOrderTable());
            assertEquals(givenOrder.getOrderLineItems().size(), savedOrder.getOrderLineItems().size());
        }

        @DisplayName("[정상] 포장 주문 유형인 주문을 등록합니다.")
        @Test
        void create_success_takeout_order_type_one_order_line_item() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, 주문품목_후라이드치킨세트);

            Order savedOrder = orderService.create(givenOrder);

            assertNotNull(savedOrder.getId());
            assertEquals(givenOrder.getType(), savedOrder.getType());
            assertEquals(WAITING, savedOrder.getStatus());
            assertNull(savedOrder.getDeliveryAddress());
            assertNull(savedOrder.getOrderTable());
            assertEquals(givenOrder.getOrderLineItems().size(), savedOrder.getOrderLineItems().size());
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 유형이 null 이어선 안된다.")
        @Test
        void create_fail_because_order_type_null() {
            Order givenOrder = OrderFixture.create(null, null, null, null);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 품목이 비어선 안됩니다.")
        @Test
        void create_fail_because_order_line_item_is_empty() {
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, new ArrayList<>());

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 품목이 null 이어선 안됩니다.")
        @Test
        void create_fail_because_order_line_item_is_null() {
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, null);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 품목에 해당하는 메뉴가 존재해야 합니다.")
        @Test
        void create_fail_because_some_menu_is_not_exist() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            // menu-not-save

            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, 주문품목_후라이드치킨세트);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문 유형이 매장식사가 아닌 경우 주문 품목의 개수가 음수 일 수 없다.")
        @Test
        void create_fail_because_eat_in_order_type_and_order_line_item_quantity_is_minus() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, 주문품목_후라이드치킨세트_마이너스_주문);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문된 메뉴가 전시 중이어야 합니다.")
        @Test
        void create_fail_because_some_menu_is_not_displayed() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트_미전시);
            Order givenOrder = OrderFixture.create(null, OrderType.DELIVERY, null, 주문품목_후라이드치킨세트_미전시);

            assertThrows(IllegalStateException.class, () -> orderService.create(givenOrder));
        }

        @DisplayName("[예외] 주문을 등록할 때 주문된 메뉴의 가격과 주문 품목의 가격이 일치해야 합니다.")
        @Test
        void create_fail_because_menu_and_order_line_item_is_not_same() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            List<OrderLineItem> givenOrderLineItems = List.of(
                    OrderLineItemFixture.create(메뉴_후라이드치킨세트, BigDecimal.valueOf(20_000L), 1L)
            );
            Order givenOrder = OrderFixture.create(null, OrderType.TAKEOUT, null, givenOrderLineItems);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }


        @NullAndEmptySource
        @DisplayName("[예외] 배달 주문 유형인 주문은 배달 주소가 있어야 합니다.")
        @ParameterizedTest
        void create_fail_because_delivery_order_type_but_delivery_address_is_null(String deliveryAddress) {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = OrderFixture.create(null, OrderType.DELIVERY, null, 주문품목_후라이드치킨세트);
            givenOrder.setDeliveryAddress(deliveryAddress);

            assertThrows(IllegalArgumentException.class, () -> orderService.create(givenOrder));
        }


        @DisplayName("[예외] 매장식사 주문 유형인 주문은 주문 테이블이 점유 중이어야 합니다.")
        @Test
        void create_fail_because_eat_in_order_type_but_order_table_is_not_occupied() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_0명_빈테이블);

            Order givenOrder = OrderFixture.create(null, OrderType.EAT_IN, null, 테이블_0명_빈테이블, 주문품목_후라이드치킨세트);

            assertThrows(IllegalStateException.class, () -> orderService.create(givenOrder));
        }

    }

    @DisplayName("'매장 식사' 주문 유형인 주문에 대해 주문 상태를 변경합니다.")
    @Nested
    class changeStatusOfEatInOrderType {

        @DisplayName("[정상] 주문이 수락됩니다.")
        @Test
        void accept_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, WAITING, 테이블_4명_점유중, 주문품목_후라이드치킨세트));

            orderService.accept(givenOrder.getId());

            assertEquals(OrderStatus.ACCEPTED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 대기 중인 주문만 수락이 가능합니다.")
        @Test
        void accept_fail_because_not_waiting_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, ACCEPTED, 테이블_4명_점유중, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.accept(givenOrder.getId()));
        }

        @DisplayName("[정상] 주문의 서빙이 완료 됩니다.")
        @Test
        void serve_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, ACCEPTED, 테이블_4명_점유중, 주문품목_후라이드치킨세트));

            orderService.serve(givenOrder.getId());

            assertEquals(OrderStatus.SERVED, givenOrder.getStatus());
        }


        @DisplayName("[예외] 수락된 주문만 서빙이 가능합니다.")
        @Test
        void serve_fail_because_not_accepted_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, WAITING, 테이블_4명_점유중, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.serve(givenOrder.getId()));
        }

        @DisplayName("[예외] 매장식사는 배달 시작를 할 수 없습니다.")
        @Test
        void start_delivery_fail_because_not_eat_in_order_type() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, SERVED, 테이블_4명_점유중, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(givenOrder.getId()));
        }

        @DisplayName("[버그 예상] 예상 정상 동작: '매장 식사는 배달 완료 처리가 불가합니다' / 실제 : '매장 식사도 배달 중 상태인 경우 배달 완료 처리가 가능합니다.'")
        @Test
        void complete_delivery_fail_because_not_eat_in_order_type() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, DELIVERING, 테이블_4명_점유중, 주문품목_후라이드치킨세트));

            orderService.completeDelivery(givenOrder.getId());

            assertEquals(DELIVERED, givenOrder.getStatus());
        }

        @DisplayName("[버그 예상] " +
                "예상 정상 동작: '서빙 완료된 주문을 완료 처리하고 관련된 주문 테이블이 있는 경우 정리합니다.' / " +
                "실제 : '서빙 완료된 주문을 완료 처리하고 관련된 주문 테이블이 없는 경우 정리합니다.'")
        @Test
        void complete_bug_about_exist_order_table_not_clear() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            OrderTable orderTable = orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, SERVED, 테이블_4명_점유중, 주문품목_후라이드치킨세트));

            orderService.complete(givenOrder.getId());

            assertEquals(COMPLETED, givenOrder.getStatus());
            assertEquals(0, orderTable.getNumberOfGuests());
            assertFalse(orderTable.isOccupied());
        }

        @DisplayName("[예외] 서빙 완료된 주문만 완료 처리할 수 있습니다.")
        @Test
        void complete_error_because_no_serverd_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            orderTableRepository.save(테이블_4명_점유중);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.EAT_IN, WAITING, 테이블_4명_점유중, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.complete(givenOrder.getId()));
        }

    }

    @DisplayName("'포장' 주문 유형인 주문에 대해 주문 상태를 변경합니다.")
    @Nested
    class changeStatusOfTakeoutOrderType {

        @DisplayName("[정상] 주문이 수락됩니다.")
        @Test
        void accept_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, WAITING, 주문품목_후라이드치킨세트));

            orderService.accept(givenOrder.getId());

            assertEquals(OrderStatus.ACCEPTED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 대기 중인 주문만 수락이 가능합니다.")
        @Test
        void accept_fail_because_not_waiting_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, ACCEPTED, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.accept(givenOrder.getId()));
        }

        @DisplayName("[정상] 주문의 서빙이 완료 됩니다.")
        @Test
        void serve_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, ACCEPTED, 주문품목_후라이드치킨세트));

            orderService.serve(givenOrder.getId());

            assertEquals(OrderStatus.SERVED, givenOrder.getStatus());
        }


        @DisplayName("[예외] 수락된 주문만 서빙이 가능합니다.")
        @Test
        void serve_fail_because_not_accepted_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, WAITING, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.serve(givenOrder.getId()));
        }

        @DisplayName("[예외] 포장은 배달 시작를 할 수 없습니다.")
        @Test
        void start_delivery_fail_because_not_eat_in_order_type() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, SERVED, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(givenOrder.getId()));
        }

        @DisplayName("[버그 예상] 예상 정상 동작: '포장은 배달 완료 처리가 불가합니다' / 실제 : '포장도 배달 중 상태인 경우 배달 완료 처리가 가능합니다.'")
        @Test
        void complete_delivery_fail_because_not_eat_in_order_type() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, DELIVERING, 주문품목_후라이드치킨세트));

            orderService.completeDelivery(givenOrder.getId());

            assertEquals(DELIVERED, givenOrder.getStatus());
        }

        @DisplayName("[정상] 서빙 완료인 주문을 완료 처리합니다.")
        @Test
        void complete_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, SERVED, 주문품목_후라이드치킨세트));

            orderService.complete(givenOrder.getId());

            assertEquals(COMPLETED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 서빙 완료된 주문만 완료 처리할 수 있습니다.")
        @Test
        void complete_error_because_no_serverd_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.TAKEOUT, WAITING, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.complete(givenOrder.getId()));
        }

    }
    @DisplayName("'배달' 주문 유형인 주문에 대해 주문 상태를 변경합니다.")
    @Nested
    class changeStatusOfDeliveryOrderType {

        @DisplayName("[정상] 주문이 수락됩니다.")
        @Test
        void accept_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, 주문품목_후라이드치킨세트));

            orderService.accept(givenOrder.getId());

            assertEquals(OrderStatus.ACCEPTED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 대기 중인 주문만 수락이 가능합니다.")
        @Test
        void accept_fail_because_not_waiting_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, ACCEPTED, 주문품목_후라이드치킨세트));


            assertThrows(IllegalStateException.class, () -> orderService.accept(givenOrder.getId()));
        }

        @DisplayName("[정상] 주문의 서빙이 완료 됩니다.")
        @Test
        void serve_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, ACCEPTED, 주문품목_후라이드치킨세트));


            orderService.serve(givenOrder.getId());

            assertEquals(OrderStatus.SERVED, givenOrder.getStatus());
        }


        @DisplayName("[예외] 수락된 주문만 서빙이 가능합니다.")
        @Test
        void serve_fail_because_not_accepted_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.serve(givenOrder.getId()));
        }

        @DisplayName("[정상] 배달 시작을 시작합니다.")
        @Test
        void start_delivery_fail_because_not_eat_in_order_type() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, SERVED, 주문품목_후라이드치킨세트));

            orderService.startDelivery(givenOrder.getId());

            assertEquals(DELIVERING, givenOrder.getStatus());
        }

        @DisplayName("[예외] 서빙 완료된 주문만 배달을 시작할 수 있습니다.")
        @Test
        void serve_fail_because_not_served_status() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.startDelivery(givenOrder.getId()));
        }


        @DisplayName("[정상] 배달이 완료된 주문을 배달완료 처리 합니다.")
        @Test
        void complete_delivery_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, DELIVERING, 주문품목_후라이드치킨세트));

            orderService.completeDelivery(givenOrder.getId());

            assertEquals(DELIVERED, givenOrder.getStatus());
        }
        @DisplayName("[예외] 배달 중이지 않은 주문은 배달 완료 처리가 불가합니다")
        @Test
        void complete_delivery_fail_because_because_not_start_delivery() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.completeDelivery(givenOrder.getId()));
        }

        @DisplayName("[정상] 배달 완료된 주문을 완료 처리 합니다.")
        @Test
        void complete_success() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, DELIVERED, 주문품목_후라이드치킨세트));

            orderService.complete(givenOrder.getId());

            assertEquals(COMPLETED, givenOrder.getStatus());
        }

        @DisplayName("[예외] 배달 완료가 아닌 주문은 완료 처리 할 수 없습니다.")
        @Test
        void complete_fail_because_no_delivered() {
            productRepository.saveAll(상품_치킨_콜라);
            menuGroupRepository.save(메뉴그룹_기본);
            menuRepository.save(메뉴_후라이드치킨세트);
            Order givenOrder = orderRepository.save(OrderFixture.create(UUID.randomUUID(), OrderType.DELIVERY, WAITING, 주문품목_후라이드치킨세트));

            assertThrows(IllegalStateException.class, () -> orderService.complete(givenOrder.getId()));
        }

    }

}
