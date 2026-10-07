DROP DATABASE IF EXISTS clinica_medica; -- APAGA O BANCO SE EXISTIR
CREATE DATABASE clinica_medica; -- CRIA NOVAMENTE SE NÃO EXISTIR
USE clinica_medica;

-- 1. Tabela de Usuários (Autenticação)
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    login VARCHAR(100) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL, -- Tamanho expandido para o Hash BCrypt
    perfil ENUM('ADMIN', 'PACIENTE', 'MEDICO') NOT NULL DEFAULT 'PACIENTE', 
    ativo BOOLEAN DEFAULT TRUE
);

-- 2. Tabela de Pacientes (Dados Sensíveis)
CREATE TABLE paciente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT UNIQUE NOT NULL,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(255) UNIQUE NOT NULL, 
    data_nascimento DATE NOT NULL,
    telefone VARCHAR(255) NOT NULL,            
    email VARCHAR(255) UNIQUE NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE RESTRICT 
);

-- 3. Tabela de Especialidades
CREATE TABLE especialidade (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO especialidade (nome) VALUES
    ('Clínica Geral'),
    ('Cardiologia'),
    ('Dermatologia'),
    ('Endocrinologia'),
    ('Ginecologia'),
    ('Neurologia'),
    ('Oftalmologia'),
    ('Ortopedia'),
    ('Pediatria'),
    ('Psiquiatria');

-- 4. Tabela de Médicos
CREATE TABLE medico (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT UNIQUE NOT NULL,   -- Um médico no sistema obrigatoriamente tem um login
    nome VARCHAR(255) NOT NULL,
    crm VARCHAR(50) UNIQUE NOT NULL,
    telefone VARCHAR(255) NOT NULL,            -- Tamanho expandido para Criptografia AES
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- 5. Tabela Intermediária: Médico <-> Especialidade
CREATE TABLE medico_especialidade (
    medico_id INT NOT NULL,
    especialidade_id INT NOT NULL,
    PRIMARY KEY (medico_id, especialidade_id),
    FOREIGN KEY (medico_id) REFERENCES medico(id) ON DELETE CASCADE,
    FOREIGN KEY (especialidade_id) REFERENCES especialidade(id) ON DELETE CASCADE
);

-- 6. Tabela de Agenda (Criando as "Gavetas" de horário)
CREATE TABLE agenda (
    id INT AUTO_INCREMENT PRIMARY KEY,
    medico_id INT NOT NULL,
    data_agenda DATE NOT NULL,
    horario TIME NOT NULL,           -- Ex: 08:00, 08:30, 09:00
    disponivel BOOLEAN DEFAULT TRUE, -- TRUE = Livre para marcar, FALSE = Ocupado
    UNIQUE (medico_id, data_agenda, horario), -- Evita criar dois horários de 09:00 para o mesmo médico no mesmo dia
    FOREIGN KEY (medico_id) REFERENCES medico(id) ON DELETE CASCADE
);

-- 7. Tabela de Consultas (Ocupando a "Gaveta")
CREATE TABLE consulta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    agenda_id INT NOT NULL,          -- O serviço libera novamente o horário quando uma consulta é cancelada.
    paciente_id INT NOT NULL,
    especialidade_id INT NULL, -- Especialidade escolhida para esta consulta
    status ENUM('AGENDADO', 'CANCELADO', 'REALIZADO', 'NÃO COMPARECEU') DEFAULT 'AGENDADO',
    observacoes TEXT,
    FOREIGN KEY (agenda_id) REFERENCES agenda(id) ON DELETE CASCADE,
    FOREIGN KEY (paciente_id) REFERENCES paciente(id) ON DELETE CASCADE,
    FOREIGN KEY (especialidade_id) REFERENCES especialidade(id) ON DELETE RESTRICT
);

-- 8. Tabela de Atendimentos Clínicos (Dados Clínicos Sensíveis)
CREATE TABLE atendimento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    consulta_id INT UNIQUE NOT NULL, -- Relacionamento 1 para 1 com a Consulta
    data_registro DATETIME DEFAULT CURRENT_TIMESTAMP, -- Marca o momento exato em que o médico salvou o prontuário
    diagnostico TEXT,                -- TEXT suporta strings longas do AES em Base64
    prescricao TEXT,                 -- TEXT para Criptografia AES
    observacoes_clinicas TEXT,       -- TEXT para Criptografia AES
    FOREIGN KEY (consulta_id) REFERENCES consulta(id) ON DELETE CASCADE
);
