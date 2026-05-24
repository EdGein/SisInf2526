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
CREATE OR REPLACE FUNCTION trg_fn_valida_nif()
RETURNS TRIGGER AS $$
DECLARE
    v_soma    INTEGER := 0;
    v_resto   INTEGER;
    v_digito  INTEGER;
    i         INTEGER;
BEGIN
    -- Verificação estrutural: 9 dígitos numéricos
    IF NEW.nif !~ '^[0-9]{9}$' THEN
        RAISE EXCEPTION 'NIF inválido: ''%'' não tem 9 dígitos numéricos.', NEW.nif;
    END IF;

    -- Cálculo do dígito de controlo
    FOR i IN 1..8 LOOP
        v_soma := v_soma + CAST(SUBSTRING(NEW.nif FROM i FOR 1) AS INTEGER) * (10 - i);
    END LOOP;

    v_resto := v_soma % 11;

    IF v_resto = 0 OR v_resto = 1 THEN
        v_digito := 0;
    ELSE
        v_digito := 11 - v_resto;
    END IF;

    -- Comparação com o dígito de controlo fornecido
    IF v_digito <> CAST(SUBSTRING(NEW.nif FROM 9 FOR 1) AS INTEGER) THEN
        RAISE EXCEPTION 'NIF inválido: dígito de controlo incorreto.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_cliente_valida_nif ON cliente;
CREATE TRIGGER trg_cliente_valida_nif
    BEFORE INSERT OR UPDATE OF nif ON cliente
    FOR EACH ROW
    EXECUTE FUNCTION trg_fn_valida_nif();
-- endregion

-- region Question 1.b
-- Validação para Email
CREATE OR REPLACE FUNCTION trg_fn_valida_email_duplicado()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM contacto_email
        WHERE cliente_nif = NEW.cliente_nif
          AND email = NEW.email
          AND contacto_email_id <> COALESCE(NEW.contacto_email_id, -1)
    ) THEN
        RAISE EXCEPTION 'Contacto duplicado: o cliente já possui este email.';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_valida_email_dup ON contacto_email;
CREATE TRIGGER trg_valida_email_dup
    BEFORE INSERT OR UPDATE ON contacto_email
    FOR EACH ROW EXECUTE FUNCTION trg_fn_valida_email_duplicado();

-- Validação para Telefone
CREATE OR REPLACE FUNCTION trg_fn_valida_telefone_duplicado()
RETURNS TRIGGER AS $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM contacto_telefone
        WHERE cliente_nif = NEW.cliente_nif
          AND telefone = NEW.telefone
          AND contacto_telefone_id <> COALESCE(NEW.contacto_telefone_id, -1)
    ) THEN
        RAISE EXCEPTION 'Contacto duplicado: o cliente já possui este telefone.';
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
CREATE OR REPLACE FUNCTION fx_media_movel(days integer, p_instrumento_isin VARCHAR(12))
RETURNS NUMERIC AS $$
DECLARE
    v_media NUMERIC;
BEGIN
    SELECT AVG(valor_fecho)
    INTO v_media
    FROM (
        SELECT valor_fecho
        FROM valor_instrumento_diario
        WHERE instrumento_isin = p_instrumento_isin
        ORDER BY data DESC
        LIMIT days
    ) AS ultimos_n;

    RETURN ROUND(v_media, 2);
END;
$$ LANGUAGE plpgsql;
-- endregion

