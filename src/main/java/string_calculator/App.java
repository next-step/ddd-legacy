package string_calculator;

import java.util.Scanner;
import string_calculator.string_parser.SimpleStringParser;

public class App {

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final SimpleStringParser simpleStringParser = new SimpleStringParser();
        final StringCalculator stringCalculator = new StringCalculator(simpleStringParser);

        System.out.print("Insert string: ");

        final String string = scanner.nextLine();

        final long result = stringCalculator.calculate(string);

        System.out.println("Result: " + result);
    }
}
