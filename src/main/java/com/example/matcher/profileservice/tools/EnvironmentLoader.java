package com.example.matcher.profileservice.tools;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Optional;

public class EnvironmentLoader {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing() // Игнорировать, если .env файл отсутствует
            .load();

    /**
     * Получить значение переменной. Сначала смотрит в системных переменных,
     * затем в .env файле, иначе возвращает null.
     *
     * @param key ключ переменной
     * @return значение переменной или null
     */
    public static String get(String key) {
        return Optional.ofNullable(System.getenv(key)) // Проверка системных переменных
                .or(() -> Optional.ofNullable(dotenv.get(key))) // Проверка .env
                .orElse(null); // Если ничего не найдено, вернуть null
    }

    public static String get(String key, String defaultValue) {
        return Optional.ofNullable(System.getenv(key)) // Проверка системных переменных
                .or(() -> Optional.ofNullable(dotenv.get(key))) // Проверка .env
                .orElse(defaultValue); // Если ничего не найдено, вернуть значение по умолчанию
    }
}

