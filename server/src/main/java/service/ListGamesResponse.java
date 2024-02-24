package service;

import java.util.Collection;

public record ListGamesResponse(Collection<GameData> gameData) {
}
