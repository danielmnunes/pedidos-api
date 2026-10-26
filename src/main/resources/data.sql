INSERT OR IGNORE INTO clientes (id, nome, email, vip, corporativo, bloqueado, data_cadastro) VALUES
    (1, 'Ana Ferreira', 'ana.ferreira@pedidosnet.com.br', 1, 0, 0, '2024-03-10T00:00:00'),
    (2, 'Bruno Salles', 'bruno.salles@pedidosnet.com.br', 0, 1, 0, '2024-06-22T00:00:00'),
    (3, 'Carla Nunes', 'carla.nunes@pedidosnet.com.br', 0, 0, 0, '2025-01-05T00:00:00'),
    (4, 'Danilo Reis', 'danilo.reis@pedidosnet.com.br', 0, 0, 1, '2023-11-30T00:00:00');

INSERT OR IGNORE INTO produtos (id, nome, categoria, preco, estoque) VALUES
    (1, 'Notebook Pro 15', 'Informática', 6500, 42),
    (2, 'Mouse sem fio', 'Periféricos', 90, 150),
    (3, 'Teclado Mecânico', 'Periféricos', 350, 80),
    (4, 'Monitor 27 144Hz', 'Informática', 1900, 25),
    (5, 'Webcam Full HD', 'Periféricos', 260, 60),
    (6, 'Headset Gamer', 'Áudio', 430, 40);
