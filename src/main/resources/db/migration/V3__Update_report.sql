ALTER TABLE report
    ADD COLUMN spent_percent_of_salary INTEGER NOT NULL DEFAULT 0;

ALTER TABLE report
    ADD COLUMN average_daily_spending INTEGER NOT NULL DEFAULT 0;

ALTER TABLE report
    ADD COLUMN daily_limit INTEGER NOT NULL DEFAULT 0;

ALTER TABLE report
    RENAME COLUMN remains TO report_balance;