package kitchenpos.ordertable.application;

import kitchenpos.common.vo.Name;
import kitchenpos.order.domain.OrderRepository;
import kitchenpos.ordertable.domain.OrderTable;
import kitchenpos.ordertable.domain.OrderTableRepository;
import kitchenpos.ordertable.dto.OrderTableRequest;
import kitchenpos.ordertable.dto.request.ChangeNumberOfGuestRequest;
import kitchenpos.ordertable.vo.NumberOfGuests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("주문 테이블")
@SpringBootTest
@Transactional
class OrderTableServiceTest {

    @Autowired
    private OrderTableRepository orderTableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderTableService orderTableService;

    @BeforeEach
    void setUp() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }

    @DisplayName("주문 테이블 목록을 조회할 수 있다.")
    @Test
    void findAll() {
        orderTableRepository.save(orderTable("주문테이블명", 1));
        assertThat(orderTableService.findAll()).hasSize(1);
    }

    @DisplayName("주문 테이블의 착석여부를 착석으로 변경할 수 있다.")
    @Test
    void fisdfndAll() {
        OrderTable orderTable = orderTableRepository.save(orderTable("주문테이블명", 1));
        orderTableService.sit(orderTable.getId());
        assertThat(orderTable.isOccupied()).isTrue();
    }

    @DisplayName("주문 테이블의 착석여부를 공석으로 변경할 수 있다.")
    @Test
    void fisdfnasdfdAll() {
        OrderTable orderTable = orderTableRepository.save(orderTable("주문테이블명", 1));
        assertThat(orderTable.isOccupied()).isFalse();
        orderTableService.sit(orderTable.getId());
        assertThat(orderTable.isOccupied()).isTrue();
    }

    @DisplayName("주문 테이블 생성 시 주문 테이블명은 필수이다.")
    @ParameterizedTest
    @NullAndEmptySource
    void createOrderTable(String name) {
        OrderTableRequest orderTableRequest = new OrderTableRequest(name);
        assertThatThrownBy(() -> orderTableService.create(orderTableRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null 이나 공백일 수 없습니다.");
    }

    @DisplayName("주문 테이블을 생성할 수 있다.")
    @Test
    void create() {
        OrderTableRequest orderTableRequest = new OrderTableRequest("주문테이블");
        assertThatNoException().isThrownBy(() -> orderTableService.create(orderTableRequest));
    }

    @DisplayName("주문 테이블의 착석 인원을 변경 할 수 있다.")
    @Test
    void changeNumberOfGuests() {
        OrderTable orderTable = orderTableRepository.save(orderTable("주문테이블명", 1));
        orderTableService.sit(orderTable.getId());
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(1);
        ChangeNumberOfGuestRequest request = new ChangeNumberOfGuestRequest(10);
        orderTableService.changeNumberOfGuests(orderTable.getId(), request);
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(10);
    }

    @DisplayName("주문 테이블의 착석 인원 변경 시 0명보다 작을 수 없다.")
    @Test
    void asdge() {
        OrderTable orderTable = orderTableRepository.save(orderTable("주문테이블명", 1));
        orderTableService.sit(orderTable.getId());
        assertThat(orderTable.getNumberOfGuests()).isEqualTo(1);
        ChangeNumberOfGuestRequest request = new ChangeNumberOfGuestRequest(-1);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문 테이블의 착석 인원 변경 시 0명보다 작을 수 없다.");
    }

    @DisplayName("주문 테이블이 공석일 경우 착석 인원을 변경 할 수 없다.")
    @Test
    void asdgesds() {
        OrderTable orderTable = orderTableRepository.save(orderTable("주문테이블명", 1));
        assertThat(orderTable.isOccupied()).isFalse();
        ChangeNumberOfGuestRequest request = new ChangeNumberOfGuestRequest(10);
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("주문 테이블이 공석일 경우 착석 인원을 변경 할 수 없다.");
    }

    private static OrderTable orderTable(String name, int numberOfGuests) {
        return new OrderTable(UUID.randomUUID(), new Name(name, false), new NumberOfGuests(numberOfGuests));
    }

}
