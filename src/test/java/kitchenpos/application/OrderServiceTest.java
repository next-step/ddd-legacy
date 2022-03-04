package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.infra.KitchenridersClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.verification.VerificationModeFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kitchenpos.stub.OrderStub.*;
import static kitchenpos.stub.OrderTableStub.generateEmptyOrderTable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private OrderTableRepository orderTableRepository;

    @Mock
    private KitchenridersClient kitchenridersClient;

    @InjectMocks
    private OrderService orderService;

    @DisplayName("새 주문을 등록할 수 있다.")
    @Test
    void createOrder() {
        //given
        Order newOrder = generateTenThousandPriceDeliverTypeOrderRequest();
        List<Menu> relatedMenus = getMenusFromRelatedOrder(newOrder);
        when(menuRepository.findAllByIdIn(any())).thenReturn(relatedMenus);
        when(menuRepository.findById(eq(findSpecificIndexOrderLineItemMenuId(newOrder, 0)))).thenReturn(Optional.of(relatedMenus.get(0)));
        when(orderRepository.save(any())).thenReturn(newOrder);

        //when
        Order result = orderService.create(newOrder);

        //then
        assertThat(result).isEqualTo(newOrder);
    }

    @DisplayName("주문은 배달, 포장, 매장식사 중 한 가지 타입을 반드시 가져야 한다.")
    @Test
    void mustHaveOrderType() {
        //given
        Order emptyOrderTypeRequest = generateEmptyOrderTypeOrderRequest();

        //when & then
        assertThatThrownBy(() -> orderService.create(emptyOrderTypeRequest)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문은 1개 이상의 메뉴를 포함하고 있어야 한다.")
    @Test
    void mustHaveOneOrMoreMenu() {
        //given
        Order emptyOrderLineItemRequest = generateEmptyOrderLineItemDeliveryOrderRequest();

        //when & then
        assertThatThrownBy(() -> orderService.create(emptyOrderLineItemRequest)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문에 포함되는 메뉴는 미리 등록된 상품이어야 한다.")
    @Test
    void mustHaveAlreadyCreatedMenu() {
        //given
        Order newOrder = generateTenThousandPriceDeliverTypeOrderRequest();
        when(menuRepository.findAllByIdIn(any())).thenReturn(Collections.emptyList());

        //when & then
        assertThatThrownBy(() -> orderService.create(newOrder)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 타입이 매장식사가 아닌 경우 주문에 포함되는 메뉴의 수량은 0 이상이어야 한다.")
    @Test
    void mustBeQuantityPositiveNumberIfNotEatInType() {
        //given
        Order negativeQuantityOrderLineItemDeliveryTypeOrderRequest = generateNegativeQuantityOrderLineItemDeliveryTypeOrderRequest();
        List<Menu> relatedMenus = getMenusFromRelatedOrder(negativeQuantityOrderLineItemDeliveryTypeOrderRequest);
        when(menuRepository.findAllByIdIn(any())).thenReturn(relatedMenus);

        //when & then
        assertThatThrownBy(() -> orderService.create(negativeQuantityOrderLineItemDeliveryTypeOrderRequest)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("미노출로 설정된 메뉴는 주문에 포함될 수 없다.")
    @Test
    void mustContainOnlyVisibleMenu() {
        //given
        Order containingInvisibleMenuDeliveryTypeOrderRequest = generateContainingInvisibleMenuDeliveryTypeOrderRequest();
        List<Menu> relatedMenus = getMenusFromRelatedOrder(containingInvisibleMenuDeliveryTypeOrderRequest);
        when(menuRepository.findAllByIdIn(any())).thenReturn(relatedMenus);
        when(menuRepository.findById(eq(findSpecificIndexOrderLineItemMenuId(containingInvisibleMenuDeliveryTypeOrderRequest, 0))))
                .thenReturn(Optional.of(relatedMenus.get(0)));

        //when & then
        assertThatThrownBy(() -> orderService.create(containingInvisibleMenuDeliveryTypeOrderRequest)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("등록되어있는 메뉴의 가격과 주문된 메뉴의 가격은 같아야 한다.")
    @Test
    void mustBeSameOrderLineItemPriceAndMenuPrice() {
        //given
        Order differentPriceBetweenOrderLineItemAndMenuOrderRequest = generateDifferentPriceBetweenOrderLineItemAndMenuOrderRequest();
        List<Menu> relatedMenus = getMenusFromRelatedOrder(differentPriceBetweenOrderLineItemAndMenuOrderRequest);
        when(menuRepository.findAllByIdIn(any())).thenReturn(relatedMenus);
        when(menuRepository.findById(eq(findSpecificIndexOrderLineItemMenuId(differentPriceBetweenOrderLineItemAndMenuOrderRequest, 0))))
                .thenReturn(Optional.of(relatedMenus.get(0)));

        //when & then
        assertThatThrownBy(() -> orderService.create(differentPriceBetweenOrderLineItemAndMenuOrderRequest)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 타입이 배달인 경우 배달주소가 함께 등록되며, 배달주소는 빈 값일 수 없다.")
    @Test
    void mustHaveDeliveryAddressIfDeliveryTypeOrder() {
        //given
        Order emptyDeliverAddressRequest = generateEmptyDeliveryAddressDeliveryTypeOrderRequest();
        List<Menu> relatedMenus = getMenusFromRelatedOrder(emptyDeliverAddressRequest);
        when(menuRepository.findAllByIdIn(any())).thenReturn(relatedMenus);
        when(menuRepository.findById(eq(findSpecificIndexOrderLineItemMenuId(emptyDeliverAddressRequest, 0)))).thenReturn(Optional.of(relatedMenus.get(0)));

        //when & then
        assertThatThrownBy(() -> orderService.create(emptyDeliverAddressRequest)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("주문 타입이 매장식사인 경우 이미 등록된 주문테이블과 함께 주문 등록이 되어야한다.")
    @Test
    void mustHaveCreatedOrderTableIfEatInTypeOrder() {
        //given
        Order eatInTypeOrderRequest = generateTenThousandPriceEatInTypeOrderRequest();
        List<Menu> relatedMenus = getMenusFromRelatedOrder(eatInTypeOrderRequest);
        when(menuRepository.findAllByIdIn(any())).thenReturn(relatedMenus);
        when(menuRepository.findById(eq(findSpecificIndexOrderLineItemMenuId(eatInTypeOrderRequest, 0)))).thenReturn(Optional.of(relatedMenus.get(0)));
        when(orderTableRepository.findById(any())).thenReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> orderService.create(eatInTypeOrderRequest)).isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("주문 타입이 매장식사인 경우 손님이 사용중이지 않은 테이블은 등록될 수 없다.")
    @Test
    void mustHaveNotEmptyOrderTableIfEatInTypeOrder() {
        //given
        Order eatInTypeOrderRequest = generateTenThousandPriceEatInTypeOrderRequest();
        List<Menu> relatedMenus = getMenusFromRelatedOrder(eatInTypeOrderRequest);
        when(menuRepository.findAllByIdIn(any())).thenReturn(relatedMenus);
        when(menuRepository.findById(eq(findSpecificIndexOrderLineItemMenuId(eatInTypeOrderRequest, 0)))).thenReturn(Optional.of(relatedMenus.get(0)));
        when(orderTableRepository.findById(any())).thenReturn(Optional.of(generateEmptyOrderTable()));

        //when & then
        assertThatThrownBy(() -> orderService.create(eatInTypeOrderRequest)).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("등록된 주문을 접수됨 상태로 바꿀 수 있다.")
    @Test
    void canChangeOrderStatusToAccepted() {
        //given
        Order waitingOrder = generateTenThousandPriceDeliverTypeWaitingOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(waitingOrder));
        doNothing().when(kitchenridersClient).requestDelivery(any(), any(), any());

        //when
        Order result = orderService.accept(waitingOrder.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
    }

    @DisplayName("상태가 대기중 상태인 주문만 접수됨 상태로 바꿀 수 있다.")
    @Test
    void mustBeWaitingStatusToChangeAccepted() {
        //given
        Order servedOrder = generateTenThousandPriceDeliverTypeServedOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(servedOrder));

        //when & then
        assertThatThrownBy(() -> orderService.accept(servedOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("접수됨 상태로 바꿀 때 주문 타입이 배달인 경우 주문ID, 주문합계금액, 배달주소로 배달 요청을 한다.")
    @Test
    void sendDeliveryRequestWhenAcceptDeliveryTypeOrder() {
        //given
        Order waitingOrder = generateTenThousandPriceDeliverTypeWaitingOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(waitingOrder));
        doNothing().when(kitchenridersClient).requestDelivery(waitingOrder.getId(), getSumOfOrderLineItems(waitingOrder), waitingOrder.getDeliveryAddress());

        //when
        orderService.accept(waitingOrder.getId());

        //then
        verify(kitchenridersClient, VerificationModeFactory.times(1))
                .requestDelivery(waitingOrder.getId(), getSumOfOrderLineItems(waitingOrder), waitingOrder.getDeliveryAddress());
    }

    @DisplayName("등록된 주문을 제공됨 상태로 바꿀 수 있다.")
    @Test
    void canChangeOrderStatusToServed() {
        //given
        Order acceptedOrder = generateTenThousandPriceDeliverTypeAcceptedOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(acceptedOrder));

        //when
        Order result = orderService.serve(acceptedOrder.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.SERVED);
    }

    @DisplayName("상태가 접수됨 상태인 주문만 제공됨 상태로 바꿀 수 있다.")
    @Test
    void mustBeAcceptedStatusToChangeServed() {
        //given
        Order servedOrder = generateTenThousandPriceDeliverTypeServedOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(servedOrder));

        //when & then
        assertThatThrownBy(() -> orderService.serve(servedOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("등록된 주문을 배달중 상태로 바꿀 수 있다.")
    @Test
    void canChangeOrderStatusToDelivering() {
        //given
        Order servedOrder = generateTenThousandPriceDeliverTypeServedOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(servedOrder));

        //when
        Order result = orderService.startDelivery(servedOrder.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERING);
    }

    @DisplayName("주문 타입이 배달인 주문만 배달중 상태로 바꿀 수 있다.")
    @Test
    void mustBeDeliveryTypeToChangeDelivering() {
        Order takeOutOrder = generateTenThousandPriceTakeOutTypeServedOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(takeOutOrder));

        //when & then
        assertThatThrownBy(() -> orderService.startDelivery(takeOutOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("상태가 제공됨 상태인 주문만 배달중 상태로 바꿀 수 있다.")
    @Test
    void mustBeServedStatusToChangeDelivering() {
        Order acceptedOrder = generateTenThousandPriceDeliverTypeAcceptedOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(acceptedOrder));

        //when & then
        assertThatThrownBy(() -> orderService.startDelivery(acceptedOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("등록된 주문을 배달완료 상태로 바꿀 수 있다.")
    @Test
    void canChangeOrderStatusToDelivered() {
        //given
        Order deliveringOrder = generateTenThousandPriceDeliverTypeDeliveringOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(deliveringOrder));

        //when
        Order result = orderService.completeDelivery(deliveringOrder.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }

    @DisplayName("상태가 배달중 상태인 주문만 배달완료 상태로 바꿀 수 있다.")
    @Test
    void mustBeDeliveringStatusToChangeDelivered() {
        Order servedOrder = generateTenThousandPriceDeliverTypeServedOrder();
        when(orderRepository.findById(any())).thenReturn(Optional.of(servedOrder));

        //when & then
        assertThatThrownBy(() -> orderService.completeDelivery(servedOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("등록된 주문을 주문완료 상태로 바꿀 수 있다.")
    @Test
    void canChangeOrderStatusToCompleted() {
        //given
        Order deliveredOrder = generateTenThousandPriceDeliverTypeDeliveredOrder();
        when(orderRepository.findById(deliveredOrder.getId())).thenReturn(Optional.of(deliveredOrder));

        //when
        Order result = orderService.complete(deliveredOrder.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 타입이 배달인 주문은 상태가 배달완료 상태인 주문만 주문완료 상태로 바꿀 수 있다.")
    @Test
    void mustBeDeliveredStatusToChangeCompletedWhenDeliveryTypeOrder() {
        //given
        Order deliveringOrder = generateTenThousandPriceDeliverTypeDeliveringOrder();
        when(orderRepository.findById(deliveringOrder.getId())).thenReturn(Optional.of(deliveringOrder));

        //when & then
        assertThatThrownBy(() -> orderService.complete(deliveringOrder.getId())).isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("주문 타입이 포장이거나 매장식사인 주문은 상태가 제공됨 상태인 주문만 주문완료 상태로 바꿀 수 있다.")
    @MethodSource("providePickUpOrEatInTypeOrderServedStatus")
    @ParameterizedTest
    void mustBeServedStatusToChangeCompleteWhenTakeOutOrEatInOrder(Order servedOrder) {
        //given
        when(orderRepository.findById(servedOrder.getId())).thenReturn(Optional.of(servedOrder));

        //when
        Order result = orderService.complete(servedOrder.getId());

        //then
        assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    }

    @DisplayName("주문 타입이 매장식사인 경우 주문과 연관된 주문테이블의 모든 주문이 완료되었다면 주문테이블에 빈 테이블이라는 표시를 한다.")
    @Test
    void changeToEmptyOrderTableIfAllOfOrderTableBoundedOrderCompletedWhenCompletingEatInTypeOrder() {
        //given
        Order eatInOrder = generateTenThousandPriceEatInTypeServedOrder();
        when(orderRepository.findById(eatInOrder.getId())).thenReturn(Optional.of(eatInOrder));

        //when
        Order complete = orderService.complete(eatInOrder.getId());

        //then
        assertThat(isOrderBoundedOrderTableEmpty(complete)).isTrue();
    }

    @DisplayName("전체 주문을 조회할 수 있다.")
    @Test
    void findAllOrders() {
        //given
        List<Order> orders = new ArrayList<>();
        orders.add(generateTenThousandPriceDeliverTypeWaitingOrder());
        orders.add(generateTenThousandPriceEatInTypeServedOrder());
        orders.add(generateTenThousandPriceDeliverTypeDeliveringOrder());
        when(orderRepository.findAll()).thenReturn(orders);

        //when
        List<Order> results = orderService.findAll();

        //then
        assertThat(results).containsExactlyElementsOf(orders);
    }

    private List<Menu> getMenusFromRelatedOrder(Order order) {
        return order.getOrderLineItems()
                .stream()
                .map(OrderLineItem::getMenu)
                .collect(Collectors.toList());
    }

    private UUID findSpecificIndexOrderLineItemMenuId(Order order, int index) {
        return order.getOrderLineItems().get(index).getMenuId();
    }

    private BigDecimal getSumOfOrderLineItems(Order order) {
        return order.getOrderLineItems()
                .stream()
                .map(orderLineItem -> orderLineItem.getPrice().multiply(BigDecimal.valueOf(orderLineItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static Stream<Arguments> providePickUpOrEatInTypeOrderServedStatus() {
        return Stream.of(
                Arguments.of(generateTenThousandPriceTakeOutTypeServedOrder()),
                Arguments.of(generateTenThousandPriceEatInTypeServedOrder())
        );
    }

    private boolean isOrderBoundedOrderTableEmpty(Order order) {
        return order.getOrderTable()
                .isEmpty();
    }



}
