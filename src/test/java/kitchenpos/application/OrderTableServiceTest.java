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

import java.util.NoSuchElementException;
import java.util.UUID;

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

    @Test
    @DisplayName("테이블이 존재하지 않는데 테이블 점유 시도할 시 NoSuchElementException이 발생한다.")
    void sit_fail_for_not_existing_table() {
        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
            .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    @DisplayName("테이블을 점유한다.")
    void sit_success() {
        OrderTable request = OrderTableRequestBuilder.builder().build();
        OrderTable orderTable = orderTableService.create(request);

        OrderTable response = orderTableService.sit(orderTable.getId());
        assertThat(response.isOccupied()).isTrue();
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