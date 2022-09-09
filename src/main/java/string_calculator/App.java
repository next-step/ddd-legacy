package string_calculator;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        System.out.print("Insert string: ");

        final String string = scanner.nextLine();

        final StringCalculator stringCalculator = new StringCalculator();
        final long result = stringCalculator.calculate(string);

        System.out.println("Result: " + result);
    }
}
