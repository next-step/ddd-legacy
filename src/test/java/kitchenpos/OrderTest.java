package kitchenpos;

import kitchenpos.application.*;
import kitchenpos.domain.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import static kitchenpos.TestConstant.*;
import static kitchenpos.domain.OrderStatus.*;
import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName(value = " Order 테스트")
public class OrderTest {
    private static final BigDecimal 후라이드치킨_DEFAULT_PRICE = new BigDecimal(20000);
    private static final BigDecimal 후라이드치킨_OVER_PRICE = new BigDecimal(21000);
    private static final UUID ORDER_TABLE_ID = UUID.fromString("8d710043-29b6-420e-8452-233f5a035520");
    private static final UUID 후라이드치킨_MENU_UUID = UUID.fromString("f59b1e1c-b145-440a-aa6f-6095a0e2d63b");

    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private FakeKitchenridersClient fakeKitchenridersClient;
    private OrderRepository orderRepository;

    private OrderService orderService;

    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        orderRepository = new InMemoryOrderRepository();
        orderTableRepository = new InMemoryOrderTableRepository();
        menuRepository = new InMemoryMenuRepository();
        menuGroupRepository = new InMemoryMenuGroupRepository();
        fakeKitchenridersClient = new FakeKitchenridersClient();
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, fakeKitchenridersClient);
    }

    @DisplayName(value = "주문 추가 기능")
    @Nested
    class OrderCreateTest {

        @DisplayName(value = "주문 추가기능 & 주문 검증이 끝나면 주문대기(WAITING) 상태가 됩니다")
        @Test
        void validateStateWaiting() {
            menuRepository.save(createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct()));
            Order orderRequest = createOrder();
            Order orderResponse = orderService.create(orderRequest);

            assertAll(
                    () -> assertThat(orderResponse).isNotNull(),
                    () -> assertThat(orderResponse.getDeliveryAddress()).isEqualTo(orderRequest.getDeliveryAddress()),
                    () -> assertThat(orderResponse.getId()).isNotNull(),
                    () -> assertThat(orderResponse.getType()).isEqualTo(orderRequest.getType()),
                    () -> assertThat(orderResponse.getOrderDateTime()).isNotNull(),
                    () -> assertThat(orderResponse.getOrderLineItems()).hasSize(1),
                    () -> assertThat(orderResponse.getStatus()).isEqualTo(WAITING),
                    () -> assertThat(orderRepository.findById(orderResponse.getId()).get().getStatus()).isEqualTo(WAITING)

            );
        }

        @DisplayName(value = "주문 타입이 없으면 안됩니다.")
        @Test
        void invalidOrderType() {
            Order order = createOrder(ORDER_UUID, ORDER_TYPE_미선택, ORDER_STATUS_주문대기, ORDER_DATE_TIME_주문요청시간,
                    createOrderLineItem(), "강남구");
            assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
        }

        @DisplayName(value = "주문은 최소한 1개 이상의 주문상품을 선택해야 합니다.")
        @Test
        void invalidOrderLineItems() {
            Order order = createOrder(ORDER_TYPE_미선택);
            assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
        }

        @DisplayName(value = "주문상품의 개수와 주문상품속 메뉴들의 총 개수는 같아야 합니다.")
        @Test
        void notEqualMenuAndOrderLineItemSize() {
            OrderLineItem orderLineItem = createOrderLineItem((UUID) null, DEFAULT_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
            Order order = createOrder(orderLineItem, "강남구");
            assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
        }

        @DisplayName(value = "배달이거나 포장주문일 경우, 주문상품의 수량은 0개 이상이어야 합니다.")
        @Test
        void invalidOrderLineItemSize() {
            OrderLineItem orderLineItem = createOrderLineItem(후라이드치킨_MENU_UUID, MINUS_QUANTITY, 후라이드치킨_DEFAULT_PRICE);
            Order order = createOrder(orderLineItem, "강남구");
            assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(order));
        }

        @DisplayName(value = "선택된 메뉴가 비노출되어 있는 상태면 안됩니다.")
        @Test
        void selectedMenuNotDisplayed() {
            var menuId = menuRepository.save(createMenu(후라이드치킨_DEFAULT_PRICE, false, createMenuProduct())).getId();
            Order order = createOrder(createOrderLineItem(menuId), "강남구");
            assertThatIllegalStateException().isThrownBy(() -> orderService.create(order));
        }

        @DisplayName(value = "선택된 메뉴의 금액과 주문요청한 메뉴의 금액은 같아야 합니다.")
        @Test
        void notEqualMenuAndOrderMenuPrice() {
            OrderLineItem orderLineItem = createOrderLineItem(후라이드치킨_MENU_UUID, DEFAULT_QUANTITY, 후라이드치킨_OVER_PRICE);
            assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(createOrder(orderLineItem, "강남구")));
        }

        @DisplayName(value = "배달주문인 경우, 반드시 배달 주소를 입력하여 배달기사에게 전달합니다.")
        @Test
        void validateDeliveryAddress() {
            assertThatIllegalArgumentException().isThrownBy(() -> orderService.create(createOrder(createOrderLineItem(), "")));
        }

        @DisplayName(value = "매장내 식사일 경우, 배정요청한 주문 테이블이 사용가능이어야 합니다.")
        @Test
        void validateEatInOrderTable() {
            var menuId = menuRepository.save(createMenu(후라이드치킨_DEFAULT_PRICE, false, createMenuProduct())).getId();
            OrderTable orderTable = createOrderTable(ORDER_TABLE_ID, ORDER_TABLE_NAME, 0, TABLE_UNUSABLE);
            orderTableRepository.save(orderTable);
            Order order = createOrder(ORDER_UUID, ORDER_TYPE_매장내식사주문, ORDER_STATUS_주문대기, ORDER_DATE_TIME_주문요청시간,
                    createOrderLineItem(menuId), "강남구", orderTable, orderTable.getId());
            assertThatIllegalStateException().isThrownBy(() -> orderService.create(order));
        }
    }

    @DisplayName(value = "주문 접수 기능")
    @Nested
    class OrderAcceptTest {
        @DisplayName(value = "시작 주문의 상태가 주문대기(WAITING)이고, 주문 수락(ACCEPTED) 상태가 되어야 합니다")
        @Test
        void validateStateWaiting() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu));
            orderRepository.save(orderRequest);
            Order orderResponse = orderService.accept(orderRequest.getId());

            assertAll(
                    () -> assertThat(orderResponse).isNotNull(),
                    () -> assertThat(orderResponse.getId()).isNotNull(),
                    () -> assertThat(orderResponse.getStatus()).isEqualTo(ACCEPTED),
                    () -> assertThat(orderRepository.findById(orderResponse.getId()).get().getStatus()).isEqualTo(ACCEPTED)
            );
        }
    }

    @DisplayName(value = "상품 제공 기능.")
    @Nested
    class OrderServedTest {
        @DisplayName(value = "시작 주문의 상태가 주문 수락(ACCEPTED) 상태여야 합니다.")
        @Test
        void validateStateAccepted() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_STATUS_주문대기);
            orderRepository.save(orderRequest);
            assertThatIllegalStateException().isThrownBy(() -> orderService.serve(orderRequest.getId()));
        }

        @DisplayName(value = "제공이 완료되면 상태를 제공완료(SERVED)로 변경합니다.")
        @Test
        void validateStateServed() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_STATUS_주문수락);
            orderRepository.save(orderRequest);
            var serveResponse = orderService.serve(orderRequest.getId());
            assertThat(serveResponse.getStatus()).isEqualTo(SERVED);
        }
    }

    @DisplayName(value = "배달 주문 배달 시작 기능.")
    @Nested
    class OrderStartDeliveryTest {
        @DisplayName(value = "주문 타입이 매장내 식사가 아닌 배달이어야 합니다")
        @Test
        void invalidOrderStatus() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_TYPE_매장내식사주문, ORDER_STATUS_주문대기);
            orderRepository.save(orderRequest);
            assertThatIllegalStateException().isThrownBy(() -> orderService.startDelivery(orderRequest.getId()));
        }

        @DisplayName(value = "요청 주문 상태가 제공완료(SERVED)상태가 아니면 안됩니다.")
        @Test
        void invalidStateServed() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_TYPE_배달주문, ORDER_STATUS_주문대기);
            orderRepository.save(orderRequest);
            assertThatIllegalStateException().isThrownBy(() -> orderService.startDelivery(orderRequest.getId()));
        }

        @DisplayName(value = "제공이 완료되면 상태를 배달중(DELIVERING)으로 변경합니다.")
        @Test
        void validateStateServed() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_TYPE_배달주문, ORDER_STATUS_제공완료);
            orderRepository.save(orderRequest);
            var serveResponse = orderService.startDelivery(orderRequest.getId());
            assertThat(serveResponse.getStatus()).isEqualTo(DELIVERING);
        }
    }

    @DisplayName(value = "배달 완료 기능.")
    @Nested
    class OrderCompleteDeliveryTest {
        @DisplayName(value = "요청된 주문의 상태는 배달중(DELIVERING)이어야 합니다.")
        @Test
        void validateOrderStatusDelivering() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_TYPE_매장내식사주문, ORDER_STATUS_주문대기);
            orderRepository.save(orderRequest);
            assertThatIllegalStateException().isThrownBy(() -> orderService.completeDelivery(orderRequest.getId()));
        }

        @DisplayName(value = "배달이 완료되면 상태를 배달 완료(DELIVERED)으로 변경합니다.")
        @Test
        void validateStateDelivered() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_TYPE_배달주문, ORDER_STATUS_배달중);
            orderRepository.save(orderRequest);
            var serveResponse = orderService.completeDelivery(orderRequest.getId());
            assertThat(serveResponse.getStatus()).isEqualTo(ORDER_STATUS_배달완료);
        }
    }

    @DisplayName(value = "주문 완료 기능.")
    @Nested
    class OrdercompleteTest {
        @DisplayName(value = "배달인 주문일 경우 현재 상태가 배달 완료(DELIVERED)여야 합니다.")
        @Test
        void valideOrderDelivered() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_TYPE_배달주문, ORDER_STATUS_주문대기);
            orderRepository.save(orderRequest);
            assertThatIllegalStateException().isThrownBy(() -> orderService.complete(orderRequest.getId()));
        }

        @DisplayName(value = "포장이나 매장내 식사 주문일 경우 현재 상태가 제공완료(SERVED)이어야 합니다.")
        @ParameterizedTest
        @CsvSource(value = {"DELIVERY", "TAKEOUT"})
        void validateNotServedTypeState(String orderType) {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            Order orderRequest = createOrder(createOrderLineItem(menu), OrderType.valueOf(orderType), ORDER_STATUS_배달중);
            orderRepository.save(orderRequest);
            assertThatIllegalStateException().isThrownBy(() -> orderService.complete(orderRequest.getId()));
        }

        @DisplayName(value = "매장내 식사 주문일 경우 제공된 주문 테이블이 제공완료 상태이면 테이블을 정리합니다.")
        @Test
        void eatInStateClearTable() {
            var menu = createMenu(후라이드치킨_DEFAULT_PRICE, true, createMenuProduct());
            menuRepository.save(menu);
            var orderTable = createOrderTable();
            orderTableRepository.save(orderTable);
            Order orderRequest = createOrder(createOrderLineItem(menu), ORDER_TYPE_매장내식사주문, ORDER_STATUS_제공완료, orderTable);
            orderRepository.save(orderRequest);
            var complete = orderService.complete(orderRequest.getId());
            assertAll(
                    () -> assertThat(complete.getOrderTable().isOccupied()).isFalse(),
                    () -> assertThat(complete.getOrderTable().getNumberOfGuests()).isZero(),
                    () -> assertThat(complete.getStatus()).isEqualTo(ORDER_STATUS_주문완료)
            );
        }
    }


    @DisplayName(value = "모든 주문 조회 기능.")
    @Nested
    class OrderFindAllTest {

        @DisplayName(value = "모든 주문을 조회할 수 있다.")
        @Test
        void findAllTest() {
            Order orderRequest = createOrder();
            orderRepository.save(orderRequest);
            var orders = orderService.findAll();

            assertThat(orders.size()).isOne();
        }
    }

}
