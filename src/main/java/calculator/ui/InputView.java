package calculator.ui;

import calculator.domain.AddNumber;
import java.util.Scanner;

public class InputView {

    private static final Scanner scanner = new Scanner(System.in);

    public AddNumber InputAddNumber() {
        System.out.println("덧셈을 진행할 문자열을 입력해주세요.");
        return AddNumber.from(scanner.nextLine());
    }
}
