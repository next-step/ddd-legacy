package calculator;

public class StringCalculator {

    private final Separator separator = new Separator();
    private final NotNegativeNumber initialNumber = new NotNegativeNumber(0);

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return initialNumber.value();
        }

        return separator
                .separate(input)
                .reduce(
                        initialNumber,
                        NotNegativeNumber::add
                ).value();
    }
}
