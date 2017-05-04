CREATE TABLE IF NOT EXISTS subscriptions(
    id SERIAL PRIMARY KEY NOT NULL,
    chat_id bigint NOT NULL,
    name character varying NOT NULL,
    since_id bigint
);