package stringCalculator;

public class StringCalculator {
    public StringCalculator() {
    }

    public int add(String text) {
        int result = 0;

        try {
            SumProcess sumProcess = new SumProcess(text);
            result = sumProcess.sum();
        } catch (IllegalArgumentException e) {
            result = 0;
        }
        catch (RuntimeException e) {
            throw e;
        }

        return result;
    }

}
