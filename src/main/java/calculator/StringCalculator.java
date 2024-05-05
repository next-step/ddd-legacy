package calculator;

class StringCalculator {

    public int add(String text) {
        Numbers numbers = NumberExtractor.extract(text);
        return numbers.sum();
    }
}
