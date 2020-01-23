package mlg.party.lobby.lobby.id;

import org.springframework.stereotype.Service;

@Service
public class IntegerIdManager implements IIDManager {
    private static int nextId = 1;

    @Override
    public String nextId() {
        return String.valueOf(nextId++);
    }
}
