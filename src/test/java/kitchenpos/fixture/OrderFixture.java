package kitchenpos.fixture;

import kitchenpos.domain.*;
import org.junit.jupiter.params.provider.Arguments;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

public class OrderFixture {

    public static final UUID ORDER_ID = UUID.randomUUID();

    public static Order.Builder anOrderRequest() {
        return Order.builder()
                .type(OrderType.EAT_IN)
                .status(OrderStatus.WAITING)
                .orderDateTime(LocalDateTime.now());
    }

    public static OrderLineItem.Builder anOrderLineItemRequest(Menu menu) {
        return OrderLineItem.builder()
                .menu(menu)
                .menuId(menu.getId())
                .quantity(1L)
                .price(menu.getPrice());
    }

    // 파라미터라이즈드 테스트용: 완료 상태를 제외한 주문 상태 Stream
    public static Stream<Arguments> orderStatusNotCompleted() {
        return Stream.of(OrderStatus.values())
                .filter(status -> status != OrderStatus.COMPLETED)
                .map(Arguments::of);
    }

    // 파라미터라이즈드 테스트용: 대기 상태를 제외한 주문 상태 Stream
    public static Stream<Arguments> orderStatusNotWaiting() {
        return Stream.of(OrderStatus.values())
                .filter(status -> status != OrderStatus.WAITING)
                .map(Arguments::of);
    }

    // 파라미터라이즈드 테스트용: 매장 주문(EAT_IN)이 아닌 주문 타입 Stream
    public static Stream<Arguments> orderTypeNotEatIn() {
        return Stream.of(OrderType.values())
                .filter(type -> type != OrderType.EAT_IN)
                .map(Arguments::of);
    }

    public static Stream<Arguments> orderStatusNotAccepted() {
        return Stream.of(OrderStatus.values())
                .filter(status -> status != OrderStatus.ACCEPTED)
                .map(Arguments::of);
    }

    public static Stream<Arguments> orderTypeNotDelivery() {
        return Stream.of(OrderType.values())
                .filter(type -> type != OrderType.DELIVERY)
                .map(Arguments::of);
    }

    public static Stream<Arguments> orderStatusNotDelivering() {
        return Stream.of(OrderStatus.values())
                .filter(status -> status != OrderStatus.DELIVERING)
                .map(Arguments::of);
    }

    public static Stream<Arguments> orderStatusNotDelivered() {
        return Stream.of(OrderStatus.values())
                .filter(status -> status != OrderStatus.DELIVERED)
                .map(Arguments::of);
    }

    public static Stream<Arguments> orderStatusNotServed() {
        return Stream.of(OrderStatus.values())
                .filter(status -> status != OrderStatus.SERVED)
                .map(Arguments::of);
    }




}