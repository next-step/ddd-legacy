package kitchenpos.calculator;

import java.util.Arrays;

/**
 * TODO
 * 2. 앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다. 예를 들어 “//;\n1;2;3”과 같이 값을 입력할 경우 커스텀 구분자는 세미콜론(;)이며, 결과 값은 6이 반환되어야 한다.
 * 3. 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.
 */
public class StringCalculator {

    public int sum(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        String[] tokens = text.split("[,:]");
        return Arrays.stream(tokens).map(Integer::valueOf).reduce(Integer::sum).get();
    }
}
