package kitchenpos.application;

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
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.domain.Product;
import kitchenpos.domain.ProductRepository;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.MenuFixture.createMenu;
import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderFixture.createOrderWithId;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTableWithId;
import static kitchenpos.fixture.ProductFixture.createProduct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;


@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MenuRepository menuRepository;


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private KitchenridersClient kitchenridersClient;

    @Nested
    class createTest {
        @DisplayName("주문 유형이 없는 경우 예외가 발생한다.")
        @Test
        void createFailWhenNotExistTypeTest() {
            Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
            Order order = createOrder(null, null, List.of(orderLineItem), null, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목이 없는 경우 예외가 발생한다.")
        @NullAndEmptySource
        @ParameterizedTest
        void createFailWhenNotExistOrderLineItemsTest(List<OrderLineItem> orderLineItems) {
            Order order = createOrder(null, null, orderLineItems, null, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("존재하지 않는 메뉴를 주문한 경우 예외가 발생한다.")
        @Test
        void createFailWhenNotExistMenuTest() {
            Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu notExistMenu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), notExistMenu, 1);
            Order order = createOrder(null, null, List.of(orderLineItem), OrderType.EAT_IN, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 비노출인 경우에 예외가 발생한다.")
        @Test
        void createFailWhenNotDisplayedMenuTest() {
            Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), false, List.of(menuProduct));
            menu = menuRepository.save(menu);
            OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
            Order order = createOrder(null, null, List.of(orderLineItem), OrderType.EAT_IN, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("메뉴의 가격과 주문 항목의 가격이 다른 경우 예외가 발생한다.")
        @Test
        void createFailWhenDifferentPriceTest() {
            Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(17000), menu, 1);
            Order order = createOrder(null, null, List.of(orderLineItem), OrderType.EAT_IN, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("먹고가기 유형인 경우")
        @Nested
        class eatInTest {
            @DisplayName("주문을 생성한다.")
            @Test
            void createSuccessTest() {
                OrderTable orderTable = createOrderTableWithId("1번", true);
                orderTable = orderTableRepository.save(orderTable);
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
                Order order = createOrder(null, orderTable, List.of(orderLineItem), OrderType.EAT_IN, null, null);

                order = orderService.create(order);

                assertThat(order.getId()).isNotNull();
                assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(order.getOrderDateTime()).isNotNull();
            }

            @DisplayName("주문 테이블이 존재하지 않는 경우에 예외가 발생한다.")
            @Test
            void createFailWhenNotExistOrderTableTest() {
                OrderTable notExistOrderTable = createOrderTableWithId("1번", true);
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
                Order order = createOrder(null, notExistOrderTable, List.of(orderLineItem), OrderType.EAT_IN, null, null);

                assertThatThrownBy(() -> orderService.create(order))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("좌석이 비어있는 경우에 예외가 발생한다.")
            @Test
            void createFailWhenOrderTableIsNotOccupiedTest() {
                OrderTable orderTable = createOrderTableWithId("1번", false);
                orderTable = orderTableRepository.save(orderTable);
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
                Order order = createOrder(null, orderTable, List.of(orderLineItem), OrderType.EAT_IN, null, null);

                assertThatThrownBy(() -> orderService.create(order))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @DisplayName("포장하기 유형인 경우")
        @Nested
        class takeOutTest {
            @DisplayName("주문을 생성한다.")
            @Test
            void createSuccessTest() {
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
                Order order = createOrder(null, null, List.of(orderLineItem), OrderType.TAKEOUT, null, null);

                order = orderService.create(order);

                assertThat(order.getId()).isNotNull();
                assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(order.getOrderDateTime()).isNotNull();
            }

            @DisplayName("수량이 0보다 작은 경우 예외가 발생한다.")
            @Test
            void createFailWhenQuantityIsLessThanZeroTest() {
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, -1);
                Order order = createOrder(null, null, List.of(orderLineItem), OrderType.TAKEOUT, null, null);

                assertThatThrownBy(() -> orderService.create(order))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @DisplayName("배달하기 유형인 경우")
        @Nested
        class deliveryTest {
            @DisplayName("주문을 생성한다.")
            @Test
            void createSuccessTest() {
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
                Order order = createOrder(null, null, List.of(orderLineItem), OrderType.DELIVERY, null, "서울 강남구 테헤란로 411, 성담빌딩 13층");

                order = orderService.create(order);

                assertThat(order.getId()).isNotNull();
                assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(order.getOrderDateTime()).isNotNull();
            }

            @DisplayName("수량이 0보다 작은 경우 예외가 발생한다.")
            @Test
            void createFailWhenQuantityIsLessThanZeroTest() {
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, -1);
                Order order = createOrder(null, null, List.of(orderLineItem), OrderType.DELIVERY, null, "서울 강남구 테헤란로 411, 성담빌딩 13층");

                assertThatThrownBy(() -> orderService.create(order))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("배달 주소가 없는 경우 예외가 발생한다.")
            @NullAndEmptySource
            @ParameterizedTest
            void createFailWhenNotExistDeliveryAddressTest(String deliveryAddress) {
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
                Order order = createOrder(null, null, List.of(orderLineItem), OrderType.DELIVERY, null, deliveryAddress);

                assertThatThrownBy(() -> orderService.create(order))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    class acceptTest {
        @DisplayName("주문을 수락할 수 있다.")
        @Test
        void acceptTest() {
            Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
            Order order = createOrder(null, null, List.of(orderLineItem), OrderType.TAKEOUT, null, null);
            order = orderService.create(order);

            order = orderService.accept(order.getId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("주문 상태가 대기 상태가 아닌 경우에 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAITING")
        void acceptFailWhenOrderStatusIsNotWaitingTest(OrderStatus orderStatus) {
            Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
            Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.TAKEOUT, orderStatus, null, LocalDateTime.now());
            order = orderRepository.save(order);

            UUID orderId = order.getId();

            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("배달하기 유형인 경우")
        @Nested
        class DeliveryTest {
            @DisplayName("라이더 매칭 시스템에 요청을 보낸다.")
            @Test
            void acceptTest() {
                Product product = createProduct("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = createMenu(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                OrderLineItem orderLineItem = createOrderLineItem(BigDecimal.valueOf(16000), menu, 1);
                Order order = createOrder(null, null, List.of(orderLineItem), OrderType.DELIVERY, null, "서울 강남구 테헤란로 411, 성담빌딩 13층");
                order = orderService.create(order);

                order = orderService.accept(order.getId());

                assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
                verify(kitchenridersClient).requestDelivery(order.getId(), BigDecimal.valueOf(16000), order.getDeliveryAddress());
            }
        }
    }

    @Nested
    class serveTest {
    }

    @Nested
    class startDeliveryTest {
    }

    @Nested
    class completeDeliveryTest {
    }

    @Nested
    class completeTest {
    }

    @Nested
    class findAllTest {
    }
}
