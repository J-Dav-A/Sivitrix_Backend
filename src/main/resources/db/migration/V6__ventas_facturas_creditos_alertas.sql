-- Numeracion consecutiva de ventas/facturas: VIT-AAAA-#####
CREATE SEQUENCE venta_numero_seq START 1;

-- F-01: ventas
CREATE TABLE venta (
    id          BIGSERIAL PRIMARY KEY,
    numero      VARCHAR(20)    NOT NULL UNIQUE,
    cliente_id  BIGINT         NOT NULL REFERENCES cliente_empresarial(id),
    fecha       TIMESTAMP      NOT NULL DEFAULT now(),
    tipo_pago   VARCHAR(10)    NOT NULL,
    estado      VARCHAR(30)    NOT NULL,
    subtotal    NUMERIC(14, 2) NOT NULL,
    iva         NUMERIC(14, 2) NOT NULL,
    total       NUMERIC(14, 2) NOT NULL
);

CREATE TABLE detalle_venta (
    id              BIGSERIAL PRIMARY KEY,
    venta_id        BIGINT         NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
    producto_id     BIGINT         NOT NULL REFERENCES producto(id),
    cantidad        INTEGER        NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(14, 2) NOT NULL,
    costo_unitario  NUMERIC(14, 2) NOT NULL,
    subtotal        NUMERIC(14, 2) NOT NULL
);

ALTER TABLE movimiento_inventario
    ADD CONSTRAINT fk_movimiento_venta FOREIGN KEY (venta_id) REFERENCES venta(id);

-- F-06: facturas (copia de los valores de la venta al momento de emitirse)
CREATE TABLE factura (
    id            BIGSERIAL PRIMARY KEY,
    numero        VARCHAR(20)    NOT NULL UNIQUE,
    venta_id      BIGINT         NOT NULL UNIQUE REFERENCES venta(id),
    fecha_emision TIMESTAMP      NOT NULL DEFAULT now(),
    subtotal      NUMERIC(14, 2) NOT NULL,
    iva           NUMERIC(14, 2) NOT NULL,
    total         NUMERIC(14, 2) NOT NULL
);

-- F-05: credito, cuotas y pagos
CREATE TABLE credito (
    id               BIGSERIAL PRIMARY KEY,
    venta_id         BIGINT         NOT NULL UNIQUE REFERENCES venta(id),
    cliente_id       BIGINT         NOT NULL REFERENCES cliente_empresarial(id),
    valor_total      NUMERIC(14, 2) NOT NULL,
    numero_cuotas    INTEGER        NOT NULL CHECK (numero_cuotas > 0),
    estado           VARCHAR(30)    NOT NULL,
    fecha_solicitud  TIMESTAMP      NOT NULL DEFAULT now(),
    fecha_resolucion TIMESTAMP,
    observacion      VARCHAR(300)
);

CREATE TABLE cuota (
    id           BIGSERIAL PRIMARY KEY,
    credito_id   BIGINT         NOT NULL REFERENCES credito(id) ON DELETE CASCADE,
    numero       INTEGER        NOT NULL,
    valor        NUMERIC(14, 2) NOT NULL,
    fecha_limite DATE           NOT NULL,
    estado       VARCHAR(15)    NOT NULL
);

CREATE TABLE pago (
    id         BIGSERIAL PRIMARY KEY,
    cuota_id   BIGINT         NOT NULL UNIQUE REFERENCES cuota(id) ON DELETE CASCADE,
    monto      NUMERIC(14, 2) NOT NULL,
    fecha_pago TIMESTAMP      NOT NULL DEFAULT now()
);

-- F-07: alertas de stock bajo
CREATE TABLE alerta_stock (
    id                BIGSERIAL PRIMARY KEY,
    producto_id       BIGINT      NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
    nivel             VARCHAR(20) NOT NULL,
    stock_al_generar  INTEGER     NOT NULL,
    stock_minimo      INTEGER     NOT NULL,
    fecha_generacion  TIMESTAMP   NOT NULL DEFAULT now(),
    activa            BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_alerta_activa ON alerta_stock (activa, producto_id);
