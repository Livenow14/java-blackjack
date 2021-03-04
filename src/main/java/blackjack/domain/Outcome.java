package blackjack.domain;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;

public enum Outcome {
    WIN("승", (a, b) -> a>b),
    LOOSE("패", (a,b) -> a<b),
    DRAW("무", (a,b) -> a==b);

    private final String name;
    private final BiPredicate<Integer, Integer> biPredicate;

    Outcome(String name, BiPredicate<Integer, Integer> biPredicate) {
        this.name = name;
        this.biPredicate = biPredicate;
    }

    public static Outcome findOutcome(int dealerScore, int playerScore) {
        Outcome outcome = getOutcomeWhenBuster(dealerScore, playerScore);
        if (!Objects.isNull(outcome)) {
            return outcome;
        }
        return Arrays.stream(values())
                .filter(o -> o.biPredicate.test(dealerScore, playerScore))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("체크할수 없습!!"));
    }

    private static Outcome getOutcomeWhenBuster(int dealerScore, int playerScore) {
        if (dealerScore > 21 && playerScore > 21) {
            return WIN;
        }

        if (dealerScore > 21) {
            return LOOSE;
        }

        if (playerScore > 21) {
            return WIN;
        }
        return null;
    }

    public static Outcome reverseResult(Outcome outcome) {
        if (outcome == WIN) {
            return LOOSE;
        }

        if (outcome == LOOSE) {
            return WIN;
        }

        return DRAW;
    }

    public String getName() {
        return name;
    }
}
