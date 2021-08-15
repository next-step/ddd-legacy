package kitchenpos.order.application;

import kitchenpos.application.OrderService;
import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static kitchenpos.domain.OrderType.DELIVERY;
import static kitchenpos.domain.OrderType.EAT_IN;
import static kitchenpos.order.fixture.OrderFixture.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@DisplayName("Order 서비스 테스트")
@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    private static final int 가격 = 16000;
    private static final int 주문_수량 = 3;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;
    @Mock
    private KitchenridersClient kitchenridersClient;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                menuRepository,
                orderTableRepository,
                kitchenridersClient);
    }

    @DisplayName("주문은 배달, 포장, 매장식사 셋 중에 하나가 아닐 경우 IllegalArgumentException을 던진다")
    @ParameterizedTest
    @NullSource
    public void createWithValidStatus(OrderType type) {
        // given
        Order 주문 = 주문(type, 서울_주소, Arrays.asList(
                주문_상품(주문_수량, 가격)));

        // when, then
        assertThatThrownBy(() -> orderService.create(주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품이 null이나 빈 경우 IllegalArgumentException을 던진다")
    @ParameterizedTest
    @NullAndEmptySource
    public void createWithoutOrderLineItem(List<OrderLineItem> orderLineItems) {
        // given
        Order 매장_주문 = 매장_주문(orderLineItems);

        // when, then
        assertThatThrownBy(() -> orderService.create(매장_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품이 등록된 상품에 없는 경우 IllegalArgumentException을 던진다")
    @Test
    public void createWithNotRegisteredMenu() {
        // given
        Order 매장_주문 = 매장_주문(Arrays.asList(
                주문_상품(주문_수량, 가격),
                주문_상품(주문_수량, 가격)));

        Menu 후라이드_한마리_메뉴 = 후라이드_한마리_메뉴(가격, false);

        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(후라이드_한마리_메뉴));

        // when, then
        assertThatThrownBy(() -> orderService.create(매장_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 상품 수량은 매장 식사가 아닌 경우 하나 이상 주문하지 않으면 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @EnumSource(value = OrderType.class, mode = EnumSource.Mode.EXCLUDE, names = "EAT_IN")
    public void createShouldOrderAtLeastOneIfOrderTypeIsNotEatIn(OrderType notEatIn) {
        // given
        int quantity = -1;

        Menu 후라이드_한마리_메뉴 = 후라이드_한마리_메뉴(가격, false);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(후라이드_한마리_메뉴));

        Order 매장_식사_아닌_주문 = 주문(notEatIn, 서울_주소, Arrays.asList(
                주문_상품(quantity, 가격)));

        // when, then
        assertThatThrownBy(() -> orderService.create(매장_식사_아닌_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴가 숨겨진 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(booleans = {false})
    public void createWithNonDisplayedMenu(boolean isDisplay) {
        // given
        Menu 숨겨진_메뉴 = 후라이드_한마리_메뉴(가격, isDisplay);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(숨겨진_메뉴));
        given(menuRepository.findById(any())).willReturn(Optional.of(숨겨진_메뉴));

        Order 배달_주문 = 배달_주문(Arrays.asList(
                주문_상품(주문_수량, 가격)));

        // when, then
        assertThatThrownBy(() -> orderService.create(배달_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴의 가격과 각 주문 항목의 가격이 다를 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @CsvSource(value = {"17000,15000","1000,100"})
    public void createWithDifferentMenuPriceAndOrderLineItemPrice(int menuPrice, int orderLineItemPrice) {
        // given
        Menu 후라이드_한마리_메뉴 = 후라이드_한마리_메뉴(menuPrice, true);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(후라이드_한마리_메뉴));
        given(menuRepository.findById(any())).willReturn(Optional.of(후라이드_한마리_메뉴));

        Order 주문 = 배달_주문(Arrays.asList(주문_상품(주문_수량, orderLineItemPrice)));

        // when, then
        assertThatThrownBy(() -> orderService.create(주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("배달 주문인 경우, 배달 주소가 null이거나 빈 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @NullAndEmptySource
    public void createWithDeliveryAddressIfItIsDelivery(String deliveryAddress) {
        // given
        OrderType delivery = DELIVERY;

        Menu menu = 후라이드_한마리_메뉴(가격, true);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(menu));
        given(menuRepository.findById(any())).willReturn(Optional.of(menu));

        Order 배달_주문 = 주문(delivery, deliveryAddress, Arrays.asList(
                주문_상품(주문_수량, 가격)));

        // when, then
        assertThatThrownBy(() -> orderService.create(배달_주문))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("매장 식사의 경우, 주문 테이블이 빈 경우 IllegalArgumentException을 던진다.")
    @ParameterizedTest
    @ValueSource(booleans = true)
    public void createWithEmptyTableIfEatIn(boolean isEmptyTable) {
        // given
        OrderType eatIn = EAT_IN;

        Menu 메뉴 = 후라이드_한마리_메뉴(가격, true);
        given(menuRepository.findAllById(anyList())).willReturn(Arrays.asList(메뉴));
        given(menuRepository.findById(any())).willReturn(Optional.of(메뉴));

        OrderTable 빈_주문_테이블 = 주문_1번_테이블(isEmptyTable);
        given(orderTableRepository.findById(any())).willReturn(Optional.of(빈_주문_테이블));

        Order 매장_주문 = 주문(eatIn, null, Arrays.asList(
                주문_상품(주문_수량, 가격)));

        // when, then
        assertThatThrownBy(() -> orderService.create(매장_주문))
                .isInstanceOf(IllegalStateException.class);
    }
}
