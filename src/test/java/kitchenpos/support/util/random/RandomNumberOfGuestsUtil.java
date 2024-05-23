package kitchenpos.support.util.random;

public class RandomNumberOfGuestsUtil {

    public static Integer 랜덤한_1명이상_6명이하_인원을_생성한다() {
        return 랜덤_숫자를_생성한다(1, 6);
    }

    public static Integer 랜덤한_마이너스_6명_이하의_인원을_생성한다() {
        return 랜덤_숫자를_생성한다(-6, -1);
    }

    public static Integer 랜덤_숫자를_생성한다(int min, int max) {
        var random = Math.random();

        return (int) (random * (max - min + 1)) + min;
    }

}
