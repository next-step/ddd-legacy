package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import kitchenpos.application.fakerepository.MenuFakeRepository;
import kitchenpos.application.fakerepository.OrderFakeRepository;
import kitchenpos.application.fakerepository.OrderTableFakeRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class OrderServiceFindTest {

    private OrderRepository orderRepository;
    private MenuRepository menuRepository;
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient mockRidersClient;

    private OrderService sut;

    @BeforeEach
    void setUp() {
        orderRepository = new OrderFakeRepository();
        menuRepository = new MenuFakeRepository();
        orderTableRepository = new OrderTableFakeRepository();

        sut = new OrderService(orderRepository, menuRepository,
            orderTableRepository, mockRidersClient);
    }

    @Test
    void findAll_등록된_모든_주문을_반환한다() {
        // given
        final Order dummy1 = orderRepository.save(new Order());
        final Order dummy2 = orderRepository.save(new Order());
        final Order dummy3 = orderRepository.save(new Order());

        // when
        final List<Order> actual = sut.findAll();

        // then
        assertThat(actual)
            .usingRecursiveFieldByFieldElementComparator()
            .contains(dummy1, dummy2, dummy3);
    }
}
