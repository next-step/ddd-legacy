package stringcalculator.factory;

import stringcalculator.factory.splitter.CustomSplitter;
import stringcalculator.factory.splitter.DefaultSplitter;
import stringcalculator.factory.splitter.Splitter;

import java.util.HashMap;
import java.util.Map;

public class SplitterFactory {

    private static final Map<Boolean, Splitter> splitters = new HashMap<>();
    private static final boolean DEFAULT = false;
    private static final boolean CUSTOM = true;

    private SplitterFactory() {
    }

    public static Splitter findSplitter(boolean type) {
        if (isNotReady()) {
            init();
        }
        return splitters.get(type);
    }

    private static void init() {
        splitters.put(DEFAULT, new DefaultSplitter());
        splitters.put(CUSTOM, new CustomSplitter());
    }

    private static boolean isNotReady() {
        return !splitters.containsKey(DEFAULT) || !splitters.containsKey(CUSTOM);
    }
}
