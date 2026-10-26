INSERT OR IGNORE INTO clientes (id, nome, email, vip, corporativo, bloqueado, data_cadastro) VALUES
    (1, 'Ana Ferreira', 'ana.ferreira@pedidosnet.com.br', 1, 0, 0, '2024-03-10T00:00:00'),
    (2, 'Bruno Salles', 'bruno.salles@pedidosnet.com.br', 0, 1, 0, '2024-06-22T00:00:00'),
    (3, 'Carla Nunes', 'carla.nunes@pedidosnet.com.br', 0, 0, 0, '2025-01-05T00:00:00'),
    (4, 'Danilo Reis', 'danilo.reis@pedidosnet.com.br', 0, 0, 1, '2023-11-30T00:00:00');

INSERT OR IGNORE INTO produtos (id, nome, categoria, preco, estoque) VALUES
    (1, 'Notebook Pro 15', 'Informática', 6500.00, 12),
    (2, 'Mouse Sem Fio', 'Periféricos', 90.00, 150),
    (3, 'Teclado Mecânico', 'Periféricos', 350.00, 80),
    (4, 'Monitor 27 144Hz', 'Informática', 1900.00, 25),
    (5, 'Webcam Full HD', 'Periféricos', 260.00, 60),
    (6, 'Headset Gamer', 'Áudio', 430.00, 40);

INSERT OR IGNORE INTO pedidos (id, cliente_id, status, total, data_criacao) VALUES
    (1, 1, 'Pago', 5931.00, '2025-05-12T00:00:00'),
    (2, 2, 'Pendente', 4150.00, '2025-06-02T00:00:00'),
    (3, 3, 'Cancelado', 260.00, '2025-04-18T00:00:00');

INSERT OR IGNORE INTO itens_pedido (id, pedido_id, produto_id, quantidade) VALUES
    (1, 1, 1, 1),
    (2, 1, 2, 1),
    (3, 2, 4, 2),
    (4, 2, 3, 1),
    (5, 3, 5, 1);
