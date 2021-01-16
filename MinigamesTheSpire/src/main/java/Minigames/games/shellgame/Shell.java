package Minigames.games.shellgame;

import Minigames.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;

import static Minigames.Minigames.makeGamePath;

public class Shell {

    static Texture shellTex = TextureLoader.getTexture(makeGamePath("shells/lagavulinshell.png"));

    public float x;
    public float targetX;
    public float y;
    public float targetY;
    public AbstractCard heldCard;
    public AbstractRelic heldRelic;
    public Hitbox hb;

    public boolean isMoving;
    public boolean yApexReached;

    public float scale = 1F;
    public float targetScale = 1F;
    public float startScale = 1F;

    public float alpha = 0F;
    public float targetAlpha = 1F;
    public float startAlpha = 0F;

    public float startX;
    public float startY;

    public float shellOffsetX;
    public float shellOffsetY;

    public float moveTimer;
    public float startMoveTimer;

    public float moveTimerY;
    public float startMoveTimerY;

    public float relicDrawScale;
    public float targetRelicDrawScale;
    public float relicTransparency;
    public float targetRelicTransparency;

    public animPhase currentPhase = animPhase.NONE;

    public Shell(float x, float y, AbstractCard held) {
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x, y, shellTex.getWidth(), shellTex.getHeight());
        this.heldCard = held;
        heldCard.current_x = heldCard.target_x = Settings.WIDTH / 2F;
        heldCard.current_y = heldCard.target_y = Settings.HEIGHT / 2F;
        heldCard.drawScale = heldCard.targetDrawScale = 1.33F;
        heldCard.targetTransparency = heldCard.transparency = 1F;
    }

    public Shell(float x, float y, AbstractRelic held) {
        this.x = x;
        this.y = y;
        this.hb = new Hitbox(x, y, shellTex.getWidth(), shellTex.getHeight());
        this.heldRelic = held;
        heldRelic.currentX = heldRelic.targetX = Settings.WIDTH / 2F;
        heldRelic.currentY = heldRelic.targetY = Settings.HEIGHT / 2F;
        relicDrawScale = targetRelicDrawScale = 1.5F;
        relicTransparency = targetRelicTransparency = 0F;
    }

    public void grantReward() {
        if (this.heldCard != null) {
            AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(this.heldCard.makeCopy(), (float) Settings.WIDTH / 2.0F, (float) Settings.HEIGHT / 2.0F));
        } else if (this.heldRelic != null) {
            AbstractDungeon.getCurrRoom().spawnRelicAndObtain(Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F, heldRelic.makeCopy());
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(Color.WHITE.cpy());
        if (heldCard != null) {
            heldCard.render(sb);
        }
        if (heldRelic != null) {
            sb.setColor(1F, 1F, 1F, 1F);
            sb.draw(heldRelic.img, heldRelic.currentX, heldRelic.currentY, 64 * relicDrawScale, 64 * relicDrawScale);
        }

        sb.setColor(1F, 1F, 1F, alpha);

        sb.draw(shellTex, x + shellOffsetX, y + shellOffsetY, shellTex.getWidth() * scale, shellTex.getHeight() * scale);

    }

    public void update(float elapsed) {
        hb.update();
        if (heldCard != null){
            heldCard.update();
        }
        if (heldRelic != null){
            heldRelic.update();
        }
        /*
        if (currentPhase != animPhase.REWARDINTRO && currentPhase != animPhase.REWARDMOVETOSPACE && currentPhase != animPhase.NONE) {
            if (this.heldCard != null) {
                this.heldCard.current_x = this.heldCard.target_x = x + (shellTex.getWidth() / 2F);
                this.heldCard.current_y = this.heldCard.target_y = y + (shellTex.getHeight() / 2F);
            } else if (this.heldRelic != null) {
                this.heldRelic.currentX = this.heldRelic.targetX = x + (shellTex.getWidth() / 2F);
                this.heldRelic.currentY = this.heldRelic.targetY = y + (shellTex.getHeight() / 2F);
            }
        }
        */
        switch (currentPhase) {
            case REWARDINTRO: {
                moveTimer += elapsed;
                if (heldCard != null) {
                    heldCard.drawScale = heldCard.targetDrawScale = MathUtils.lerp(ShellGame.cardScaleStart, ShellGame.cardScalePeak, moveTimer / startMoveTimer);
                     heldCard.transparency = heldCard.targetTransparency = MathUtils.lerp(0F, 2F, moveTimer / startMoveTimer);
                    if (moveTimer >= startMoveTimer) {
                        heldCard.drawScale = ShellGame.cardScalePeak;
                        startMoveTimer = 0.3F;
                        moveTimer = 0F;
                        currentPhase = animPhase.REWARDMOVETOSPACE;
                        heldCard.transparency = heldCard.targetTransparency = 1F;
                    }
                } else if (heldRelic != null) {

                }
                break;
            }
            case REWARDMOVETOSPACE: {
                moveTimer += elapsed;
                if (heldCard != null) {
                    heldCard.drawScale = heldCard.targetDrawScale = MathUtils.lerp(ShellGame.cardScalePeak, ShellGame.cardScaleCup, moveTimer / startMoveTimer);
                    // heldCard.transparency = heldCard.targetTransparency = MathUtils.lerp(1F, 0.6F, moveTimer / 0.5F);
                    x = MathUtils.lerp(x, targetX, moveTimer / startMoveTimer);
                    heldCard.current_x = heldCard.target_x = x;
                    if (moveTimer >= startMoveTimer) {
                        heldCard.drawScale = ShellGame.cardScaleCup;
                        heldCard.current_x = heldCard.target_x = targetX;
                    }
                } else if (heldRelic != null) {

                }
                break;
            }
            case SHELLINTRO: {
                if (moveTimerY < startMoveTimerY) {
                    moveTimerY += elapsed;
                    shellOffsetY = MathUtils.lerp(ShellGame.offscreenShellHeight, 0F, moveTimerY / startMoveTimerY);
                    alpha = Math.min(1F, MathUtils.lerp(startAlpha, targetAlpha, moveTimerY / (startMoveTimerY / 2)));
                } else {
                    shellOffsetY = 0F;
                    alpha = 1F;
                }
                break;
            }
            case SWITCHEROO: {
                if (isMoving) {
                    moveTimer += elapsed;
                    moveTimerY += elapsed;

                    scale = MathUtils.lerp(startScale, targetScale, moveTimerY / startMoveTimerY);
                    y = MathUtils.lerp(startY, targetY, moveTimerY / startMoveTimerY);

                    if (!yApexReached) {
                        if (moveTimerY >= startMoveTimerY) {
                            y = targetY;
                            startY = targetY;
                            targetY = ShellGame.yMid;
                            yApexReached = true;
                            scale = targetScale;
                            targetScale = 1F;
                            startScale = scale;
                            moveTimerY -= startMoveTimerY;
                        }
                    }

                    x = MathUtils.lerp(startX, targetX, moveTimer / startMoveTimer);

                    if (moveTimer >= startMoveTimer) {
                        x = targetX;
                        y = ShellGame.yMid;
                        scale = 1F;
                        isMoving = false;
                        ShellGame.receiveSwapComplete();
                    }
                }
                break;
            }
            case WAITINGFORPLAYER: {

                break;
            }
            case SHELLOUTRO: {
                if (moveTimerY < startMoveTimerY) {
                    moveTimerY += Gdx.graphics.getDeltaTime();
                    shellOffsetY = MathUtils.lerp(0F, ShellGame.offscreenShellHeight, moveTimerY / startMoveTimerY);
                    // alpha = MathUtils.lerp(targetAlpha, startAlpha, moveTimerY / startMoveTimerY);
                }
                break;
            }
        }

    }


    public enum animPhase {
        NONE,
        REWARDINTRO,
        REWARDMOVETOSPACE,
        SHELLINTRO,
        SWITCHEROO,
        WAITINGFORPLAYER,
        SHELLOUTRO;

        animPhase() {
        }
    }
}
