package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class CarTest {
    @DisplayName("자동차를 생성한다.")
    @Test
    void create() {
        final Car car = new Car("BMW");
        assertThat(car).isNotNull();
    }

    @DisplayName("자동차의 이름은 다섯 글자를 넘을 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"BMX X1", "SONATA"})
    void exceededName(String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));

//        assertThatThrownBy(() -> {
//            final Car car = new Car("BMW X1");
//        }).isInstanceOf(IllegalArgumentException.class);

//        assertThatThrownBy(() -> new Car("BMW X1")).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차의 이름은 빈 값일 수 없다.")
    @Test
    void emptyName() {
        assertThatThrownBy(() -> {
            final Car car = new Car("");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 움직인다.")
    @Test
    void validateCarMoving() {
        final Car car = new Car("BMW");
//        car.move(new TestMovingStrategy());
//        굳이 트루만을 반환하는 가짜 객체 대신 람다 쓸 수 있다
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    /*
        junit5 는 왜 주피터?! 목성이 다섯번째라서ㅎ

        변수선언안하면 실행안될텐데, junit엔진에서는 실행을 한다

        parameterized test -> spock 의 where 과 유사

        move 테스트는 개발자가 의도할 수 없다
        -> 어떻게 핸들링 할 것 인가?
            1. move가 받는 매개변수를 int로 받는다
                -> 기존 클라이언트 코드를 모두 변경해야함 backward compatibility
                -> 변경되기 쉬운 비즈니스 규칙, 정책이다
                    -> 가짜 객체를 만들어보자, 확장가능하게?! fake, not mock
                    -> 인터페이스를 추출한다
                    -> 가짜 구현체를 주입하고, 소스에선 인자로 인터페이스를 받는다


        @override 써서 빨간줄 그어지면 추출 및 메서드 생성 <- 왜안됨ㅎㅎ


     */
}