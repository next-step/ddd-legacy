package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import kitchenpos.config.FakeKitchenridersClient;
import kitchenpos.config.FakePurgomalumClient;
import kitchenpos.config.InMemoryMenuGroupRepository;
import kitchenpos.config.InMemoryMenuRepository;
import kitchenpos.config.InMemoryOrderRepository;
import kitchenpos.config.InMemoryOrderTableRepository;
import kitchenpos.config.InMemoryProductRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.infra.PurgomalumClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderTableService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderTableServiceTest {

    private OrderRepository orderRepository = new InMemoryOrderRepository();

    private MenuRepository menuRepository = new InMemoryMenuRepository();

    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private ProductRepository productRepository = new InMemoryProductRepository();

    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();

    private KitchenridersClient kitchenridersClient = new FakeKitchenridersClient();

    private PurgomalumClient purgomalumClient = new FakePurgomalumClient();

    private MenuService menuService;

    private MenuGroupService menuGroupService;

    private ProductService productService;

    private OrderService orderService;

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
                purgomalumClient);
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
                kitchenridersClient);
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
        createAndCompleteOrder(saved);

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

    private void createAndCompleteOrder(OrderTable orderTable){
        Order order = createOrder(orderTable);
        orderService.accept(order.getId());
        orderService.serve(order.getId());
        orderService.complete(order.getId());
    }

    private Order createOrder(OrderTable orderTable){
        Order eatInRequest = OrderFixture.createEatInRequest(orderTable,
                OrderFixture.createOrderLineItem(createFriedMenu(), 2));
        return orderService.create(eatInRequest);
    }

    private Menu createFriedMenu() {
        MenuGroup menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
        Product fried = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        return menuService.create(
                MenuFixture.createRequest("후라이드1+1", 30_000L, menuGroup, fried, 2));
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
}