//package kitchenpos.application;
//
//import kitchenpos.domain.OrderRepository;
//import kitchenpos.domain.OrderTable;
//import kitchenpos.domain.OrderTableRepository;
//import org.assertj.core.api.SoftAssertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.NullSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.NoSuchElementException;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//
//@ExtendWith(MockitoExtension.class)
//@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
//class OrderTableServiceTest {
//
//    private final String normalTableName = "9번";
//
////    @Mock
////    private OrderRepository orderRepository;
////
////    @Autowired
////    private OrderTableRepository orderTableRepository;
//
////    @Autowired
////    private OrderTableService orderTableService;
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private OrderTableRepository orderTableRepository;
//
//    @InjectMocks
//    private OrderTableService orderTableService;
//
////    private OrderTableService orderTableService = new OrderTableService(
////            orderTableRepository,
////            orderRepository
////    );
//
//
//    private SoftAssertions softAssertions;
//
//    @DisplayName("테이블 생성")
//    @Test
//    void create() {
//        softAssertions = new SoftAssertions();
//        OrderTable orderTable = orderTableService.create(createRequest(normalTableName));
//        softAssertions.assertThat(orderTable.getId()).isNotNull();
//        softAssertions.assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
//        softAssertions.assertThat(orderTable.isEmpty()).isTrue();
//        softAssertions.assertAll();
//    }
//
//    @DisplayName("테이블 생성시 name validation")
//    @NullSource
//    @ParameterizedTest
//    void createValidationName(String name) {
//        assertThatThrownBy(() -> orderTableService.create(createRequest(name)))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @DisplayName("테이블 착석")
//    @Test
//    void sit() {
//        OrderTable orderTable = orderTableService.create(createRequest(normalTableName));
//        OrderTable notEmptyOrderTable = orderTableService.sit(orderTable.getId());
//        assertThat(notEmptyOrderTable.isEmpty()).isFalse();
//    }
//
//    @DisplayName("테이블 착석 처리시 미존재 테이블")
//    @Test
//    void sitValidationNotExistsTable() {
//        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @DisplayName("테이블 정리")
//    @Test
//    void clear() {
//        softAssertions = new SoftAssertions();
//        OrderTable orderTable = orderTableService.create(createRequest(normalTableName));
//        softAssertions.assertThat(orderTable.getNumberOfGuests()).isEqualTo(0);
//        softAssertions.assertThat(orderTable.isEmpty()).isTrue();
//        softAssertions.assertAll();
//    }
//
//    @DisplayName("테이블 정리시 미존재 테이블 validation")
//    @Test
//    void clearValidationNotExistsTable() {
//        assertThatThrownBy(() -> orderTableService.clear(UUID.randomUUID()))
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
////    @DisplayName("테이블 정리시 테이블 상태 validation")
////    @Test
////    void clearValidationTableStatus() {
////        OrderTable orderTable = orderTableService.create(createRequest(normalTableName));
////        given(orderRepository.existsByOrderTableAndStatusNot(orderTable, OrderStatus.COMPLETED)).willReturn(true);
////
////        assertThatThrownBy(() -> orderTableService.clear(orderTable.getId()))
////                .isInstanceOf(IllegalStateException.class);
////    }
//
//    @DisplayName("테이블 인원 수 변경")
//    @Test
//    void changeNumberOfGuests() {
//        int numberOfGuest = 5;
//        OrderTable orderTable = changeNumberOfGuestsRequest(numberOfGuest);
//        orderTable.setNumberOfGuests(numberOfGuest);
//        OrderTable changedOrderTable = orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable);
//
//        assertThat(changedOrderTable.getNumberOfGuests()).isEqualTo(numberOfGuest);
//    }
//
//    @DisplayName("테이블 인원 수 변경시 변경 인원 음수값")
//    @Test
//    void changeNumberOfGuestsValidationIllegalNumber() {
//        int numberOfGuest = -5;
//        OrderTable orderTable = changeNumberOfGuestsRequest(numberOfGuest);
//
//        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//
//    @DisplayName("테이블 인원 수 변경시 미존재 테이블")
//    @Test
//    void changeNumberOfGuestsValidationNotExistsTable() {
//        int numberOfGuest = 5;
//        OrderTable orderTable = changeNumberOfGuestsRequest(numberOfGuest);
//
//        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(UUID.randomUUID(), orderTable))
//                .isInstanceOf(NoSuchElementException.class);
//    }
//
//    @DisplayName("테이블 인원 수 변경시 비어있는 테이블 인원 변경")
//    @Test
//    void changeNumberOfGuestsValidationEmptyTable() {
//        OrderTable orderTable = orderTableService.create(createRequest(normalTableName));
//        orderTable.setNumberOfGuests(5);
//
//        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(orderTable.getId(), orderTable))
//                .isInstanceOf(IllegalStateException.class);
//    }
//
//    @DisplayName("모든 테이블 조회")
//    @Test
//    void findAll() {
//        assertThat(orderTableRepository.findAll().size())
//                .isEqualTo(orderTableService.findAll().size());
//    }
//
//    private OrderTable changeNumberOfGuestsRequest(int number) {
//        OrderTable orderTable = orderTableService.create(createRequest(normalTableName));
//        orderTableService.sit(orderTable.getId());
//        orderTable.setNumberOfGuests(number);
//        return orderTable;
//    }
//
//    private OrderTable createRequest(String name) {
//        OrderTable orderTable = new OrderTable();
//        orderTable.setName(name);
//
//        return orderTable;
//    }
//}
