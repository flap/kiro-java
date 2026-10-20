-- Flyway: migracao versionada V1.
-- Convencao: V<versao>__<descricao>.sql  (dois underscores apos a versao).
-- REGRA DE OURO: uma vez aplicada, NUNCA edite esta migracao. Crie uma nova (V2, V3...).

CREATE TABLE greeting_log (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipient   VARCHAR(100) NOT NULL,
    message     VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_greeting_log_recipient ON greeting_log (recipient);
