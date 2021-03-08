package blackjack.domain.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsersTest {

    private static final List<AbstractUser> DUMMY_EIGHT_PLAYERS = new ArrayList<>(Arrays.asList(new Player("pobi"), new Player("sobi"), new Player("aobi"), new Player("bobi"), new Player("xobi"), new Player("dobi"), new Player("eobi"), new Player("fobi")));

    @DisplayName("보장된 인원수 (인원수는 딜러 포함 최소 2명, 최대 8명이다)면 객체 정상 생성된다.")
    @Test
    void userSizeTest() {
        //given
        assertThatCode(() -> new Users(DUMMY_EIGHT_PLAYERS))
                .doesNotThrowAnyException();
    }

    @DisplayName("보장된 인원수 (인원수는 딜러 포함 최소 2명, 최대 8명이다)가 아니면 에러가 발생한다.")
    @Test
    void userSizeExceptionTest() {
        //given
        int minUserSize = 2;
        int maxUserSize = 8;
        AbstractUser dealer = new Dealer();

        assertThatThrownBy(() -> new Users(Collections.singletonList(dealer)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("인원수는 딜러포함 %d명 이상 %d이여하야 합니다. 현재 인원수: %d", minUserSize, maxUserSize, 1));

        DUMMY_EIGHT_PLAYERS.add(dealer);

        assertThatThrownBy(() -> new Users(DUMMY_EIGHT_PLAYERS))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format("인원수는 딜러포함 %d명 이상 %d이여하야 합니다. 현재 인원수: %d", minUserSize, maxUserSize, DUMMY_EIGHT_PLAYERS.size()));
    }
}