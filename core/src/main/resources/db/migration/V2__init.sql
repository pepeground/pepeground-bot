CREATE TABLE IF NOT EXISTS chats(
    id SERIAL PRIMARY KEY NOT NULL,
    telegram_id bigint NOT NULL,
    chat_type smallint NOT NULL,
    random_chance smallint DEFAULT 5 NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    name character varying
);

CREATE INDEX IF NOT EXISTS index_chats_on_telegram_id ON chats USING btree (telegram_id);

CREATE TABLE IF NOT EXISTS pairs (
    id SERIAL PRIMARY KEY NOT NULL,
    chat_id integer NOT NULL,
    first_id integer,
    second_id integer,
    created_at timestamp without time zone NOT NULL
);

CREATE INDEX IF NOT EXISTS index_pairs_on_chat_id ON pairs USING btree (chat_id);
CREATE INDEX IF NOT EXISTS index_pairs_on_first_id ON pairs USING btree (first_id);
CREATE INDEX IF NOT EXISTS index_pairs_on_second_id ON pairs USING btree (second_id);
CREATE UNIQUE INDEX IF NOT EXISTS unique_pair_chat_id_first_id ON pairs USING btree (chat_id, first_id) WHERE (second_id IS NULL);
CREATE UNIQUE INDEX IF NOT EXISTS unique_pair_chat_id_first_id_second_id ON pairs USING btree (chat_id, first_id, second_id);
CREATE UNIQUE INDEX IF NOT EXISTS unique_pair_chat_id_second_id ON pairs USING btree (chat_id, second_id) WHERE (first_id IS NULL);

CREATE TABLE IF NOT EXISTS replies (
    id SERIAL PRIMARY KEY NOT NULL,
    pair_id integer NOT NULL,
    word_id integer,
    count bigint DEFAULT 1 NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS unique_reply_pair_id ON replies USING btree (pair_id) WHERE (word_id IS NULL);
CREATE UNIQUE INDEX IF NOT EXISTS unique_reply_pair_id_word_id ON replies USING btree (pair_id, word_id);

CREATE TABLE IF NOT EXISTS words (
    id SERIAL PRIMARY KEY NOT NULL,
    word character varying NOT NULL
);

CREATE INDEX IF NOT EXISTS index_words_on_word ON words USING btree (word);