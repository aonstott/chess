package reqres;

import service.GameData;

import java.util.Collection;

public record ListGamesResponse(Collection<GameData> games) {
}
