package Minigames.games.shellgame;

import Minigames.games.AbstractMinigame;
import Minigames.games.input.bindings.BindingGroup;

public class ShellGame extends AbstractMinigame {

    /*
    So, basically the way this should be coded, I think
    is that we have the 3 shell objects.
    We initialize the rewards on wherever this stuff inits,
    then initialize the shells with those rewards.
    Phase is 0 on setup, which is where we see the rewards, and then the shell x/y move to cover them.
    Then we do phase 1, which is the shuffling part. You watch them shuffle
    and then once they're done shuffling, we make it clear you can click,
    and then that's phase 2. On phase 2, when you click,
    pull up the corresponding shell, and grant the reward in a pretty fashion,
    like how Gremlin Match puts the cards in your deck from the screen. Boom!
     */

    private Shell shell1;
    private Shell shell2;
    private Shell shell3;

    private int chosen;

    private void onClick() {
        switch (phase) {
            case 2:
                if (shell1.hb.hovered) {
                    chosen = 1;
                    phase = 3;
                }
                else if (shell2.hb.hovered) {
                    chosen = 2;
                    phase = 3;
                }
                else if (shell3.hb.hovered) {
                    chosen = 3;
                    phase = 3;
                }
        }
    }

    @Override
    protected BindingGroup getBindings() {
        BindingGroup bindings = new BindingGroup();

        bindings.addMouseBind((x, y, pointer) -> isWithinArea(x, y), (p) -> onClick());
        return bindings;
    }
}
