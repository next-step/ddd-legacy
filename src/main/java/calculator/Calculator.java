package calculator;

class Calculator {
    private final NumberStrings numberStrings;

    public Calculator(final String target) {
        numberStrings = Separator.separate(new TargetString(target));
    }

    public int calculate() {
        if (numberStrings.isEmpty()) {
            return 0;
        }
        return numberStrings.getNumbers().stream().mapToInt(Integer::parseInt).sum();
    }
}
