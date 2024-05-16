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
import kitchenpos.fixture.MenuFixture;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static kitchenpos.fixture.MenuProductFixture.createMenuProduct;
import static kitchenpos.fixture.OrderFixture.createOrder;
import static kitchenpos.fixture.OrderFixture.createOrderWithId;
import static kitchenpos.fixture.OrderLineItemFixture.createOrderLineItem;
import static kitchenpos.fixture.OrderTableFixture.createOrderTableWithId;
import static kitchenpos.fixture.ProductFixture.createProductWithId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;


@SpringBootTest
@Transactional
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

    @MockBean
    private KitchenridersClient kitchenridersClient;

    @Nested
    class createTest {
        @DisplayName("주문 유형이 없는 경우 예외가 발생한다.")
        @Test
        void createFailWhenNotExistTypeTest() {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            final Order order = createOrder(null, null, List.of(orderLineItem), null, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 항목이 없는 경우 예외가 발생한다.")
        @NullAndEmptySource
        @ParameterizedTest
        void createFailWhenNotExistOrderLineItemsTest(final List<OrderLineItem> orderLineItems) {
            final Order order = createOrder(null, null, orderLineItems, OrderType.EAT_IN, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("존재하지 않는 메뉴를 주문한 경우 예외가 발생한다.")
        @Test
        void createFailWhenNotExistMenuTest() {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            final Menu notExistMenu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            final OrderLineItem orderLineItem = createOrderLineItem(notExistMenu, BigDecimal.valueOf(16000), 1);
            final Order order = createOrder(null, null, List.of(orderLineItem), OrderType.EAT_IN, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 비노출인 경우에 예외가 발생한다.")
        @Test
        void createFailWhenNotDisplayedMenuTest() {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), false, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            final Order order = createOrder(null, null, List.of(orderLineItem), OrderType.EAT_IN, null, null);

            assertThatThrownBy(() -> orderService.create(order))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("메뉴의 가격과 주문 항목의 가격이 다른 경우 예외가 발생한다.")
        @Test
        void createFailWhenDifferentPriceTest() {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(17000), 1);
            final Order order = createOrder(null, null, List.of(orderLineItem), OrderType.EAT_IN, null, null);

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
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);

                final Order order = orderService.create(createOrder(orderTable,
                        List.of(orderLineItem),
                        OrderType.EAT_IN,
                        null,
                        null));

                assertAll(
                        () -> assertThat(order.getId()).isNotNull(),
                        () -> assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING),
                        () -> assertThat(order.getOrderDateTime()).isNotNull()
                );
            }

            @DisplayName("주문 테이블이 존재하지 않는 경우에 예외가 발생한다.")
            @Test
            void createFailWhenNotExistOrderTableTest() {
                final OrderTable notExistOrderTable = createOrderTableWithId("1번", true);
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                final Order order = createOrder(null, notExistOrderTable, List.of(orderLineItem), OrderType.EAT_IN, null, null);

                assertThatThrownBy(() -> orderService.create(order))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @DisplayName("좌석이 비어있는 경우에 예외가 발생한다.")
            @Test
            void createFailWhenOrderTableIsNotOccupiedTest() {
                OrderTable orderTable = createOrderTableWithId("1번", false);
                orderTable = orderTableRepository.save(orderTable);
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                final Order order = createOrder(null, orderTable, List.of(orderLineItem), OrderType.EAT_IN, null, null);

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
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrder(null, null, List.of(orderLineItem), OrderType.TAKEOUT, null, null);

                order = orderService.create(order);

                assertThat(order.getId()).isNotNull();
                assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(order.getOrderDateTime()).isNotNull();
            }

            @DisplayName("수량이 0보다 작은 경우 예외가 발생한다.")
            @Test
            void createFailWhenQuantityIsLessThanZeroTest() {
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), -1);
                final Order order = createOrder(null, null, List.of(orderLineItem), OrderType.TAKEOUT, null, null);

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
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrder(null, null, List.of(orderLineItem), OrderType.DELIVERY, null, "서울 강남구 테헤란로 411, 성담빌딩 13층");

                order = orderService.create(order);

                assertThat(order.getId()).isNotNull();
                assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
                assertThat(order.getOrderDateTime()).isNotNull();
            }

            @DisplayName("수량이 0보다 작은 경우 예외가 발생한다.")
            @Test
            void createFailWhenQuantityIsLessThanZeroTest() {
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), -1);
                final Order order = createOrder(null, null, List.of(orderLineItem), OrderType.DELIVERY, null, "서울 강남구 테헤란로 411, 성담빌딩 13층");

                assertThatThrownBy(() -> orderService.create(order))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @DisplayName("배달 주소가 없는 경우 예외가 발생한다.")
            @NullAndEmptySource
            @ParameterizedTest
            void createFailWhenNotExistDeliveryAddressTest(final String deliveryAddress) {
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                final Order order = createOrder(null, null, List.of(orderLineItem), OrderType.DELIVERY, null, deliveryAddress);

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
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrder(null, null, List.of(orderLineItem), OrderType.TAKEOUT, null, null);
            order = orderService.create(order);

            order = orderService.accept(order.getId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @DisplayName("주문 상태가 대기 상태가 아닌 경우에 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "WAITING")
        void acceptFailWhenOrderStatusIsNotWaitingTest(final OrderStatus orderStatus) {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.TAKEOUT, orderStatus, null, LocalDateTime.now());
            order = orderRepository.save(order);

            final UUID orderId = order.getId();

            assertThatThrownBy(() -> orderService.accept(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("배달하기 유형인 경우")
        @Nested
        class DeliveryTest {
            @DisplayName("라이더 매칭 시스템에 요청을 보낸다.")
            @Test
            void acceptTest() {
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
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
        @DisplayName("주문을 손님에게 제공한다.")
        @Test
        void serveSuccessTest() {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrder(null, null, List.of(orderLineItem), OrderType.TAKEOUT, null, null);
            order = orderService.create(order);

            order = orderService.accept(order.getId());
            order = orderService.serve(order.getId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @DisplayName("수락상태가 아닌 경우에 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "ACCEPTED")
        void serveFailWhenOrderStatusIsNotAcceptedTest(final OrderStatus orderStatus) {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.TAKEOUT, orderStatus, null, LocalDateTime.now());
            order = orderRepository.save(order);

            final UUID orderId = order.getId();

            assertThatThrownBy(() -> orderService.serve(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class startDeliveryTest {
        @DisplayName("배달하기 타입인 경우 주문 상태가 서빙 상태인 경우에 배달을 시작할 수 있다.")
        @Test
        void startDeliverySuccessTest() {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.DELIVERY, OrderStatus.SERVED, "서울 강남구 테헤란로 411, 성담빌딩 13층", LocalDateTime.now());
            order = orderRepository.save(order);

            order = orderService.startDelivery(order.getId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @DisplayName("배달하기 타입이 아닌 경우 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERY")
        void startDeliveryWhenNotDeliveryTest(final OrderType orderType) {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            final Order order = createOrderWithId(null, List.of(orderLineItem), orderType, OrderStatus.SERVED, null, LocalDateTime.now());
            orderRepository.save(order);

            final UUID orderId = order.getId();

            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }

        @DisplayName("serve 상태가 아닌 경우 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SERVED")
        void startDeliveryWhenNotServedTest(final OrderStatus orderStatus) {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.DELIVERY, orderStatus, null, LocalDateTime.now());
            order = orderRepository.save(order);

            final UUID orderId = order.getId();

            assertThatThrownBy(() -> orderService.startDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class completeDeliveryTest {
        @DisplayName("배달 중 상태인 경우에 배달을 완료할 수 있다.")
        @Test
        void completeDeliverySuccessTest() {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.DELIVERY, OrderStatus.DELIVERING, "서울 강남구 테헤란로 411, 성담빌딩 13층", LocalDateTime.now());
            order = orderRepository.save(order);

            order = orderService.completeDelivery(order.getId());

            assertThat(order.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @DisplayName("배달 중 상태가 아닌 경우 예외가 발생한다.")
        @ParameterizedTest
        @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERING")
        void completeDeliveryFailWhenOrderStatusIsNotDeliveringTest(final OrderStatus orderStatus) {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.DELIVERY, orderStatus, "서울 강남구 테헤란로 411, 성담빌딩 13층", LocalDateTime.now());
            order = orderRepository.save(order);

            final UUID orderId = order.getId();

            assertThatThrownBy(() -> orderService.completeDelivery(orderId))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    class completeTest {
        @Nested
        class EatInTest {
            @DisplayName("먹고가기 주문을 완료할 수 있다.")
            @Test
            void completeEatInTest() {
                OrderTable orderTable = createOrderTableWithId("1번", true);
                orderTable = orderTableRepository.save(orderTable);
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrderWithId(orderTable, List.of(orderLineItem), OrderType.EAT_IN, OrderStatus.SERVED, null, LocalDateTime.now());
                order = orderRepository.save(order);

                order = orderService.complete(order.getId());

                assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                assertThat(order.getOrderTable().getNumberOfGuests()).isZero();
                assertThat(order.getOrderTable().isOccupied()).isFalse();
            }

            @DisplayName("serve 상태가 아닌 경우 예외가 발생한다.")
            @ParameterizedTest
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SERVED")
            void completeEatInFailWhenOrderStatusIsNotServedTest(final OrderStatus orderStatus) {
                OrderTable orderTable = createOrderTableWithId("1번", true);
                orderTable = orderTableRepository.save(orderTable);
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrderWithId(orderTable, List.of(orderLineItem), OrderType.EAT_IN, orderStatus, null, LocalDateTime.now());
                order = orderRepository.save(order);

                final UUID orderId = order.getId();

                assertThatThrownBy(() -> orderService.complete(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }

            @DisplayName("완료된 주문이 없는 경우 테이블을 치우지 않는다.")
            @Test
            void completeEatInFailWhenNotExistOrderTest() {
                OrderTable orderTable = createOrderTableWithId("1번", true, 2);
                orderTable = orderTableRepository.save(orderTable);
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrderWithId(orderTable, List.of(orderLineItem), OrderType.EAT_IN, OrderStatus.SERVED, null, LocalDateTime.now());
                order = orderRepository.save(order);
                final Order order2 = createOrderWithId(orderTable, List.of(orderLineItem), OrderType.EAT_IN, OrderStatus.SERVED, null, LocalDateTime.now());
                order = orderRepository.save(order2);

                final UUID orderId = order.getId();

                order = orderService.complete(orderId);

                assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
                assertThat(order2.getStatus()).isEqualTo(OrderStatus.SERVED);
                assertThat(order.getOrderTable().getNumberOfGuests()).isEqualTo(2);
                assertThat(order.getOrderTable().isOccupied()).isTrue();
            }
        }

        @Nested
        class TakeOutTest {
            @DisplayName("포장하기 주문을 완료할 수 있다.")
            @Test
            void completeTakeOutTest() {
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.TAKEOUT, OrderStatus.SERVED, null, LocalDateTime.now());
                order = orderRepository.save(order);

                order = orderService.complete(order.getId());

                assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("serve 상태가 아닌 경우 예외가 발생한다.")
            @ParameterizedTest
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "SERVED")
            void completeTakeOutFailWhenOrderStatusIsNotServedTest(final OrderStatus orderStatus) {
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.TAKEOUT, orderStatus, null, LocalDateTime.now());
                order = orderRepository.save(order);

                final UUID orderId = order.getId();

                assertThatThrownBy(() -> orderService.complete(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }

        @Nested
        class DeliveryTest {
            @DisplayName("배달하기 주문을 완료할 수 있다.")
            @Test
            void completeDeliveryTest() {
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.DELIVERY, OrderStatus.DELIVERED, "서울 강남구 테헤란로 411, 성담빌딩 13층", LocalDateTime.now());
                order = orderRepository.save(order);

                order = orderService.complete(order.getId());

                assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("DELIVERED 상태가 아닌 경우 예외가 발생한다.")
            @ParameterizedTest
            @EnumSource(value = OrderStatus.class, mode = EnumSource.Mode.EXCLUDE, names = "DELIVERED")
            void completeDeliveryFailWhenOrderStatusIsNotDeliveredTest(final OrderStatus orderStatus) {
                Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
                product = productRepository.save(product);
                MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
                menuGroup = menuGroupRepository.save(menuGroup);
                final MenuProduct menuProduct = createMenuProduct(product, 1);
                Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
                menu = menuRepository.save(menu);
                final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
                Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.DELIVERY, orderStatus, "서울 강남구 테헤란로 411, 성담빌딩 13층", LocalDateTime.now());
                order = orderRepository.save(order);

                final UUID orderId = order.getId();

                assertThatThrownBy(() -> orderService.complete(orderId))
                        .isInstanceOf(IllegalStateException.class);
            }
        }
    }

    @Nested
    class findAllTest {
        @DisplayName("주문을 전체 조회 한다.")
        @Test
        void findAllSuccessTest() {
            Product product = createProductWithId("떡볶이", BigDecimal.valueOf(16000));
            product = productRepository.save(product);
            MenuGroup menuGroup = MenuGroupFixture.createMenuGroupWithId("추천 그룹");
            menuGroup = menuGroupRepository.save(menuGroup);
            final MenuProduct menuProduct = createMenuProduct(product, 1);
            Menu menu = MenuFixture.createMenuWithId(menuGroup, "떡볶이", BigDecimal.valueOf(16000), true, List.of(menuProduct));
            menu = menuRepository.save(menu);
            final OrderLineItem orderLineItem = createOrderLineItem(menu, BigDecimal.valueOf(16000), 1);
            Order order = createOrderWithId(null, List.of(orderLineItem), OrderType.TAKEOUT, OrderStatus.ACCEPTED, null, LocalDateTime.now());
            order = orderRepository.save(order);

            final List<Order> orders = orderService.findAll();
            final List<UUID> orderIds = orders.stream()
                    .map(Order::getId)
                    .toList();

            assertThat(orders).hasSize(1);
            assertThat(orderIds).contains(order.getId());
        }
    }
}
