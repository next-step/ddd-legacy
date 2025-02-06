package kitchenpos.application;

import kitchenpos.application.fixture.*;
import kitchenpos.domain.*;
import kitchenpos.infra.FakeKitchenridersClient;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

class OrderTableServiceTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private ProductRepository productRepository;
    private OrderTableRepository orderTableRepository;
    private KitchenridersClient kitchenridersClient;
    private OrderService orderService;
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        this.orderRepository = new InMemoryOrderRepository();
        this.menuRepository = new InMemoryMenuRepository();
        this.menuGroupRepository = new InMemoryMenuGroupRepository();
        this.productRepository = new InMemoryProductRepository();
        this.orderTableRepository = new InMemoryOrderTableRepository();
        this.kitchenridersClient = new FakeKitchenridersClient();
        this.orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, kitchenridersClient);
        this.orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블을 등록할 수 있다")
    @Nested
    class OrderTableCreator {

        @DisplayName("주문 테이블의 이름을 지정하여 생성할 수 있다")
        @Test
        void createOrderTable() {
            OrderTable request = OrderTableFixture.createOrderTable("1번테이블");

            OrderTable orderTable = orderTableService.create(request);

            assertThat(orderTable.getName()).isEqualTo("1번테이블");
        }

        @DisplayName("주문 테이블의 이름은 공백만 입력할 수 없으며 반드시 입력되어야 한다")
        @NullAndEmptySource
        @ParameterizedTest
        void notNullTableName(String name) {
            OrderTable request = OrderTableFixture.createOrderTable(name);

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderTableService.create(request));
        }

        @DisplayName("주문 테이블 생성 시, 고객의 수는 0명, 테이블의 사용유무는 사용안함이다")
        @Test
        void createOrderTableWithNullName() {
            OrderTable request = OrderTableFixture.createOrderTable("1번");

            OrderTable orderTable = orderTableService.create(request);

            assertThat(orderTable.getNumberOfGuests()).isZero();
            assertThat(orderTable.isOccupied()).isFalse();
        }
    }

    //region [고객을 주문 테이블에 배정]
    @DisplayName("주문 테이블에 고객이 배정되면 테이블의 사용유무가 사용 중으로 바뀐다")
    @Test
    void sit() {
        OrderTable orderTable = orderTableService.create(OrderTableFixture.createOrderTable("1번테이블"));

        OrderTable resultOrderTable = orderTableService.sit(orderTable.getId());

        assertThat(resultOrderTable.isOccupied()).isTrue();
    }
    //endregion

    @Nested
    @DisplayName("주문 테이블을 정리할 수 있다")
    class TableCleaner {

        @DisplayName("테이블의 주문 상태가 완료가 아니면 정리할 수 없다")
        @EnumSource(value = OrderStatus.class, names = "COMPLETED", mode = EnumSource.Mode.EXCLUDE)
        @ParameterizedTest
        void validateOrderStatus(OrderStatus status) {
            //given
            OrderTable orderTable = orderTableService.create(OrderTableFixture.createOrderTable("1번테이블"));
            orderTableService.sit(orderTable.getId());
            ReflectionTestUtils.setField(orderTable, "numberOfGuests", 4);
            orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

            Order order = createUnnamedOrder(orderTable.getId());
            //when, then
            ReflectionTestUtils.setField(order, "status", status);
            assertThatIllegalStateException()
                    .isThrownBy(() -> orderTableService.clear(orderTable.getId()));
        }

        @DisplayName("테이블 정리하면 손님의 수 0명, 테이블 사용유무 안함으로 변경된다")
        @Test
        void clear() {
            //given
            OrderTable orderTable = orderTableService.create(OrderTableFixture.createOrderTable("1번테이블"));
            orderTableService.sit(orderTable.getId());
            ReflectionTestUtils.setField(orderTable, "numberOfGuests", 4);
            orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

            Order order = createUnnamedOrder(orderTable.getId());
            //when
            ReflectionTestUtils.setField(order, "status", OrderStatus.COMPLETED);
            OrderTable clearOrder = orderTableService.clear(orderTable.getId());
            //then
            assertThat(clearOrder.getNumberOfGuests()).isZero();
            assertThat(clearOrder.isOccupied()).isFalse();
        }
    }

    @DisplayName("주문 테이블의 고객의 수를 변경할 수 있다")
    @Nested
    class GuestNumberUpdater {

        @DisplayName("테이블의 고객 수를 0미만으로 변경할 수 없다")
        @Test
        void changeNumberOfGuestsByNegative() {
            OrderTable orderTable = orderTableService.create(OrderTableFixture.createOrderTable("1번테이블"));

            ReflectionTestUtils.setField(orderTable, "numberOfGuests", -1);

            assertThatIllegalArgumentException()
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }

        @DisplayName("테이블의 사용유무가 사용안함이면 손님의 수를 변경할 수 없다")
        @Test
        void changeNumberOfGuestsByUnUse() {
            OrderTable orderTable = orderTableService.create(OrderTableFixture.createOrderTable("1번테이블"));

            ReflectionTestUtils.setField(orderTable, "occupied", false);
            ReflectionTestUtils.setField(orderTable, "numberOfGuests", 10);

            assertThatIllegalStateException()
                    .isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable));
        }

    }

    //region [모든 주문 테이블 조회]
    @DisplayName("모든 테이블 조회가 가능하다")
    @Test
    void findAll() {
        orderTableService.create(OrderTableFixture.createOrderTable("1번테이블"));
        orderTableService.create(OrderTableFixture.createOrderTable("2번테이블"));
        orderTableService.create(OrderTableFixture.createOrderTable("3번테이블"));

        List<OrderTable> orderTables = orderTableService.findAll();

        assertThat(orderTables).hasSize(3);
        assertThat(orderTables)
                .extracting(OrderTable::getName)
                .contains("1번테이블", "2번테이블", "3번테이블");
    }
    //endregion

    private Order createEatInOrder(List<OrderLineItem> orderLineItems, UUID orderTableId, LocalDateTime orderDateTime) {
        return OrderFixture.createOrder(OrderType.EAT_IN, orderLineItems, null, orderTableId, orderDateTime);
    }

    private Order createUnnamedOrder(UUID orderTableId) {
        Product unnamedProduct = productRepository.save(ProductFixture.createProduct(UUID.randomUUID(), "unnamed", BigDecimal.ZERO));
        MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.createMenuGroup(UUID.randomUUID(), "unnamed"));
        MenuProduct displayMenuProduct = MenuProductFixture.createMenuProduct(unnamedProduct, 1);
        Menu unknownMenu = menuRepository.save(MenuFixture.createMenu(UUID.randomUUID(), menuGroup, menuGroup.getId(), "unnamed", BigDecimal.ZERO, true, List.of(displayMenuProduct)));

        OrderLineItem orderLineItem = OrderFixture.createOrderLineItem(unknownMenu.getId(), BigDecimal.ZERO, 1);
        return orderService.create(createEatInOrder(List.of(orderLineItem), orderTableId, LocalDateTime.now()));
    }
}