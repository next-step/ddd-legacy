package calculator.verifier;

import java.util.List;

@FunctionalInterface
public interface NumberVerifier {

    void verify(List<String> expressions);
}
