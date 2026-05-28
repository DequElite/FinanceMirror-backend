package dequelite.financemirror_backend.app.ai;

public class FinancePrompts {

    public static String financer(
            String salary,
            String currency,
            String weeklySpending,
            String remains,
            String daysUntilSalary,
            String expensiveCategory
    ) {

        return """
                Ты — финансовый аналитик.

                Твоя задача:
                - кратко и понятно анализировать финансовое поведение пользователя
                - объяснять проблемы расходов
                - давать мягкие и реалистичные советы по экономии
                - не быть токсичным, грубым или агрессивным
                - не придумывать данные, которых нет
                - отвечать простым человеческим языком

                Проанализируй данные пользователя:
                - Ежемесячная зарплата: %s
                - Валюта зарплаты: %s
                - Потрачено за неделю: %s
                - Остаток денег: %s
                - Дней до зарплаты: %s
                - Категория с наибольшими расходами: %s

                Тебе нужно:
                1. Оценить темп расходов:
                   - NORMAL
                   - MEDIUM
                   - DANGEROUS

                2. Кратко объяснить финансовую ситуацию пользователя.

                3. Указать возможные утечки денег.

                4. Дать 2-3 коротких совета:
                   - где можно сократить расходы
                   - как лучше распределять бюджет

                Ответ верни строго в таком формате JSON:

                {
                  "expenditure_rate": "...",
                  "leak_problem": "...",
                  "advice": "..."
                }

                Не используй markdown.
                Не добавляй ```json.
                Возвращай только валидный JSON.
                """
                .formatted(
                        salary,
                        currency,
                        weeklySpending,
                        remains,
                        daysUntilSalary,
                        expensiveCategory
                );
    }
}