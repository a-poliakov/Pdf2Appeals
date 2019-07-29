package ru.apolyakov;

public enum AppealState implements IState{
    CYPHER("cypher", "Шифр"),
    AUTHOR ("author", "Автор"),
    EMAIL ("email", "E-mail"),
    SUBJECT ("subject", "Тема обращения"),
    REGION ("region", "Регион"),
    DATE ("date", "Дата вопроса"),
    DESCRIPTION ("description", "Вопрос");

    private String code;
    private String title;

    private AppealState(String code, String title)
    {
        this.code = code;
        this.title = title;
    }

    @Override
    public String getStateCode() {
        return code;
    }

    @Override
    public String getStateTitle() {
        return title;
    }
}
