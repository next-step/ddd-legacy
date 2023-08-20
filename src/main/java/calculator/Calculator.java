package calculator;

import javax.transaction.NotSupportedException;

public class Calculator {

    private final StringConvertors stringConvertors;

    public Calculator() {
        this.stringConvertors = new StringConvertors();
    }

    public int add(String text) throws NotSupportedException {
        return stringConvertors.convert(text).sum();
    }
}
