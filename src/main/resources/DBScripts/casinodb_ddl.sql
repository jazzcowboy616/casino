SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

/* create table players and its constraints */
CREATE TABLE public.t_players (
      id bigint NOT NULL,
      birth_date date NOT NULL,
      name character varying(255) NOT NULL,
      password character varying(255) NOT NULL,
      username character varying(100) NOT NULL,
      balance numeric(16,2) DEFAULT 100.00 NOT NULL,
      last_login_time timestamp(6) without time zone,
      online boolean DEFAULT false NOT NULL
);

ALTER TABLE public.t_players ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.t_players_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    );

ALTER TABLE ONLY public.t_players
    ADD CONSTRAINT uk_player_username UNIQUE (username);

ALTER TABLE ONLY public.t_players
    ADD CONSTRAINT t_players_pkey PRIMARY KEY (id);

/* create table games and its constraints */
CREATE TABLE public.t_games (
    id bigint NOT NULL,
    description character varying(255),
    max_bet numeric(38,2),
    min_bet numeric(38,2),
    name character varying(255) NOT NULL,
    win_multiplier double precision NOT NULL,
    win_rate double precision NOT NULL
);

ALTER TABLE public.t_games ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.t_games_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    );

ALTER TABLE ONLY public.t_games
    ADD CONSTRAINT t_games_pkey PRIMARY KEY (id);


/* create table rounds and its constraints */
CREATE TABLE public.t_rounds (
     id bigint NOT NULL,
     is_settled boolean NOT NULL,
     game_id bigint NOT NULL
);

ALTER TABLE public.t_rounds ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.t_rounds_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    );

ALTER TABLE ONLY public.t_rounds
    ADD CONSTRAINT t_rounds_pkey PRIMARY KEY (id);

/* create table bets and its constraints */
CREATE TABLE public.t_bets (
   id bigint NOT NULL,
   amount numeric(8,2) NOT NULL,
   bet_at timestamp(6) without time zone NOT NULL,
   win boolean,
   win_amount numeric(8,2) DEFAULT 0.00,
   player_id bigint NOT NULL,
   round_id bigint NOT NULL
);


ALTER TABLE public.t_bets OWNER TO postgres;

ALTER TABLE public.t_bets ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME public.t_bets_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
    );

ALTER TABLE ONLY public.t_bets
    ADD CONSTRAINT t_bets_pkey PRIMARY KEY (id);


ALTER TABLE ONLY public.t_rounds
    ADD CONSTRAINT fk_gameid_game_id FOREIGN KEY (game_id) REFERENCES public.t_games(id);


ALTER TABLE ONLY public.t_bets
    ADD CONSTRAINT fk_roundid_round_id FOREIGN KEY (round_id) REFERENCES public.t_rounds(id);


ALTER TABLE ONLY public.t_bets
    ADD CONSTRAINT fk_playerid_player_id FOREIGN KEY (player_id) REFERENCES public.t_players(id);