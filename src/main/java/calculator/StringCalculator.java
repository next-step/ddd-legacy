package calculator;

public class StringCalculator {

    private final Separator separator = new Separator();

    public int add(String input) {
        if (input == null || input.isEmpty()) {
            return 0;
        }

        return separator.separate(input)
                .reduce(PositiveNumber::add)
                .orElseThrow()
                .value();
    }
}
