package kitchenpos.application;

import kitchenpos.IntegrationTest;
import kitchenpos.domain.*;
import kitchenpos.fixture.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class OrderTableServiceIntegrationTest extends IntegrationTest {

    private final OrderTableService orderTableService;
    private final OrderTableRepository orderTableRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MenuGroupRepository menuGroupRepository;
    private final MenuRepository menuRepository;


    OrderTableServiceIntegrationTest(OrderTableService orderTableService,
                                     OrderTableRepository orderTableRepository,
                                     OrderRepository orderRepository,
                                     ProductRepository productRepository,
                                     MenuGroupRepository menuGroupRepository,
                                     MenuRepository menuRepository) {
        this.orderTableService = orderTableService;
        this.orderTableRepository = orderTableRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.menuGroupRepository = menuGroupRepository;
        this.menuRepository = menuRepository;
    }


    @DisplayName("[정상] 주문 테이블을 등록합니다.")
    @Test
    void create_success() {
        OrderTable givenOrderTable = OrderTableFixture.create();

        OrderTable actualResult = orderTableService.create(givenOrderTable);

        assertEquals(givenOrderTable.getName(), actualResult.getName());
        assertEquals(givenOrderTable.getNumberOfGuests(), 0);
        assertFalse(givenOrderTable.isOccupied());
    }

    static Object[][] create_fail_because_illegal_name() {
        return new Object[][]{
                {OrderTableFixture.create(UUID.randomUUID(), "")},
                {OrderTableFixture.create(UUID.randomUUID(), null)},
        };
    }

    @DisplayName("[예외] 비정상적인 이름으로 주문 테이블 등록에 실패합니다.")
    @MethodSource
    @ParameterizedTest
    void create_fail_because_illegal_name(OrderTable givenOrderTable) {
        assertThatThrownBy(() -> orderTableService.create(givenOrderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[정상] 주문 테이블을 착석 처리 합니다.")
    @Test
    void sit() {
        OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.create());

        OrderTable actualResult = orderTableService.sit(givenOrderTable.getId());

        assertTrue(actualResult.isOccupied());
    }

    @DisplayName("[정상] 주문 테이블을 정리 처리 합니다.")
    @Test
    void clear() {
        OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.create());
        List<Product> products = productRepository.saveAll(Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
        ));
        MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.create());
        List<MenuProduct> menuProducts = MenuProductFixture.create(products , 1);
        Menu createdMenu = menuRepository.save(MenuFixture.create(
                UUID.randomUUID(), "후라이드 치킨 세트", BigDecimal.valueOf(1_000L),
                menuProducts, menuGroup, true
        ));
        OrderLineItem orderLineItem = OrderLineItemFixture.create(createdMenu, 1L);
        orderRepository.save(OrderFixture.create(
                UUID.randomUUID(), OrderType.EAT_IN, OrderStatus.COMPLETED, givenOrderTable, Arrays.asList(orderLineItem)
        ));

        OrderTable actualResult = orderTableService.clear(givenOrderTable.getId());

        assertEquals(0, actualResult.getNumberOfGuests());
        assertFalse(actualResult.isOccupied());
    }
    @DisplayName("[예외] 주문이 완료 상태가 아니므로 주문 테이블 정리를 할 수 없습니다.")
    @Test
    void clear_fail_because_order_status_is_not_completed() {
        OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.create());
        List<Product> products = productRepository.saveAll(Arrays.asList(
                ProductFixture.create(UUID.randomUUID(), "후라이드 치킨", BigDecimal.valueOf(18_000L)),
                ProductFixture.create(UUID.randomUUID(), "코카콜라 1.5L", BigDecimal.valueOf(2_000L))
        ));
        MenuGroup menuGroup = menuGroupRepository.save(MenuGroupFixture.create());
        List<MenuProduct> menuProducts = MenuProductFixture.create(products , 1);
        Menu createdMenu = menuRepository.save(MenuFixture.create(
                UUID.randomUUID(), "후라이드 치킨 세트", BigDecimal.valueOf(1_000L),
                menuProducts, menuGroup, true
        ));
        OrderLineItem orderLineItem = OrderLineItemFixture.create(createdMenu, 1L);
        orderRepository.save(OrderFixture.create(
                UUID.randomUUID(), OrderType.EAT_IN, OrderStatus.WAITING, givenOrderTable, Arrays.asList(orderLineItem)
        ));


        assertThatThrownBy(() -> orderTableService.clear(givenOrderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("[정상] 주문 테이블에 고객 수를 변경합니다.")
    @Test
    void changeNumberOfGuests_success() {
        OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.create(
                UUID.randomUUID(), "테이블", 0, true)
        );
        OrderTable changingNumberOfGuest = OrderTableFixture.create(
                givenOrderTable.getId(), givenOrderTable.getName(), 3, givenOrderTable.isOccupied()
        );

        OrderTable actualResult = orderTableService.changeNumberOfGuests(givenOrderTable.getId(), changingNumberOfGuest);

        assertEquals(changingNumberOfGuest.getNumberOfGuests(), actualResult.getNumberOfGuests());
        assertTrue(actualResult.isOccupied());
    }

    @DisplayName("[예외] 주문 테이블에 고객 수를 0명 미만으로 변경합니다.")
    @Test
    void changeNumberOfGuests_fail_because_change_zero_guest() {
        OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.create(
                UUID.randomUUID(), "테이블", 0, true)
        );

        OrderTable changingNumberOfGuest = OrderTableFixture.create(
                givenOrderTable.getId(), givenOrderTable.getName(), -1, givenOrderTable.isOccupied()
        );
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(givenOrderTable.getId(), changingNumberOfGuest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[예외] 점유되지 않은 주문 테이블에 대해 고객 수 변경을 시도합니다.")
    @Test
    void changeNumberOfGuests_fail_because_order_table_is_not_occupied() {
        OrderTable givenOrderTable = orderTableRepository.save(OrderTableFixture.create(
                UUID.randomUUID(), "테이블", 0, false)
        );

        OrderTable changingNumberOfGuest = OrderTableFixture.create(
                givenOrderTable.getId(), givenOrderTable.getName(), 3, givenOrderTable.isOccupied()
        );
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(givenOrderTable.getId(), changingNumberOfGuest))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 테이블을 조회합니다.")
    @Test
    void findAll() {
        OrderTable firstOrderTable = orderTableRepository.save(OrderTableFixture.create());
        OrderTable secondOrderTable = orderTableRepository.save(OrderTableFixture.create());

        List<OrderTable> actualResult = orderTableService.findAll();

        assertThat(actualResult).containsExactly(firstOrderTable, secondOrderTable);
    }

}