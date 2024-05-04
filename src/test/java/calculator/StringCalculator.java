package calculator;

class StringCalculator {
    private final NumberExtractor numberExtractor;
    private final NumberSummer numberSummer;

    public StringCalculator() {
        this.numberExtractor = new NumberExtractor();
        this.numberSummer = new NumberSummer();
    }

    public Integer add(String text) {
        return numberSummer.sum(numberExtractor.extract(text));
    }
}