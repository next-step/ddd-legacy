package calculator;

class Calculator {

    public static int add(NumberStrings numberStrings) {
        if (numberStrings.isEmpty()) {
            return 0;
        }
        return numberStrings.getNumbers().stream().mapToInt(Integer::parseInt).sum();
    }
}
