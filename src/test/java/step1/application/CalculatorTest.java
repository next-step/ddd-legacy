package step1.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import step1.common.CalculatorConstant;
import step1.domain.Number;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CalculatorTest {

    @DisplayName("입력값 내 연산자 추출")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3", "1:2,3", "5,6", "5:7"})
    void parseOperators(String expression) {
        Calculator calculator = new CalculatorImpl(expression);
        List<String> operators = calculator.parseOperators(expression);
        assertTrue(() -> operators.contains(CalculatorConstant.COMMA) || operators.contains(CalculatorConstant.COLON));
    }

    // \n 으로 인해서 @CsvSource 방식으로 테스트 불가능
    @DisplayName("입력값 내 커스텀 연산자 추출")
    @Test
    void parseOperatorsByCustomOperator() {
        Map<String, String> source = new HashMap<>();
        source.put("//;\n1;2;3", ";");
        source.put("//&\n1&2&3", "&");
        source.put("//$\n1$2$3", "$");
        source.put("//#\n1#2#3", "#");

        for (Map.Entry<String, String> entry : source.entrySet()) {
            Calculator calculator = new CalculatorImpl(entry.getKey());
            List<String> operators = calculator.parseOperators(entry.getKey());
            assertEquals(entry.getValue(), operators.get(0));
        }

    }

    @DisplayName("입력값 내 연산에 사용될 숫자 추출")
    @ParameterizedTest
    @CsvSource(value = {"1,2:3* 3", "1,2:3,4* 4", "1:2:3,4:5* 5"}, delimiter = '*')
    void parseNumbers(String expression, int numberCount) {
        Calculator calculator = new CalculatorImpl(expression);
        List<Number> numbers = calculator.parseNumbers(expression);
        assertEquals(numberCount, numbers.size());
    }

    // \n 으로 인해서 @CsvSource 방식으로 테스트 불가능
    @DisplayName("입력값 내 연산에 사용될 숫자 추출(custom 연산자)")
    @Test
    void parseNumbersByCustomOperator() {
        Map<String, Integer> source = new HashMap<>();
        source.put("//;\n1;2", 2);
        source.put("//&\n1&2&3", 3);
        source.put("//#\n1#2#3#4", 4);

        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            Calculator calculator = new CalculatorImpl(entry.getKey());
            List<Number> numbers = calculator.parseNumbers(entry.getKey());
            assertEquals(entry.getValue(), numbers.size());
        }

    }

    @DisplayName("입력값에 대한 합계 연산")
    @ParameterizedTest
    @CsvSource(value = {"1,2:3* 6", "1,2:3,4* 10", "1:2:3,4:5* 15", "5* 5", "1,3* 4"}, delimiter = '*')
    void sum(String expression, int result) {
        Calculator calculator = new CalculatorImpl(expression);
        int sum = calculator.sum(expression);
        assertEquals(result, sum);
    }

    // \n 으로 인해서 @CsvSource 방식으로 테스트 불가능
    @DisplayName("입력값에 대한 합계 연산(custom 연산자)")
    @Test
    void sumByCustomOperator() {
        Map<String, Integer> source = new HashMap<>();
        source.put("//;\n1;2;3", 6);
        source.put("//&\n3&4&5", 12);
        source.put("//#\n4#5#6", 15);

        for (Map.Entry<String, Integer> entry : source.entrySet()) {
            Calculator calculator = new CalculatorImpl(entry.getKey());
            int sum = calculator.sum(entry.getKey());
            assertEquals(entry.getValue(), sum);
        }

    }

    @DisplayName("마이너스 입력값에 대한 합계 연산시 Exception 타입 검증")
    @ParameterizedTest
    @CsvSource(value = {"-1,2:3* 6", "1,2:-3,4* 10", "1:-2:3,4:5* 15", "-5* 5", "1,-3* 4"}, delimiter = '*')
    void calculateMinus(String expression) {
        Calculator calculator = new CalculatorImpl(expression);
        assertThrows(RuntimeException.class, () -> calculator.sum(expression));
    }

    // \n 으로 인해서 @CsvSource 방식으로 테스트 불가능
    @DisplayName("마이너스 입력값에 대한 합계 연산(custom 연산자)시 Exception 타입 검증")
    @Test
    void calculateMinusByCustomOperator() {
        Map<String, String> source = new HashMap<>();
        source.put("//;\n-1;2;3", ";");
        source.put("//&\n1&-2&3", "&");
        source.put("//#\n1#2#-3", "#");

        for (Map.Entry<String, String> entry : source.entrySet()) {
            Calculator calculator = new CalculatorImpl(entry.getKey());
            assertThrows(RuntimeException.class, () -> calculator.sum(entry.getKey()));
        }
    }

    @DisplayName("null 또는 empty 값에 대한 연산")
    @ParameterizedTest
    @NullSource
    @EmptySource
    void nullOrEmptySum(String expression) {
        Calculator calculator = new CalculatorImpl(expression);
        int sum = calculator.sum(expression);
        assertEquals(0, sum);
    }

}