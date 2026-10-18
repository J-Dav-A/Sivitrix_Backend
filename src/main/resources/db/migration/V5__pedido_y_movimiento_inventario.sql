-- F-03: pedidos a proveedores
CREATE TABLE pedido (
    id            BIGSERIAL PRIMARY KEY,
    proveedor_id  BIGINT       NOT NULL REFERENCES proveedor(id),
    fecha_pedido  TIMESTAMP    NOT NULL DEFAULT now(),
    estado        VARCHAR(30)  NOT NULL,
    observaciones VARCHAR(500)
);

CREATE TABLE detalle_pedido (
    id                  BIGSERIAL PRIMARY KEY,
    pedido_id           BIGINT  NOT NULL REFERENCES pedido(id) ON DELETE CASCADE,
    producto_id         BIGINT  NOT NULL REFERENCES producto(id),
    cantidad_solicitada INTEGER NOT NULL CHECK (cantidad_solicitada > 0),
    cantidad_recibida   INTEGER NOT NULL DEFAULT 0 CHECK (cantidad_recibida >= 0)
);

-- F-02: kardex de entradas y salidas. venta_id se enlaza en V6.
CREATE TABLE movimiento_inventario (
    id          BIGSERIAL PRIMARY KEY,
    producto_id BIGINT       NOT NULL REFERENCES producto(id),
    tipo        VARCHAR(10)  NOT NULL,
    cantidad    INTEGER      NOT NULL CHECK (cantidad > 0),
    fecha       TIMESTAMP    NOT NULL DEFAULT now(),
    observacion VARCHAR(300),
    pedido_id   BIGINT REFERENCES pedido(id),
    venta_id    BIGINT
);

CREATE INDEX idx_movimiento_producto ON movimiento_inventario (producto_id, fecha DESC);
