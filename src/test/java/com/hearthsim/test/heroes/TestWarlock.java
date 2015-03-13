package com.hearthsim.test.heroes;

import com.hearthsim.card.Card;
import com.hearthsim.card.Deck;
import com.hearthsim.card.minion.Hero;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.minion.concrete.BoulderfistOgre;
import com.hearthsim.card.minion.concrete.RaidLeader;
import com.hearthsim.card.minion.heroes.TestHero;
import com.hearthsim.card.minion.heroes.Warlock;
import com.hearthsim.card.spellcard.concrete.TheCoin;
import com.hearthsim.exception.HSException;
import com.hearthsim.model.BoardModel;
import com.hearthsim.model.PlayerModel;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.player.playercontroller.BruteForceSearchAI;
import com.hearthsim.util.tree.CardDrawNode;
import com.hearthsim.util.tree.HearthTreeNode;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestWarlock {

    private HearthTreeNode board;
    private PlayerModel currentPlayer;
    private PlayerModel waitingPlayer;

    @Before
    public void setup() throws HSException {
        board = new HearthTreeNode(new BoardModel(new Warlock(), new TestHero()));
        currentPlayer = board.data_.getCurrentPlayer();
        waitingPlayer = board.data_.getWaitingPlayer();

        board.data_.placeMinion(PlayerSide.CURRENT_PLAYER, new RaidLeader());
        board.data_.placeMinion(PlayerSide.CURRENT_PLAYER, new BoulderfistOgre());

        board.data_.placeMinion(PlayerSide.WAITING_PLAYER, new RaidLeader());
        board.data_.placeMinion(PlayerSide.WAITING_PLAYER, new BoulderfistOgre());

        currentPlayer.setMana((byte) 8);
        waitingPlayer.setMana((byte) 8);
    }

    @Test
    public void testHeropower() throws HSException {
        Hero warrior = currentPlayer.getHero();

        HearthTreeNode ret = warrior.useHeroAbility(PlayerSide.CURRENT_PLAYER, 0, board);
        assertNotEquals(board, ret);
        assertTrue(ret instanceof CardDrawNode);

        assertEquals(currentPlayer.getHand().size(), 0);
        assertEquals(currentPlayer.getMana(), 6);
        assertEquals(currentPlayer.getHero().getHealth(), 28);

        assertEquals(currentPlayer.getHand().size(), 0);
        assertEquals(((CardDrawNode)ret).getNumCardsToDraw(), 1);
        assertEquals(currentPlayer.getMana(), 6);
        assertEquals(currentPlayer.getHero().getHealth(), 28);
    }

    @Test
    public void testHeropowerCannotTargetMinion() throws HSException {
        Hero warrior = currentPlayer.getHero();

        HearthTreeNode ret = warrior.useHeroAbility(PlayerSide.WAITING_PLAYER, 2, board);
        assertNull(ret);

        assertEquals(currentPlayer.getHand().size(), 0);
        assertEquals(currentPlayer.getMana(), 8);
        assertEquals(currentPlayer.getHero().getHealth(), 30);

        assertEquals(currentPlayer.getMinions().get(1).getHealth(), 7);
    }

    @Test
    public void testHeropowerCannotTargetOpponent() throws HSException {
        Hero warrior = currentPlayer.getHero();

        HearthTreeNode ret = warrior.useHeroAbility(PlayerSide.WAITING_PLAYER, 0, board);
        assertNull(ret);

        assertEquals(currentPlayer.getHand().size(), 0);
        assertEquals(currentPlayer.getMana(), 8);
        assertEquals(currentPlayer.getHero().getHealth(), 30);
        assertEquals(waitingPlayer.getMana(), 8);
        assertEquals(waitingPlayer.getHero().getHealth(), 30);
    }

    @Test
    public void testAiCardDrawScore() throws HSException {
        Card cards[] = new Card[30];
        for (int index = 0; index < 30; ++index) {
            cards[index] = new TheCoin();
        }

        Deck deck = new Deck(cards);

        Minion minion = currentPlayer.getMinions().get(0);
        HearthTreeNode ret = minion.attack(PlayerSide.WAITING_PLAYER, 0, board, deck, null, false);
        assertEquals(board, ret);

        currentPlayer.setDeckPos((byte) 30);

        Hero hero = currentPlayer.getHero();
        ret = hero.useHeroAbility(PlayerSide.CURRENT_PLAYER, 0, board);
        assertEquals(currentPlayer.getHand().size(), 0);

        assertTrue(ret instanceof CardDrawNode);
        assertEquals(((CardDrawNode)ret).getNumCardsToDraw(), 1);

        BruteForceSearchAI ai0 = BruteForceSearchAI.buildStandardAI1();
        double cardDrawScore = ((CardDrawNode)ret).cardDrawScore(deck, ai0.scorer);
        assertTrue(cardDrawScore < 0.0);
    }
}
