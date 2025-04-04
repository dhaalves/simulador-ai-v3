-- Script de inserção de dados para ambiente de desenvolvimento

-- Inserir usuários de exemplo
INSERT INTO usuarios (id, nome, cpf, data_nascimento, email, senha, data_ingresso_servico_publico, cargo_atual, sexo) 
VALUES (1, 'João Silva', '12345678901', '1970-05-15', 'joao.silva@exemplo.com', 'senha123', '1995-02-10', 'Analista Administrativo', 'M');

INSERT INTO usuarios (id, nome, cpf, data_nascimento, email, senha, data_ingresso_servico_publico, cargo_atual, sexo) 
VALUES (2, 'Maria Souza', '98765432109', '1975-08-22', 'maria.souza@exemplo.com', 'senha123', '2000-03-15', 'Professora', 'F');

INSERT INTO usuarios (id, nome, cpf, data_nascimento, email, senha, data_ingresso_servico_publico, cargo_atual, sexo) 
VALUES (3, 'Pedro Santos', '45678912307', '1965-03-30', 'pedro.santos@exemplo.com', 'senha123', '1990-01-05', 'Policial Civil', 'M');

-- Inserir períodos de serviço para João Silva
INSERT INTO periodos_servico (id, usuario_id, data_inicio, data_fim, orgao_empregador, tipo_servico, cargo, numero_portaria, tempo_convertido, fator_conversao, insalubridade, concomitante) 
VALUES (1, 1, '1995-02-10', '2005-12-31', 'Secretaria de Administração', 'ESTATUTARIO', 'Assistente Administrativo', 'PORT-123/1995', false, null, false, false);

INSERT INTO periodos_servico (id, usuario_id, data_inicio, data_fim, orgao_empregador, tipo_servico, cargo, numero_portaria, tempo_convertido, fator_conversao, insalubridade, concomitante) 
VALUES (2, 1, '2006-01-01', '2023-12-31', 'Secretaria de Administração', 'ESTATUTARIO', 'Analista Administrativo', 'PORT-456/2006', false, null, false, false);

INSERT INTO periodos_servico (id, usuario_id, data_inicio, data_fim, orgao_empregador, tipo_servico, cargo, numero_portaria, tempo_convertido, fator_conversao, insalubridade, concomitante) 
VALUES (3, 1, '1990-01-15', '1995-01-31', 'Empresa Privada XYZ', 'CLT', 'Auxiliar Administrativo', 'CTC-789/2010', false, null, false, false);

-- Inserir períodos de serviço para Maria Souza
INSERT INTO periodos_servico (id, usuario_id, data_inicio, data_fim, orgao_empregador, tipo_servico, cargo, numero_portaria, tempo_convertido, fator_conversao, insalubridade, concomitante) 
VALUES (4, 2, '2000-03-15', '2023-12-31', 'Secretaria de Educação', 'MAGISTERIO', 'Professora', 'PORT-234/2000', false, null, false, false);

INSERT INTO periodos_servico (id, usuario_id, data_inicio, data_fim, orgao_empregador, tipo_servico, cargo, numero_portaria, tempo_convertido, fator_conversao, insalubridade, concomitante) 
VALUES (5, 2, '1998-02-01', '2000-03-14', 'Escola Municipal XYZ', 'MAGISTERIO', 'Professora', 'CTC-345/2005', false, null, false, false);

-- Inserir períodos de serviço para Pedro Santos
INSERT INTO periodos_servico (id, usuario_id, data_inicio, data_fim, orgao_empregador, tipo_servico, cargo, numero_portaria, tempo_convertido, fator_conversao, insalubridade, concomitante) 
VALUES (6, 3, '1990-01-05', '2000-12-31', 'Secretaria de Segurança', 'ESTATUTARIO', 'Agente de Polícia', 'PORT-567/1990', false, null, false, false);

INSERT INTO periodos_servico (id, usuario_id, data_inicio, data_fim, orgao_empregador, tipo_servico, cargo, numero_portaria, tempo_convertido, fator_conversao, insalubridade, concomitante) 
VALUES (7, 3, '2001-01-01', '2023-12-31', 'Secretaria de Segurança', 'ESTATUTARIO', 'Investigador', 'PORT-678/2001', false, null, true, false);

INSERT INTO periodos_servico (id, usuario_id, data_inicio, data_fim, orgao_empregador, tipo_servico, cargo, numero_portaria, tempo_convertido, fator_conversao, insalubridade, concomitante) 
VALUES (8, 3, '1988-01-10', '1989-12-31', 'Exército Brasileiro', 'SERVICO_MILITAR', 'Soldado', 'CTC-890/2010', false, null, false, false);

-- Inserir algumas simulações de exemplo
INSERT INTO simulacoes (id, usuario_id, data_simulacao, nome_simulacao, regra_aposentadoria, tempo_contribuicao_anos, tempo_contribuicao_meses, tempo_contribuicao_dias, tempo_servico_publico_anos, tempo_servico_publico_meses, tempo_servico_publico_dias, tempo_cargo_anos, tempo_cargo_meses, tempo_cargo_dias, data_previsao_aposentadoria, idade_aposentadoria, pontuacao, percentual_beneficio, elegivel, valor_beneficio_estimado, observacoes)
VALUES (1, 1, '2023-08-10 14:30:00', 'Simulação Regra Permanente', 'REGRA_PERMANENTE', 33, 10, 15, 28, 10, 21, 17, 11, 30, '2025-05-15', 55, 89, 86.0, false, 4500.0, 'Faltam alguns anos para atingir idade mínima');

INSERT INTO simulacoes (id, usuario_id, data_simulacao, nome_simulacao, regra_aposentadoria, tempo_contribuicao_anos, tempo_contribuicao_meses, tempo_contribuicao_dias, tempo_servico_publico_anos, tempo_servico_publico_meses, tempo_servico_publico_dias, tempo_cargo_anos, tempo_cargo_meses, tempo_cargo_dias, data_previsao_aposentadoria, idade_aposentadoria, pontuacao, percentual_beneficio, elegivel, valor_beneficio_estimado, observacoes)
VALUES (2, 2, '2023-08-11 10:15:00', 'Simulação Professor', 'REGRA_ESPECIAL_PROFESSOR', 25, 6, 10, 23, 9, 16, 23, 9, 16, '2023-09-01', 48, 73, 71.0, true, 3800.0, 'Elegível para aposentadoria especial de professor');

INSERT INTO simulacoes (id, usuario_id, data_simulacao, nome_simulacao, regra_aposentadoria, tempo_contribuicao_anos, tempo_contribuicao_meses, tempo_contribuicao_dias, tempo_servico_publico_anos, tempo_servico_publico_meses, tempo_servico_publico_dias, tempo_cargo_anos, tempo_cargo_meses, tempo_cargo_dias, data_previsao_aposentadoria, idade_aposentadoria, pontuacao, percentual_beneficio, elegivel, valor_beneficio_estimado, observacoes)
VALUES (3, 3, '2023-08-12 16:45:00', 'Simulação Policial', 'REGRA_ESPECIAL_POLICIAL', 35, 11, 25, 33, 11, 26, 22, 11, 30, '2023-08-15', 58, 94, 91.0, true, 6200.0, 'Elegível para aposentadoria especial de policial');

-- Reset das sequências conforme os IDs inseridos
ALTER SEQUENCE usuarios_seq RESTART WITH 4;
ALTER SEQUENCE periodos_servico_seq RESTART WITH 9;
ALTER SEQUENCE simulacoes_seq RESTART WITH 4;
