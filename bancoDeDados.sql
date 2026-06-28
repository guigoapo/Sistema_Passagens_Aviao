DROP DATABASE IF EXISTS sistema_passagens;

CREATE DATABASE sistema_passagens;
USE sistema_passagens;

CREATE TABLE funcionarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    matricula VARCHAR(20) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL 
);

CREATE TABLE passageiros (
    nome VARCHAR(100) NOT NULL,
    cpf VARCHAR(50) NOT NULL PRIMARY KEY,
    telefone VARCHAR(100) NOT NULL,
    dataNascimento VARCHAR(100) NOT NULL
);

CREATE TABLE aeroportos (
    codigo CHAR(3) PRIMARY KEY,
    cidade VARCHAR(50) NOT NULL,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE distancias (
    origem_cod CHAR(3),
    destino_cod CHAR(3),
    milhas INT NOT NULL,
    PRIMARY KEY (origem_cod, destino_cod),
    FOREIGN KEY (origem_cod) REFERENCES aeroportos(codigo),
    FOREIGN KEY (destino_cod) REFERENCES aeroportos(codigo)
);

CREATE TABLE voos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_voo VARCHAR(10) UNIQUE NOT NULL,
    origem_cod CHAR(3) NOT NULL,
    destino_cod CHAR(3) NOT NULL,
    data_hora_partida DATETIME DEFAULT CURRENT_TIMESTAMP, -- Ajustado para evitar erro se não passar data
    portao_embarque VARCHAR(10), 
    total_assentos INT DEFAULT 100,
    FOREIGN KEY (origem_cod) REFERENCES aeroportos(codigo),
    FOREIGN KEY (destino_cod) REFERENCES aeroportos(codigo)
);

CREATE TABLE assentos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    voo_id INT NOT NULL,
    numero_assento VARCHAR(5) NOT NULL, 
    esta_ocupado BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (voo_id) REFERENCES voos(id),
    UNIQUE(voo_id, numero_assento) 
);

CREATE TABLE vendas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numero_eticket VARCHAR(50) UNIQUE NOT NULL,
    data_venda DATETIME DEFAULT CURRENT_TIMESTAMP,
    voo_id INT NOT NULL,
    assento_id INT NOT NULL,
    nome_passageiro VARCHAR(100) NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    
    -- Alterado de ENUM para VARCHAR para evitar erro de "Data Truncated" com acentos
    forma_pagamento VARCHAR(100) NOT NULL, 
    
    funcionario_id INT, 
    
    -- Nova coluna para armazenar o PDF
    pdf_arquivo LONGBLOB,

    FOREIGN KEY (voo_id) REFERENCES voos(id),
    FOREIGN KEY (assento_id) REFERENCES assentos(id),
    FOREIGN KEY (funcionario_id) REFERENCES funcionarios(id)
);

-- Inserção dos Aeroportos Padrão
INSERT INTO aeroportos (codigo, cidade, nome) VALUES 
('FLN', 'Florianópolis', 'Hercílio Luz'),
('CGH', 'São Paulo', 'Congonhas'),
('CNF', 'Belo Horizonte', 'Confins'),
('VIX', 'Vitória', 'Eurico de Aguiar Salles'),
('FOR', 'Fortaleza', 'Pinto Martins'),
('BSB', 'Brasília', 'Pres. Juscelino Kubitschek');

-- Inserção das Distâncias Padrão
INSERT INTO distancias (origem_cod, destino_cod, milhas) VALUES 
('FLN', 'CGH', 304),
('CGH', 'CNF', 305),
('CGH', 'VIX', 464),
('CGH', 'FOR', 464),
('CGH', 'BSB', 541),
('FLN', 'BSB', 816);