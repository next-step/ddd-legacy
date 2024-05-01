package calculator.util;

public class Delimiter {
    private String delimiter;

    public Delimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public void addDelimiter(String delimiter){
        this.delimiter = this.delimiter + "|" + delimiter;
    }

    public String[] split(String text){
        return text.split(delimiter);
    }
}
