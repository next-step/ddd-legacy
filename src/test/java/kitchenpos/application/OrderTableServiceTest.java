package kitchenpos.application;

import kitchenpos.domain.*;
import kitchenpos.integration_test_step.DatabaseCleanStep;
import kitchenpos.integration_test_step.MenuIntegrationStep;
import kitchenpos.integration_test_step.OrderIntegrationStep;
import kitchenpos.integration_test_step.OrderTableIntegrationStep;
import kitchenpos.test_fixture.OrderTableTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("OrderTableService 클래스")
class OrderTableServiceTest {

    private OrderTableService sut;
    private OrderTableRepository orderTableRepository;
    private OrderRepository orderRepository;
    private OrderTableIntegrationStep orderTableIntegrationStep;
    private OrderIntegrationStep orderIntegrationStep;
    private ProductRepository productRepository;
    private MenuRepository menuRepository;
    private MenuGroupRepository menuGroupRepository;
    private MenuIntegrationStep menuIntegrationStep;

    @DisplayName("새로운 주문 테이블 등록 테스트")
    @Nested
    class Describe_create {

        @BeforeEach
        void setUp() {
            orderTableRepository = new FakeOrderTableRepository();
            orderRepository = new FakeOrderRepository();
            orderTableIntegrationStep = new OrderTableIntegrationStep(orderTableRepository);
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            orderIntegrationStep = new OrderIntegrationStep(orderRepository, menuIntegrationStep, orderTableIntegrationStep);
            sut = new OrderTableService(orderTableRepository, orderRepository);
        }

        @DisplayName("새로운 주문 테이블을 등록할 수 있다.")
        @Test
        void create() {
            // given
            OrderTable orderTable = OrderTableTestFixture.create()
                    .changeId(null)
                    .getOrderTable();

            // when
            OrderTable result = sut.create(orderTable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isNotNull();
            assertThat(result.getName()).isEqualTo("테스트 테이블");
            assertThat(result.getNumberOfGuests()).isEqualTo(0);
            assertThat(result.isOccupied()).isFalse();
        }

        @DisplayName("새로운 주문 테이블의 이름이 빈 값이면 예외가 발생한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void createNullOrEmptyNameExceptionThrown(String name) {
            // given
            OrderTable orderTable = OrderTableTestFixture.create()
                    .changeId(null)
                    .changeName(name)
                    .getOrderTable();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.create(orderTable));
        }

        @DisplayName("새롭게 등록하는 주문 테이블의 `테이블에 앉은 고객 수`는 0명으로 생성된다.")
        @Test
        void createNumberOfGuests() {
            // given
            OrderTable orderTable = OrderTableTestFixture.create()
                    .changeId(null)
                    .getOrderTable();

            // when
            OrderTable result = sut.create(orderTable);

            // then
            assertThat(result.getNumberOfGuests()).isEqualTo(0);
        }

        @DisplayName("새롭게 등록하는 주문 테이블의 `테이블 사용 여부`는 `사용하지 않는 상태`로 생성된다.")
        @Test
        void createOccupied() {
            // given
            OrderTable orderTable = OrderTableTestFixture.create()
                    .changeId(null)
                    .getOrderTable();

            // when
            OrderTable result = sut.create(orderTable);

            // then
            assertThat(result.isOccupied()).isFalse();
        }
    }

    @DisplayName("주문 테이블을 테이블 사용 상태로 변경")
    @Nested
    class Describe_sit {

        @BeforeEach
        void setUp() {
            orderTableRepository = new FakeOrderTableRepository();
            orderRepository = new FakeOrderRepository();
            orderTableIntegrationStep = new OrderTableIntegrationStep(orderTableRepository);
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            orderIntegrationStep = new OrderIntegrationStep(orderRepository, menuIntegrationStep, orderTableIntegrationStep);
            sut = new OrderTableService(orderTableRepository, orderRepository);
        }

        @DisplayName("주문 테이블에 고객이 앉았음을 등록할 수 있다.")
        @Test
        void sit() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createEmptyTable();

            // when
            OrderTable result = sut.sit(orderTable.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderTable.getId());
            assertThat(result.isOccupied()).isTrue();
        }

