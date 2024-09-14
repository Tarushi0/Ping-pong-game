package com.example.proj;

import java.util.List;

public interface IGameDAO {
    void upsertGame(Game game);
    Game loadGame(String gameName);

    Game loadLatestGame();

    List<String> getAllGameNames();
}
