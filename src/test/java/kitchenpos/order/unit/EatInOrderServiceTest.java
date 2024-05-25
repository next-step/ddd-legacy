package kitchenpos.order.unit;

import kitchenpos.application.OrderService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
import kitchenpos.domain.OrderTableRepository;
import kitchenpos.domain.OrderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static kitchenpos.menu.fixture.MenuFixture.김치찜_1인_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.봉골레_파스타_세트_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.피클_메뉴_숨김;
import static kitchenpos.order.fixture.EatInOrderFixture.김치찜_1인_메뉴_1개_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.대기중인_메뉴_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.봉골레_세트_1개_토마토_단품_1개_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.봉골레_세트_메뉴_마이너스_1개_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.빈_주문항목_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.손님에게_전달한_메뉴_매장주문_식사완료;
import static kitchenpos.order.fixture.EatInOrderFixture.손님에게_전달한_메뉴_매장주문_식사진행중;
import static kitchenpos.order.fixture.EatInOrderFixture.숨김처리된_메뉴_1개_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.점유하지않고_봉골레_파스타_세트_메뉴_1개_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.주문_항목_가격_메뉴_가격_불일치_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.주문수락한_메뉴_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.주문항목미존재_매장주문;
import static kitchenpos.order.fixture.EatInOrderFixture.타입미존재_매장주문;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하고있는_테이블_1;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하고있는_테이블_2;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하고있는_테이블_3;
import static kitchenpos.ordertable.fixture.OrderTableFixture.점유하지_않고_있는_테이블_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class EatInOrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private OrderTableRepository orderTableRepository;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(
                orderRepository, menuRepository, orderTableRepository, null
        );
    }

    @Nested
    class 등록 {

        @Test
        @DisplayName("[성공] 매장 주문을 등록한다.")
        void create() {
            // given
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(김치찜_1인_메뉴));
            given(menuRepository.findById(any())).willReturn(Optional.of(김치찜_1인_메뉴));

            given(orderTableRepository.findById(any())).willReturn(Optional.of(점유하고있는_테이블_1));

            // when
            orderService.create(김치찜_1인_메뉴_1개_매장주문);

            // then
            then(orderRepository).should(times(1)).save(any());
        }

        @Nested
        class 타입검증 {

            @Test
            @DisplayName("[실패] 주문 유형을 입력하지 않으면 등록하지 못한다.")
            void 주문유형_null() {
                // when & then
                assertThatThrownBy(() -> orderService.create(타입미존재_매장주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

        @Nested
        class 주문항목목록검증 {

            @Test
            @DisplayName("[실패] 주문 항목 목록을 입력하지 않으면 등록하지 못한다.")
            void 주문항목목록_null() {
                // when & then
                assertThatThrownBy(() -> orderService.create(주문항목미존재_매장주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목 목록이 비어있으면 등록하지 못한다.")
            void 주문항목목록_empty() {
                // when & then
                assertThatThrownBy(() -> orderService.create(빈_주문항목_매장주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목 목록이 전부 메뉴에 등록되어 있어야 한다.")
            void 주문항목목록_미등록메뉴포함() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(봉골레_파스타_세트_메뉴));

                // when & then
                assertThatThrownBy(() -> orderService.create(봉골레_세트_1개_토마토_단품_1개_매장주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class 주문항목검증 {

            @Test
            @DisplayName("[성공] 수량이 마이너스인 주문 항목이 포함된 주문을 등록한다.")
            void 주문항목_수량마이너스() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(봉골레_파스타_세트_메뉴));
                given(menuRepository.findById(any())).willReturn(Optional.of(봉골레_파스타_세트_메뉴));

                given(orderTableRepository.findById(any())).willReturn(Optional.of(점유하고있는_테이블_1));

                // when
                orderService.create(봉골레_세트_메뉴_마이너스_1개_매장주문);

                // then
                then(orderRepository).should(times(1)).save(any());

            }

            @Test
            @DisplayName("[실패] 주문 항목이 미등록된 메뉴일 경우 등록하지 못한다.")
            void 주문항목_미등록메뉴() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(김치찜_1인_메뉴));
                given(menuRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> orderService.create(김치찜_1인_메뉴_1개_매장주문))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목이 숨김처리된 메뉴일 경우 등록하지 못한다.")
            void 주문항목_숨김처리메뉴() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(피클_메뉴_숨김));
                given(menuRepository.findById(any())).willReturn(Optional.of(피클_메뉴_숨김));

                // when & then
                assertThatThrownBy(() -> orderService.create(숨김처리된_메뉴_1개_매장주문))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목의 가격과 메뉴의 가격이 동일하지 않으면 등록하지 못한다.")
            void 주문항목가격_메뉴가격_불일치() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(김치찜_1인_메뉴));
                given(menuRepository.findById(any())).willReturn(Optional.of(김치찜_1인_메뉴));

                // when & then
                assertThatThrownBy(() -> orderService.create(주문_항목_가격_메뉴_가격_불일치_매장주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

        @Nested
        class 테이블검증 {

            @Test
            @DisplayName("[실패] 등록하지 않은 테이블에서 주문할 경우 등록하지 못한다.")
            void 테이블_미등록() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(김치찜_1인_메뉴));
                given(menuRepository.findById(any())).willReturn(Optional.of(김치찜_1인_메뉴));

                given(orderTableRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> orderService.create(김치찜_1인_메뉴_1개_매장주문))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("[실패] 앉아있지 않은 테이블에서 주문할 경우 등록하지 못한다.")
            void 테이블_notOccupied() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(봉골레_파스타_세트_메뉴));
                given(menuRepository.findById(any())).willReturn(Optional.of(봉골레_파스타_세트_메뉴));

                given(orderTableRepository.findById(any())).willReturn(Optional.of(점유하지_않고_있는_테이블_1));

                // when & then
                assertThatThrownBy(() -> orderService.create(점유하지않고_봉골레_파스타_세트_메뉴_1개_매장주문))
                        .isInstanceOf(IllegalStateException.class);
            }

        }

    }

    @Nested
    class 수락 {

        @Test
        @DisplayName("[성공] 주문을 수락한다.")
        void accept() {
            // given
            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(대기중인_메뉴_매장주문));

            // when
            var updated = orderService.accept(UUID.randomUUID());

            // then
            assertThat(updated.getStatus()).isEqualTo(OrderStatus.ACCEPTED);
        }

        @Nested
        class 주문등록여부검증 {

            @Test
            @DisplayName("[실패] 주문이 등록되어 있지 않으면 수락할 수 없다.")
            void 주문_미등록() {
                // given
                given(orderRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

        @Nested
        class 상태검증 {

            @ParameterizedTest
            @DisplayName("[실패] 주문 상태가 대기중 상태가 아니면 수락할 수 없다.")
            @EnumSource(value = OrderStatus.class, names = {"ACCEPTED", "SERVED", "COMPLETED"})
            void 상태_not대기중(OrderStatus status) {
                // given
                var 주문 = 상태검증을_위한_배달주문을_생성한다(status);
                given(orderRepository.findById(any())).willReturn(Optional.of(주문));

                // when & then
                assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
                        .isInstanceOf(IllegalStateException.class);
            }

        }

    }

    @Nested
    class 전달 {

        @Test
        @DisplayName("[성공] 주문한 음식들을 전달한다.")
        void serve() {
            // given
            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(주문수락한_메뉴_매장주문));

            // when
            var updated = orderService.serve(UUID.randomUUID());

            // then
            assertThat(updated.getStatus()).isEqualTo(OrderStatus.SERVED);
        }

        @Nested
        class 주문등록여부검증 {

            @Test
            @DisplayName("[실패] 주문이 등록되어 있지 않으면 전달할 수 없다.")
            void 주문_미등록() {
                // given
                given(orderRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

        @Nested
        class 상태검증 {

            @ParameterizedTest
            @DisplayName("[실패] 주문 상태가 수락 상태가 아니면 전달할 수 없다.")
            @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "COMPLETED"})
            void 상태_not수락(OrderStatus status) {
                // given
                var 주문 = 상태검증을_위한_배달주문을_생성한다(status);
                given(orderRepository.findById(any())).willReturn(Optional.of(주문));

                // when & then
                assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                        .isInstanceOf(IllegalStateException.class);
            }

        }

    }

    @Nested
    class 완료 {

        @Test
        @DisplayName("[성공] 주문을 완료한다.")
        void complete() {
            // given
            given(orderRepository.findById(any()))
                    .willReturn(Optional.of(손님에게_전달한_메뉴_매장주문_식사완료));

            given(orderRepository.existsByOrderTableAndStatusNot(점유하고있는_테이블_2, OrderStatus.COMPLETED)).willReturn(false);

            // when
            var updated = orderService.complete(UUID.randomUUID());

            // then
            assertAll(
                    () -> assertThat(updated.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                    () -> assertThat(updated.getOrderTable().getNumberOfGuests()).isEqualTo(0),
                    () -> assertThat(updated.getOrderTable().isOccupied()).isEqualTo(false)
            );
        }

        @Nested
        class 주문등록여부검증 {

            @Test
            @DisplayName("[실패] 주문이 등록되어 있지 않으면 완료할 수 없다.")
            void 주문_미등록() {
                // given
                given(orderRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                        .isInstanceOf(NoSuchElementException.class);
            }

        }

        @Nested
        class 상태검증 {

            @ParameterizedTest
            @DisplayName("[실패] 주문 상태가 전달 상태가 아니면 완료할 수 없다.")
            @EnumSource(value = OrderStatus.class, names = {"WAITING", "SERVED", "COMPLETED"})
            void 상태_not전달(OrderStatus status) {
                // given
                var 주문 = 상태검증을_위한_배달주문을_생성한다(status);
                given(orderRepository.findById(any())).willReturn(Optional.of(주문));

                // when & then
                assertThatThrownBy(() -> orderService.serve(UUID.randomUUID()))
                        .isInstanceOf(IllegalStateException.class);
            }

        }

        @Nested
        class 테이블검증 {

            @Test
            @DisplayName("[성공] 식사를 다 마친 테이블이 아닌 경우 정리할 수 없다.")
            void 테이블_notComplete() {
                // given
                given(orderRepository.findById(any()))
                        .willReturn(Optional.of(손님에게_전달한_메뉴_매장주문_식사진행중));

                given(orderRepository.existsByOrderTableAndStatusNot(점유하고있는_테이블_3, OrderStatus.COMPLETED)).willReturn(true);

                // when
                var updated = orderService.complete(UUID.randomUUID());

                // then
                assertAll(
                        () -> assertThat(updated.getStatus()).isEqualTo(OrderStatus.COMPLETED),
                        () -> assertThat(updated.getOrderTable().getNumberOfGuests()).isEqualTo(점유하고있는_테이블_3.getNumberOfGuests()),
                        () -> assertThat(updated.getOrderTable().isOccupied()).isEqualTo(true)
                );
            }

        }

    }

    private Order 상태검증을_위한_배달주문을_생성한다(OrderStatus status) {
        var 주문 = new Order();
        주문.setType(OrderType.EAT_IN);
        주문.setStatus(status);

        return 주문;
    }

}
