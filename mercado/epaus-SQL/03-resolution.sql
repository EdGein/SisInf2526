/*
 * ISEL-DEI-SisInf
 * ND 2022-2026
 *
 *   
 * Information Systems Project - Active Databases
 * Didactic material to support 
 * the Information Systems course
 * 
 *  * */

/* ### DO NOT CHANGE OR REMOVE THE MARKERS BELOW 
 * ### ONLY WRITE to THE TODO ZONE
 * ### */


-- region Question 1.a

-- Helper: recalcula o total dos portefolios afectados por um instrumento.
CREATE OR REPLACE FUNCTION f_valida_nif()
RETURNS TRIGGER AS $$
DECLARE
    soma INTEGER := 0;
    resto INTEGER;
    digito_controlo INTEGER;
    i INTEGER;
BEGIN
    -- Validar tamanho e caracteres numéricos
    IF NEW.nif NOT SIMILAR TO '[0-9]{9}' THEN
        RAISE EXCEPTION 'NIF inválido: deve conter exatamente 9 dígitos.' USING ERRCODE = '45000';
    END IF;

    -- Validar dígito inicial permitido
    IF SUBSTRING(NEW.nif FROM 1 FOR 1) NOT IN ('1', '2', '3', '5', '6', '8', '9') THEN
        RAISE EXCEPTION 'NIF inválido: dígito inicial não permitido.' USING ERRCODE = '45000';
    END IF;

    -- Cálculo do algoritmo módulo 11
    FOR i IN 1..8 LOOP
        soma := soma + (CAST(SUBSTRING(NEW.nif FROM i FOR 1) AS INTEGER) * (10 - i));
    END LOOP;

    resto := soma % 11;
    IF resto < 2 THEN
        digito_controlo := 0;
    ELSE
        digito_controlo := 11 - resto;
    END IF;

    IF CAST(SUBSTRING(NEW.nif FROM 9 FOR 1) AS INTEGER) <> digito_controlo THEN
        RAISE EXCEPTION 'NIF inválido: dígito de controlo incorreto.' USING ERRCODE = '45000';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_valida_nif
BEFORE INSERT OR UPDATE ON cliente
FOR EACH ROW
EXECUTE FUNCTION f_valida_nif();
-- endregion

-- region Question 1.b
-- -------------------------------------------------------
-- CATEGORIA 1: Validação de Formato
-- -------------------------------------------------------

-- 1a. Validação de formato de email
-- A regex verifica: parte_local @ domínio . extensão (mín. 2 chars)
CREATE OR REPLACE FUNCTION trg_fn_valida_formato_email()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.email !~ '^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$' THEN
        RAISE EXCEPTION 'Email inválido: ''%'' não tem um formato válido (esperado: utilizador@dominio.ext).', NEW.email;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_email_formato ON contacto_email;
CREATE TRIGGER trg_valida_email_formato
    BEFORE INSERT OR UPDATE OF email ON contacto_email
    FOR EACH ROW EXECUTE FUNCTION trg_fn_valida_formato_email();


-- 1b. Validação de formato de telefone
-- -------------------------------------------------------
-- CATEGORIA 1: Validação de Formato
-- -------------------------------------------------------

-- 1a. Validação de formato de email
-- A regex verifica: parte_local @ domínio . extensão (mín. 2 chars)
CREATE OR REPLACE FUNCTION trg_fn_valida_formato_email()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.email !~ '^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$' THEN
        RAISE EXCEPTION 'Email inválido: ''%'' não tem um formato válido (esperado: utilizador@dominio.ext).', NEW.email;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_email_formato ON contacto_email;
CREATE TRIGGER trg_valida_email_formato
    BEFORE INSERT OR UPDATE OF email ON contacto_email
    FOR EACH ROW EXECUTE FUNCTION trg_fn_valida_formato_email();


-- 1b. Validação de formato de telefone
-- Aceita: dígitos, espaços, hífens e '+' no início (formato internacional)
-- Comprimento mínimo: 9 dígitos efectivos
CREATE OR REPLACE FUNCTION trg_fn_valida_formato_telefone()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.telefone !~ '^\+?[0-9\s\-]{9,}$' THEN
        RAISE EXCEPTION 'Telefone inválido: ''%'' não tem um formato válido (mín. 9 dígitos, pode incluir +, espaços e hífens).', NEW.telefone;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_telefone_formato ON contacto_telefone;
CREATE TRIGGER trg_valida_telefone_formato
    BEFORE INSERT OR UPDATE OF telefone ON contacto_telefone
    FOR EACH ROW EXECUTE FUNCTION trg_fn_valida_formato_telefone();


-- -------------------------------------------------------
-- CATEGORIA 2: Validação de Duplicados por Cliente
-- -------------------------------------------------------

