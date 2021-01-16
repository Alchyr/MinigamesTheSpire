package Minigames.games.gremlinFlip;

/*
    Game name: Gremlin Flip

    Inspired by Voltorb Flip, a minigame in Pokemon HeartGold and SoulSilver, Gremlin Flip tasks the player with flipping cards to gain gold and avoiding Gremlin Nobs.
    It features a 5x5 grid, containing tiles which give gold, and Gremlin Nobs.

    The first tile flipped gives the player that much gold on the first card, and each subsequent card that is greater than 1 multiplies the total by the number on the card.
    For subsequent tiles that are flipped that have the number 1, 1 is added to the player's gold.
    Flipping a Gremlin Nob causes the play to spill all gold and lose the minigame.

    The player presses left-click to flip tiles, and can press right-click to mark a tile.

 */

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;
import Minigames.games.gremlinFlip.boards.*;
import Minigames.games.gremlinFlip.tiles.AbstractTile;
import Minigames.games.gremlinFlip.tiles.GameTile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.GenericEventDialog;
import com.megacrit.cardcrawl.localization.EventStrings;

import java.util.ArrayList;

import static Minigames.Minigames.makeID;

public class gremlinFlip extends AbstractMinigame {

    public static final String ID = makeID(gremlinFlip.class.getSimpleName());
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    protected static AbstractBoard board;
    protected static ArrayList<AbstractBoard> eligibleBoards = new ArrayList<>();

    public static boolean locked;
    public static boolean failedMinigame;
    public static int goldScore;
    private float countdown = 0.1f;
    private float baseCD = 0.1f;

    private int currentIndexPointer = 0;
    private boolean finished = false;

    private int GOLD = 0;
    private final int GOLD_CLEAR_REWARD = 50;
    private int screenNum = 0;

    public gremlinFlip() {
        super();
        hasInstructionScreen = true;
        hasPostgameScreen = true;

        locked = false;
        failedMinigame = false;
        goldScore = 0;
        board = generateBoard();
        board.init();
        eligibleBoards.clear();
    }

    @Override
    public void initialize() {
        super.initialize();
        phase = 0;
        setScale(getMaxScale());
    }

    @Override
    public String getOption() { return NAME; }

    @Override
    public void setupInstructionScreen(GenericEventDialog event) {
        event.updateBodyText(DESCRIPTIONS[0]);
        event.setDialogOption(OPTIONS[0]);
    }

    @Override
    public void setupPostgameScreen(GenericEventDialog event) {
        GOLD = GOLD_CLEAR_REWARD + gremlinFlip.goldScore;
        if(failedMinigame){
            event.updateBodyText(DESCRIPTIONS[2]);
            event.setDialogOption(OPTIONS[2]);
            screenNum = 0;
        }
        else {
            event.updateBodyText(String.format(DESCRIPTIONS[1], GOLD_CLEAR_REWARD, goldScore));
            event.setDialogOption(String.format(OPTIONS[1], GOLD));
            screenNum = 1;
        }
    }
    @Override
    public boolean postgameButtonPressed(int buttonIndex) {
        if(screenNum == 1){ AbstractDungeon.player.gainGold(GOLD); }
        return super.postgameButtonPressed(buttonIndex);
    }

    @Override
    public void update(float elapsed) {
        super.update(elapsed);
        switch (phase)
        {
            case 0:
                board.update();
                if(locked){ phase = 1; }
                else if(flippedAllCoinTiles()){
                    locked = true;
                    countdown = 1F;
                    phase = 1;
                }
                break;
            case 1:
                board.update();
                countdown -= Gdx.graphics.getDeltaTime();
                if(countdown <= 0f){
                    if(finished) { phase = 2; }
                    AbstractTile currentTile = board.getTile(currentIndexPointer);
                    if(currentTile instanceof GameTile && !((GameTile) currentTile).isFlipped()){ ((GameTile) currentTile).unclickedShowTile(); }
                    if(allTilesFlipped()){
                        countdown = 1F;
                        finished = true;
                    }
                    else { countdown = baseCD; }
                    currentIndexPointer += 1;
                }
                break;
            case 2:
                isDone = true;
                break;
        }
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (phase != 2)
        {
            board.render(sb);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public boolean onKeyDown(int keycode) {
        return false;
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        return bindings;
    }

    public boolean allTilesFlipped(){
        for(AbstractTile t: board.getTiles()){
            if(t instanceof GameTile && !((GameTile) t).isFlipped()){ return false; }
        }
        return true;
    }
    public boolean flippedAllCoinTiles(){
        for(AbstractTile t: board.getTiles()){
            if(t instanceof GameTile && (!((GameTile) t).isFlipped() && !((GameTile) t).isEnemy())){ return false; }
        }
        return true;
    }

    public AbstractBoard generateBoard(){
        eligibleBoards.add(new board1());
        eligibleBoards.add(new board2());
        eligibleBoards.add(new board3());
        eligibleBoards.add(new board4());
        eligibleBoards.add(new board5());
        eligibleBoards.add(new board6());
        return eligibleBoards.get(AbstractDungeon.eventRng.random(eligibleBoards.size() - 1));
    }
}
