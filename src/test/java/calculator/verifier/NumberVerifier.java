package calculator.verifier;

import java.util.List;

@Deprecated
@FunctionalInterface
public interface NumberVerifier {

    void verify(List<String> expressions);
}
