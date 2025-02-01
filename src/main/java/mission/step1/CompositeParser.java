package mission.step1;

public class CompositeParser implements ParserStrategy {
    private final Parser defaultParser;
    private final CustomParser customParser;

    public CompositeParser(Parser defaultParser, CustomParser customParser) {
        this.defaultParser = defaultParser;
        this.customParser = customParser;
    }

    @Override
    public String[] splitWithDelimiter(String input) {
        if (customParser.isCustomFormat(input)) {
            return customParser.splitWithDelimiter(input);
        }
        return defaultParser.splitWithDelimiter(input);
    }

}