package fr.quatrevieux.araknemu.game.exploration.interaction.action;

import fr.quatrevieux.araknemu.game.event.exploration.action.PlayerMoveFinished;
import fr.quatrevieux.araknemu.game.event.exploration.action.PlayerMoving;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.world.map.PathStep;

import java.util.List;

/**
 * Move the player
 */
final public class Move implements BlockingAction {
    final private ExplorationPlayer player;
    private List<PathStep> path;

    private int id;

    public Move(ExplorationPlayer player, List<PathStep> path) {
        this.player = player;
        this.path = path;
    }

    @Override
    public void start() throws Exception {
        if (path.isEmpty()) {
            throw new Exception("Empty path");
        }

        player.map().dispatch(new PlayerMoving(player, this));
    }

    @Override
    public void cancel(String argument) throws Exception {
        if (argument == null) {
            return;
        }

        int cellId = Integer.parseInt(argument);

        for (PathStep step : path) {
            if (step.cell() == cellId) {
                player.move(cellId);

                return;
            }
        }

        throw new Exception("Invalid cell");
    }

    @Override
    public void end() {
        player.move(path.get(path.size() - 1).cell());
        player.map().dispatch(new PlayerMoveFinished(player));
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.MOVE;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {
            player.map().decoder().encodePath(path)
        };
    }

    public List<PathStep> path() {
        return path;
    }

    /**
     * Replace the move path
     */
    public void replace(List<PathStep> path) {
        this.path = path;
    }
}