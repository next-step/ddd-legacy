package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.*;


public class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다")
    @Test
    void constructor(){
       // 5글자가 넘는 경우, IllegalArgumentException 발생
       assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(
               () -> {
                  new Car("동해물과백두산이");
               }
       );

    }

    @DisplayName("자동차가 움직이는 조건은 0 ~ 9로 무작위 값을 구한 후, 무작위 값이 4이상인 경우에만")
    @Test
    void moving(){

    }

}
