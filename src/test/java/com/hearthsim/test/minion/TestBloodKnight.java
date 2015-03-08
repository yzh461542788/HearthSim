package com.hearthsim.test.minion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.hearthsim.model.PlayerModel;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.hearthsim.card.Card;
import com.hearthsim.card.minion.Minion;
import com.hearthsim.card.minion.concrete.BloodKnight;
import com.hearthsim.card.minion.concrete.ScarletCrusader;
import com.hearthsim.exception.HSException;
import com.hearthsim.exception.HSInvalidPlayerIndexException;
import com.hearthsim.model.BoardModel;
import com.hearthsim.model.PlayerSide;
import com.hearthsim.util.tree.HearthTreeNode;

public class TestBloodKnight {

    private HearthTreeNode board;

    @Before
    public void setup() throws HSInvalidPlayerIndexException, HSException {
        board = new HearthTreeNode(new BoardModel());

        Minion minion0_0 = new ScarletCrusader();
        Minion minion1_0 = new ScarletCrusader();

        board.data_.getCurrentPlayer().placeCardHand(new BloodKnight());

        board.data_.getCurrentPlayer().setMana((byte)18);
        board.data_.getWaitingPlayer().setMana((byte)18);

        board.data_.getCurrentPlayer().setMaxMana((byte)8);
        board.data_.getWaitingPlayer().setMaxMana((byte)8);

        minion0_0.summonMinion(PlayerSide.CURRENT_PLAYER, board.data_.modelForSide(PlayerSide.CURRENT_PLAYER).getHero(), board, null, null, false, true);
        minion1_0.summonMinion(PlayerSide.WAITING_PLAYER, board.data_.modelForSide(PlayerSide.WAITING_PLAYER).getHero(), board, null, null, false, true);
    }

    @Test
    public void testStealsDivineShieldMultiple() throws HSException {
        Minion target = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER).getCharacter(1);
        Card theCard = board.data_.getCurrentPlayer().getHand().get(0);
        HearthTreeNode ret = theCard.useOn(PlayerSide.CURRENT_PLAYER, target, board, null, null);

        assertEquals(board, ret);
        PlayerModel currentPlayer = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER);
        PlayerModel waitingPlayer = board.data_.modelForSide(PlayerSide.WAITING_PLAYER);

        assertEquals(board.data_.getCurrentPlayer().getHand().size(), 0);
        assertEquals(currentPlayer.getNumMinions(), 2);
        assertEquals(board.data_.getCurrentPlayer().getMana(), 15);

        assertEquals(currentPlayer.getMinions().get(1).getTotalHealth(), 9);
        assertEquals(currentPlayer.getMinions().get(1).getTotalAttack(), 9);

        assertFalse(currentPlayer.getMinions().get(0).getDivineShield());
        assertFalse(waitingPlayer.getMinions().get(0).getDivineShield());
    }

    @Test
    public void testStealsDivineShieldSingle() throws HSException {
        board.data_.modelForSide(PlayerSide.CURRENT_PLAYER).getCharacter(1).setDivineShield(false);
        Minion target = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER).getCharacter(1);
        Card theCard = board.data_.getCurrentPlayer().getHand().get(0);
        HearthTreeNode ret = theCard.useOn(PlayerSide.CURRENT_PLAYER, target, board, null, null);

        assertEquals(board, ret);
        PlayerModel currentPlayer = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER);
        PlayerModel waitingPlayer = board.data_.modelForSide(PlayerSide.WAITING_PLAYER);

        assertEquals(board.data_.getCurrentPlayer().getHand().size(), 0);
        assertEquals(currentPlayer.getNumMinions(), 2);
        assertEquals(board.data_.getCurrentPlayer().getMana(), 15);

        assertEquals(currentPlayer.getMinions().get(1).getTotalHealth(), 6);
        assertEquals(currentPlayer.getMinions().get(1).getTotalAttack(), 6);

        assertFalse(currentPlayer.getMinions().get(0).getDivineShield());
        assertFalse(waitingPlayer.getMinions().get(0).getDivineShield());
    }

    @Test
    public void testNoDivineShields() throws HSException {
        board.data_.modelForSide(PlayerSide.CURRENT_PLAYER).getCharacter(1).setDivineShield(false);
        board.data_.modelForSide(PlayerSide.WAITING_PLAYER).getCharacter(1).setDivineShield(false);
        Card theCard = board.data_.getCurrentPlayer().getHand().get(0);
        HearthTreeNode ret = theCard.useOn(PlayerSide.CURRENT_PLAYER, 1, board, null, null);

        assertEquals(board, ret);
        PlayerModel currentPlayer = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER);
        PlayerModel waitingPlayer = board.data_.modelForSide(PlayerSide.WAITING_PLAYER);

        assertEquals(board.data_.getCurrentPlayer().getHand().size(), 0);
        assertEquals(currentPlayer.getNumMinions(), 2);
        assertEquals(board.data_.getCurrentPlayer().getMana(), 15);

        assertEquals(currentPlayer.getMinions().get(1).getTotalHealth(), 3);
        assertEquals(currentPlayer.getMinions().get(1).getTotalAttack(), 3);

        assertFalse(currentPlayer.getMinions().get(0).getDivineShield());
        assertFalse(waitingPlayer.getMinions().get(0).getDivineShield());
    }

    @Test
    @Ignore("Existing bug")
    public void testSilenced() throws HSException {
        Card theCard = board.data_.getCurrentPlayer().getHand().get(0);
        HearthTreeNode ret = theCard.useOn(PlayerSide.CURRENT_PLAYER, 1, board, null, null);
        assertEquals(board, ret);

        PlayerModel currentPlayer = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER);
        PlayerModel waitingPlayer = board.data_.modelForSide(PlayerSide.WAITING_PLAYER);

        Minion target = board.data_.modelForSide(PlayerSide.CURRENT_PLAYER).getCharacter(2);
        target.silenced(PlayerSide.CURRENT_PLAYER, board);

        assertEquals(currentPlayer.getMinions().get(1).getTotalHealth(), 3);
        assertEquals(currentPlayer.getMinions().get(1).getTotalAttack(), 3);

        assertFalse(currentPlayer.getMinions().get(0).getDivineShield());
        assertFalse(waitingPlayer.getMinions().get(0).getDivineShield());
    }
}
