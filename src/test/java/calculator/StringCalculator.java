package calculator;

class StringCalculator {
    private final NumberExtractor numberExtractor;

    public StringCalculator() {
        this.numberExtractor = new NumberExtractor();
    }

    public Integer add(String text) {
        return numberExtractor.extract(text).sum();
    }
}