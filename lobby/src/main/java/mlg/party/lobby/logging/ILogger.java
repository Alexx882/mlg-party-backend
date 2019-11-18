package mlg.party.lobby.logging;

public interface ILogger {
    default void log(Object caller, String message) {
        log(caller.getClass().getName(), message);
    }

    default void error(Object caller, String message) {
        error(caller.getClass().getName(), message);
    }

    void log(String caller, String message);

    void error(String caller, String message);
}
