package calculator;

class StringCalculator {

    public Integer add(String text) {
        Numbers numbers = NumberExtractor.extract(text);
        return numbers.sum();
    }
}
