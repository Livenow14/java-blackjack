package blackjack.domain;

import blackjack.domain.card.Deck;
import blackjack.domain.state.DealerTurnOver;
import blackjack.domain.state.State;
import blackjack.domain.state.StateFactory;
import blackjack.domain.user.AbstractUser;
import blackjack.domain.user.Dealer;
import blackjack.domain.user.Player;
import blackjack.domain.user.Users;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Round {
    private final Deck deck;
    private final Users users;

    private Round(Deck deck, Users<?> users) {
        this.deck = deck;
        this.users = users;
    }

    public static Round valueOf(Deck deck, List<String> playerNames, List<BigDecimal> bettingMoneys) {
        List<AbstractUser> users = new ArrayList<>();
        users.add(new Dealer(drawTwoCard(deck)));

        List<AbstractUser> players = playerNames.stream()
                .map(playerName -> new Player(drawTwoCard(deck), playerName, bettingMoneys.remove(0)))
                .collect(Collectors.toList());

        users.addAll(players);

        return new Round(deck, new Users<>(users));
    }

    private static State drawTwoCard(Deck deck) {
        return StateFactory.draw(deck.makeOneCard(), deck.makeOneCard());
    }

    public AbstractUser getDealer() {
        return users.getDealer();
    }

    public String getDealerName() {
        return getDealer().getName();
    }

    public List<AbstractUser> getPlayers() {
        return Collections.unmodifiableList(users.getPlayers());
    }

    public boolean isDealerCanDraw() {
        return getDealer().canDraw();
    }

    public void addDealerCard() {
        AbstractUser findDealer = getDealer();
        State state = findDealer.getState().draw(deck.makeOneCard());
        if (!state.isFinish() && !findDealer.canDraw()) {
            findDealer.changeState(new DealerTurnOver(state.cards()));
        }
    }

    public void addPlayerCard(AbstractUser player) {
        AbstractUser findPlayer = findPlayer(player);
        State state = findPlayer.getState().draw(deck.makeOneCard());
        findPlayer.changeState(state);
    }

    public void makePlayerStay(AbstractUser player) {
        AbstractUser findPlayer = findPlayer(player);
        State state = findPlayer.getState().stay();
        findPlayer.changeState(state);
    }

    private AbstractUser findPlayer(AbstractUser player) {
        return getPlayers().stream()
                .filter(p -> p.equals(player))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 유저입니다"));
    }

    public Map<String, BigDecimal> findUsersProfit() {
        LinkedHashMap<String, BigDecimal> result = new LinkedHashMap<>();
        AbstractUser dealer = getDealer();
        List<AbstractUser> players = getPlayers();
        List<BigDecimal> profits = getProfits(dealer, players);
        result.put(dealer.getName(), BigDecimal.valueOf(profits.stream().mapToInt(profit -> -profit.intValue()).sum()));

        players.forEach(player ->
                result.put(player.getName(), profits.remove(0))
        );
        return result;
    }

    private List<BigDecimal> getProfits(AbstractUser dealer, List<AbstractUser> players) {
        return players.stream()
                .map(player -> player.getState().profit(dealer.getState(), player.getBettingMoney()))
                .collect(Collectors.toList());
    }
}
