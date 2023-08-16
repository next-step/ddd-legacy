package racingcar;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;


class CarTest {

    @Nested
    @DisplayName("자동차 이름 테스트")
    static class Name {
        @Test
        @DisplayName("자동차는 이름을 가지고 있다.")
        void test1() {
            var result = new Car("김지원");

            assertThat(result.getName()).isEqualTo("김지원");
        }

        @TestFactory
        @DisplayName("자동차 이름 유효성 테스트")
        Collection<DynamicTest> name() {
            return List.of(
                    dynamicTest("5글자 이하인 경우 생성 성공", () -> {
                        var result = new Car("김지원");

                        assertThat(result.getName()).isEqualTo("김지원");
                    }),

                    dynamicTest("5글자인 경우 생성 성공", () -> {
                        var result = new Car("김지원");

                        assertThat(result.getName()).isEqualTo("김지원");
                    }),

                    dynamicTest("5글자를 초과하는 경우 IllegalArgumentException 발생", () -> {
                        var exception = catchThrowable(() -> new Car("김지원이다마"));

                        assertThat(exception).isInstanceOf(IllegalArgumentException.class);
                    })
            );
        }

    }

    @Nested
    @DisplayName("자동차 이동 테스트")
    static class Move {
        @Test
        @DisplayName("자동차 생성 시 초기위치로 0을 가진다.")
        void test2() {
            var car = new Car("김지원");

            assertThat(car.getPosition()).isEqualTo(0);
        }


        @ParameterizedTest(name = "무작위 값이 {0} 이상이면 전진한다.")
        @ValueSource(ints = {4, 5, 6})
        void test3(final int value) {
            var car = new Car("김지원" + value);

            car.move(new NumberMoveCondition(value));

            assertThat(car.getPosition()).isEqualTo(1);
        }


        @ParameterizedTest(name = "무작위 값이 {0} 이하이면 정지한다.")
        @ValueSource(ints = {0, 1, 2, 3})
        void test4(final int value) {
            var car = new Car("김지원" + value);

            car.move(new NumberMoveCondition(value));

            assertThat(car.getPosition()).isEqualTo(0);
        }
    }

}
