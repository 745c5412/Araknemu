package fr.quatrevieux.araknemu.game.event;

/**
 * Dispatch event
 */
public interface Dispatcher {
    /**
     * Dispatch the event to listeners
     *
     * @param event Event to dispatch
     */
    public void dispatch(Object event);
}