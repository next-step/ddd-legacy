package kitchenpos.application;

import config.UnitTest;
import kitchenpos.MenuFixture;
import kitchenpos.OrderTableFixture;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderTable;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.infra.IdGenerator;
import kitchenpos.infra.InmemoryOrderRepository;
import kitchenpos.infra.InmemoryOrderTableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@DisplayName("주문 테이블 서비스 테스트")
class OrderTableServiceTest {
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;

    private OrderTableService sut;

    @BeforeEach
    void setUp() {
        orderTableRepository = new InmemoryOrderTableRepository();
        orderRepository = new InmemoryOrderRepository();
        sut = new OrderTableService(orderTableRepository, orderRepository);
    }

    @Nested
    @DisplayName("새로운 매장 테이블 추가")
    class CreateTests {

        @Test
        @DisplayName("성공: 올바른 이름으로 테이블 생성 시 기본 상태는 미사용, 인원수는 0이다.")
        void create_success() {
            // given
            String name = "테이블 1";
            OrderTable request = OrderTableFixture.주문테이블_Request(name);

            // when
            OrderTable created = sut.create(request);

            // then
            assertAll(
                    () -> assertThat(created).isNotNull(),
                    () -> assertThat(created.getId()).isNotNull(),
                    () -> assertThat(created.getName()).isEqualTo(name),
                    () -> assertThat(created.getNumberOfGuests()).isEqualTo(0),
                    () -> assertThat(created.isOccupied()).isFalse()
            );
        }

        @Test
        @DisplayName("실패: 테이블 이름이 null이면 OrderTableNameException 발생")
        void create_fail_whenNameNull() {
            // given
            OrderTable request = OrderTableFixture.주문테이블_Request(null);

            // when & then
            assertThrows(OrderTableNameException.class, () -> sut.create(request));
        }

        @Test
        @DisplayName("실패: 테이블 이름이 빈 문자열이면 OrderTableNameException 발생")
        void create_fail_whenNameEmpty() {
            // given: 이름이 빈 문자열인 요청 객체
            OrderTable request = new OrderTable();
            request.setName("");

            // when & then: 예외 발생 검증
            assertThrows(OrderTableNameException.class, () -> sut.create(request));
        }
    }

    // ─────────────────────────────────────────────
    // 2. 매장 테이블 착석 (sit)
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("테이블 착석")
    class SitTests {

        @Test
        @DisplayName("성공: 존재하는 테이블에 대해 sit 요청 시 occupied 상태로 변경")
        void sit_success() {
            // given: 테이블 생성 후 기본 상태는 미사용임
            OrderTable request = orderTableRepository.save(OrderTableFixture.주문테이블_Request());
            assertThat(request.isOccupied()).isFalse();

            // when: 착석(sit) 요청
            OrderTable updated = sut.sit(request.getId());

            // then: occupied 상태가 true로 변경됨
            assertThat(updated.isOccupied()).isTrue();
        }


        @Test
        @DisplayName("실패: 존재하지 않는 테이블에 대해 sit 요청 시 OrderTableNotFoundException 발생")
        void sit_fail_tableNotFound() {
            // given
            UUID request = UUID.randomUUID();

            // when & then
            assertThrows(OrderTableNotFoundException.class, () -> sut.sit(request.getId()));
        }
    }

