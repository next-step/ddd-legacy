package string_calculator.string_parser;

public class SimpleStringParser extends StringParser {

    @Override
    protected String[] tokens(String string) {
        return string.split("[,:]");
    }
}
