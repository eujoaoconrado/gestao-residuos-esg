-- =====================================================================
-- V2: Dados iniciais para testes (empresas, pontos, residuos, coletas)
-- Os usuarios admin/user sao criados pelo DataSeeder em runtime
-- =====================================================================

-- Empresas
INSERT INTO EMPRESAS (NOME, CNPJ, ENDERECO, TELEFONE, DATA_CADASTRO)
VALUES ('EcoBrasil Reciclagem LTDA', '12.345.678/0001-90', 'Av. Paulista, 1000 - Sao Paulo/SP', '(11) 4002-8922', CURRENT_TIMESTAMP);

INSERT INTO EMPRESAS (NOME, CNPJ, ENDERECO, TELEFONE, DATA_CADASTRO)
VALUES ('Verde Coleta Seletiva ME', '98.765.432/0001-10', 'Rua das Flores, 250 - Campinas/SP', '(19) 3232-1010', CURRENT_TIMESTAMP);

-- Pontos de coleta (3 pontos distribuidos entre as duas empresas)
INSERT INTO PONTOS_COLETA (NOME, ENDERECO, TIPO_RESIDUO, CAPACIDADE_MAXIMA_KG, NIVEL_ATUAL_KG, ID_EMPRESA)
VALUES ('Ecoponto Centro', 'Rua XV de Novembro, 100 - Centro', 'PLASTICO', 500.00, 425.50, 1);

INSERT INTO PONTOS_COLETA (NOME, ENDERECO, TIPO_RESIDUO, CAPACIDADE_MAXIMA_KG, NIVEL_ATUAL_KG, ID_EMPRESA)
VALUES ('Ecoponto Norte', 'Av. Brasil, 450 - Bairro Norte', 'PAPEL', 300.00, 120.00, 1);

INSERT INTO PONTOS_COLETA (NOME, ENDERECO, TIPO_RESIDUO, CAPACIDADE_MAXIMA_KG, NIVEL_ATUAL_KG, ID_EMPRESA)
VALUES ('Ecoponto Sul', 'Rua das Palmeiras, 22 - Bairro Sul', 'VIDRO', 400.00, 380.00, 2);

INSERT INTO PONTOS_COLETA (NOME, ENDERECO, TIPO_RESIDUO, CAPACIDADE_MAXIMA_KG, NIVEL_ATUAL_KG, ID_EMPRESA)
VALUES ('Ecoponto Leste', 'Rua XV de Novembro, 77 - Leste', 'METAL', 600.00, 50.00, 2);

-- Residuos registrados em alguns pontos
INSERT INTO RESIDUOS (TIPO, DESCRICAO, PESO_KG, DATA_COLETA, ID_PONTO)
VALUES ('PLASTICO', 'Garrafas PET recolhidas em escolas', 50.50, CURRENT_TIMESTAMP - INTERVAL '5 days', 1);

INSERT INTO RESIDUOS (TIPO, DESCRICAO, PESO_KG, DATA_COLETA, ID_PONTO)
VALUES ('PLASTICO', 'Embalagens flexiveis de mercado', 75.00, CURRENT_TIMESTAMP - INTERVAL '3 days', 1);

INSERT INTO RESIDUOS (TIPO, DESCRICAO, PESO_KG, DATA_COLETA, ID_PONTO)
VALUES ('PAPEL', 'Caixas de papelao do comercio local', 40.00, CURRENT_TIMESTAMP - INTERVAL '2 days', 2);

INSERT INTO RESIDUOS (TIPO, DESCRICAO, PESO_KG, DATA_COLETA, ID_PONTO)
VALUES ('VIDRO', 'Garrafas e potes de vidro variados', 80.00, CURRENT_TIMESTAMP - INTERVAL '1 day', 3);

INSERT INTO RESIDUOS (TIPO, DESCRICAO, PESO_KG, DATA_COLETA, ID_PONTO)
VALUES ('METAL', 'Latas de aluminio', 25.00, CURRENT_TIMESTAMP, 4);

-- Coletas em diferentes estados
INSERT INTO COLETAS (DATA_COLETA, PESO_TOTAL_KG, STATUS, ID_PONTO, OBSERVACOES)
VALUES (CURRENT_TIMESTAMP - INTERVAL '10 days', 320.00, 'REALIZADA', 1, 'Coleta normal de PET no ecoponto centro');

INSERT INTO COLETAS (DATA_COLETA, PESO_TOTAL_KG, STATUS, ID_PONTO, OBSERVACOES)
VALUES (CURRENT_TIMESTAMP - INTERVAL '7 days', 180.50, 'REALIZADA', 2, 'Papelao coletado e enviado a recicladora parceira');

INSERT INTO COLETAS (DATA_COLETA, PESO_TOTAL_KG, STATUS, ID_PONTO, OBSERVACOES)
VALUES (CURRENT_TIMESTAMP + INTERVAL '2 days', 0, 'AGENDADA', 3, 'Coleta urgente, ecoponto sul perto da capacidade maxima');

INSERT INTO COLETAS (DATA_COLETA, PESO_TOTAL_KG, STATUS, ID_PONTO, OBSERVACOES)
VALUES (CURRENT_TIMESTAMP + INTERVAL '5 days', 0, 'AGENDADA', 1, 'Coleta semanal regular');

INSERT INTO COLETAS (DATA_COLETA, PESO_TOTAL_KG, STATUS, ID_PONTO, OBSERVACOES)
VALUES (CURRENT_TIMESTAMP - INTERVAL '1 day', 0, 'CANCELADA', 4, 'Coleta cancelada por falta de motorista');
