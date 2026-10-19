INSERT OR IGNORE INTO clientes (id, nome, email, vip, corporativo, bloqueado, data_cadastro) VALUES
    ('97b36cfb-76f7-5c00-a772-af57c9288ab3', 'Ana Ferreira', 'ana.ferreira@pedidosnet.com.br', 1, 0, 0, '2024-03-10T00:00:00'),
    ('3ae71c03-da13-57db-bb8d-e0708c29c75b', 'Bruno Salles', 'bruno.salles@pedidosnet.com.br', 0, 1, 0, '2024-06-22T00:00:00'),
    ('1c5d07e5-e1f1-5218-b971-ed105a63c9bd', 'Carla Nunes', 'carla.nunes@pedidosnet.com.br', 0, 0, 0, '2025-01-05T00:00:00'),
    ('008158ba-95df-5ddf-a341-738b95258dbe', 'Danilo Reis', 'danilo.reis@pedidosnet.com.br', 0, 0, 1, '2023-11-30T00:00:00');

INSERT OR IGNORE INTO produtos (id, nome, categoria, preco, estoque) VALUES
    ('cf6d3d8d-d3a0-554d-9336-2e968b4a5d85', 'Teclado mecânico', 'Periféricos', 349.90, 42),
    ('b1fa0332-9ac6-5e0f-a270-dbf1c3d040f9', 'Mouse sem fio', 'Periféricos', 129.90, 80),
    ('865d3129-5149-5ef4-ac8c-4ae39217e103', 'Monitor 27 polegadas', 'Monitores', 1599.00, 15),
    ('d5734acf-c4c7-53f0-884d-e0c64c4f1f32', 'Cadeira ergonômica', 'Móveis', 1890.00, 8),
    ('14956ef7-a1c5-5ecc-ba92-28587a71473c', 'Headset com microfone', 'Áudio', 279.50, 36),
    ('62fd23d9-20c5-5ffe-a1d5-26da427d0213', 'SSD 1 TB', 'Armazenamento', 449.00, 54),
    ('195f5741-c05a-5484-8996-4ceae1e01868', 'Webcam Full HD', 'Periféricos', 219.90, 27);
