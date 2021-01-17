package Minigames.events;

import Minigames.games.AbstractMinigame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.events.GenericEventDialog;

public abstract class AbstractMinigameEvent extends AbstractImageEvent {
    public static AbstractMinigame game; //should never be more than one. Also lets you make sure it is disposed.

    public AbstractMinigameEvent(String title, String body, String imgUrl) {
        super(title, body, imgUrl);

        if (game != null)
        {
            game.dispose(); //player quit in middle of a minigame, dispose the old one.
        }
    }

    protected void startGame(AbstractMinigame newGame)
    {
        CardCrawlGame.music.playTempBgmInstantly("minigames:carnivalMusic", true);
        this.imageEventText.clearAllDialogs();
        GenericEventDialog.hide();

        game = newGame;
        game.initialize();
    }

    public void update() {
        if (game != null && game.playing())
        {
            game.update(Gdx.graphics.getRawDeltaTime()); //no superfast mode shenangnagiagngas

            if (game.gameDone())
            {
                game.dispose();
                game = null;
                finishGame();
            }
        }
        super.update();
    }

    @Override
    public void render(SpriteBatch sb) {
        if (game != null && game.playing())
        {
            game.render(sb);
        }
    }

    public void finishGame() {
        GenericEventDialog.show();
    }

    public void endOfEvent() {
        CardCrawlGame.music.fadeOutTempBGM();
        this.imageEventText.clearAllDialogs();

        this.imageEventText.updateBodyText("hmmmm");
        this.imageEventText.setDialogOption("I guess it's over?");
    }
}
