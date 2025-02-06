package kitchenpos.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import kitchenpos.application.OrderService;
import kitchenpos.application.fixture.MenuFixture;
import kitchenpos.application.fixture.OrderFixture;
import kitchenpos.application.fixture.OrderLineItemFixture;
import kitchenpos.application.fixture.OrderTableFixture;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    private Order order;
    private Menu chickenMenu;

    @BeforeEach
    void setUp() {
        chickenMenu = MenuFixture.init().create();
        order = OrderFixture.init().create();
    }

    @Nested
    @DisplayName("주문 조회")
    class 주문_조회 {

        @Test
        @DisplayName("성공 : 특정 조건 없이 상품의 모든 목록을 조회할 수 있다.")
        void 주문목록_조회() {
            when(orderRepository.findAll()).thenReturn(List.of(order));
            List<Order> result = orderService.findAll();

            assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertEquals(result.size(), 1)
            );
        }
    }

    @Nested
    @DisplayName("주문 등록")
    class 주문_등록 {

        @Test
        @DisplayName("성공")
        void 주문등록_성공() {
            mockCreateOrder();

            var result = orderService.create(order);

            assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getType(), order.getType()),
                () -> assertEquals(result.getStatus(), order.getStatus()),
                () -> assertEquals(result.getOrderDateTime(), order.getOrderDateTime()),
                () -> assertEquals(result.getOrderLineItems(), order.getOrderLineItems()),
                () -> assertEquals(result.getDeliveryAddress(), order.getDeliveryAddress()),
                () -> assertEquals(result.getOrderTable(), order.getOrderTable())
            );

        }

        @ParameterizedTest
        @DisplayName("배달, 먹고가기, 포장(주문 유형)이 반드시 있어야 한다.")
        @NullSource
        void 주문유형_있는지_검사(final OrderType orderType) {
            order = OrderFixture.test(
                orderType,
                null,
                null,
                null,
                null,
                null
            ).create();

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
        }

        @ParameterizedTest
        @DisplayName("주문 아이템이 반드시 있어야 한다.")
        @NullAndEmptySource
        void 주문아이템_있는지_검사(final List<OrderLineItem> orderLineItems) {
            order = OrderFixture.test(
                OrderType.DELIVERY,
                null,
                null,
                orderLineItems,
                null,
                null
            ).create();

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
        }

        @ParameterizedTest
        @DisplayName("주문 아이템의 수량은 0개 이상이어야 한다.")
        @ValueSource(ints = {-100, 0, 100})
        void 주문아이템_수량이_0개이상_인지_검사(final int qty) {
            order = OrderFixture.test(
                OrderType.DELIVERY,
                null,
                null,
                List.of(OrderLineItemFixture.test(
                    null,
                    qty,
                    null
                ).create()),
                null,
                OrderTableFixture.init().create()
            ).create();

            if (qty < 0) {
                mockFindAllByMenu(order);
                assertThatExceptionOfType(IllegalArgumentException.class)
                    .isThrownBy(() -> orderService.create(order));
            }
        }

        @Test
        @DisplayName("주문 아이템의 메뉴는 반드시 있어야 한다.")
        void 주문아이템의_메뉴가_존재하는지_검사() {
            mockFindAllByMenu(order);
            assertThatExceptionOfType(NoSuchElementException.class)
                .isThrownBy(() -> orderService.create(order));
        }

        @Test
        @DisplayName("메뉴가 노출된 상태여야 한다.")
        void 메뉴가_노출상태인지_검사() {
            chickenMenu.setDisplayed(false);

            mockFindAllByMenu(order);
            mockFindByMenu(chickenMenu);

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(order));
        }

        @Test
        @DisplayName("메뉴가격과 주문 아이템 가격이 다르면 안된다.")
        void 메뉴가격_주문아이템가격_비교() {
            chickenMenu = MenuFixture.test(null, BigDecimal.valueOf(500_000), null, true, null)
                .create();

            mockFindAllByMenu(order);

            mockFindByMenu(chickenMenu);

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
        }

        @ParameterizedTest
        @DisplayName("배달 주문시 배달 주소가 반드시 있어야 한다.")
        @NullAndEmptySource
        void 배달일경우_배달지주소_여부검사(final String address) {
            order.setDeliveryAddress(address);

            mockFindAllByMenu(order);

            mockFindByMenu(chickenMenu);

            assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> orderService.create(order));
        }

        @Test
        @DisplayName("먹고가기(주문유형)의 경우 주문 테이블내역이 있어야 한다.")
        void 먹고가기일경우_주문테이블내역_여부검사() {
            order.setType(OrderType.EAT_IN);
            order.getOrderTable().setOccupied(false);

            mockFindAllByMenu(order);

            mockFindByMenu(chickenMenu);

            mockFindByOrderTable(order.getOrderTable());

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.create(order));
        }
    }

    @Nested
    @DisplayName("주문 수락")
    class 주문_수락 {

        @Test
        @DisplayName("성공")
        void 주문수락_성공() {
            mockFindByOrder();

            assertThatCode(() -> {
                orderService.accept(order.getId());
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("현 주문상태가 **대기**이어야 한다.")
        void 주문상태_대기인지_검사() {
            order = OrderFixture.test(
                null,
                OrderStatus.ACCEPTED,
                null,
                null,
                null,
                null
            ).create();

            mockFindByOrder();

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.accept(order.getId()));
        }

        @Test
        @DisplayName("배달 주문인 경우, 라이더에게 주문번호, 주문 아이템의 총 금액, 배달 주소를 전달해 배달 요청한다.")
        void 배달주문_라이더에게_배달정보_전달_후_배달요청() {
            mockFindByOrder();

            var result = orderService.accept(order.getId());

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("서빙/준비 완료")
    class 서빙_준비_완료 {

        @Test
        @DisplayName("성공")
        void 주문수락_성공() {
            order.setStatus(OrderStatus.ACCEPTED);

            mockFindByOrder();

            assertThatCode(() -> {
                orderService.serve(order.getId());
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("현 주문상태가 **수락**이어야 한다.")
        void 주문상태_수락인지_검사() {
            mockFindByOrder();
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.serve(order.getId()));

        }
    }

    @Nested
    @DisplayName("배달 시작")
    class 배달_시작 {

        @Test
        @DisplayName("성공")
        void 배달시작_성공() {
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.SERVED);

            mockFindByOrder();

            assertThatCode(() -> {
                orderService.startDelivery(order.getId());
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("주문 유형이 **배달**이어야 한다.")
        void 주문유형_배달인지_검사() {
            order = OrderFixture.test(
                OrderType.EAT_IN,
                null,
                null,
                null,
                null,
                null
            ).create();
            mockFindByOrder();
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(order.getId()));
        }

        @Test
        @DisplayName("현 주문상태가 **서빙/준비 완료**이어야 한다.")
        void 주문상태_서빙완료인지_검사() {
            mockFindByOrder();
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.startDelivery(order.getId()));
        }
    }

    @Nested
    @DisplayName("배달 완료")
    class 배달_완료 {

        @Test
        @DisplayName("성공")
        void 배달완료_성공() {
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERING);

            mockFindByOrder();

            assertThatCode(() -> {
                orderService.completeDelivery(order.getId());
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("현 주문상태가 **배달중**이어야 한다.")
        void 주문상태_배달중인지_검사() {
            mockFindByOrder();
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.completeDelivery(order.getId()));
        }
    }

    @Nested
    @DisplayName("주문 완료")
    class 주문_완료 {

        @Test
        @DisplayName("성공")
        void 주문완료_성공() {
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERED);

            mockFindByOrder();

            assertThatCode(() -> {
                orderService.complete(order.getId());
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("배달(주문유형)인데 배달완료(주문상태)가 아니면 안된다.")
        void 배달이면_배달완료인지_검사() {
            mockFindByOrder();

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(order.getId()));
        }

        @Test
        @DisplayName("포장, 먹고가기(주문유형)일 경우 서빙완료(주문상태)이어야 한다.")
        void 포장_먹고가기이면_서빙완료인지_검사() {
            order = OrderFixture.test(
                OrderType.TAKEOUT,
                OrderStatus.DELIVERED,
                null,
                null,
                null,
                null
            ).create();

            mockFindByOrder();

            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> orderService.complete(order.getId()));
        }

        @Test
        @DisplayName("먹고가기(주문유형)일 경우, 해당 주문을 완료 처리 하고 해당 테이블에 다른 진행 중인 주문이 없다면 테이블을 비우고 인원 수를 0명으로 설정한다.")
        void 먹고가기이면_주문완료처리하고_테이블_초기화처리() {
            order = OrderFixture.test(
                OrderType.EAT_IN,
                OrderStatus.SERVED,
                null,
                List.of(OrderLineItemFixture.init().create()),
                null,
                OrderTableFixture.init().create()
            ).create();

            mockFindByOrder();

            mockExistsByOrderTable(order, false);

            var result = orderService.complete(order.getId());

            assertAll(
                () -> assertEquals(result.getOrderTable().getNumberOfGuests(), 0),
                () -> assertFalse(result.getOrderTable().isOccupied())
            );
        }


    }

    private void mockFindByOrder() {
        when(orderRepository.findById(Mockito.any()))
            .thenReturn(Optional.of(order));
    }

    private void mockExistsByOrderTable(Order order, boolean status) {
        when(orderRepository.existsByOrderTableAndStatusNot(order.getOrderTable(),
            OrderStatus.COMPLETED))
            .thenReturn(status);
    }

    private void mockFindAllByMenu(Order order) {
        when(menuRepository.findAllByIdIn(Mockito.any()))
            .thenReturn(order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList()));
    }

    private void mockFindByMenu(Menu menu) {
        when(menuRepository.findById(Mockito.any()))
            .thenReturn(Optional.of(chickenMenu));
    }

    private void mockFindByOrderTable(OrderTable orderTable) {
        when(orderTableRepository.findById(Mockito.any()))
            .thenReturn(Optional.of(orderTable));
    }

    private void mockSaveMenu() {
        when(orderRepository.save(Mockito.any(Order.class))).thenReturn(order);
    }

    private void mockCreateOrder() {
        mockFindAllByMenu(order);
        mockFindByMenu(chickenMenu);
        mockSaveMenu();
    }

}
