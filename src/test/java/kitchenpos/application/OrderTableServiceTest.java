package kitchenpos.application;

import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.InMemoryOrderRepository;
import kitchenpos.infra.InMemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTableServiceTest {

    private OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private OrderRepository orderRepository = new InMemoryOrderRepository();

    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("테이블 이름이 null이거나 빈 문자열일 경우 IllegalArgumentException이 발생한다.")
    void create_fail_for_null_or_empty_name(String name) {
        OrderTable request = OrderTableRequestBuilder.builder()
            .withName(name)
            .build();

        assertThatThrownBy(() -> orderTableService.create(request))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("테이블을 생성한다.")
    public void create_success() {
        OrderTable request = OrderTableRequestBuilder.builder().build();
        OrderTable response = orderTableService.create(request);
        assertThat(response.getId()).isNotNull();
    }
}

class OrderTableRequestBuilder {

    private String name = "테이블 이름";
    private int numberOfGuests = 0;
    private boolean occupied = false;

    public static OrderTableRequestBuilder builder() {
        return new OrderTableRequestBuilder();
    }

    public OrderTableRequestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public OrderTableRequestBuilder withNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
        return this;
    }

    public OrderTableRequestBuilder withOccupied(boolean occupied) {
        this.occupied = occupied;
        return this;
    }

    public OrderTable build() {
        OrderTable orderTable = new OrderTable();
        orderTable.setName(name);
        orderTable.setNumberOfGuests(numberOfGuests);
        orderTable.setOccupied(occupied);
        return orderTable;
    }
}