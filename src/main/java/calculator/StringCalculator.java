package calculator;

class StringCalculator {

    public Integer add(String text) {
        return NumberExtractor.extract(text).sum();
    }
}