-- 2a. Validação de email duplicado por cliente
-- COALESCE(NEW.contacto_email_id, -1) garante que em INSERT (onde o ID ainda
-- não existe) a condição de exclusão não elimina nenhuma linha válida.
CREATE OR REPLACE FUNCTION trg_fn_valida_email_duplicado()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM contacto_email
        WHERE cliente_nif = NEW.cliente_nif
          AND email = NEW.email
          AND contacto_email_id <> COALESCE(NEW.contacto_email_id, -1)
    ) THEN
        RAISE EXCEPTION 'Contacto duplicado: o cliente ''%'' já possui o email ''%''.', NEW.cliente_nif, NEW.email;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_email_dup ON contacto_email;
CREATE TRIGGER trg_valida_email_dup
    BEFORE INSERT OR UPDATE ON contacto_email
    FOR EACH ROW EXECUTE FUNCTION trg_fn_valida_email_duplicado();


-- 2b. Validação de telefone duplicado por cliente
CREATE OR REPLACE FUNCTION trg_fn_valida_telefone_duplicado()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM contacto_telefone
        WHERE cliente_nif = NEW.cliente_nif
          AND telefone = NEW.telefone
          AND contacto_telefone_id <> COALESCE(NEW.contacto_telefone_id, -1)
    ) THEN
        RAISE EXCEPTION 'Contacto duplicado: o cliente ''%'' já possui o telefone ''%''.', NEW.cliente_nif, NEW.telefone;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_telefone_dup ON contacto_telefone;
CREATE TRIGGER trg_valida_telefone_dup
    BEFORE INSERT OR UPDATE ON contacto_telefone
    FOR EACH ROW EXECUTE FUNCTION trg_fn_valida_telefone_duplicado();
-- endregion

-- region Question 2
CREATE OR REPLACE FUNCTION fx_media_movel(days integer, isin VARCHAR(12))
RETURNS NUMERIC AS $$
DECLARE
    media NUMERIC(15,2);
BEGIN
    IF days <= 0 THEN
        RAISE EXCEPTION 'O número de dias para a média móvel deve ser superior a zero.' USING ERRCODE = '22023';
    END IF;

    SELECT ROUND(AVG(valor_fecho), 2) INTO media
    FROM (
        SELECT valor_fecho
        FROM valor_instrumento_diario
        WHERE instrumento_isin = isin
        ORDER BY data DESC
        LIMIT days
    ) subquery;

    RETURN COALESCE(media, 0.00);
END;
$$ LANGUAGE plpgsql;
-- endregion

