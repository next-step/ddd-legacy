package kitchenpos.application;

import static kitchenpos.domain.MenuFixture.MenuWithUUIDAndMenuGroup;
import static kitchenpos.domain.MenuGroupFixture.MenuGroupWithUUID;
import static kitchenpos.domain.MenuProductFixture.MenuProductWithProduct;
import static kitchenpos.domain.OrderFixture.Order;
import static kitchenpos.domain.OrderFixture.OrderWithUUIDAndOrderDateTimeAndStatus;
import static kitchenpos.domain.OrderLineItemFixture.OrderLineIterm;
import static kitchenpos.domain.OrderLineItemFixture.OrderLineItermWithMenu;
import static kitchenpos.domain.OrderStatus.ACCEPTED;
import static kitchenpos.domain.OrderStatus.COMPLETED;
import static kitchenpos.domain.OrderStatus.DELIVERED;
import static kitchenpos.domain.OrderStatus.DELIVERING;
import static kitchenpos.domain.OrderStatus.SERVED;
import static kitchenpos.domain.OrderStatus.WAITING;
import static kitchenpos.domain.OrderTableFixture.OrderTableWithUUID;
import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.domain.OrderType.TAKEOUT;
import static kitchenpos.domain.ProductFixture.ProductWithUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderLineItem;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderServiceTest extends IntegrationTest {

    @Autowired
    private OrderService orderService;

    @DisplayName("새로운 주문을 생성할 수 있다.")
    @Nested
    class Create {

        private Menu 햄버거_콜라_세트메뉴;
        private OrderTable 테이블_1번;

        @BeforeEach
        void setUp() {
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
            MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            햄버거_콜라_세트메뉴 = MenuWithUUIDAndMenuGroup(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴,
                햄버거_메뉴상품, 콜라_메뉴상품
            );
            햄버거_콜라_세트메뉴.setDisplayed(true);
            햄버거_콜라_세트메뉴 = menuRepository.save(햄버거_콜라_세트메뉴);
            테이블_1번 = OrderTableWithUUID("테이블 1번");
            테이블_1번.setOccupied(true);
            테이블_1번 = orderTableRepository.save(테이블_1번);
        }

        @DisplayName("성공")
        @Test
        void success() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            Order request = Order(
                테이블_1번.getId(),
                EAT_IN,
                "서울특별시 최현구",
                orderLineItem
            );

            // when
            Order result = orderService.create(request);

            // then
            assertThat(result.getId()).isNotNull();
            assertThat(result.getType()).isEqualTo(EAT_IN);
            assertThat(result.getStatus()).isEqualTo(WAITING);
        }

        @DisplayName("주문 생성요청 속 메뉴가 우선 존재해야한다.")
        @Test
        void menuNotFoundException() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            Order request = Order(
                테이블_1번.getId(),
                EAT_IN,
                "서울특별시 최현구",
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("메뉴가 전시(노출) 중이어야 한다.")
        @Test
        void menuNotDisplayedException() {
            // given
            햄버거_콜라_세트메뉴.setDisplayed(false);
            menuRepository.save(햄버거_콜라_세트메뉴);

            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            Order request = Order(
                테이블_1번.getId(),
                EAT_IN,
                "서울특별시 최현구",
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문에는 1개 이상의 메뉴가 포함되어야 한다.")
        @Test
        void menuEmptyException() {
            // given
            Order request = Order(
                테이블_1번.getId(),
                EAT_IN,
                "서울특별시 최현구"
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("배달 주문 생성요청의 메뉴 수량은 음수일 수 없다.")
        @Test
        void deliveryOrderQuantityException() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                -1
            );

            Order request = Order(
                테이블_1번.getId(),
                DELIVERY,
                "서울특별시 최현구",
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("포장 주문 생성요청의 메뉴 수량은 음수일 수 없다.")
        @Test
        void takeOutOrderQuantityException() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                -1
            );

            Order request = Order(
                테이블_1번.getId(),
                TAKEOUT,
                "서울특별시 최현구",
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("매장 주문 생성요청의 메뉴 수량은 음수일 수도 있다.")
        @Test
        void eatInOrderQuantity() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                -1
            );

            Order request = Order(
                테이블_1번.getId(),
                EAT_IN,
                "서울특별시 최현구",
                orderLineItem
            );

            // when, then
            assertThat(orderService.create(request)).isNotNull();
        }

        @DisplayName("주문 생성요청의 메뉴 금액과 실제 메뉴 금액이 일치해야한다.")
        @Test
        void nonMatchMenuPriceException() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                20_000,
                1
            );

            Order request = Order(
                테이블_1번.getId(),
                EAT_IN,
                "서울특별시 최현구",
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문 생성요청의 주문형태가 null 이면 예외가 발생한다.")
        @Test
        void orderTypeNullException() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            Order request = Order(
                테이블_1번.getId(),
                null,
                "서울특별시 최현구",
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문형태가 배달일 경우, 주소가 null 이면 예외가 발생한다.")
        @Test
        void deliveryAddressNullException() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            Order request = Order(
                null,
                DELIVERY,
                null,
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문형태가 배달일 경우, 주소가 공백이면 예외가 발생한다.")
        @Test
        void deliveryAddressEmptyException() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            Order request = Order(
                null,
                DELIVERY,
                "",
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("주문형태가 매장식사일 경우, 주문테이블이 우선 존재해야 한다.")
        @Test
        void orderTableNotFoundException() {
            // given
            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            Order request = Order(
                UUID.fromString("00000000-0000-0000-0000-000000000000"),
                EAT_IN,
                null,
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("주문형태가 매장식사일 경우, 주문테이블에 손님이 있어야 한다.")
        @Test
        void notOccupiedException() {
            // given
            테이블_1번.setOccupied(false);
            orderTableRepository.save(테이블_1번);

            OrderLineItem orderLineItem = OrderLineIterm(
                햄버거_콜라_세트메뉴.getId(),
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            Order request = Order(
                테이블_1번.getId(),
                EAT_IN,
                null,
                orderLineItem
            );

            // when, then
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문상태를 접수완료로 변경할 수 있다.")
    @Nested
    class Accept {

        private Order 햄버거_콜라_세트메뉴_주문;

        @BeforeEach
        void setUp() {
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
            MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            Menu 햄버거_콜라_세트메뉴 = MenuWithUUIDAndMenuGroup(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴,
                햄버거_메뉴상품, 콜라_메뉴상품
            );
            햄버거_콜라_세트메뉴.setDisplayed(true);
            햄버거_콜라_세트메뉴 = menuRepository.save(햄버거_콜라_세트메뉴);
            OrderTable 테이블_1번 = OrderTableWithUUID("테이블 1번");
            테이블_1번.setOccupied(true);
            테이블_1번 = orderTableRepository.save(테이블_1번);

            OrderLineItem orderLineItem = OrderLineItermWithMenu(
                햄버거_콜라_세트메뉴,
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            햄버거_콜라_세트메뉴_주문 = orderRepository.save(OrderWithUUIDAndOrderDateTimeAndStatus(
                테이블_1번.getId(),
                EAT_IN,
                "서울특별시 최현구",
                LocalDateTime.of(2022, 9, 1, 18, 0),
                WAITING,
                orderLineItem
            ));
        }

        @DisplayName("성공")
        @Test
        void success() {
            // when
            Order result = orderService.accept(햄버거_콜라_세트메뉴_주문.getId());

            // then
            assertThat(result.getId()).isEqualTo(햄버거_콜라_세트메뉴_주문.getId());
            assertThat(result.getStatus()).isEqualTo(ACCEPTED);
        }

        @DisplayName("주문이 우선 존재해야 한다.")
        @Test
        void orderNotFoundException() {
            // when, then
            assertThatThrownBy(() -> orderService.accept(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("기존 주문상태가 대기중이 아닌 다른 상태면 접수완료로 변경할 수 없다.")
        @Test
        void notWaitingException() {
            // given
            햄버거_콜라_세트메뉴_주문.setStatus(ACCEPTED);
            orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.accept(
                햄버거_콜라_세트메뉴_주문.getId()
            )).isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("기존 주문형태가 배달일 경우 라이더에게 배달요청을 보낸다.")
        @Test
        void requestDeliveryToRidersIfDeliveryStatus() {
            // TODO: 2022/09/05 테스트 방법이 고안되면 테스트 작성
        }
    }

    @DisplayName("주문상태를 준비완료 변경할 수 있다.")
    @Nested
    class Serve {

        private Order 햄버거_콜라_세트메뉴_주문;

        @BeforeEach
        void setUp() {
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
            MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            Menu 햄버거_콜라_세트메뉴 = MenuWithUUIDAndMenuGroup(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴,
                햄버거_메뉴상품, 콜라_메뉴상품
            );
            햄버거_콜라_세트메뉴.setDisplayed(true);
            햄버거_콜라_세트메뉴 = menuRepository.save(햄버거_콜라_세트메뉴);
            OrderTable 테이블_1번 = OrderTableWithUUID("테이블 1번");
            테이블_1번.setOccupied(true);
            테이블_1번 = orderTableRepository.save(테이블_1번);

            OrderLineItem orderLineItem = OrderLineItermWithMenu(
                햄버거_콜라_세트메뉴,
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            햄버거_콜라_세트메뉴_주문 = orderRepository.save(OrderWithUUIDAndOrderDateTimeAndStatus(
                테이블_1번.getId(),
                EAT_IN,
                "서울특별시 최현구",
                LocalDateTime.of(2022, 9, 1, 18, 0),
                ACCEPTED,
                orderLineItem
            ));
        }

        @DisplayName("성공")
        @Test
        void success() {
            // when
            Order result = orderService.serve(햄버거_콜라_세트메뉴_주문.getId());

            // then
            assertThat(result.getId()).isEqualTo(햄버거_콜라_세트메뉴_주문.getId());
            assertThat(result.getStatus()).isEqualTo(SERVED);
        }

        @DisplayName("주문이 우선 존재해야 한다.")
        @Test
        void orderNotFoundException() {
            // when, then
            assertThatThrownBy(() -> orderService.serve(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("기존 주문상태가 접수완료가 아닐 경우 준비완료로 변경할 수 없다.")
        @Test
        void notAcceptedException() {
            // given
            햄버거_콜라_세트메뉴_주문.setStatus(SERVED);
            햄버거_콜라_세트메뉴_주문 = orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.serve(햄버거_콜라_세트메뉴_주문.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문상태를 배달중으로 변경할 수 있다.")
    @Nested
    class StartDelivery {

        private Order 햄버거_콜라_세트메뉴_주문;

        @BeforeEach
        void setUp() {
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
            MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            Menu 햄버거_콜라_세트메뉴 = MenuWithUUIDAndMenuGroup(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴,
                햄버거_메뉴상품, 콜라_메뉴상품
            );
            햄버거_콜라_세트메뉴.setDisplayed(true);
            햄버거_콜라_세트메뉴 = menuRepository.save(햄버거_콜라_세트메뉴);
            OrderTable 테이블_1번 = OrderTableWithUUID("테이블 1번");
            테이블_1번.setOccupied(true);
            테이블_1번 = orderTableRepository.save(테이블_1번);

            OrderLineItem orderLineItem = OrderLineItermWithMenu(
                햄버거_콜라_세트메뉴,
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            햄버거_콜라_세트메뉴_주문 = orderRepository.save(OrderWithUUIDAndOrderDateTimeAndStatus(
                null,
                DELIVERY,
                "서울특별시 최현구",
                LocalDateTime.of(2022, 9, 1, 18, 0),
                SERVED,
                orderLineItem
            ));
        }

        @DisplayName("성공")
        @Test
        void success() {
            // when
            Order result = orderService.startDelivery(햄버거_콜라_세트메뉴_주문.getId());

            // then
            assertThat(result.getId()).isEqualTo(햄버거_콜라_세트메뉴_주문.getId());
            assertThat(result.getStatus()).isEqualTo(DELIVERING);
        }

        @DisplayName("주문이 우선 존재해야 한다.")
        @Test
        void orderNotFoundException() {
            // when, then
            assertThatThrownBy(() -> orderService.startDelivery(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("기존 주문형태가 배달이어야 한다.")
        @Test
        void orderTypeNotDeliveryException() {
            // given
            햄버거_콜라_세트메뉴_주문.setType(EAT_IN);
            orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.startDelivery(
                햄버거_콜라_세트메뉴_주문.getId()
            )).isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("기존 주문상태가 준비완료가 아닐일 경우 배달중으로 변경할 수 없다.")
        @Test
        void orderStatusNotServedException() {
            // given
            햄버거_콜라_세트메뉴_주문.setStatus(DELIVERING);
            orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.startDelivery(
                햄버거_콜라_세트메뉴_주문.getId()
            )).isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문상태를 배달완료로 변경할 수 있다.")
    @Nested
    class CompleteDelivery {

        private Order 햄버거_콜라_세트메뉴_주문;

        @BeforeEach
        void setUp() {
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
            MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            Menu 햄버거_콜라_세트메뉴 = MenuWithUUIDAndMenuGroup(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴,
                햄버거_메뉴상품, 콜라_메뉴상품
            );
            햄버거_콜라_세트메뉴.setDisplayed(true);
            햄버거_콜라_세트메뉴 = menuRepository.save(햄버거_콜라_세트메뉴);
            OrderTable 테이블_1번 = OrderTableWithUUID("테이블 1번");
            테이블_1번.setOccupied(true);
            테이블_1번 = orderTableRepository.save(테이블_1번);

            OrderLineItem orderLineItem = OrderLineItermWithMenu(
                햄버거_콜라_세트메뉴,
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            햄버거_콜라_세트메뉴_주문 = orderRepository.save(OrderWithUUIDAndOrderDateTimeAndStatus(
                null,
                DELIVERY,
                "서울특별시 최현구",
                LocalDateTime.of(2022, 9, 1, 18, 0),
                DELIVERING,
                orderLineItem
            ));
        }

        @DisplayName("성공")
        @Test
        void success() {
            // when
            Order result = orderService.completeDelivery(햄버거_콜라_세트메뉴_주문.getId());

            // then
            assertThat(result.getId()).isEqualTo(햄버거_콜라_세트메뉴_주문.getId());
            assertThat(result.getStatus()).isEqualTo(DELIVERED);
        }

        @DisplayName("주문이 우선 존재해야 한다.")
        @Test
        void orderNotFoundException() {
            // when, then
            assertThatThrownBy(() -> orderService.completeDelivery(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("기존 주문형태가 배달이어야 한다.")
        @Test
        void orderTypeNotDeliveryException() {
            // given
            햄버거_콜라_세트메뉴_주문.setType(EAT_IN);
            orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.completeDelivery(
                햄버거_콜라_세트메뉴_주문.getId()
            )).isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("기존 주문상태가 준비완료가 아닐일 경우 배달중으로 변경할 수 없다.")
        @Test
        void orderStatusNotDeliveringException() {
            // given
            햄버거_콜라_세트메뉴_주문.setStatus(DELIVERED);
            orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.completeDelivery(
                햄버거_콜라_세트메뉴_주문.getId()
            )).isExactlyInstanceOf(IllegalStateException.class);
        }
    }

    @DisplayName("주문상태를 처리완료로 변경할 수 있다.")
    @Nested
    class Complete {

        private Order 햄버거_콜라_세트메뉴_주문;

        @BeforeEach
        void setUp() {
            Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
            Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
            MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
            MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
            MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
            Menu 햄버거_콜라_세트메뉴 = MenuWithUUIDAndMenuGroup(
                "햄버거 + 콜라 세트메뉴",
                17_000,
                세트메뉴,
                햄버거_메뉴상품, 콜라_메뉴상품
            );
            햄버거_콜라_세트메뉴.setDisplayed(true);
            햄버거_콜라_세트메뉴 = menuRepository.save(햄버거_콜라_세트메뉴);
            OrderTable 테이블_1번 = OrderTableWithUUID("테이블 1번");
            테이블_1번.setOccupied(true);
            테이블_1번 = orderTableRepository.save(테이블_1번);

            OrderLineItem orderLineItem = OrderLineItermWithMenu(
                햄버거_콜라_세트메뉴,
                햄버거_콜라_세트메뉴.getPrice(),
                1
            );

            햄버거_콜라_세트메뉴_주문 = orderRepository.save(OrderWithUUIDAndOrderDateTimeAndStatus(
                null,
                DELIVERY,
                "서울특별시 최현구",
                LocalDateTime.of(2022, 9, 1, 18, 0),
                DELIVERED,
                orderLineItem
            ));
        }

        @DisplayName("성공")
        @Test
        void success() {
            // when
            Order result = orderService.complete(햄버거_콜라_세트메뉴_주문.getId());

            // then
            assertThat(result.getId()).isEqualTo(햄버거_콜라_세트메뉴_주문.getId());
            assertThat(result.getStatus()).isEqualTo(COMPLETED);
        }

        @DisplayName("주문이 우선 존재해야 한다.")
        @Test
        void orderNotFoundException() {
            // when, then
            assertThatThrownBy(() -> orderService.complete(
                UUID.fromString("00000000-0000-0000-0000-000000000000")
            )).isExactlyInstanceOf(NoSuchElementException.class);
        }

        @DisplayName("주문형태가 배달일 때, 주문상태가 배달완료가 아니라면 처리완료로 변경할 수 없다.")
        @Test
        void orderStatusNotDeliveredExceptionIfTypeDelivery() {
            // given
            햄버거_콜라_세트메뉴_주문.setType(DELIVERY);
            햄버거_콜라_세트메뉴_주문.setStatus(ACCEPTED);
            orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.complete(햄버거_콜라_세트메뉴_주문.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문형태가 테이크아웃 일 때, 준비완료가 아니라면 처리완료로 변경할 수 없다.")
        @Test
        void orderStatusNotServedExceptionIfTypeTakeOut() {
            // given
            햄버거_콜라_세트메뉴_주문.setType(TAKEOUT);
            햄버거_콜라_세트메뉴_주문.setStatus(ACCEPTED);
            orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.complete(햄버거_콜라_세트메뉴_주문.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문형태가 매장식사 일 때, 준비완료가 아니라면 처리완료로 변경할 수 없다.")
        @Test
        void orderStatusNotServedExceptionIfTypeEatIn() {
            // given
            햄버거_콜라_세트메뉴_주문.setType(EAT_IN);
            햄버거_콜라_세트메뉴_주문.setStatus(ACCEPTED);
            orderRepository.save(햄버거_콜라_세트메뉴_주문);

            // when, then
            assertThatThrownBy(() -> orderService.complete(햄버거_콜라_세트메뉴_주문.getId()))
                .isExactlyInstanceOf(IllegalStateException.class);
        }

        @DisplayName("주문형태가 매장식사일 때, 주문테이블의 상태를 초기화한다.")
        @Test
        void orderTableResetIfOrderTypeEatIn() {
            // TODO: orderService.complete() -> LazyLoading 관련 설정 확인
        }
    }

    @DisplayName("전체 주문을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        Product 햄버거 = productRepository.save(ProductWithUUID("햄버거", 15_000));
        Product 콜라 = productRepository.save(ProductWithUUID("콜라", 2_000));
        MenuProduct 햄버거_메뉴상품 = MenuProductWithProduct(햄버거, 1);
        MenuProduct 콜라_메뉴상품 = MenuProductWithProduct(콜라, 1);
        MenuGroup 세트메뉴 = menuGroupRepository.save(MenuGroupWithUUID("세트메뉴"));
        Menu 햄버거_콜라_세트메뉴 = MenuWithUUIDAndMenuGroup(
            "햄버거 + 콜라 세트메뉴",
            17_000,
            세트메뉴,
            햄버거_메뉴상품, 콜라_메뉴상품
        );
        햄버거_콜라_세트메뉴.setDisplayed(true);
        햄버거_콜라_세트메뉴 = menuRepository.save(햄버거_콜라_세트메뉴);

        OrderLineItem orderLineItem = OrderLineItermWithMenu(
            햄버거_콜라_세트메뉴,
            햄버거_콜라_세트메뉴.getPrice(),
            1
        );

        Order 최현구_햄버거_콜라_세트메뉴_주문 = orderRepository.save(OrderWithUUIDAndOrderDateTimeAndStatus(
            null,
            DELIVERY,
            "서울특별시 최현구",
            LocalDateTime.of(2022, 9, 1, 18, 0),
            DELIVERED,
            orderLineItem
        ));

        Order 강남구_햄버거_콜라_세트메뉴_주문 = orderRepository.save(OrderWithUUIDAndOrderDateTimeAndStatus(
            null,
            TAKEOUT,
            "서울특별시 강남구",
            LocalDateTime.of(2022, 9, 3, 21, 0),
            SERVED,
            orderLineItem
        ));

        // when
        List<Order> result = orderService.findAll();

        // then // TODO: orderService.findAll() -> LazyLoading 관련 설정 확인
        assertThat(result).usingRecursiveFieldByFieldElementComparatorIgnoringFields("orderLineItems")
            .containsExactly(최현구_햄버거_콜라_세트메뉴_주문, 강남구_햄버거_콜라_세트메뉴_주문);
    }
}
