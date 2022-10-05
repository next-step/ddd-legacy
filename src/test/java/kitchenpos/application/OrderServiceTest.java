package kitchenpos.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.IntStream;
import kitchenpos.domain.FakeMenuRepository;
import kitchenpos.domain.FakeOrderRepository;
import kitchenpos.domain.FakeOrderTableRepository;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import kitchenpos.infra.FakeKitchenridersClient;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class OrderServiceTest {

    private final MenuRepository menuRepository = new FakeMenuRepository();

    private final OrderRepository orderRepository = new FakeOrderRepository();

    private final OrderTableRepository orderTableRepository = new FakeOrderTableRepository();

    private final KitchenridersClient kitchenridersClient = new FakeKitchenridersClient();

    // SUT

    private final OrderService orderService = new OrderService(
        orderRepository,
        menuRepository,
        orderTableRepository,
        kitchenridersClient
    );

    @DisplayName("생성")
    @Nested
    class Ogdphkck {

        @DisplayName("매장 주문")
        @Nested
        class Dpgosmuv {

            @DisplayName("매장 주문을 생성할 수 있다.")
            @Test
            void hcotnptr() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTable.setOccupied(true);
                orderTableRepository.save(orderTable);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.EAT_IN);
                requestOrder.setOrderLineItems(orderLineItems);
                requestOrder.setOrderTableId(orderTable.getId());

                // when
                final Order order = orderService.create(requestOrder);

                // then
                assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            }

            @DisplayName("주문 유형이 설정되지 않으면 안된다.")
            @Test
            void vefgeesp() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTable.setOccupied(true);
                orderTableRepository.save(orderTable);

                final Order requestOrder = new Order();
                requestOrder.setOrderLineItems(orderLineItems);
                requestOrder.setOrderTableId(orderTable.getId());

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("주문 항목이 설정되지 않거나 비어 있으면 안된다.")
            @NullAndEmptySource
            @ParameterizedTest
            void djmcakoo(final List<OrderLineItem> orderLineItems) {
                // given
                final OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTable.setOccupied(true);
                orderTableRepository.save(orderTable);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.EAT_IN);
                requestOrder.setOrderLineItems(orderLineItems);
                requestOrder.setOrderTableId(orderTable.getId());

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("숨겨진 메뉴가 포함되어 있으면 안된다.")
            @Test
            void tuwfpcxh() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(false);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTable.setOccupied(true);
                orderTableRepository.save(orderTable);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.EAT_IN);
                requestOrder.setOrderLineItems(orderLineItems);
                requestOrder.setOrderTableId(orderTable.getId());

                // when / then
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("메뉴의 가격과 주문 항목의 가격이 다르면 안된다.")
            @Test
            void xdwivtxd() {
                // given
                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.valueOf(10000));
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(BigDecimal.valueOf(20000));

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTable.setOccupied(true);
                orderTableRepository.save(orderTable);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.EAT_IN);
                requestOrder.setOrderLineItems(orderLineItems);
                requestOrder.setOrderTableId(orderTable.getId());

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("매장 주문인 경우 주문 테이블이 설정되지 않으면 안된다.")
            @Test
            void jaieuuoj() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.EAT_IN);
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("설정된 주문 테이블이 점유중인 상태가 아니면 안된다.")
            @Test
            void clvqgbao() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final OrderTable orderTable = new OrderTable();
                orderTable.setId(UUID.randomUUID());
                orderTable.setOccupied(false);
                orderTableRepository.save(orderTable);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.EAT_IN);
                requestOrder.setOrderLineItems(orderLineItems);
                requestOrder.setOrderTableId(orderTable.getId());

                // when / then
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }
        }

        @DisplayName("포장 주문")
        @Nested
        class Jfigstry {

            @DisplayName("포장 주문을 생성할 수 있다.")
            @Test
            void mbrvmree() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.TAKEOUT);
                requestOrder.setOrderLineItems(orderLineItems);

                // when
                final Order order = orderService.create(requestOrder);

                // then
                assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            }

            @DisplayName("주문 유형이 설정되지 않으면 안된다.")
            @Test
            void cseggemc() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("주문 항목이 설정되지 않거나 비어 있으면 안된다.")
            @NullAndEmptySource
            @ParameterizedTest
            void bdslmzbv(final List<OrderLineItem> orderLineItems) {
                // given
                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.TAKEOUT);
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("주문에 포함된 메뉴의 수량이 음수이면 안된다.")
            @Test
            void qapzynna() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(-1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.TAKEOUT);
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("숨겨진 메뉴가 포함되어 있으면 안된다.")
            @Test
            void haqjwfdo() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(false);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.TAKEOUT);
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("메뉴의 가격과 주문 항목의 가격이 다르면 안된다.")
            @Test
            void gcatacio() {
                // given
                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.valueOf(10000));
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(BigDecimal.valueOf(20000));

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.TAKEOUT);
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }
        }

        @DisplayName("배달 주문")
        @Nested
        class Fdmzuntf {

            @DisplayName("배달 주문")
            @Test
            void bobzygmn() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.DELIVERY);
                requestOrder.setDeliveryAddress("peculiar");
                requestOrder.setOrderLineItems(orderLineItems);

                // when
                final Order order = orderService.create(requestOrder);

                // then
                assertThat(order.getStatus()).isEqualTo(OrderStatus.WAITING);
            }

            @DisplayName("주문 유형이 설정되지 않으면 안된다.")
            @Test
            void mchewldl() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setDeliveryAddress("peculiar");
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("주문 항목이 설정되지 않거나 비어 있으면 안된다.")
            @NullAndEmptySource
            @ParameterizedTest
            void mrpzgxzf(final List<OrderLineItem> orderLineItems) {
                // given
                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.DELIVERY);
                requestOrder.setDeliveryAddress("peculiar");
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("주문에 포함된 메뉴의 수량이 음수이면 안된다.")
            @Test
            void lhxeqmqy() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(-1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.DELIVERY);
                requestOrder.setDeliveryAddress("peculiar");
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("숨겨진 메뉴가 포함되어 있으면 안된다.")
            @Test
            void qqdantkp() {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(false);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.DELIVERY);
                requestOrder.setDeliveryAddress("peculiar");
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("메뉴의 가격과 주문 항목의 가격이 다르면 안된다.")
            @Test
            void pwwcrmmt() {
                // given
                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(BigDecimal.valueOf(10000));
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(BigDecimal.valueOf(20000));

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.DELIVERY);
                requestOrder.setDeliveryAddress("peculiar");
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }

            @DisplayName("배달 주소가 설정되지 않거나 비어 있으면 안된다.")
            @NullAndEmptySource
            @ParameterizedTest
            void uviodjeu(final String deliveryAddress) {
                // given
                final BigDecimal price = BigDecimal.valueOf(10000);

                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setDisplayed(true);
                menu.setPrice(price);
                menuRepository.save(menu);

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(price);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order requestOrder = new Order();
                requestOrder.setType(OrderType.DELIVERY);
                requestOrder.setOrderLineItems(orderLineItems);

                // when / then
                assertThatIllegalArgumentException().isThrownBy(() ->
                    orderService.create(requestOrder));
            }
        }
    }

    @DisplayName("접수")
    @Nested
    class Uykgeymh {

        @DisplayName("매장 주문")
        @Nested
        class Exfbtcqb {

            @DisplayName("매장 주문을 접수할 수 있다.")
            @Test
            void ckktqkgi() {
                // given
                final OrderTable orderTable = new OrderTable();

                final Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setStatus(OrderStatus.WAITING);
                order.setOrderTable(orderTable);
                order.setOrderTableId(orderTable.getId());
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.accept(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }

            @DisplayName("대기중이 아닌 매장 주문은 접수할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"WAITING"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void pyqaiydd(OrderStatus status) {
                // given
                final OrderTable orderTable = new OrderTable();

                final Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setStatus(status);
                order.setOrderTable(orderTable);
                order.setOrderTableId(orderTable.getId());
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.accept(order.getId()));
            }
        }

        @DisplayName("포장 주문")
        @Nested
        class Edwetgkn {

            @DisplayName("포장 주문을 접수할 수 있다.")
            @Test
            void tzntbynt() {
                // given
                final Order order = new Order();
                order.setType(OrderType.TAKEOUT);
                order.setStatus(OrderStatus.WAITING);
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.accept(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }

            @DisplayName("대기중이 아닌 포장 주문은 접수할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"WAITING"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void lpyuuakk(OrderStatus status) {
                // given
                final Order order = new Order();
                order.setType(OrderType.TAKEOUT);
                order.setStatus(status);
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.accept(order.getId()));
            }
        }

        @DisplayName("배달 주문")
        @Nested
        class Vpksogjf {

            @DisplayName("배달 주문을 접수할 수 있다.")
            @Test
            void qiyuxqep() {
                // given
                final Menu menu = new Menu();
                menu.setId(UUID.randomUUID());
                menu.setPrice(BigDecimal.valueOf(10000));

                final OrderLineItem orderLineItem = new OrderLineItem();
                orderLineItem.setMenu(menu);
                orderLineItem.setMenuId(menu.getId());
                orderLineItem.setQuantity(1);
                orderLineItem.setPrice(BigDecimal.valueOf(10000));
                orderLineItem.setSeq(1L);

                final List<OrderLineItem> orderLineItems = new ArrayList<>();
                orderLineItems.add(orderLineItem);

                final Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setStatus(OrderStatus.WAITING);
                order.setOrderLineItems(orderLineItems);
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.accept(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
            }

            @DisplayName("아직 접수되지 않은 배달 주문은 서빙할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"WAITING"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void cajdjhvw(OrderStatus status) {
                // given
                final Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setStatus(status);
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.accept(order.getId()));
            }
        }
    }

    @DisplayName("서빙")
    @Nested
    class Xkgneryp {

        @DisplayName("매장 주문")
        @Nested
        class Yrtacqwh {

            @DisplayName("매장 주문을 서빙할 수 있다.")
            @Test
            void ycasbmqs() {
                // given
                final OrderTable orderTable = new OrderTable();

                final Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setStatus(OrderStatus.ACCEPTED);
                order.setOrderTable(orderTable);
                order.setOrderTableId(orderTable.getId());
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.serve(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
            }

            @DisplayName("아직 접수되지 않은 매장 주문은 서빙할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"ACCEPTED"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void noddcjmr(OrderStatus status) {
                // given
                final OrderTable orderTable = new OrderTable();

                final Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setStatus(status);
                order.setOrderTable(orderTable);
                order.setOrderTableId(orderTable.getId());
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.serve(order.getId()));
            }
        }

        @DisplayName("포장 주문")
        @Nested
        class Gugqyucr {

            @DisplayName("포장 주문을 서빙할 수 있다.")
            @Test
            void scnmydbk() {
                // given
                final Order order = new Order();
                order.setType(OrderType.TAKEOUT);
                order.setStatus(OrderStatus.ACCEPTED);
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.serve(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
            }

            @DisplayName("아직 접수되지 않은 포장 주문은 서빙할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"ACCEPTED"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void udxuochl(OrderStatus status) {
                // given
                final Order order = new Order();
                order.setType(OrderType.TAKEOUT);
                order.setStatus(status);
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.serve(order.getId()));
            }
        }

        @DisplayName("배달 주문")
        @Nested
        class Aghjzmqf {

            @DisplayName("배달 주문을 서빙할 수 있다.")
            @Test
            void gccvcbyy() {
                // given
                final Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setStatus(OrderStatus.ACCEPTED);
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.serve(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.SERVED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.SERVED);
            }

            @DisplayName("아직 접수되지 않은 배달 주문은 서빙할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"ACCEPTED"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void liajxqwm(OrderStatus status) {
                // given
                final Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setStatus(status);
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.serve(order.getId()));
            }
        }
    }

    @DisplayName("배달 시작")
    @Nested
    class Dkfrhskp {

        @DisplayName("배달 주문의 배달을 시작할 수 있다.")
        @Test
        void dbcwtyot() {
            // given
            final Order order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.SERVED);
            orderRepository.save(order);

            // when
            final Order deliveryStartedOrder = orderService.startDelivery(order.getId());

            // then
            assertThat(deliveryStartedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);

            final Order foundOrder = orderRepository.findById(order.getId())
                .orElse(null);
            assertThat(foundOrder).isNotNull();
            assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.DELIVERING);
        }

        @DisplayName("아직 서빙되지 않은 배달 주문의 배달을 시작할 수 없다.")
        @EnumSource(
            value = OrderStatus.class,
            names = {"SERVED"},
            mode = Mode.EXCLUDE
        )
        @ParameterizedTest
        void wtqmylrr(OrderStatus status) {
            // given
            final Order order = new Order();
            order.setType(OrderType.TAKEOUT);
            order.setStatus(status);
            orderRepository.save(order);

            // when
            assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(order.getId()));
        }

        @DisplayName("매장 주문은 배달을 시작할 수 없다.")
        @EnumSource(OrderStatus.class)
        @ParameterizedTest
        void twiuwlki(OrderStatus status) {
            // given
            final OrderTable orderTable = new OrderTable();

            final Order order = new Order();
            order.setType(OrderType.EAT_IN);
            order.setStatus(status);
            order.setOrderTable(orderTable);
            order.setOrderTableId(orderTable.getId());
            orderRepository.save(order);

            // when
            assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(order.getId()));
        }

        @DisplayName("포장 주문은 배달을 시작할 수 없다.")
        @EnumSource(OrderStatus.class)
        @ParameterizedTest
        void qzcienfs(OrderStatus status) {
            // given
            final Order order = new Order();
            order.setType(OrderType.TAKEOUT);
            order.setStatus(status);
            orderRepository.save(order);

            // when
            assertThatIllegalStateException().isThrownBy(() ->
                orderService.startDelivery(order.getId()));
        }
    }

    @DisplayName("배달 완료")
    @Nested
    class Uxkffbnl {

        @DisplayName("배달 주문의 배달을 완료할 수 있다.")
        @Test
        void htsdjbmm() {
            // given
            final Order order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setStatus(OrderStatus.DELIVERING);
            orderRepository.save(order);

            // when
            final Order deliveryCompletedOrder = orderService.completeDelivery(order.getId());

            // then
            assertThat(deliveryCompletedOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);

            final Order foundOrder = orderRepository.findById(order.getId())
                .orElse(null);
            assertThat(foundOrder).isNotNull();
            assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.DELIVERED);
        }

        @DisplayName("배달중이지 않은 배달 주문의 배달은 완료할 수 없다.")
        @EnumSource(
            value = OrderStatus.class,
            names = {"DELIVERING"},
            mode = Mode.EXCLUDE
        )
        @ParameterizedTest
        void tuhynphr(OrderStatus status) {
            // given
            final Order order = new Order();
            order.setType(OrderType.DELIVERY);
            order.setStatus(status);
            orderRepository.save(order);

            // when
            assertThatIllegalStateException().isThrownBy(() ->
                orderService.completeDelivery(order.getId()));
        }
    }

    @DisplayName("주문 완료")
    @Nested
    class Boinkwuf {

        @DisplayName("매장 주문")
        @Nested
        class Cigfowrk {

            @DisplayName("매장 주문을 완료할 수 있다.")
            @Test
            void ebsvzpit() {
                // given
                final OrderTable orderTable = new OrderTable();

                final Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setStatus(OrderStatus.SERVED);
                order.setOrderTable(orderTable);
                order.setOrderTableId(orderTable.getId());
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.complete(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("매장 주문이 완료된 경우 주문 테이블이 비워져야 한다.")
            @Test
            void ptfmzxdz() {
                // given
                final OrderTable orderTable = new OrderTable();
                orderTable.setNumberOfGuests(4);
                orderTable.setOccupied(true);

                final Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setStatus(OrderStatus.SERVED);
                order.setOrderTable(orderTable);
                order.setOrderTableId(orderTable.getId());
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.complete(order.getId());

                // then
                assertThat(completedOrder.getOrderTable().getNumberOfGuests()).isZero();
                assertThat(completedOrder.getOrderTable().isOccupied()).isFalse();

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getOrderTable().getNumberOfGuests()).isZero();
                assertThat(foundOrder.getOrderTable().isOccupied()).isFalse();
            }

            @DisplayName("아직 서빙되지 않은 매장 주문은 완료할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"SERVED"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void sqqhfkvo(OrderStatus status) {
                // given
                final OrderTable orderTable = new OrderTable();

                final Order order = new Order();
                order.setType(OrderType.EAT_IN);
                order.setStatus(status);
                order.setOrderTable(orderTable);
                order.setOrderTableId(orderTable.getId());
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.complete(order.getId()));
            }
        }

        @DisplayName("포장 주문")
        @Nested
        class Sqttdmsm {

            @DisplayName("포장 주문을 완료할 수 있다.")
            @Test
            void fjalsdhg() {
                // given
                final Order order = new Order();
                order.setType(OrderType.TAKEOUT);
                order.setStatus(OrderStatus.SERVED);
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.complete(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("아직 서빙되지 않은 포장 주문은 완료할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"SERVED"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void zqjjmsos(OrderStatus status) {
                // given
                final Order order = new Order();
                order.setType(OrderType.TAKEOUT);
                order.setStatus(status);
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.complete(order.getId()));
            }
        }

        @DisplayName("배달 주문")
        @Nested
        class Cozkwhuv {

            @DisplayName("배달 주문을 완료할 수 있다.")
            @Test
            void ltvgpuzi() {
                // given
                final Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setStatus(OrderStatus.DELIVERED);
                orderRepository.save(order);

                // when
                final Order completedOrder = orderService.complete(order.getId());

                // then
                assertThat(completedOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);

                final Order foundOrder = orderRepository.findById(order.getId())
                    .orElse(null);
                assertThat(foundOrder).isNotNull();
                assertThat(foundOrder.getStatus()).isEqualTo(OrderStatus.COMPLETED);
            }

            @DisplayName("아직 배달되지 않은 배달 주문은 완료할 수 없다.")
            @EnumSource(
                value = OrderStatus.class,
                names = {"DELIVERED"},
                mode = Mode.EXCLUDE
            )
            @ParameterizedTest
            void zqjjmsos(OrderStatus status) {
                // given
                final Order order = new Order();
                order.setType(OrderType.DELIVERY);
                order.setStatus(status);
                orderRepository.save(order);

                // when
                assertThatIllegalStateException().isThrownBy(() ->
                    orderService.complete(order.getId()));
            }
        }
    }

    @DisplayName("목록 조회")
    @Nested
    class Utfiduxm {

        @DisplayName("존재하는 주문을 모두 조회할 수 있다.")
        @ValueSource(ints = {
            19, 10, 26, 14, 21,
            4, 22, 20, 12, 15,
        })
        @ParameterizedTest
        void cissfqiu(final int size) {
            // given
            IntStream.range(0, size)
                .forEach(n -> {
                    final Order order = new Order();
                    order.setId(UUID.randomUUID());
                    orderRepository.save(order);
                });

            // when
            final List<Order> orders = orderService.findAll();

            // then
            assertThat(orders).hasSize(size);
        }

        @DisplayName("메뉴가 없는 상태에서 모두 조회시 빈 list가 반환되어야 한다.")
        @Test
        void cvyqtuuq() {
            // when
            final List<Order> orders = orderService.findAll();

            // then
            assertThat(orders).isEmpty();
        }
    }
}
