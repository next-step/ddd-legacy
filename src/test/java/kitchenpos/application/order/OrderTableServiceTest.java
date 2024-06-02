package kitchenpos.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.application.MenuService;
import kitchenpos.application.OrderService;
import kitchenpos.application.OrderTableService;
import kitchenpos.domain.common.FakeUuidBuilder;
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
import kitchenpos.domain.menu.FakeMenuGroupRepository;
import kitchenpos.domain.menu.FakeMenuRepository;
import kitchenpos.domain.order.FakeOrderRepository;
import kitchenpos.domain.order.FakeOrderTableRepository;
import kitchenpos.domain.product.FakeProductRepository;
import kitchenpos.fixture.MenuFixture;
import kitchenpos.fixture.MenuGroupFixture;
import kitchenpos.fixture.OrderFixture;
import kitchenpos.fixture.OrderTableFixture;
import kitchenpos.fixture.ProductFixture;
import kitchenpos.infra.DefaultKitchenridersClient;
import kitchenpos.infra.FakeBadWordsValidator;
import kitchenpos.infra.KitchenridersClient;
import kitchenpos.infra.BadWordsValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class OrderTableServiceTest {

  public static final int TEN_THOUSAND = 10_000;
  public static final long TWENTY_THOUSANDS = 20_000L;
  public static final String UDON = "우동";
  public static final String RAMEN = "라면";
  public static final String FOR_TWO = "이인용";
  public static final String TWO_UDONS = "우동과라면";

  private MenuGroupRepository menuGroupRepository;
  private MenuRepository menuRepository;
  private MenuService menuService;
  private ProductRepository productRepository;
  private OrderTableRepository orderTableRepository;
  private OrderRepository orderRepository;
  private BadWordsValidator badWordsValidator;
  private FakeUuidBuilder fakeUuidBuilder;
  private OrderTableService orderTableService;
  private OrderService orderService;
  private Product udon;
  private Product ramen;
  private MenuGroup menuGroup;
  private Menu menu;
  private KitchenridersClient kitchenridersClient;


  @BeforeEach
  void setUp() {
    menuRepository = new FakeMenuRepository();
    menuGroupRepository = new FakeMenuGroupRepository();
    badWordsValidator = new FakeBadWordsValidator();
    productRepository = new FakeProductRepository();
    fakeUuidBuilder = new FakeUuidBuilder();
    orderRepository = new FakeOrderRepository();
    orderTableRepository = new FakeOrderTableRepository();
    kitchenridersClient = new DefaultKitchenridersClient();

    menuService = new MenuService(menuRepository, menuGroupRepository, productRepository,
        badWordsValidator);

    udon = productRepository.save(
        ProductFixture.createProduct(UDON, TWENTY_THOUSANDS, fakeUuidBuilder));
    ramen = productRepository.save(
        ProductFixture.createProduct(RAMEN, TEN_THOUSAND, fakeUuidBuilder));

    menuGroup = MenuGroupFixture.createMenuGroup(FOR_TWO);
    menuGroupRepository.save(menuGroup);

    orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    menu = menuService.create(
        MenuFixture.createMenu(TWO_UDONS, TWENTY_THOUSANDS, menuGroup, udon, 2));

    orderService = new OrderService(orderRepository, menuRepository, orderTableRepository,
        kitchenridersClient);
  }

  @Nested
  @DisplayName("주문 테이블을 등록한다.")
  class OrderTableRegistration {

    @Test
    @DisplayName("주문 테이블을 등록한다.")
    void createOrderTable() {
      OrderTable actual = orderTableService.create(OrderTableFixture.createTable("table-name"));

      assertAll(() -> assertThat(actual.getName()).isEqualTo("table-name"),
          () -> assertThat(actual.getNumberOfGuests()).isEqualTo(0),
          () -> assertThat(actual.isOccupied()).isFalse()

      );
    }

    @DisplayName("주문 테이블의 이름은 비어있을 수 없다.")
    @ParameterizedTest
    @EmptySource
    void failToCreateOrderTableWithEmptyString(final String name) {

      assertThatIllegalArgumentException().isThrownBy(
          () -> orderTableService.create(OrderTableFixture.createTable(name)));
    }
  }

  @Nested
  @DisplayName("주문 테이블에 손님을 등록한다.")
  class OrderTableCustomRegistration {

    @Test
    @DisplayName("주문 테이블이 등록 되어있다면 상태를 `사용중`으로 변경한다.")
    void registerCustomerToOrderTable() {
      OrderTable orderTableRequest = OrderTableFixture.createTable("table-name1");
      OrderTable firstTable = orderTableService.create(orderTableRequest);

      OrderTable created = orderTableService.create(firstTable);
      OrderTable actual = orderTableService.sit(created.getId());
      assertAll(() -> assertThat(actual.isOccupied()).isEqualTo(true));
    }

  }

  @Nested
  @DisplayName("주문 테이블의 손님의 수를 변경한다.")
  class OrderTableChangeCustomer {

    @DisplayName("주문 테이블의 변경하고자 하는 손님의 수가 음수라면 변경되지 않는다.")
    @ParameterizedTest
    @ValueSource(ints = {-324234, -234, -1})
    void failToRegisterWithNegativeCustomer(int numOfGuests) {
      OrderTable orderTableRequest = OrderTableFixture.createTable("table-name1");
      OrderTable firstTable = orderTableService.create(orderTableRequest);

      OrderTable actual = orderTableService.create(firstTable);

      orderTableRequest.setNumberOfGuests(numOfGuests);
      assertThatIllegalArgumentException().isThrownBy(
          () -> orderTableService.changeNumberOfGuests(actual.getId(), orderTableRequest));
    }

    @Test
    @DisplayName("주문 테이블이 등록되어 있지 않으면 변경되지 않는다.")
    void failToRegisterWithNotExistingOrderTable() {

      assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
          () -> orderTableService.sit(UUID.randomUUID()));
    }
  }

  @Nested
  @DisplayName("손님의 수를 초기화하여(0이 기본값) 주문 테이블의 상태를 `미사용중`으로 변경할 수 있다.")
  class OrderTableClean {

    @Test
    @DisplayName("주문 테이블의 주문 상태가 `주문완료`이어야 한다.")
    void cleanOrderTableWithOrderStatusToComplete() {

      OrderTable orderTableRequest = OrderTableFixture.createTable("table-name1");
      OrderTable actual = orderTableService.create(orderTableRequest);

      orderTableService.sit(actual.getId());
      Order order = orderService.create(
          OrderFixture.createEatInOrder(actual, OrderFixture.createOrderLineItem(menu, 2)));
      assertAll(() -> assertThat(actual.isOccupied()).isEqualTo(true));

      orderService.accept(order.getId());
      orderService.serve(order.getId());
      orderService.complete(order.getId());
      OrderTable result = orderTableService.clear(actual.getId());
      assertAll(() -> assertThat(result.isOccupied()).isEqualTo(false),
          () -> assertThat(result.getNumberOfGuests()).isEqualTo(0));
    }

    @Test
    @DisplayName("주문 테이블이 등록되어 있지 않으면 변경되지 않는다.")
    void failToCleanOrderTableWithNotRegisteredOrderTable() {
      assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(
          () -> orderTableService.clear(UUID.randomUUID()));
    }

    @Test
    @DisplayName("주문 테이블의 상태가 `사용 중`이 아니라면 변경되지 않는다.")
    void failToCleanOrderTableWithOrderTableStatusToInUse() {

      OrderTable orderTableRequest = OrderTableFixture.createTable("table-name1");
      OrderTable firstTable = orderTableService.create(orderTableRequest);

      orderTableService.create(firstTable);
      orderTableService.sit(firstTable.getId());
      Order order = orderService.create(
          OrderFixture.createEatInOrder(firstTable, OrderFixture.createOrderLineItem(menu, 2)));
      orderService.accept(order.getId());

      assertAll(() -> assertThat(order.getStatus()).isEqualTo(OrderStatus.ACCEPTED));

      assertThatExceptionOfType(IllegalStateException.class)
          .isThrownBy(
          () -> orderTableService.clear(firstTable.getId()));
    }

  }

  @Nested
  @DisplayName("주문 테이블들을 조회할 수 있다.")
  class OrderTableView {

    @Test
    @DisplayName("사장님이 주문 테이블들을 조회할 수 있다.")
    void viewOrderTable() {
      OrderTable firstTable = orderTableService.create(
          OrderTableFixture.createTable("table-name1"));
      OrderTable secondTable = orderTableService.create(
          OrderTableFixture.createTable("table-name2"));
      orderTableService.create(firstTable);
      orderTableService.create(secondTable);

      List<OrderTable> actual = orderTableService.findAll();

      assertThat(actual).contains(firstTable, secondTable);
    }

  }
}
