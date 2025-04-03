package com.nttn.habit_engine;

import java.util.List;

public interface iIcsFileReader {
    List<Todo> readTodosFromIcs(String filePath);
}
