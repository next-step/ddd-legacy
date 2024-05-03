package stringCalculator;

public class StringCalculator {
    private final SumProcess sumProcess = new SumProcess();

    public StringCalculator() {
    }

    public int add(String text) {
        int result = 0;

        try {
            NumberConvertor numberConvertor = new NumberConvertor(text);

            result = sumProcess.sum(numberConvertor.getNumbers());
        } catch (IllegalArgumentException e) {
            result = 0;
        }
        catch (RuntimeException e) {
            throw e;
        }

        return result;
    }

}
