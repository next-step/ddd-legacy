package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuGroupRepository;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fake.FakeKitchenridersClient;
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
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("OrderService")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderServiceTest {

    private OrderRepository orderRepository = new InMemoryOrderRepository();

    private MenuRepository menuRepository = new InMemoryMenuRepository();

    private MenuGroupRepository menuGroupRepository = new InMemoryMenuGroupRepository();

    private ProductRepository productRepository = new InMemoryProductRepository();

    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();

    private KitchenridersClient kitchenridersClient = new FakeKitchenridersClient();

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
                kitchenridersClient);
    }


    @Test
    void 주문_하나에_여러_메뉴를_주문할_수_있다() {
        Order createRequest = OrderFixture.createTakeOutRequest(
                createFriedOrderLineItem(),
                createFriedOrderLineItem());

        Order actual = orderService.create(createRequest);

        assertThat(actual.getId()).isNotNull();
    }

    @Test
    void 노출되지_않은_메뉴를_주문하면_예외를_던진다() {
        Menu friedMenu = createFriedMenu();
        friedMenu.setDisplayed(false);
        Order createRequest = OrderFixture.createTakeOutRequest(
                createOrderLineItem(friedMenu));

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
                createFriedOrderLineItem());
        Order deliveryRequest = OrderFixture.createDeliveryRequest("주소",
                createFriedOrderLineItem());
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
                    createFriedOrderLineItem());

            assertThatThrownBy(() -> orderService.create(createRequest)).isInstanceOf(
                    IllegalArgumentException.class);
        }

        @Test
        void 배달주문이_생성되면_주문은_승인을_WAITING하는_상태가_된다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());

            Order actual = orderService.create(createRequest);

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        void WAITING_상태의_배달주문을_승인한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            Order actual = orderService.accept(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        void WAITING_상태가_아닌_배달주문을_승인하면_예외를_던진다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            assertThatThrownBy(() -> orderService.accept(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void ACCEPTED_상태의_배달주문을_SERVED_상태로_변경한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            Order actual = orderService.serve(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void ACCEPTED_상태가_아닌_배달주문을_SERVED_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.serve(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void SERVED된_배달_주문의_음식을_배달중_상태로_변경한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            Order actual = orderService.startDelivery(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @Test
        void SERVED_상태가_아닌_배달주문을_배달중_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.startDelivery(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 배달주문이_아닌_주문을_배달중_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            assertThatThrownBy(() -> orderService.startDelivery(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 배달중인_배달주문을_배달완료_상태로_변경한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
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
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.completeDelivery(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 배달주문이_아닌_주문을_배달완료_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            assertThatThrownBy(() -> orderService.completeDelivery(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 배달이_완료된_배달주문을_완료상태로_변경한다() {
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
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
            Order createRequest = OrderFixture.createDeliveryRequest("주소",
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.complete(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }
    }

    @Nested
    class TakeoutOrder {

        @Test
        void 포장주문이_생성되면_주문은_승인을_WAITING하는_상태가_된다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());

            Order actual = orderService.create(createRequest);

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        void WAITING_상태의_포장주문을_승인한다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            Order actual = orderService.accept(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        void WAITING_상태가_아닌_포장주문을_승인하면_예외를_던진다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            assertThatThrownBy(() -> orderService.accept(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void ACCEPTED_상태의_포장주문을_SERVED_상태로_변경한다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            Order actual = orderService.serve(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void ACCEPTED_상태가_아닌_포장주문을_SERVED_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.serve(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void SERVED된_포장주문을_완료상태로_변경한다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());
            orderService.serve(saved.getId());

            Order actual = orderService.complete(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        void SERVED_상태가_아닌_포장주문을_완료상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createTakeOutRequest(
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.complete(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }
    }

    @Nested
    class EatInOrder {

        @Test
        void 매장주문_생성시_테이블을_빠뜨리면_예외를_던진다() {
            Order createRequest = OrderFixture.createEatInRequest(null,
                    createFriedOrderLineItem());

            assertThatThrownBy(() -> orderService.create(createRequest)).isInstanceOf(
                    NoSuchElementException.class);
        }

        @Test
        void 매장주문_생성시_손님이_앉아있지_않은_테이블이면_예외를_던진다() {
            Order createRequest = OrderFixture.createEatInRequest(createTable(),
                    createFriedOrderLineItem());

            assertThatThrownBy(() -> orderService.create(createRequest)).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void 매장주문이_생성되면_주문은_승인을_WAITING하는_상태가_된다() {
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    createFriedOrderLineItem());

            Order actual = orderService.create(createRequest);

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.WAITING);
        }

        @Test
        void WAITING_상태의_매장주문을_승인한다() {
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            Order actual = orderService.accept(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Test
        void WAITING_상태가_아닌_매장주문을_승인하면_예외를_던진다() {
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            assertThatThrownBy(() -> orderService.accept(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void ACCEPTED_상태의_매장주문을_SERVED_상태로_변경한다() {
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);
            orderService.accept(saved.getId());

            Order actual = orderService.serve(saved.getId());

            assertThat(actual.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Test
        void ACCEPTED_상태가_아닌_매장주문을_SERVED_상태로_변경하면_예외를_던진다() {
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.serve(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }

        @Test
        void SERVED된_매장주문을_완료상태로_변경하면_테이블은_초기값으로_세팅된다() {
            OrderTable orderTable = createOccupiedTable();
            Order createRequest = OrderFixture.createEatInRequest(orderTable,
                    createFriedOrderLineItem());
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
            Order createRequest = OrderFixture.createEatInRequest(createOccupiedTable(),
                    createFriedOrderLineItem());
            Order saved = orderService.create(createRequest);

            assertThatThrownBy(() -> orderService.complete(saved.getId())).isInstanceOf(
                    IllegalStateException.class);
        }
    }

    private OrderLineItem createFriedOrderLineItem() {
        return createOrderLineItem(createFriedMenu());
    }

    private Menu createFriedMenu() {
        MenuGroup chickenMenuGroup = menuGroupRepository.save(MenuGroupFixture.createChicken());
        Product friedProduct = productRepository.save(ProductFixture.createFired());
        return menuRepository.save(
                MenuFixture.createFriedOnePlusOne(chickenMenuGroup, friedProduct));
    }

    private OrderLineItem createOrderLineItem(Menu menu) {
        return OrderFixture.createOrderLineItem(menu, 2);
    }

    private OrderTable createOccupiedTable() {
        OrderTable orderTable = createTable();
        orderTable.setOccupied(true);
        return orderTable;
    }

    private OrderTable createTable() {
        return orderTableRepository.save(OrderTableFixture.createNumber1());
    }
}