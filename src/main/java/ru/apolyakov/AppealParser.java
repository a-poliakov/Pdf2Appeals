package ru.apolyakov;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Парсер для анализа всех страниц PDF-документа и выделения из них данных Заявок
 * Позволит помимо непосредственно текстовых данных о заявке в дальнейшем
 * извлекать также и другие (изображения, поля ввода, таблицы и т.д.) при расширении имеющегося функционала.
 */
public class AppealParser implements IParser<Appeal> {
    private static final AppealState[] states = {
            AppealState.CYPHER,
            AppealState.AUTHOR,
            AppealState.EMAIL,
            AppealState.SUBJECT,
            AppealState.REGION,
            AppealState.DATE,
            AppealState.DESCRIPTION
    };

    /**
     * На основе всех страниц PDF документа получаем набор Заявок
     * @param pages страницы PDF
     * @return
     */
    @Override
    public List<Appeal> parse(List<Page> pages) {
        List<Appeal> result = new ArrayList<>();

        for (Page page : pages) {
            StringBuilder cypher = new StringBuilder();
            StringBuilder email = new StringBuilder();
            StringBuilder subject = new StringBuilder();
            StringBuilder region = new StringBuilder();
            StringBuilder author = new StringBuilder();
            StringBuilder date = new StringBuilder();
            StringBuilder description = new StringBuilder();

            boolean tagStarted = false;
            boolean tagCompleted = false;

            int currentStateIndex = -1;
            AppealState currentState = null;
            AppealState nextState = states[currentStateIndex + 1];

            for (TextRow t : page.getText()) {
                String tString = t.getRun();
//                if (!isLastState(currentState) && isRequiredSwitchState(tString, nextState))
                if (!isLastState(currentState))
                {
                    AppealState state = isRequiredSwitchState(currentState, tString);
                    if (state != null)
                    {
                        currentState = state;
                    }
                    //currentState = nextState;
                    /*if (!isLastState(currentState)) {
                        currentStateIndex++;
                        nextState = states[currentStateIndex + 1];
                    }
                    else
                    {
                        nextState = null;
                    }*/
                }
                if (currentState == null)
                {
                    continue;
                }
                switch (currentState)
                {
                    case CYPHER:
                        cypher.append(processCurrentStateString(currentState, tString));
                        break;
                    case AUTHOR:
                        author.append(processCurrentStateString(currentState, tString));
                        break;
                    case EMAIL:
                        email.append(processCurrentStateString(currentState, tString));
                        break;
                    case SUBJECT:
                        subject.append(processCurrentStateString(currentState, tString));
                        break;
                    case REGION:
                        region.append(processCurrentStateString(currentState, tString));
                        break;
                    case DATE:
                        date.append(processCurrentStateString(currentState, tString));
                        break;
                    case DESCRIPTION:
                        description.append(processCurrentStateString(currentState, tString));
                        break;
                }
            }
            Appeal appeal = proccessAppeal(cypher.toString(), email.toString(), subject.toString(), region.toString(), author.toString(), date.toString(), description.toString(), page.getNumber());
            if (appeal.getCypher() != -1) {
                result.add(appeal);
            }
        }


        return result;
    }

    private Appeal proccessAppeal(String tCypher,
                                  String email,
                                  String subject,
                                  String region,
                                  String author,
                                  String tDate,
                                  String description,
                                  int startPage)
    {
        int cypher;
        try {
            tCypher = tCypher.trim();
            cypher = Integer.valueOf(tCypher);
        }
        catch (Exception e)
        {
            cypher = -1;
        }
        Date date = null;
        try {
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy", new Locale("ru"));
            format.setLenient(false);
            tDate = tDate.trim();
            date = format.parse(tDate);
        } catch (Exception e)
        {
            date = new Date();
        }

        return new Appeal(cypher, email.trim(), subject.trim(), region.trim(), author.trim(), date, description.trim(), startPage);
    }

    private boolean isRequiredSwitchState(String currentString, AppealState nextState)
    {
        return currentString != null && currentString.startsWith(nextState.getStateTitle());
    }

    private AppealState isRequiredSwitchState(AppealState currentState, String currentString)
    {
        for (AppealState state : states)
        {
            if (state.equals(currentState))
            {
                continue;
            }
            if (currentString != null && currentString.startsWith(state.getStateTitle()))
            {
                return state;
            }
        }
        return null;
    }

    private String processCurrentStateString(AppealState state, String value)
    {
        value = value.replace(state.getStateTitle(), "");
        if (value.startsWith(":"))
        {
            value = value.replace(":", "");
        }
        return value;
    }

    private boolean isLastState(AppealState state)
    {
        // а вообще для универсальности лучше так: states.length > 0 && states[states.length-1] != null && states[states.length-1] .equals(state);
        return AppealState.DESCRIPTION.equals(state);
    }
}
