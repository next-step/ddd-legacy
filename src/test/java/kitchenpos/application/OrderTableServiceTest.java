package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fake.InMemoryMenuGroupRepository;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import kitchenpos.fake.InMemoryProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.fixture.ProductFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayName("OrderTableService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderTableServiceTest {

    private OrderRepository orderRepository = new InMemoryOrderRepository();

    private MenuRepository menuRepository = new InMemoryMenuRepository();

    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private ProductRepository productRepository = new InMemoryProductRepository();

    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Test
    void 테이블을_생성한다() {
        OrderTable request = OrderTableFixture.createRequest("1번테이블");

        OrderTable actual = orderTableService.create(request);

        assertAll(() -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getNumberOfGuests()).isZero(),
                () -> assertThat(actual.isOccupied()).isFalse());
    }

    @Test
    void 테이블이름이_비어있으면_예외를던진다() {
        OrderTable createRequest = OrderTableFixture.createRequest("");

        assertThatIllegalArgumentException().isThrownBy(
                () -> orderTableService.create(createRequest));
    }

    @Test
    void 먹고가는_손님이_오면_테이블점유를_체크한다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);

        OrderTable actual = orderTableService.sit(saved.getId());

        assertThat(actual.isOccupied()).isTrue();
    }

    @Test
    void 주문이_완료된_테이블을_치울_수_있다() {
        OrderTable createRequset = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequset);
        orderTableService.sit(saved.getId());
        createCompleteOrder(saved);

        OrderTable actual = orderTableService.clear(saved.getId());

        assertAll(() -> assertThat(actual.isOccupied()).isFalse(),
                () -> assertThat(actual.getNumberOfGuests()).isZero());
    }

    @Test
    void 주문이_완료되지_않은_테이블을_치우면_예외를던진다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);
        orderTableService.sit(saved.getId());
        createOrder(saved);

        assertThatThrownBy(() -> orderTableService.clear(saved.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    private void createCompleteOrder(OrderTable orderTable) {
        Order order = createOrder(orderTable);
        order.setStatus(OrderStatus.COMPLETED);
    }

    private Order createOrder(OrderTable orderTable) {
        MenuGroup chickenMenuGroup = menuGroupRepository.save(MenuGroupFixture.createChicken());
        Product friedProduct = productRepository.save(ProductFixture.createFired());
        Menu friedMenu = menuRepository.save(
                MenuFixture.createFriedOnePlusOne(chickenMenuGroup, friedProduct));
        return orderRepository.save(OrderFixture.createEatIn(orderTable, friedMenu));
    }

    @Test
    void 점유되어있는_테이블의_손님수를_변경할_수_있다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);
        orderTableService.sit(saved.getId());
        OrderTable changeRequest = OrderTableFixture.changeNumberOfGuestsRequest(4);

        OrderTable actual = orderTableService.changeNumberOfGuests(saved.getId(), changeRequest);

        assertThat(actual.getNumberOfGuests()).isEqualTo(4);
    }

    @Test
    void 점유되어있지_않은_테이블의_손님수를_변경하면_예외를던진다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);
        OrderTable changeRequest = OrderTableFixture.changeNumberOfGuestsRequest(4);

        assertThatIllegalStateException().isThrownBy(
                () -> orderTableService.changeNumberOfGuests(saved.getId(), changeRequest));
    }

    @Test
    void 테이블의_손님수를_마이너스로_변경하면_예외를던진다() {
        OrderTable createRequest = OrderTableFixture.createRequest("1번테이블");
        OrderTable saved = orderTableService.create(createRequest);
        orderTableService.sit(saved.getId());
        OrderTable changeRequest = OrderTableFixture.changeNumberOfGuestsRequest(-10);

        assertThatIllegalArgumentException().isThrownBy(
                () -> orderTableService.changeNumberOfGuests(saved.getId(), changeRequest));
    }

    @Test
    void 모든_테이블_목록을_볼_수_있다() {
        orderTableService.create(OrderTableFixture.createRequest("1번테이블"));
        orderTableService.create(OrderTableFixture.createRequest("2번테이블"));

        List<OrderTable> actual = orderTableService.findAll();

        assertThat(actual).hasSize(2);
    }
}