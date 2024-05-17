package kitchenpos.application;

import kitchenpos.config.ServiceTest;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.OrderFixture.createOrderWithId;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTable;
import static kitchenpos.fixture.ProductFixture.createProductWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ServiceTest
class OrderTableServiceTest {
    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Nested
    class createTest {
        @DisplayName("주문 테이블을 생성한다.")
        @Test
        void createSuccessTest() {
            final OrderTable orderTable = orderTableService.create(createOrderTable("1번"));

            assertAll(
                    () -> assertThat(orderTable.getId()).isNotNull(),
                    () -> assertThat(orderTable.getNumberOfGuests()).isZero(),
                    () -> assertThat(orderTable.isOccupied()).isFalse()
            );
        }

        @DisplayName("이름이 빈값이거나 null인 경우 예외가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createFailWhenNameIsNullAndEmptyTest(final String name) {
            final OrderTable orderTable = createOrderTable(name);

            assertThatThrownBy((() -> orderTableService.create(orderTable)))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    class sitTest {
        @DisplayName("좌석에 착석 할 수 있다.")
        @Test
        void sitSuccessTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);

            orderTable = orderTableService.sit(orderTable.getId());

            assertThat(orderTable.isOccupied()).isTrue();
        }

        @DisplayName("존재하지 않은 주문 테이블의 경우 예외가 발생한다.")
        @Test
        void sitFailWhenNotExistOrderTableTest() {
            final UUID notExistOrderTableId = UUID.randomUUID();
            assertThatThrownBy(() -> orderTableService.sit(notExistOrderTableId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    @Nested
    class clearTest {
        @DisplayName("좌석을 비울 수 있다.")
        @Test
        void clearSuccessTest() {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);
            orderTable = orderTableService.sit(orderTable.getId());

            orderTable = orderTableService.clear(orderTable.getId());

            assertThat(orderTable.isOccupied()).isFalse();
            assertThat(orderTable.getNumberOfGuests()).isZero();
        }

        @DisplayName("좌석에 완료된 주문이 없는 경우 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "COMPLETED")
        void clearFailWhenNotExistCompletedOrderTest(final OrderStatus orderStatus) {
            OrderTable orderTable = createOrderTable("1번");
            orderTable = orderTableService.create(orderTable);
            orderTable = orderTableService.sit(orderTable.getId());

            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            final Order order = createOrderWithId(orderTable, List.of(orderLineItem), OrderType.EAT_IN, orderStatus, null, LocalDateTime.now());
            orderRepository.save(order);

            final UUID orderTableId = orderTable.getId();

            assertThatThrownBy(() -> orderTableService.clear(orderTableId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class changeNumberOfGuestsTest {
        @DisplayName("손님 수를 변경할 수 있다.")
        @Test
        void changeNumberOfGuestsSuccessTest() {
            OrderTable orderTable = orderTableService.create(createOrderTable("1번"));
            orderTableService.sit(orderTable.getId());
            orderTable.setNumberOfGuests(4);
            orderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);

            assertThat(orderTable.getNumberOfGuests()).isEqualTo(4);
        }

        @DisplayName("손님 수가 0보다 작으면 예외가 발생한다.")
        @Test
        void changeNumberOfGuestsFailWhenNumberOfGuestsIsLessThanZeroTest() {
            final OrderTable orderTable = orderTableService.create(createOrderTable("1번"));
            orderTableService.sit(orderTable.getId());
            orderTable.setNumberOfGuests(-1);

            final UUID orderTableId = orderTable.getId();

            assertAll(
                    () -> assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, orderTable))
                            .isInstanceOf(IllegalArgumentException.class)
            );
        }

        @DisplayName("착석하지 않은 경우 예외가 발생한다.")
        @Test
        void changeNumberOfGuestsFailWhenNotSitTest() {
            final OrderTable orderTable = orderTableService.create(createOrderTable("1번"));
            orderTable.setNumberOfGuests(4);

            final UUID orderTableId = orderTable.getId();

            assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTableId, orderTable))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class findAllTest {
        @DisplayName("주문 테이블 목록을 조회한다.")
        @Test
        void findAllSuccessTest() {
            final OrderTable orderTable = orderTableService.create(createOrderTable("1번"));

            final List<OrderTable> orderTables = orderTableService.findAll();
            final List<UUID> orderTableIds = orderTables.stream()
                    .map(OrderTable::getId)
                    .toList();

            assertAll(
                    () -> assertThat(orderTables).hasSize(1),
                    () -> assertThat(orderTableIds).contains(orderTable.getId())
            );
        }
    }
}
