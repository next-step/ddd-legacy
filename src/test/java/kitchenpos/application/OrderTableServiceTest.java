package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static kitchenpos.Fixtures.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class OrderTableServiceTest {

    @Autowired
    private OrderTableService orderTableService;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;


    @Test
    @DisplayName("주문테이블을 생성한다.")
    void create01() {
        OrderTable orderTable = createOrderTable("1번 테이블", false);

        OrderTable savedOrderTable = orderTableService.create(orderTable);

        OrderTable findOrderTable = orderTableRepository.findById(savedOrderTable.getId()).orElseThrow();
        assertThat(findOrderTable.getId()).isNotNull();
    }

    @Test
    @DisplayName("테이블의 이름은 비어있을 수 없다.")
    void create02() {
        OrderTable orderTable = createOrderTable(null, false);

        assertThatThrownBy(() -> orderTableService.create(orderTable)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블에 손님이 앉으면, 테이블을 점유한다.")
    void sit01() {
        OrderTable orderTable = createOrderTable("1번 테이블", false);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        assertThat(orderTable.isOccupied()).isFalse();

        OrderTable sitOrderTable = orderTableService.sit(savedOrderTable.getId());

        assertThat(sitOrderTable.isOccupied()).isTrue();
    }

    @Test
    @DisplayName("테이블을 치운다.")
    void clear01() {
        OrderTable orderTable = createOrderTable("1번 테이블", true);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);

        OrderTable sitOrderTable = orderTableService.clear(savedOrderTable.getId());

        assertThat(sitOrderTable.isOccupied()).isFalse();
        assertThat(sitOrderTable.getNumberOfGuests()).isZero();
    }

    @Test
    @DisplayName("주문의 상태가 종료되지 않으면 테이블을 치울 수 없다.")
    void clear02() {
        Menu menu = getSavedMenu(true);
        OrderLineItem orderLineItem = createOrderLineItem(menu, menu.getPrice());
        Order order = createOrder(OrderStatus.SERVED, OrderType.EAT_IN, List.of(orderLineItem), null);
        OrderTable savedOrderTable = getSavedOrderTable(true);
        order.setOrderTable(savedOrderTable);
        order.setOrderTableId(savedOrderTable.getId());
        orderRepository.save(order);

        assertThatThrownBy(() -> orderTableService.clear(savedOrderTable.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("테이블 손님의 수를 변경한다.")
    void changeNumberOfGuests01() {
        OrderTable orderTable = createOrderTable("1번 테이블", true);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        orderTable.setNumberOfGuests(10);

        OrderTable changedOrderTable = orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTable);

        assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(10);
    }

    @Test
    @DisplayName("손님의 수는 0보다 작을 수 없다.")
    void changeNumberOfGuests02() {
        OrderTable orderTable = createOrderTable("1번 테이블", true);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);
        orderTable.setNumberOfGuests(-1);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTable))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블이 점유되어있지 않으면 변경할 수 없다.")
    void changeNumberOfGuests03() {
        OrderTable orderTable = createOrderTable("1번 테이블", false);
        OrderTable savedOrderTable = orderTableRepository.save(orderTable);

        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(savedOrderTable.getId(), orderTable))
                .isInstanceOf(IllegalStateException.class);
    }

    private OrderTable getSavedOrderTable(boolean occupied) {
        OrderTable orderTable = createOrderTable("1번테이블", occupied);
        return orderTableRepository.save(orderTable);
    }

    public Menu getSavedMenu(boolean displayed) {
        MenuGroup menuGroup = createMenuGroup("메뉴그룹");
        MenuGroup savedMenuGroup = menuGroupRepository.save(menuGroup);

        Product product1 = createProduct("반마리 치킨", new BigDecimal("10000"));
        Product product2 = createProduct("후라이드 치킨", new BigDecimal("20000"));
        List<Product> savedProducts = productRepository.saveAll(List.of(product1, product2));

        Menu menu = createMenu("1.5인분 치킨메뉴", new BigDecimal("20000"), savedMenuGroup,
                               List.of(createMenuProduct(1L, savedProducts.get(0), 1L),
                                       createMenuProduct(2L, savedProducts.get(1), 1L))
        );
        menu.setDisplayed(displayed);

        return menuRepository.save(menu);
    }
}
