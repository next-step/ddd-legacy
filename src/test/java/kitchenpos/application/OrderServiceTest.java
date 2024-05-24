package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.fake.FakeKitchenridersClient;
import kitchenpos.fake.FakePurgomalumClient;
import kitchenpos.fake.InMemoryMenuGroupRepository;
import kitchenpos.fake.InMemoryMenuRepository;
import kitchenpos.fake.InMemoryOrderRepository;
import kitchenpos.fake.InMemoryOrderTableRepository;
import kitchenpos.fake.InMemoryProductRepository;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderServiceTest {

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

    private MenuGroup menuGroup;

    @BeforeEach
    void setUp() {
        menuGroupService = new MenuGroupService(menuGroupRepository);
        productService = new ProductService(productRepository, menuRepository, purgomalumClient);
        menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
                purgomalumClient);
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
                kitchenridersClient);
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
        menuGroup = menuGroupService.create(MenuGroupFixture.createRequest("치킨"));
    }


    @Test
    void 주문_하나에_여러_메뉴를_주문할_수_있다() {
        Order createRequest = OrderFixture.createTakeOutRequest(
                OrderFixture.createOrderLineItem(createFriedMenu(), 2),
                OrderFixture.createOrderLineItem(createSeasonedMenu(), 1));

        Order actual = orderService.create(createRequest);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 노출되지_않은_메뉴를_주문하면_예외를_던진다() {
        Menu friedMenu = createFriedMenu();
        menuService.hide(friedMenu.getId());
        Order createRequest = OrderFixture.createTakeOutRequest(
                OrderFixture.createOrderLineItem(friedMenu, 2));

        assertThatThrownBy(() -> orderService.create(createRequest)).isInstanceOf(
                IllegalStateException.class);
    }

    @Test
    void 메뉴를_1개이상_주문하지_않으면_예외를_던진다() {
        Order createRequest = OrderFixture.createTakeOutRequest();

        assertThatThrownBy(() -> orderService.create(createRequest)).isInstanceOf(
                IllegalArgumentException.class);
    }

    @Test
    void 모든_주문_목록을_볼_수_있다() {
        Order takeOutRequest = OrderFixture.createTakeOutRequest(
                OrderFixture.createOrderLineItem(createFriedMenu(), 2));
        Order deliveryRequest = OrderFixture.createDeliveryRequest("주소",
                OrderFixture.createOrderLineItem(createFriedMenu(), 2));
        orderService.create(takeOutRequest);
        orderService.create(deliveryRequest);

        List<Order> actual = orderService.findAll();
        assertThat(actual).hasSize(2);
    }

    @Nested
    class DeliveryOrder {

        @Test
        void 배달주문_생성시_주소를_빠뜨리면_예외를_던진다() {
            Order createRequest = OrderFixture.createDeliveryRequest(null,
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));

            assertThatThrownBy(() -> orderService.create(createRequest)).isInstanceOf(
                    IllegalArgumentException.class);
        }

        @Test
        void 배달주문이_생성되면_주문은_승인을_WAITING하는_상태가_된다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));

            Order actual = orderService.create(createRequest);

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        void WAITING_상태의_배달주문을_승인한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);
            Order actual = orderService.accept(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        void WAITING_상태가_아닌_배달주문을_승인하면_예외를_던진다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            assertThatThrownBy(() -> orderService.accept(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void ACCEPTED_상태의_배달주문을_SERVED_상태로_변경한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            Order actual = orderService.serve(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void ACCEPTED_상태가_아닌_배달주문을_SERVED_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.serve(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void SERVED된_배달_주문의_음식을_배달중_상태로_변경한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            Order actual = orderService.startDelivery(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        void SERVED_상태가_아닌_배달주문을_배달중_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.startDelivery(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 배달주문이_아닌_주문을_배달중_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            assertThatThrownBy(() -> orderService.startDelivery(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 배달중인_배달주문을_배달완료_상태로_변경한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());
            orderService.startDelivery(saved.getId());

            Order actual = orderService.completeDelivery(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @Test
        void 배달중_상태가_아닌_배달주문을_배달완료_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(createFriedMenu(), 2));
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.completeDelivery(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 배달주문이_아닌_주문을_배달완료_상태로_변경하면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            assertThatThrownBy(() -> orderService.completeDelivery(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 배달이_완료된_배달주문을_완료상태로_변경한다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());
            orderService.startDelivery(saved.getId());
            orderService.completeDelivery(saved.getId());

            Order actual = orderService.complete(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        void 배달완료_상태가_아닌_주문을_완료상태로_변경하면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.complete(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }
    }

    @Nested
    class TakeoutOrder {

        @Test
        void 포장주문이_생성되면_주문은_승인을_WAITING하는_상태가_된다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(friedMenu, 2));

            Order actual = orderService.create(createRequest);

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        void WAITING_상태의_포장주문을_승인한다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            Order actual = orderService.accept(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        void WAITING_상태가_아닌_포장주문을_승인하면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            assertThatThrownBy(() -> orderService.accept(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void ACCEPTED_상태의_포장주문을_SERVED_상태로_변경한다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            Order actual = orderService.serve(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void ACCEPTED_상태가_아닌_포장주문을_SERVED_상태로_변경하면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.serve(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void SERVED된_포장주문을_완료상태로_변경한다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            Order actual = orderService.complete(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        void SERVED_상태가_아닌_포장주문을_완료상태로_변경하면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createTakeOutRequest(
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.complete(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }
    }

    @Nested
    class EatInOrder {

        @Test
        void 매장주문_생성시_테이블을_빠뜨리면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createEatInRequest(null,
                    OrderFixture.createOrderLineItem(friedMenu, 2));

            assertThatThrownBy(() -> orderService.create(createRequest)).isInstanceOf(
                    NoSuchElementException.class);
        }

        @Test
        void 매장주문_생성시_손님이_앉아있지_않은_테이블이면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createEatInRequest(createTable(),
                    OrderFixture.createOrderLineItem(friedMenu, 2));

            assertThatThrownBy(() -> orderService.create(createRequest)).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 매장주문이_생성되면_주문은_승인을_WAITING하는_상태가_된다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    OrderFixture.createOrderLineItem(friedMenu, 2));

            Order actual = orderService.create(createRequest);

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        void WAITING_상태의_매장주문을_승인한다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            Order actual = orderService.accept(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        void WAITING_상태가_아닌_매장주문을_승인하면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            assertThatThrownBy(() -> orderService.accept(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void ACCEPTED_상태의_매장주문을_SERVED_상태로_변경한다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            Order actual = orderService.serve(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void ACCEPTED_상태가_아닌_매장주문을_SERVED_상태로_변경하면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.serve(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void SERVED된_매장주문을_완료상태로_변경하면_테이블은_초기값으로_세팅된다() {
            Menu friedMenu = createFriedMenu();
            OrderTable orderTable = createOccupiedTable();
            Order createRequest = OrderFixture.createEatInRequest(orderTable,
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            Order actual = orderService.complete(saved.getId());

            assertAll(() -> assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                    () -> assertThat(orderTable.isOccupied()).isFalse(),
                    () -> assertThat(orderTable.getNumberOfGuests()).isZero());
        }

        @Test
        void SERVED_상태가_아닌_포장주문을_완료상태로_변경하면_예외를_던진다() {
            Menu friedMenu = createFriedMenu();
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    OrderFixture.createOrderLineItem(friedMenu, 2));
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.complete(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }
    }

    private Menu createFriedMenu() {
        Product fried = productService.create(ProductFixture.createRequest("후라이드", 20_000L));
        return menuService.create(
                MenuFixture.createRequest("후라이드1+1", 30_000L, menuGroup, fried, 2));
    }

    private Menu createSeasonedMenu() {
        Product seasoned = productService.create(ProductFixture.createRequest("양념", 25_000L));
        return menuService.create(
                MenuFixture.createRequest("양념1+1", 35_000L, menuGroup, seasoned, 2));
    }

    private OrderTable createOccupiedTable() {
        OrderTable orderTable = createTable();
        return orderTableService.sit(orderTable.getId());
    }

    private OrderTable createTable() {
        return orderTableService.create(OrderTableFixture.createRequest("1번 테이블"));
    }
}