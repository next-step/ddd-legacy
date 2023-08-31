package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.fixture.MenuProductFixture;
import kitchenpos.support.BaseServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuGroupFixture.createMenuGroup;
import static kitchenpos.fixture.MenuProductFixture.createMenuProductWithDefaultId;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class OrderTableServiceTest extends BaseServiceTest {
    private final OrderTableService orderTableService;
    private final OrderTableRepository orderTableRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final ProductRepository productRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;

    public OrderTableServiceTest(final OrderTableService orderTableService, final OrderTableRepository orderTableRepository, final MenuGroupRepository menuGroupRepository, final ProductRepository productRepository, final MenuRepository menuRepository, final OrderRepository orderRepository) {
        this.orderTableService = orderTableService;
        this.orderTableRepository = orderTableRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.productRepository = productRepository;
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
    }

    @DisplayName("등록")
    @Nested
    class Create {
        @DisplayName("테이블은 등록이 가능하며 청소된 상태로 등록된다")
        @Test
        void test1() {
            final OrderTable orderTable = createOrderTable(5, true);

            final OrderTable createdOrderTable = orderTableService.create(orderTable);

            final OrderTable foundOrderTable = orderTableRepository.findAll().get(0);

            assertAll(
                    () -> assertThat(createdOrderTable.getId()).isNotNull(),
                    () -> assertThat(createdOrderTable.getName()).isEqualTo(orderTable.getName()),
                    () -> assertThat(createdOrderTable.getNumberOfGuests()).isEqualTo(0),
                    () -> assertThat(createdOrderTable.isOccupied()).isFalse(),
                    () -> assertThat(foundOrderTable.getId()).isEqualTo(createdOrderTable.getId())
            );
        }

        @DisplayName("테이블의 이름은 공백이면 안된다.")
        @NullAndEmptySource
        @ParameterizedTest
        void test2(final String name) {
            final OrderTable orderTable = createOrderTable(name, 5, true);

            assertThatIllegalArgumentException().isThrownBy(() -> orderTableService.create(orderTable));
        }
    }

    @DisplayName("착석")
    @Nested
    class Sit {
        @DisplayName("테이블은 착석이 가능하다.")
        @Test
        void test1() {
            final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));

            orderTableService.sit(orderTable.getId());

            assertThat(orderTable.isOccupied()).isTrue();
        }
    }


    @DisplayName("전체 조회")
    @Nested
    class Clear {
        @DisplayName("테이블은 청소가 가능하다.")
        @Test
        void test1() {
            final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup(UUID.randomUUID()));
            final Product product = productRepository.save(createProduct(UUID.randomUUID()));
            final MenuProduct menuProduct = MenuProductFixture.createMenuProductWithDefaultId(product);
            final Menu menu = menuRepository.save(createMenu(UUID.randomUUID(), BigDecimal.ONE, menuGroup, true, List.of(menuProduct)));
            final List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(menu, 1, BigDecimal.TEN));
            final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, true));
            orderRepository.save(createOrder(UUID.randomUUID(), OrderType.EAT_IN, OrderStatus.COMPLETED, null, orderLineItems, orderTable));

            orderTableService.clear(orderTable.getId());

            assertAll(
                    () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                    () -> assertThat(orderTable.isOccupied()).isFalse()
            );
        }

        @DisplayName("테이블을 청소할 때는 테이블에 있던 주문은 종료 상태여야 한다.")
        @EnumSource(value = OrderStatus.class, names = {"COMPLETED"}, mode = EnumSource.Mode.EXCLUDE)
        @ParameterizedTest
        void test2(final OrderStatus orderStatus) {
            final MenuGroup menuGroup = menuGroupRepository.save(createMenuGroup(UUID.randomUUID()));
            final Product product = productRepository.save(createProduct(UUID.randomUUID()));
            final MenuProduct menuProduct = MenuProductFixture.createMenuProductWithDefaultId(product);
            final Menu menu = menuRepository.save(createMenu(UUID.randomUUID(), BigDecimal.ONE, menuGroup, true, List.of(menuProduct)));
            final List<OrderLineItem> orderLineItems = List.of(createOrderLineItem(menu, 1, BigDecimal.TEN));
            final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, true));
            orderRepository.save(createOrder(UUID.randomUUID(), OrderType.EAT_IN, orderStatus, null, orderLineItems, orderTable));

            assertThatIllegalStateException().isThrownBy(() -> orderTableService.clear(orderTable.getId()));
        }
    }


    @DisplayName("인원수 수정")
    @Nested
    class ChangeNumberOfGuest {
        @DisplayName("테이블은 인원수를 수정할 수 있다.")
        @Test
        void test1() {
            final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, true));
            final OrderTable changeOrderTable = createOrderTable(1, true);

            orderTableService.changeNumberOfGuests(orderTable.getId(), changeOrderTable);

            assertThat(orderTable.getNumberOfGuests()).isEqualTo(changeOrderTable.getNumberOfGuests());
        }

        @DisplayName("테이블 인원수 수정시 변경 될 인원수는 0명 이상이어야 한다")
        @Test
        void test2() {
            final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, true));
            final OrderTable changeOrderTable = createOrderTable(-1, true);

            assertThatIllegalArgumentException().isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changeOrderTable));
        }

        @DisplayName("테이블 인원수 수정시 테이블은 착석중이어야 한다.")
        @Test
        void test3() {
            final OrderTable orderTable = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));
            final OrderTable changeOrderTable = createOrderTable(1, true);

            assertThatIllegalStateException().isThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), changeOrderTable));
        }
    }

    @DisplayName("전체 조회")
    @Nested
    class FindAll {
        @DisplayName("테이블은 전체 조회가 가능하다")
        @Test
        void test1() {
            final OrderTable orderTable1 = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));
            final OrderTable orderTable2 = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));
            final OrderTable orderTable3 = orderTableRepository.save(createOrderTable(UUID.randomUUID(), 5, false));

            final List<OrderTable> foundOrderTables = orderTableService.findAll();

            assertThat(foundOrderTables).containsExactly(orderTable1, orderTable2, orderTable3);
        }
    }
}