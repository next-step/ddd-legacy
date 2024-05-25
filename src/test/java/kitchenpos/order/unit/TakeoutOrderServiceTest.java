package kitchenpos.order.unit;

import kitchenpos.application.OrderService;
import kitchenpos.domain.MenuRepository;
import kitchenpos.domain.Order;
import kitchenpos.domain.OrderRepository;
import kitchenpos.domain.OrderStatus;
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
import static kitchenpos.menu.fixture.MenuFixture.토마토_파스타_단품_메뉴;
import static kitchenpos.menu.fixture.MenuFixture.피클_메뉴_숨김;
import static kitchenpos.order.fixture.TakeoutOrderFixture.대기중인_메뉴_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.봉골레_세트_1개_토마토_단품_1개_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.봉골레_세트_메뉴_마이너스_1개_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.빈_주문항목_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.손님에게_전달한_메뉴_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.숨김처리된_메뉴_1개_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.주문_항목_가격_메뉴_가격_불일치_테이크주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.주문수락한_메뉴_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.주문항목미존재_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.타입미존재_테이크아웃주문;
import static kitchenpos.order.fixture.TakeoutOrderFixture.토마토_파스타_단품_메뉴_1개_테이크아웃주문;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class TakeoutOrderServiceTest {

    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() {
        this.orderService = new OrderService(
                orderRepository, menuRepository, null, null
        );
    }

    @Nested
    class 등록 {

        @Test
        @DisplayName("[성공] 테이크아웃 주문을 등록한다.")
        void create() {
            // given
            given(menuRepository.findAllByIdIn(any())).willReturn(List.of(토마토_파스타_단품_메뉴));
            given(menuRepository.findById(any())).willReturn(Optional.of(토마토_파스타_단품_메뉴));

            // when
            var saved = orderService.create(토마토_파스타_단품_메뉴_1개_테이크아웃주문);

            // then
            then(orderRepository).should(times(1)).save(any());
        }

        @Nested
        class 타입검증 {

            @Test
            @DisplayName("[실패] 주문 유형을 입력하지 않으면 등록하지 못한다.")
            void 주문유형_null() {
                // when & then
                assertThatThrownBy(() -> orderService.create(타입미존재_테이크아웃주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

        @Nested
        class 주문항목목록검증 {

            @Test
            @DisplayName("[실패] 주문 항목 목록을 입력하지 않으면 등록하지 못한다.")
            void 주문항목목록_null() {
                // when & then
                assertThatThrownBy(() -> orderService.create(주문항목미존재_테이크아웃주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목 목록이 비어있으면 등록하지 못한다.")
            void 주문항목목록_empty() {
                // when & then
                assertThatThrownBy(() -> orderService.create(빈_주문항목_테이크아웃주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목 목록이 전부 메뉴에 등록되어 있어야 한다.")
            void 주문항목목록_미등록메뉴포함() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(토마토_파스타_단품_메뉴));

                // when & then
                assertThatThrownBy(() -> orderService.create(봉골레_세트_1개_토마토_단품_1개_테이크아웃주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class 주문항목검증 {

            @Test
            @DisplayName("[실패] 수량이 마이너스인 주문 항목이 존재할 경우 등록하지 못한다.")
            void 주문항목_수량마이너스() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(봉골레_파스타_세트_메뉴));

                // when & then
                assertThatThrownBy(() -> orderService.create(봉골레_세트_메뉴_마이너스_1개_테이크아웃주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목이 미등록된 메뉴일 경우 등록하지 못한다.")
            void 주문항목_미등록메뉴() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(토마토_파스타_단품_메뉴));
                given(menuRepository.findById(any())).willReturn(Optional.empty());

                // when & then
                assertThatThrownBy(() -> orderService.create(토마토_파스타_단품_메뉴_1개_테이크아웃주문))
                        .isInstanceOf(NoSuchElementException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목이 숨김처리된 메뉴일 경우 등록하지 못한다.")
            void 주문항목_숨김처리메뉴() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(피클_메뉴_숨김));
                given(menuRepository.findById(any())).willReturn(Optional.of(피클_메뉴_숨김));

                // when & then
                assertThatThrownBy(() -> orderService.create(숨김처리된_메뉴_1개_테이크아웃주문))
                        .isInstanceOf(IllegalStateException.class);
            }

            @Test
            @DisplayName("[실패] 주문 항목의 가격과 메뉴의 가격이 동일하지 않으면 등록하지 못한다.")
            void 주문항목가격_메뉴가격_불일치() {
                // given
                given(menuRepository.findAllByIdIn(any())).willReturn(List.of(김치찜_1인_메뉴));
                given(menuRepository.findById(any())).willReturn(Optional.of(김치찜_1인_메뉴));

                // when & then
                assertThatThrownBy(() -> orderService.create(주문_항목_가격_메뉴_가격_불일치_테이크주문))
                        .isInstanceOf(IllegalArgumentException.class);
            }

        }

    }

    @Nested
    class 수락 {

        @Test
        @DisplayName("[성공] 주문을 수락한다.")
        void accept() {
            // given
            given(orderRepository.findById(any())).willReturn(Optional.of(대기중인_메뉴_테이크아웃주문));

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
                var 주문 = 상태검증을_위한_테이크아웃주문을_생성한다(status);
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
            given(orderRepository.findById(any())).willReturn(Optional.of(주문수락한_메뉴_테이크아웃주문));

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
                assertThatThrownBy(() -> orderService.accept(UUID.randomUUID()))
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
                var 주문 = 상태검증을_위한_테이크아웃주문을_생성한다(status);
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
            given(orderRepository.findById(any())).willReturn(Optional.of(손님에게_전달한_메뉴_테이크아웃주문));

            // when
            var updated = orderService.complete(UUID.randomUUID());

            // then
            assertThat(updated.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Nested
        class 주문등록여부검증 {

            @Test
            @DisplayName("[실패] 주문이 등록되어 있지 않으면 완료할 수 없다.")
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
            @DisplayName("[실패] 주문 상태가 전달 상태가 아니면 완료할 수 없다.")
            @EnumSource(value = OrderStatus.class, names = {"WAITING", "ACCEPTED", "COMPLETED"})
            void 상태_not전달(OrderStatus status) {
                // given
                var 주문 = 상태검증을_위한_테이크아웃주문을_생성한다(status);
                given(orderRepository.findById(any())).willReturn(Optional.of(주문));

                // when & then
                assertThatThrownBy(() -> orderService.complete(UUID.randomUUID()))
                        .isInstanceOf(IllegalStateException.class);
            }

        }

    }

    private Order 상태검증을_위한_테이크아웃주문을_생성한다(OrderStatus status) {
        var 주문 = new Order();
        주문.setType(OrderType.TAKEOUT);
        주문.setStatus(status);

        return 주문;
    }

}
