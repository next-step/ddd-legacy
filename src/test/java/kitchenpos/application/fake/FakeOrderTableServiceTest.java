package kitchenpos.application.fake;

import kitchenpos.application.OrderTableService;
import kitchenpos.application.fake.helper.InMemoryOrderRepository;
import kitchenpos.application.fake.helper.InMemoryOrderTableRepository;
import kitchenpos.application.fake.helper.OrderFixtureFactory;
import kitchenpos.application.fake.helper.OrderTableFixtureFactory;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@ExtendWith(MockitoExtension.class)
class FakeOrderTableServiceTest {

    private final OrderTableRepository orderTableRepository = new InMemoryOrderTableRepository();
    private final OrderRepository orderRepository = new InMemoryOrderRepository();

    private OrderTableService orderTableService;

    @BeforeEach
    void setup() {
        orderTableService = new OrderTableService(orderTableRepository, orderRepository);
    }


    private static Stream<String> provideTableNameForNullAndEmptyString() {
        return Stream.of(
                null,
                ""
        );
    }

    @DisplayName("테이블 등록 - 테이블은 반드시 이름을 가져야 한다.")
    @MethodSource("provideTableNameForNullAndEmptyString")
    @ParameterizedTest
    void create01(String 등록할_테이블_이름) {
        //given
        OrderTable 테이블_등록_요청 = new OrderTableFixtureFactory.Builder()
                .name(등록할_테이블_이름)
                .build();
        //when & then
        assertThatThrownBy(() -> orderTableService.create(테이블_등록_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 등록 - 테이블을 등록 할 수 있다.")
    @Test
    void create02() {

        //given & when
        OrderTable saved = orderTableService.create(OrderTableFixtureFactory.오션뷰_테이블_01);

        //then
        assertAll(
                () -> assertThat(saved.getId()).isNotNull(),
                () -> assertThat(saved.getName()).isEqualTo(OrderTableFixtureFactory.오션뷰_테이블_01.getName()),
                () -> assertThat(saved.getNumberOfGuests()).isEqualTo(0),
                () -> assertThat(saved.isEmpty()).isTrue()
        );
    }

    //@TODO 더블부킹이 가능하다 -> 이미 착성한 테이블에 착성 할 수 없도록 해야하지 않을까?
    @DisplayName("테이블 착석 - 테이블에 착성 할 수 있다.")
    @Test
    void sit01() {
        //given
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_01);
        //when
        OrderTable updated = orderTableService.sit(OrderTableFixtureFactory.오션뷰_테이블_01.getId());
        //then
        assertThat(updated.isEmpty()).isFalse();
    }

    @DisplayName("테이블 착석 - 착석하려는 테이블은 반드시 존재해야 한다.")
    @Test
    void sit02() {
        //when & then
        assertThatThrownBy(() -> orderTableService.sit(UUID.randomUUID()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블 정리 - 식사가 완료되지 않은 테이블은 정리 할 수 없다.")
    @Test
    void clear01() {
        //given
        orderRepository.save(OrderFixtureFactory.테이블에서_식사중인_주문);
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_02_이용중);

        //when & then
        assertThatThrownBy(() -> orderTableService.clear(OrderTableFixtureFactory.오션뷰_테이블_02_이용중.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블 정리 - 테이블을 정리(clear) 할 수 있다.")
    @Test
    void clear02() {
        //given
        orderRepository.save(OrderFixtureFactory.테이블에서_식사가_완료된_주문);
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_02_이용중);

        //when
        OrderTable updated = orderTableService.clear(OrderTableFixtureFactory.오션뷰_테이블_02_이용중.getId());
        //then
        assertAll(
                () -> assertThat(updated.isEmpty()).isTrue(),
                () -> assertThat(updated.getNumberOfGuests()).isEqualTo(0)
        );
    }

    @DisplayName("테이블 정리 - 정리하려는 테이블은 반드시 존재해야 한다.")
    @Test
    void clear03() {
        //given
        UUID 정리할_테이블_아이디 = UUID.randomUUID();
        //when & then
        assertThatThrownBy(() -> orderTableService.clear(정리할_테이블_아이디))
                .isInstanceOf(NoSuchElementException.class);
    }

    //@TODO 0명이 착석할 수 있음. 의도한 동작인지 확인후 개선
    @DisplayName("테이블 인원 변경 - 테이블에 손님은 반드시 0명 이상이여야 한다.")
    @Test
    void changeNumberOfGuests01() {
        //given
        OrderTable 테이블_인원_변경_요청 = new OrderTableFixtureFactory.Builder()
                .numberOfGuests(-1)
                .build();
        //when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(OrderTableFixtureFactory.오션뷰_테이블_02_이용중.getId(), 테이블_인원_변경_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("테이블 인원 변경 - 비어 있지 않은 테이블에만 손님을 지정할 수 있다.")
    @Test
    void changeNumberOfGuests02() {
        //given
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_01);
        OrderTable 테이블_인원_변경_요청 = new OrderTableFixtureFactory.Builder()
                .numberOfGuests(2)
                .build();
        //when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(OrderTableFixtureFactory.오션뷰_테이블_01.getId(), 테이블_인원_변경_요청))
                .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("테이블 인원 변경 - 테이블에 손님 수를 변경 할 수 있다.")
    @Test
    void changeNumberOfGuests03() {
        //given
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_02_이용중);
        OrderTable 테이블_인원_변경_요청 = new OrderTableFixtureFactory.Builder()
                .numberOfGuests(2)
                .build();
        //when
        OrderTable updated = orderTableService.changeNumberOfGuests(OrderTableFixtureFactory.오션뷰_테이블_02_이용중.getId(), 테이블_인원_변경_요청);
        //then
        assertThat(updated.getNumberOfGuests()).isEqualTo(2);
    }

    @DisplayName("테이블 인원 변경 - 인원변경할 테이블은 반드시 존재해야 한다.")
    @Test
    void changeNumberOfGuests04() {
        //given
        OrderTable 테이블_인원_변경_요청 = new OrderTableFixtureFactory.Builder()
                .numberOfGuests(2)
                .build();
        //when & then
        assertThatThrownBy(() -> orderTableService.changeNumberOfGuests(OrderTableFixtureFactory.오션뷰_테이블_01.getId(), 테이블_인원_변경_요청))
                .isInstanceOf(NoSuchElementException.class);
    }

    @DisplayName("테이블 조회 - 등록된 모든 메뉴 테이블을 조회할 수 있다.")
    @Test
    void findAll() {
        // given
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_01);
        orderTableRepository.save(OrderTableFixtureFactory.오션뷰_테이블_02_이용중);
        //when
        List<OrderTable> tables = orderTableService.findAll();
        //then
        assertAll(
                () -> assertThat(tables).hasSize(2),
                () -> assertThat(tables).contains(OrderTableFixtureFactory.오션뷰_테이블_01, OrderTableFixtureFactory.오션뷰_테이블_02_이용중)
        );
    }

}
