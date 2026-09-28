CREATE TABLE producto (
    id                  BIGSERIAL PRIMARY KEY,
    codigo              VARCHAR(20)     NOT NULL UNIQUE,
    nombre              VARCHAR(150)    NOT NULL,
    categoria           VARCHAR(30)     NOT NULL,
    precio_venta        NUMERIC(12, 2)  NOT NULL CHECK (precio_venta > 0),
    costo_adquisicion   NUMERIC(12, 2)  NOT NULL CHECK (costo_adquisicion > 0),
    stock_actual        INTEGER         NOT NULL DEFAULT 0 CHECK (stock_actual >= 0),
    stock_minimo        INTEGER         NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0),
    creado_en           TIMESTAMP       NOT NULL DEFAULT now(),
    actualizado_en      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_producto_categoria ON producto (categoria);
CREATE INDEX idx_producto_nombre ON producto (lower(nombre));
