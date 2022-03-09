package kitchenpos.application;

import kitchenpos.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static kitchenpos.fixture.KitchenposFixture.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("주문을 하면 주문 대기 상태가 된다.")
    @Test
    void waitingOrder() {
        // given
        Order order = orderTakeout(createMenu());

        // when
        Order actual = orderService.create(order);

        // then
        assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
    }

    @DisplayName("매장 내 주문시 주문이 완료되면 주문 테이블이 사용 가능해진다.")
    @Test
    void tableClear() {
        Menu menu = createMenu();
        Order order = orderEatIn(menu);
        orderTableRepository.save(order.getOrderTable());
        order.setStatus(OrderStatus.SERVED);
        order = orderRepository.save(order);

        orderService.complete(order.getId());

        OrderTable orderTable = orderTableRepository.findById(order.getOrderTable().getId()).get();

        assertThat(orderTable.isEmpty()).isTrue();
        assertThat(orderTable.getNumberOfGuests()).isZero();
    }

    private Menu createMenu() {
        Product chickenProduct = chickenProduct();
        Product pastaProduct = pastaProduct();
        productRepository.saveAll(Arrays.asList(chickenProduct, pastaProduct));

        MenuGroup menuGroup = menuGroup();
        menuGroupRepository.save(menuGroup);

        Menu menu = menu(menuGroup, chickenProduct, pastaProduct);
        return menuRepository.save(menu);
    }
}
