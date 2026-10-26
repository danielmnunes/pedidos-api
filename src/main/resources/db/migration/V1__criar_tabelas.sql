CREATE TABLE clientes (
    id            INTEGER NOT NULL PRIMARY KEY,
    nome          TEXT    NOT NULL,
    email         TEXT    NOT NULL,
    vip           INTEGER NOT NULL DEFAULT 0,
    corporativo   INTEGER NOT NULL DEFAULT 0,
    bloqueado     INTEGER NOT NULL DEFAULT 0,
    data_cadastro TEXT    NOT NULL
);

CREATE UNIQUE INDEX idx_clientes_email ON clientes (email);

CREATE TABLE produtos (
    id        INTEGER NOT NULL PRIMARY KEY,
    nome      TEXT    NOT NULL,
    categoria TEXT    NOT NULL,
    preco     NUMERIC NOT NULL,
    estoque   INTEGER NOT NULL
);

CREATE TABLE pedidos (
    id           INTEGER NOT NULL PRIMARY KEY,
    cliente_id   INTEGER NOT NULL,
    status       TEXT    NOT NULL,
    total        NUMERIC NOT NULL,
    data_criacao TEXT    NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES clientes (id)
);

CREATE INDEX idx_pedidos_cliente_id ON pedidos (cliente_id);

CREATE TABLE itens_pedido (
    id         INTEGER NOT NULL PRIMARY KEY,
    pedido_id  INTEGER NOT NULL,
    produto_id INTEGER NOT NULL,
    quantidade INTEGER NOT NULL,
    FOREIGN KEY (pedido_id) REFERENCES pedidos (id),
    FOREIGN KEY (produto_id) REFERENCES produtos (id)
);

CREATE INDEX idx_itens_pedido_pedido_id ON itens_pedido (pedido_id);
CREATE INDEX idx_itens_pedido_produto_id ON itens_pedido (produto_id);
