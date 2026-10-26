INSERT OR IGNORE INTO clientes (id, nome, email, vip, corporativo, bloqueado, data_cadastro) VALUES
    (1, 'Ana Ferreira', 'ana.ferreira@pedidosnet.com.br', 1, 0, 0, '2024-03-10T00:00:00'),
    (2, 'Bruno Salles', 'bruno.salles@pedidosnet.com.br', 0, 1, 0, '2024-06-22T00:00:00'),
    (3, 'Carla Nunes', 'carla.nunes@pedidosnet.com.br', 0, 0, 0, '2025-01-05T00:00:00'),
    (4, 'Danilo Reis', 'danilo.reis@pedidosnet.com.br', 0, 0, 1, '2023-11-30T00:00:00');

INSERT OR IGNORE INTO produtos (id, nome, categoria, preco, estoque) VALUES
    (1, 'Teclado mecânico', 'Periféricos', 349.90, 42),
    (2, 'Mouse sem fio', 'Periféricos', 129.90, 80),
    (3, 'Monitor 27 polegadas', 'Monitores', 1599.00, 15),
    (4, 'Cadeira ergonômica', 'Móveis', 1890.00, 8),
    (5, 'Headset com microfone', 'Áudio', 279.50, 36),
    (6, 'SSD 1 TB', 'Armazenamento', 449.00, 54),
    (7, 'Webcam Full HD', 'Periféricos', 219.90, 27);
