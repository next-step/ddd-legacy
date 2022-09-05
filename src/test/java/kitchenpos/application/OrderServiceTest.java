package kitchenpos.application;

import static kitchenpos.domain.MenuFixture.MenuWithUUIDAndMenuGroup;
import static kitchenpos.domain.MenuGroupFixture.MenuGroupWithUUID;
import static kitchenpos.domain.MenuProductFixture.MenuProductWithProduct;
import static kitchenpos.domain.OrderFixture.Order;
import static kitchenpos.domain.OrderLineItemFixture.OrderLineIterm;
import static kitchenpos.domain.OrderTableFixture.OrderTableWithUUID;
import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.domain.ProductFixture.ProductWithUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

        @DisplayName("주문 생성요청의 메뉴 수량은 음수일 수 없다.")
        @Test
        void nonMatchMenuException() {
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
            assertThatThrownBy(() -> orderService.create(request))
                .isExactlyInstanceOf(IllegalArgumentException.class);
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
                테이블_1번.getId(),
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
                테이블_1번.getId(),
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
}
