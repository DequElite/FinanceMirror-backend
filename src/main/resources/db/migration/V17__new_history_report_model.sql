CREATE TABLE history_report (
                                id                                         UUID PRIMARY KEY,
                                profile_id                                 UUID NOT NULL,
                                created_at                                 DATE NOT NULL,
                                updated_at                                 DATE NOT NULL,
                                period_start_date                          DATE NOT NULL,
                                period_end_date                            DATE NOT NULL,
                                total_spent                                INTEGER NOT NULL,
                                current_balance                            INTEGER NOT NULL,
                                total_replenishments                       INTEGER NOT NULL,
                                average_daily_spending                     INTEGER NOT NULL,
                                spent_difference                           INTEGER,
                                spent_difference_percent                   INTEGER,
                                replenished_difference                     INTEGER,
                                replenished_difference_percent             INTEGER,
                                balance_difference                         INTEGER,
                                balance_difference_percent                 INTEGER,
                                average_daily_spending_difference          INTEGER,
                                average_daily_spending_difference_percent  INTEGER,

                                CONSTRAINT fk_history_report_profile
                                    FOREIGN KEY (profile_id) REFERENCES profile (id)
                                        ON DELETE CASCADE,

                                CONSTRAINT uc_history_report_profile_period_start
                                    UNIQUE (profile_id, period_start_date)
);