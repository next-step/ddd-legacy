package kitchenpos.application.fake;

import kitchenpos.application.OrderService;
import kitchenpos.application.fake.helper.*;
import kitchenpos.domain.*;
import kitchenpos.infra.RidersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static kitchenpos.application.fake.helper.ProductFixtureFactory.레몬에이드;
import static kitchenpos.application.fake.helper.ProductFixtureFactory.미트파이;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;


class FakeOrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final MenuRepository menuRepository = new InMemoryMenuRepository();
    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final RidersClient ridersClient = new FakeRidersClient();

    private OrderService orderService;

    @BeforeEach
    void setup() {
        orderService = new OrderService(orderRepository, menuRepository, orderTableRepository, ridersClient);
    }

    @DisplayName("주문 등록(wating) - 주문은 반드시 타입(매장식사, 배달, 테이크아웃)을 선택해야 한다.")
    @Test
    void create01() {
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder().build();
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<List<OrderLineItem>> provideOrderLineItemsForNullAndEmpty() {
        return Stream.of(
                null,
                Collections.emptyList()
        );
    }

    @DisplayName("주문 등록(wating) - 주문은 반드시 하나 이상의 메뉴(menu)를 포함해야 한다.")
    @MethodSource("provideOrderLineItemsForNullAndEmpty")
    @ParameterizedTest
    void create02(List<OrderLineItem> 주문_등록_요청_메뉴들) {
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder()
                .type(OrderType.DELIVERY)
                .build();
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 등록(wating) - 존재하는 메뉴만 선택할 수 있다.")
    @Test
    void create03() {
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder()
                .type(OrderType.DELIVERY)
                .addOrderLineItem(MenuFixtureFactory.미트파이_하나를_포함한_메뉴, 1)
                .build();

        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }


    private static Stream<OrderType> provideOrderTypeForDeliveryAndTakeout() {
        return Stream.of(
                OrderType.DELIVERY,
                OrderType.TAKEOUT
        );
    }

    //@TODO 매장식사는 음수로 주문이 가능함. 의도한 동작인지 확인후 개선
    @DisplayName("주문 등록(wating) - 메뉴 품목별 수량은 매장 식사가 아닌 경우 0보다 큰 값을 가져야 한다.")
    @MethodSource("provideOrderTypeForDeliveryAndTakeout")
    @ParameterizedTest
    void create04(OrderType 주문_등록_요청_타입) {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_하나를_포함한_메뉴);
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder()
                .type(주문_등록_요청_타입)
                .addOrderLineItem(MenuFixtureFactory.미트파이_하나를_포함한_메뉴, -1)
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("주문 등록(wating) - 진열된 메뉴만 선택할 수 있다")
    @Test
    void create05() {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_하나를_포함한_메뉴_미진열);
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder()
                .type(OrderType.EAT_IN)
                .addOrderLineItem(MenuFixtureFactory.미트파이_하나를_포함한_메뉴_미진열, 1)
                .build();
        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 등록(wating) - 주문 가격(진열된 메뉴의 가격과)과 메뉴의 가격은 다를 수 없다.")
    @Test
    void create07() {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);
        BigDecimal 실제_메뉴_가격보다_비싼_가격 = MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴.getPrice().add(BigDecimal.valueOf(100L));
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder()
                .type(OrderType.EAT_IN)
                .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1, 실제_메뉴_가격보다_비싼_가격)
                .build();
        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Stream<String> provideDeliveryAddressForNullAndEmptyString() {
        return Stream.of(
                null,
                ""
        );
    }

    @DisplayName("주문 등록(wating) - 배달 주문의 경우 반드시 배달 주소를 가져야 한다.")
    @MethodSource("provideDeliveryAddressForNullAndEmptyString")
    @ParameterizedTest
    void create08(String 배송_받을_주소) {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder()
                .type(OrderType.DELIVERY)
                .deliveryAddress(배송_받을_주소)
                .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("주문 등록(wating) - 매장 식사의 경우 반드시 착석한 테이블을 선택해야 한다.")
    @Test
    void create09() {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_01);
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder()
                .type(OrderType.EAT_IN)
                .orderTable(OrderTableFixtureFactory.오션뷰_테이블_01)
                .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 등록(wating) - 매장 식사의 경우 반드시 착석할 테이블이 존재해야 한다.")
    @Test
    void create10() {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);
        Order 주문_등록_요청 = new OrderFixtureFactory.Builder()
                .type(OrderType.EAT_IN)
                .orderTable(OrderTableFixtureFactory.오션뷰_테이블_01)
                .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
                .build();

        //when & then
        assertThatThrownBy(() -> orderService.create(주문_등록_요청))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 등록(wating) - 주문을 등록할 수 있다.")
    @Test
    void create11() {
        //given
        Menu 주문할_메뉴 = new MenuFixtureFactory.Builder()
                .id(UUID.randomUUID())
                .name("미트파이")
                .price(BigDecimal.valueOf(1800L))
                .addProduct(미트파이, 1)
                .addProduct(레몬에이드, 1)
                .displayed(true)
                .build();

        OrderTable 사용할_테이블 = new OrderTableFixtureFactory.Builder()
                .id(UUID.randomUUID())
                .name("오션뷰 테이블 02")
                .empty(false)
                .numberOfGuests(1)
                .build();


        menuRepository.save(주문할_메뉴);
        orderTableRepository.save(사용할_테이블);

        Order 주문_요청 = new OrderFixtureFactory.Builder()
                .type(OrderType.EAT_IN)
                .addOrderLineItem(주문할_메뉴, 1)
                .orderTable(사용할_테이블)
                .build();


        //when
        Order saved = orderService.create(주문_요청);

        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getStatus()).isEqualTo(OrderStatus.WAITING),
                () -> assertThat(saved.getOrderDateTime()).isNotNull()
        );
    }


    private static Stream<OrderStatus> provideOrderStatusExceptForWaiting() {
        return Stream.of(
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING,
                OrderStatus.SERVED
        );
    }

    @DisplayName("주문 승인(accept) - 대기중(waiting)인 주문만 승인할 수 있다.")
    @MethodSource("provideOrderStatusExceptForWaiting")
    @ParameterizedTest
    void accept01(OrderStatus 조회된_주문_상태) {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_02_이용중);
        UUID 승인할_주문_아이디 = UUID.randomUUID();
        Order 승인할_주문 = new OrderFixtureFactory.Builder()
                .id(승인할_주문_아이디)
                .status(조회된_주문_상태)
                .build();
        orderRepository.save(승인할_주문);

        //when & then
        assertThatThrownBy(() -> orderService.accept(승인할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }


    @DisplayName("주문 승인(accept) -  배달주문의 경우 라이더에게 배달을 요청해야 한다.")
    @Test
    void accept02() {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_02_이용중);
        UUID 승인할_주문_아이디 = UUID.randomUUID();
        Order 승인할_주문 = new OrderFixtureFactory.Builder()
                .id(승인할_주문_아이디)
                .status(OrderStatus.WAITING)
                .type(OrderType.DELIVERY)
                .addOrderLineItem(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴, 1)
                .deliveryAddress("우리집")
                .build();
        orderRepository.save(승인할_주문);

        //when & then
        Order updated = orderService.accept(승인할_주문_아이디);

        //then
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("주문 승인(accept) - 주문을 승인할 수 있다.")
    @Test
    void accept03() {
        //given
        orderRepository.save(OrderFixtureFactory.대기중인_포장_주문);

        //when
        Order updated = orderService.accept(OrderFixtureFactory.대기중인_포장_주문.getId());

        //then
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }


    @DisplayName("주문 승인(accept) - 존재하는 주문만 승인할 수 있다.")
    @Test
    void accept04() {
        //when & then
        assertThatThrownBy(() -> orderService.accept(OrderFixtureFactory.대기중인_포장_주문.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    private static Stream<OrderStatus> provideOrderStatusExceptForAccepted() {
        return Stream.of(
                OrderStatus.WAITING,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING,
                OrderStatus.SERVED
        );
    }

    @DisplayName("주문 서빙(serve) - 승인된(accept) 주문만 서빙할 수 있다.")
    @MethodSource("provideOrderStatusExceptForAccepted")
    @ParameterizedTest
    void serve01(OrderStatus 조회된_주문_상태) {
        //given
        menuRepository.save(MenuFixtureFactory.미트파이_레몬에이드_세트_메뉴);
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_02_이용중);
        UUID 서빙할_주문_아이디 = UUID.randomUUID();
        Order 서빙할_주문 = new OrderFixtureFactory.Builder()
                .id(서빙할_주문_아이디)
                .status(조회된_주문_상태)
                .build();
        orderRepository.save(서빙할_주문);


        //when & then
        assertThatThrownBy(() -> orderService.serve(서빙할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 서빙(serve) - 주문을 서빙할 수 있다.")
    @Test
    void serve02() {
        //given
        orderRepository.save(OrderFixtureFactory.승인된_포장_주문);

        //when & then
        Order updated = orderService.serve(OrderFixtureFactory.승인된_포장_주문.getId());

        //then
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("주문 서빙(accept) - 존재하는 주문만 서빙할 수 있다.")
    @Test
    void serve03() {
        //given
        UUID 서빙할_주문_아이디 = UUID.randomUUID();

        //when & then
        assertThatThrownBy(() -> orderService.serve(서빙할_주문_아이디))
                .isInstanceOf(NoSuchElementException.class);
    }

    private static Stream<OrderType> provideOrderTypeExceptForDelivery() {
        return Stream.of(
                OrderType.EAT_IN,
                OrderType.TAKEOUT
        );
    }

    @DisplayName("주문 배달(delivering) 시작 - 배달주문인 경우에만 배달을 시작할 수 있다.")
    @MethodSource("provideOrderTypeExceptForDelivery")
    @ParameterizedTest
    void startDelivery01(OrderType 조회된_주문_타입) {
        //given
        UUID 배달할_주문_아이디 = UUID.randomUUID();
        Order 배달을_시작할_주문 = new OrderFixtureFactory.Builder()
                .id(배달할_주문_아이디)
                .type(조회된_주문_타입)
                .status(OrderStatus.ACCEPTED)
                .build();
        orderRepository.save(배달을_시작할_주문);

        //when & then
        assertThatThrownBy(() -> orderService.startDelivery(배달할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }


    private static Stream<OrderStatus> provideOrderStatusExceptForServed() {
        return Stream.of(
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.DELIVERING,
                OrderStatus.WAITING
        );
    }

    @DisplayName("주문 배달(delivering) 시작 - 서빙(serve)된 주문만 배달을 시작할 수 있다.")
    @MethodSource("provideOrderStatusExceptForServed")
    @ParameterizedTest
    void startDelivery02(OrderStatus 조회된_주문_상태) {
        //given
        UUID 배달할_주문_아이디 = UUID.randomUUID();
        Order 배달을_시작할_주문 = new OrderFixtureFactory.Builder()
                .id(배달할_주문_아이디)
                .type(OrderType.DELIVERY)
                .status(조회된_주문_상태)
                .build();
        orderRepository.save(배달을_시작할_주문);

        //when & then
        assertThatThrownBy(() -> orderService.startDelivery(배달할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 배달(delivering) 시작 - 배달을 시작할 수 있다.")
    @Test
    void startDelivery03() {
        //given
        UUID 배달할_주문_아이디 = UUID.randomUUID();
        Order 배달을_시작할_주문 = new OrderFixtureFactory.Builder()
                .id(배달할_주문_아이디)
                .type(OrderType.DELIVERY)
                .status(OrderStatus.SERVED)
                .build();
        orderRepository.save(배달을_시작할_주문);

        //when
        Order updated = orderService.startDelivery(배달할_주문_아이디);

        //then
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문 배달(delivering) 시작 - 존재하는 주문만 배달을 시작할 수 있다.")
    @Test
    void startDelivery04() {
        //given
        UUID 배달할_주문_아이디 = UUID.randomUUID();

        //when & then
        assertThatThrownBy(() -> orderService.startDelivery(배달할_주문_아이디))
                .isInstanceOf(NoSuchElementException.class);
    }

    private static Stream<OrderStatus> provideOrderStatusExceptForDelivering() {
        return Stream.of(
                OrderStatus.ACCEPTED,
                OrderStatus.COMPLETED,
                OrderStatus.DELIVERED,
                OrderStatus.SERVED,
                OrderStatus.WAITING
        );
    }

    @DisplayName("배달 완료(delivered) - 배달중(delivering)인 주문만 배달을 완료할 수 있다.")
    @MethodSource("provideOrderStatusExceptForDelivering")
    @ParameterizedTest
    void completeDelivery01(OrderStatus 조회된_주문_상태) {
        //given
        UUID 배달_완료할_주문_아이디 = UUID.randomUUID();
        Order 배달_완료할_주문 = new OrderFixtureFactory.Builder()
                .id(배달_완료할_주문_아이디)
                .type(OrderType.DELIVERY)
                .status(조회된_주문_상태)
                .build();
        orderRepository.save(배달_완료할_주문);

        //when & then
        assertThatThrownBy(() -> orderService.completeDelivery(배달_완료할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("배달 완료(delivered) - 배달을 완료할 수 있다.")
    @Test
    void completeDelivery02() {
        //given
        UUID 배달_완료할_주문_아이디 = UUID.randomUUID();
        Order 배달_완료할_주문 = new OrderFixtureFactory.Builder()
                .id(배달_완료할_주문_아이디)
                .type(OrderType.DELIVERY)
                .status(OrderStatus.DELIVERING)
                .build();
        orderRepository.save(배달_완료할_주문);

        //when
        Order updated = orderService.completeDelivery(배달_완료할_주문_아이디);

        //then
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("배달 완료(accept) - 존재하는 주문만 배달을 완료할 수 있다.")
    @Test
    void completeDelivery03() {
        //given
        UUID 배달_완료할_주문_아이디 = UUID.randomUUID();

        //when & then
        assertThatThrownBy(() -> orderService.startDelivery(배달_완료할_주문_아이디))
                .isInstanceOf(NoSuchElementException.class);
    }


    @DisplayName("주문 완료(complete) - 매장식사, 테이크아웃의 경우 서빙된 주문만 완료 할 수 있다.")
    @MethodSource("provideOrderTypeExceptForDelivery")
    @ParameterizedTest
    void complete01(OrderType 조회된_주문_타입) {
        //given
        UUID 완료할_주문_아이디 = UUID.randomUUID();
        Order 완료할_주문 = new OrderFixtureFactory.Builder()
                .id(완료할_주문_아이디)
                .type(조회된_주문_타입)
                .status(OrderStatus.WAITING)
                .build();
        orderRepository.save(완료할_주문);

        //when & then
        assertThatThrownBy(() -> orderService.complete(완료할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 완료(complete) - 배달주문의 경우 배달완료된 주문만 완료 할 수 있다.")
    @Test
    void complete02() {
        //given
        UUID 완료할_주문_아이디 = UUID.randomUUID();
        Order 완료할_주문 = new OrderFixtureFactory.Builder()
                .id(완료할_주문_아이디)
                .type(OrderType.DELIVERY)
                .status(OrderStatus.WAITING)
                .build();
        orderRepository.save(완료할_주문);

        //when & then
        assertThatThrownBy(() -> orderService.complete(완료할_주문_아이디))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 완료(complete) - 매장식사의 경우 주문이 완료 되면 테이블을 정리해야 한다.")
    @Test
    void complete03() {
        //given
        orderRepository.save(OrderFixtureFactory.테이블에서_식사가_서빙된_주문);
        //when
        Order updated = orderService.complete(OrderFixtureFactory.테이블에서_식사가_서빙된_주문.getId());
        //then
        assertAll(
                () -> assertThat(updated.getOrderTable().isEmpty()).isTrue(),
                () -> assertThat(updated.getOrderTable().getNumberOfGuests()).isEqualTo(0)
        );
    }

    @DisplayName("주문 완료(complete) - 주문을 완료할 수 있다.")
    @Test
    void complete04() {
        //given
        orderRepository.save(OrderFixtureFactory.배달이_완료된_주문);
        //when
        Order updated = orderService.complete(OrderFixtureFactory.배달이_완료된_주문.getId());
        //then
        assertThat(updated.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("배달 완료(accept) - 존재하는 주문만 주문을 완료할 수 있다.")
    @Test
    void complete05() {
        //given
        UUID 완료할_주문_아이디 = UUID.randomUUID();
        //when & then
        assertThatThrownBy(() -> orderService.complete(완료할_주문_아이디))
                .isInstanceOf(NoSuchElementException.class);
    }


    @DisplayName("주문 조회 - 등록된 모든 주문을 조회할 수 있다.")
    @Test
    void findAll() {
        //given
        orderRepository.save(OrderFixtureFactory.배달이_완료된_주문);
        orderRepository.save(OrderFixtureFactory.테이블에서_식사가_서빙된_주문);

        List<Order> orders = orderService.findAll();
        //then
        assertAll(
                () -> assertThat(orders).hasSize(2),
                () -> assertThat(orders).contains(OrderFixtureFactory.배달이_완료된_주문, OrderFixtureFactory.테이블에서_식사가_서빙된_주문)
        );
    }

}
