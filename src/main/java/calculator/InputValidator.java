package calculator;

public record InputValidator() {

    public void validate(String input) {
        if (input == null || input.isBlank()) {
            return;
        }

        if (input.contains("-")) {
            throw new RuntimeException();
        }
    }
}