-- region Question 3
CREATE OR REPLACE FUNCTION fx_portefolio_info(p_portfolio_id BIGINT)
RETURNS TABLE (
    isin              VARCHAR,
    quantidade        NUMERIC,
    valor_atual       NUMERIC,
    perc_variacao     NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        p.instrumento_isin AS isin,
        p.quantidade,
        df.valor_actual AS valor_atual,
        ROUND(
            CASE
                WHEN vd.valor_abertura IS NOT NULL AND vd.valor_abertura <> 0
                THEN ((df.valor_actual - vd.valor_abertura) / vd.valor_abertura) * 100
                ELSE 0
            END,
        2) AS perc_variacao
    FROM posicao p
    JOIN dados_fundamentais df ON p.instrumento_isin = df.instrumento_isin
    LEFT JOIN valor_instrumento_diario vd
        ON p.instrumento_isin = vd.instrumento_isin AND vd.data = CURRENT_DATE
    WHERE p.portefolio = p_portfolio_id;
END;
$$ LANGUAGE plpgsql;
-- endregion

-- region Question 4
CREATE OR REPLACE PROCEDURE p_actualizaValorDiario(
    p_identificador VARCHAR(12),
    p_datatempo     TIMESTAMP,
    p_valor         NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_data DATE := p_datatempo::DATE;
BEGIN
    -- 1. Registar o triplo
    INSERT INTO triplo_externo (identificador, data_tempo, valor)
    VALUES (p_identificador, p_datatempo, p_valor)
    ON CONFLICT DO NOTHING;

    -- 2. Verificar se o instrumento existe
    IF NOT EXISTS (SELECT 1 FROM instrumento WHERE instrumento_id = p_identificador) THEN
        RETURN;
    END IF;

    -- 3. Atualizar registo diário do instrumento
    INSERT INTO valor_instrumento_diario (instrumento_isin, data, valor_minimo, valor_maximo, valor_abertura, valor_fecho)
    VALUES (p_identificador, v_data, p_valor, p_valor, p_valor, p_valor)
    ON CONFLICT (instrumento_isin, data) DO UPDATE
        SET valor_minimo = LEAST(valor_instrumento_diario.valor_minimo, EXCLUDED.valor_minimo),
            valor_maximo = GREATEST(valor_instrumento_diario.valor_maximo, EXCLUDED.valor_maximo),
            valor_fecho  = EXCLUDED.valor_fecho;

    -- 4. Atualizar dados fundamentais
    UPDATE dados_fundamentais
    SET valor_actual = p_valor
    WHERE instrumento_isin = p_identificador;
END;
$$;
-- endregion

-- region Question 5
-- 1.VIEW
CREATE OR REPLACE VIEW contacto_cliente(nif, cartao_cidadao, nome, tipo_contacto, contacto, descricao)
AS
SELECT
    c.nif, c.cartao_cidadao, c.nome,
    'email'::VARCHAR AS tipo_contacto,
    ce.email AS contacto,
    ce.descricao
FROM cliente c
JOIN contacto_email ce ON c.nif = ce.cliente_nif
UNION ALL
SELECT
    c.nif, c.cartao_cidadao, c.nome,
    'telefone'::VARCHAR AS tipo_contacto,
    ct.telefone AS contacto,
    ct.descricao
FROM cliente c
JOIN contacto_telefone ct ON c.nif = ct.cliente_nif;

-- 2. Trigger
CREATE OR REPLACE FUNCTION trg_fn_vw_contacto_cliente_insert()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM cliente WHERE nif = NEW.nif) THEN
        INSERT INTO cliente (nif, cartao_cidadao, nome)
        VALUES (NEW.nif, NEW.cartao_cidadao, NEW.nome);
    END IF;

    IF NEW.tipo_contacto = 'email' THEN
        INSERT INTO contacto_email (cliente_nif, descricao, email)
        VALUES (NEW.nif, NEW.descricao, NEW.contacto);
    ELSIF NEW.tipo_contacto = 'telefone' THEN
        INSERT INTO contacto_telefone (cliente_nif, descricao, telefone)
        VALUES (NEW.nif, NEW.descricao, NEW.contacto);
    ELSE
        RAISE EXCEPTION 'Tipo de contacto inválido. Use "email" ou "telefone".';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 3. Associar o trigger à VIEW (só funciona depois da VIEW existir)
DROP TRIGGER IF EXISTS trg_vw_contacto_cliente_ins ON contacto_cliente;
CREATE TRIGGER trg_vw_contacto_cliente_ins
    INSTEAD OF INSERT ON contacto_cliente
    FOR EACH ROW EXECUTE FUNCTION trg_fn_vw_contacto_cliente_insert();
-- endregion

-- region Other changes
--TODO
-- endregion




