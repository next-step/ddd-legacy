package calculator;

import calculator.domain.*;

import java.util.List;

public class CalculatorMain {
    public static void main(String[] args) {
        // 미리 정의된 입력 리스트 (사용자 입력 대신 활용)
        List<String> inputs = List.of(
                "1,2:3",
                "//;\n1;2;3",
                "4,5,6",
                "//#\n7#8#9",
                "1a,2,3",
                "exit"
        );

        // 의존성 주입
        StringCalculatorDelimiters delimiters = StringCalculatorDelimiters.create();
        StringCalculatorInputValidator stringCalculatorInputValidator = new StringCalculatorInputValidator(delimiters);
        InputParser parser = new StringCalculatorInputParser(delimiters);
        StringCalculator calculator = new StringCalculator(stringCalculatorInputValidator, parser);

        System.out.println("🎯 문자열 덧셈 계산기를 실행합니다.");
        System.out.println("입력 형식: 숫자(쉼표, 콜론 구분) 또는 //구분자\\n숫자");
        System.out.println("예시: 1,2:3 또는 //;\\n1;2;3");

        for (String input : inputs) {
            System.out.println(delimiters.toString());
            System.out.println("👉 숫자 입력: " + input);

            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("🔚 프로그램을 종료합니다.");
                break;
            }

            try {
                int result = calculator.add(input);
                System.out.println("✅ 결과: " + result);
            } catch (RuntimeException e) {
                System.out.println("❌ 오류: " + e.getMessage());
            }
        }
    }
}