-- region Question 3
CREATE OR REPLACE FUNCTION fx_portefolio_info(p_id BIGINT)
RETURNS TABLE(
    isin VARCHAR(12),
    quantidade NUMERIC(15,4),
    valor_actual NUMERIC(15,2),
    variacao_percentual NUMERIC(7,2)
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        pos.instrumento_isin,
        pos.quantidade,
        df.valor_actual,
        ROUND(
            CASE
                WHEN fecho_anterior.valor_fecho IS NULL OR fecho_anterior.valor_fecho = 0 THEN 0.00
                ELSE ((df.valor_actual - fecho_anterior.valor_fecho) / fecho_anterior.valor_fecho) * 100
            END,
            2
        ) AS variacao_percentual
    FROM posicao pos
    JOIN dados_fundamentais df ON pos.instrumento_isin = df.instrumento_isin
    LEFT JOIN LATERAL (
        SELECT vid.valor_fecho
        FROM valor_instrumento_diario vid
        WHERE vid.instrumento_isin = pos.instrumento_isin
        ORDER BY vid.data DESC
        LIMIT 1 OFFSET 1 -- Obtém o segundo registo mais recente (dia anterior)
    ) fecho_anterior ON TRUE
    WHERE pos.portefolio = p_id;
END;
$$ LANGUAGE plpgsql;
-- endregion

-- region Question 4

DROP PROCEDURE IF EXISTS p_actualizaValorDiario();

CREATE OR REPLACE PROCEDURE p_actualizaValorDiario() AS $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN (
        SELECT
            identificador                          AS isin,
            data_tempo::DATE                       AS data_dia,
            MIN(valor)                             AS val_min,
            MAX(valor)                             AS val_max,
            MIN(valor) FILTER (WHERE rn_asc  = 1) AS val_abertura,
            MIN(valor) FILTER (WHERE rn_desc = 1) AS val_fecho
        FROM (
            SELECT
                identificador,
                data_tempo,
                valor,
                ROW_NUMBER() OVER (
                    PARTITION BY identificador, data_tempo::DATE
                    ORDER BY data_tempo ASC
                ) AS rn_asc,
                ROW_NUMBER() OVER (
                    PARTITION BY identificador, data_tempo::DATE
                    ORDER BY data_tempo DESC
                ) AS rn_desc
            FROM triplo_externo
            WHERE identificador IN (SELECT instrumento_id FROM instrumento)
        ) ranked
        GROUP BY identificador, data_tempo::DATE
    ) LOOP
        INSERT INTO valor_instrumento_diario
            (instrumento_isin, data, valor_minimo, valor_maximo, valor_abertura, valor_fecho)
        VALUES
            (r.isin, r.data_dia, r.val_min, r.val_max, r.val_abertura, r.val_fecho)
        ON CONFLICT (instrumento_isin, data) DO UPDATE
        SET valor_minimo = LEAST(valor_instrumento_diario.valor_minimo, r.val_min),
            valor_maximo = GREATEST(valor_instrumento_diario.valor_maximo, r.val_max),
            valor_fecho  = r.val_fecho;

        INSERT INTO dados_fundamentais
            (instrumento_isin, variacao_diaria, valor_actual,
             media_6_meses, variacao_6_meses,
             percentagem_variacao_diaria, percentagem_variacao_6_meses)
        VALUES
            (r.isin, r.val_max - r.val_min, r.val_fecho, r.val_fecho, 0, 0, 0)
        ON CONFLICT (instrumento_isin) DO UPDATE
        SET valor_actual    = r.val_fecho,
            variacao_diaria = r.val_max - r.val_min;
    END LOOP;

    INSERT INTO valor_mercado_diario (mercado, data, valor_indice, valor_abertura, variacao_diaria)
    SELECT
        i.mercado,
        vid.data,
        SUM(vid.valor_fecho)    AS valor_indice,
        SUM(vid.valor_abertura) AS valor_abertura,
        0                       AS variacao_diaria
    FROM valor_instrumento_diario vid
    JOIN instrumento i ON vid.instrumento_isin = i.instrumento_id
    GROUP BY i.mercado, vid.data
    ON CONFLICT (mercado, data) DO UPDATE
    SET valor_indice = EXCLUDED.valor_indice;
END;
$$ LANGUAGE plpgsql;

-- endregion

-- endregion

-- region Question 5
-- Criação da Vista Unificada
CREATE OR REPLACE VIEW contacto_cliente AS
SELECT
    c.nif,
    c.cartao_cidadao,
    c.nome,
    'Email'::VARCHAR(10) AS tipo_contacto,
    e.email AS contacto,
    e.descricao,
    e.contacto_email_id AS contacto_id
FROM cliente c
JOIN contacto_email e ON c.nif = e.cliente_nif
UNION ALL
SELECT
    c.nif,
    c.cartao_cidadao,
    c.nome,
    'Telefone'::VARCHAR(10) AS tipo_contacto,
    t.telefone AS contacto,
    t.descricao,
    t.contacto_telefone_id AS contacto_id
FROM cliente c
JOIN contacto_telefone t ON c.nif = t.cliente_nif;

-- Função de Gatilho INSTEAD OF
CREATE OR REPLACE FUNCTION f_dml_contacto_cliente()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' THEN
        -- Garantir que o cliente existe
        IF NOT EXISTS (SELECT 1 FROM cliente WHERE nif = NEW.nif) THEN
            INSERT INTO cliente (nif, cartao_cidadao, nome)
            VALUES (NEW.nif, NEW.cartao_cidadao, NEW.nome);
        END IF;

        -- Inserir na tabela correta dependendo do tipo
        IF NEW.tipo_contacto = 'Email' THEN
            INSERT INTO contacto_email (cliente_nif, descricao, email)
            VALUES (NEW.nif, NEW.descricao, NEW.contacto);
        ELSEIF NEW.tipo_contacto = 'Telefone' THEN
            INSERT INTO contacto_telefone (cliente_nif, descricao, telefone)
            VALUES (NEW.nif, NEW.descricao, NEW.contacto);
        ELSE
            RAISE EXCEPTION 'Tipo de contacto desconhecido: %', NEW.tipo_contacto USING ERRCODE = '45000';
        END IF;
        RETURN NEW;

    ELSEIF TG_OP = 'UPDATE' THEN
        IF OLD.tipo_contacto = 'Email' THEN
            UPDATE contacto_email
            SET email = NEW.contacto, descricao = NEW.descricao
            WHERE contacto_email_id = OLD.contacto_id;
        ELSEIF OLD.tipo_contacto = 'Telefone' THEN
            UPDATE contacto_telefone
            SET telefone = NEW.contacto, descricao = NEW.descricao
            WHERE contacto_telefone_id = OLD.contacto_id;
        END IF;
        RETURN NEW;

    ELSEIF TG_OP = 'DELETE' THEN
        IF OLD.tipo_contacto = 'Email' THEN
            DELETE FROM contacto_email WHERE contacto_email_id = OLD.contacto_id;
        ELSEIF OLD.tipo_contacto = 'Telefone' THEN
            DELETE FROM contacto_telefone WHERE contacto_telefone_id = OLD.contacto_id;
        END IF;
        RETURN OLD;
    END IF;
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_dml_contacto_cliente
INSTEAD OF INSERT OR UPDATE OR DELETE ON contacto_cliente
FOR EACH ROW
EXECUTE FUNCTION f_dml_contacto_cliente();
-- endregion

-- region Other changes
-- Campo necessario para optimistic locking em JPA. O 02-insert-data.sql continua valido porque a coluna tem DEFAULT.
ALTER TABLE cliente ADD COLUMN IF NOT EXISTS versao BIGINT NOT NULL DEFAULT 1;
-- endregion




