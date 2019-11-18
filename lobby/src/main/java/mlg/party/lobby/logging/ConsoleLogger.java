package mlg.party.lobby.logging;

import org.springframework.stereotype.Service;

@Service
public class ConsoleLogger implements ILogger {
    @Override
    public void log(String callerName, String message) {
        System.out.println(String.format("[%s]: %s", callerName, message));
    }

    @Override
    public void error(String callerName, String message) {
        System.out.println(String.format("[%s]: ERROR %s", callerName, message));
    }
}
