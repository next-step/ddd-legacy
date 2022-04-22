package kitchenpos.infra;

/**
 * <pre>
 * kitchenpos.infra
 *      ProfanityClient
 * </pre>
 *
 * @author YunJin Choi(zzdd1558@gmail.com)
 * @since 2022-04-20 오전 12:26
 */

public interface ProfanityClient {
    boolean containsProfanity(String text);
}