        @DisplayName("주문 테이블에 고객이 앉았음을 등록할 떄 주문 테이블이 존재하지 않으면 예외가 발생한다.")
        @Test
        void sitNotExistsOrderTable() {
            // given
            OrderTable notPersistOrderTable = OrderTableTestFixture.create()
                    .getOrderTable();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.sit(notPersistOrderTable.getId()));
        }
    }

    @DisplayName("주문 테이블을 테이블 비사용 상태로 변경")
    @Nested
    class Describe_clear {

        @BeforeEach
        void setUp() {
            orderTableRepository = new FakeOrderTableRepository();
            orderRepository = new FakeOrderRepository();
            orderTableIntegrationStep = new OrderTableIntegrationStep(orderTableRepository);
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            orderIntegrationStep = new OrderIntegrationStep(orderRepository, menuIntegrationStep, orderTableIntegrationStep);
            sut = new OrderTableService(orderTableRepository, orderRepository);
        }

        @DisplayName("고객이 앉았던 주문 테이블 정보를 비어있는 테이블로 변경할 수 있다.")
        @Test
        void clear() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createEmptyTable();
            sut.sit(orderTable.getId());

            // when
            OrderTable result = sut.clear(orderTable.getId());

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderTable.getId());
            assertThat(result.isOccupied()).isFalse();
        }

        @DisplayName("주문 테이블을 비어있는 테이블로 변경할 때 주문 테이블이 존재하지 않으면 예외가 발생한다.")
        @Test
        void clearNotExistsOrderTable() {
            // given
            OrderTable notPersistOrderTable = OrderTableTestFixture.create()
                    .getOrderTable();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.clear(notPersistOrderTable.getId()));
        }

        @DisplayName("주문 테이블을 비어있는 테이블로 변경할 때 만약 주문 테이블에 주문이 존재하고 주문 상태가 `주문 완료`가 아니면 예외가 발생한다.")
        @Test
        void clearOrderTableNotCompletedOrder() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createEmptyTable();
            sut.sit(orderTable.getId());
            orderIntegrationStep.createWaitingEatInOrder(orderTable);

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.clear(orderTable.getId()));
        }

        @DisplayName("주문 테이블을 비어있는 테이블로 변경할 때 만약 주문 테이블에 주문이 존재하고 주문 상태가 `주문 완료`이면 주문 테이블의 `테이블에 앉은 고객 수`는 0명으로 변경된다.")
        @Test
        void clearOrderTableCompletedOrder() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createEmptyTable();
            sut.sit(orderTable.getId());
            orderIntegrationStep.createCompletedEatInOrder(orderTable);

            // when
            OrderTable result = assertDoesNotThrow(() -> sut.clear(orderTable.getId()));

            // then
            assertThat(result.isOccupied()).isFalse();
            assertThat(result.getNumberOfGuests()).isEqualTo(0);
        }
    }

    @DisplayName("주문 테이블의 `테이블에 앉은 고객 수`를 변경")
    @Nested
    class Describe_change_number_of_guests {
        @BeforeEach
        void setUp() {
            orderTableRepository = new FakeOrderTableRepository();
            orderRepository = new FakeOrderRepository();
            orderTableIntegrationStep = new OrderTableIntegrationStep(orderTableRepository);
            productRepository = new FakeProductRepository();
            menuRepository = new FakeMenuRepository();
            menuGroupRepository = new FakeMenuGroupRepository();
            menuIntegrationStep = new MenuIntegrationStep(menuRepository, menuGroupRepository, productRepository);
            orderIntegrationStep = new OrderIntegrationStep(orderRepository, menuIntegrationStep, orderTableIntegrationStep);
            sut = new OrderTableService(orderTableRepository, orderRepository);
        }

        @DisplayName("주문 테이블의 `테이블에 앉은 고객 수`를 변경할 수 있다.")
        @Test
        void changeNumberOfGuests() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createSitTable();
            OrderTable request = OrderTableTestFixture.create()
                    .changeNumberOfGuests(5)
                    .getOrderTable();

            // when
            OrderTable result = sut.changeNumberOfGuests(orderTable.getId(), request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(orderTable.getId());
            assertThat(result.getNumberOfGuests()).isEqualTo(5);
        }

        @DisplayName("주문 테이블의 `테이블에 앉은 고객 수`를 변경할 때 고객 수가 음수이면 예외가 발생한다.")
        @Test
        void changeNumberOfGuestsNegative() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createSitTable();
            OrderTable request = OrderTableTestFixture.create()
                    .changeNumberOfGuests(-1)
                    .getOrderTable();

            // when & then
            assertThrows(IllegalArgumentException.class, () -> sut.changeNumberOfGuests(orderTable.getId(), request));
        }

        @DisplayName("주문 테이블의 `테이블에 앉은 고객 수`를 변경할 때 주문 테이블이 존재하지 않으면 예외가 발생한다.")
        @Test
        void changeNumberOfGuestsNotExistsOrderTable() {
            // given
            OrderTable notPersistOrderTable = OrderTableTestFixture.create()
                    .getOrderTable();
            OrderTable request = OrderTableTestFixture.create()
                    .changeNumberOfGuests(5)
                    .getOrderTable();

            // when & then
            assertThrows(NoSuchElementException.class, () -> sut.changeNumberOfGuests(notPersistOrderTable.getId(), request));
        }

        @DisplayName("주문 테이블의 `테이블에 앉은 고객 수`를 변경할 때 주문 테이블이 `비어있는 상태`이면 예외가 발생한다.")
        @Test
        void changeNumberOfGuestsNotOccupied() {
            // given
            OrderTable orderTable = orderTableIntegrationStep.createEmptyTable();
            OrderTable request = OrderTableTestFixture.create()
                    .changeNumberOfGuests(5)
                    .getOrderTable();

            // when & then
            assertThrows(IllegalStateException.class, () -> sut.changeNumberOfGuests(orderTable.getId(), request));
        }
    }
}
