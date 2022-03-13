package kitchenpos.application;

import kitchenpos.domain.OrderTable;

class OrderTableServiceTest {

    public static OrderTable createOrderTableCreateRequest(String name) {
        OrderTable orderTableCreateRequest = new OrderTable();
        orderTableCreateRequest.setName(name);
        return orderTableCreateRequest;
    }
}
