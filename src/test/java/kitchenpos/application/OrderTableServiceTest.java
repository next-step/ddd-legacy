package kitchenpos.application;

import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static kitchenpos.fixture.OrderTableFixture.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderTableServiceTest {

    @MockBean
    @Autowired
    OrderTableRepository orderTableRepository;

    @Autowired
    OrderTableService orderTableService;

    @DisplayName("가게 테이블을 생성할 수 있습니다.")
    @Test
    void createOrderTable() {
        when(orderTableRepository.save(any(OrderTable.class))).then(returnsFirstArg());

        final OrderTable orderTable = orderTableService.create(orderTable(
                DEFAULT_ORDER_TABLE_NAME, DEFAULT_NUMBER_OF_GUESTS, DEFAULT_OCCUPIED
        ));

        assertAll(
                () -> assertNotNull(orderTable.getId()),
                () -> assertThat(orderTable.getName()).isEqualTo(DEFAULT_ORDER_TABLE_NAME),
                () -> assertThat(orderTable.getNumberOfGuests()).isEqualTo(DEFAULT_NUMBER_OF_GUESTS),
                () -> assertThat(orderTable.isOccupied()).isEqualTo(DEFAULT_OCCUPIED)
        );
    }
}
