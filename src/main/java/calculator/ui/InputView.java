package calculator.ui;

import java.util.Scanner;

public class InputView {

    private static final Scanner scanner = new Scanner(System.in);

    public String InputAddNumber() {
        System.out.println("덧셈을 진행할 문자열을 입력해주세요.");
        return scanner.nextLine();
    }
}