    // ─────────────────────────────────────────────
    // 3. 매장 테이블 청소 (clear)
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("매장 테이블 청소")
    class ClearTests {
/*
        @Test
        @DisplayName("성공: 모든 주문이 완료된 테이블은 청소 시 미사용 상태로, 인원 수 0으로 초기화됨")
        void clear_success() {
            // given: 테이블 생성 후 착석 및 인원 수 변경
            OrderTable request = new OrderTable();
            request.setName("테이블 1");
            OrderTable created = sut.create(request);
            sut.sit(created.getId());
            // 사용 중인 테이블로 인원 수 변경 (1명 이상의 유효한 값)
            OrderTable guestChangeRequest = new OrderTable();
            guestChangeRequest.setNumberOfGuests(3);
            OrderTable occupiedTable = sut.changeNumberOfGuests(created.getId(), guestChangeRequest);

            // 주문 저장소(InmemoryOrderRepository)는 기본적으로 false를 리턴하므로, 청소 조건(모든 주문이 완료됨)을 만족함
            // when: 청소 요청
            OrderTable cleared = sut.clear(created.getId());

            // then: 테이블 상태가 미사용, 인원 수가 0으로 초기화됨
            assertThat(cleared.isOccupied()).isFalse();
            assertThat(cleared.getNumberOfGuests()).isEqualTo(0);
        }

        @Test
        @DisplayName("실패: 완료되지 않은 주문이 존재하는 테이블은 청소할 수 없어 IllegalStateException 발생")
        void clear_fail_whenOrderNotCompleted() {
            // given: 테이블 생성 및 착석
            OrderTable request = new OrderTable();
            request.setName("테이블 2");
            OrderTable created = sut.create(request);
            sut.sit(created.getId());

            // 시뮬레이션: 주문 저장소에 COMPLETED가 아닌 주문 추가
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setOrderTable(created);
            order.setOrderStatus(OrderStatus.COOKING); // 완료 상태가 아님
            // InmemoryOrderRepository를 다운캐스팅하여 직접 저장 (실제 인메모리 저장소의 구현에 따라 달라질 수 있음)
            ((InmemoryOrderRepository) orderRepository).save(order);

            // when & then: 청소 요청 시 주문이 완료되지 않아 IllegalStateException 발생
            assertThatThrownBy(() -> sut.clear(created.getId()))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 테이블에 대해 청소 요청 시 NoSuchElementException 발생")
        void clear_fail_tableNotFound() {
            // given: 임의의 UUID 사용
            UUID randomId = UUID.randomUUID();

            // when & then: 존재하지 않는 테이블이면 예외 발생
            assertThatThrownBy(() -> sut.clear(randomId))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }

    // ─────────────────────────────────────────────
    // 4. 특정 매장 테이블 인원 수 변경
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("매장 테이블 인원 수 변경")
    class ChangeNumberOfGuestsTests {

        @Test
        @DisplayName("성공: 사용중인 테이블의 인원 수를 유효한 값(1명 이상)으로 변경")
        void changeNumberOfGuests_success() {
            // given: 테이블 생성 및 착석
            OrderTable request = new OrderTable();
            request.setName("테이블 3");
            OrderTable created = sut.create(request);
            sut.sit(created.getId());

            // when: 인원 수 변경 요청 (예: 4명)
            OrderTable changeRequest = new OrderTable();
            changeRequest.setNumberOfGuests(4);
            OrderTable updated = sut.changeNumberOfGuests(created.getId(), changeRequest);

            // then: 인원 수가 변경됨
            assertThat(updated.getNumberOfGuests()).isEqualTo(4);
        }

        @Test
        @DisplayName("실패: 변경 인원 수가 1명 미만이면 IllegalArgumentException 발생")
        void changeNumberOfGuests_fail_invalidNumber() {
            // given: 테이블 생성 및 착석
            OrderTable request = new OrderTable();
            request.setName("테이블 4");
            OrderTable created = sut.create(request);
            sut.sit(created.getId());

            // when: 인원 수를 0으로 변경 요청 (유효하지 않음)
            OrderTable changeRequest = new OrderTable();
            changeRequest.setNumberOfGuests(0);

            // then: 예외 발생
            assertThatThrownBy(() -> sut.changeNumberOfGuests(created.getId(), changeRequest))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 테이블에 대해 인원 수 변경 요청 시 NoSuchElementException 발생")
        void changeNumberOfGuests_fail_tableNotFound() {
            // given: 임의의 UUID 사용, 유효한 인원 수 요청
            OrderTable changeRequest = new OrderTable();
            changeRequest.setNumberOfGuests(3);

            // when & then: 존재하지 않는 테이블이면 예외 발생
            assertThatThrownBy(() -> sut.changeNumberOfGuests(UUID.randomUUID(), changeRequest))
                    .isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("실패: 사용중이지 않은(미착석) 테이블의 인원 수 변경 시 IllegalStateException 발생")
        void changeNumberOfGuests_fail_notOccupied() {
            // given: 테이블 생성은 하지만 착석(sit)하지 않음 → 미사용 상태
            OrderTable request = new OrderTable();
            request.setName("테이블 5");
            OrderTable created = sut.create(request);

            // when: 인원 수 변경 요청
            OrderTable changeRequest = new OrderTable();
            changeRequest.setNumberOfGuests(3);

            // then: 미사용 상태의 테이블은 인원 수 변경 불가 → 예외 발생
            assertThatThrownBy(() -> sut.changeNumberOfGuests(created.getId(), changeRequest))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // ─────────────────────────────────────────────
    // 5. 모든 매장 테이블 목록 조회
    // ─────────────────────────────────────────────
    @Nested
    @DisplayName("모든 매장 테이블 조회")
    class FindAllTests {

        @Test
        @DisplayName("성공: 등록된 모든 매장 테이블 목록을 조회")
        void findAll_success() {
            // given: 두 개 이상의 테이블 생성
            OrderTable table1 = new OrderTable();
            table1.setName("테이블 1");
            OrderTable created1 = sut.create(table1);

            OrderTable table2 = new OrderTable();
            table2.setName("테이블 2");
            OrderTable created2 = sut.create(table2);

            // when: 전체 테이블 목록 조회
            List<OrderTable> allTables = sut.findAll();

            // then: 생성된 테이블들이 모두 포함됨
            assertThat(allTables).contains(created1, created2);
        }
    }*/

}}