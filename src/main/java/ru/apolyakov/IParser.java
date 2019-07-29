package ru.apolyakov;

import java.util.List;

/**
 * Парсер для распознаных страниц PDF в нужный нам тип
 */
public interface IParser <T> {
    List<T> parse (List<Page> pages);
}
