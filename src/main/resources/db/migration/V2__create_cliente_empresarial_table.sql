CREATE TABLE cliente_empresarial (
    id                  BIGSERIAL PRIMARY KEY,
    nit                 VARCHAR(20)     NOT NULL UNIQUE,
    nombre              VARCHAR(150)    NOT NULL,
    telefono            VARCHAR(20)     NOT NULL,
    direccion           VARCHAR(200)    NOT NULL,
    email               VARCHAR(150)    NOT NULL,
    contacto_nombre     VARCHAR(150)    NOT NULL,
    contacto_cedula     VARCHAR(20)     NOT NULL,
    saldo_credito       NUMERIC(12, 2)  NOT NULL DEFAULT 0,
    creado_en           TIMESTAMP       NOT NULL DEFAULT now(),
    actualizado_en      TIMESTAMP       NOT NULL DEFAULT now()
);

CREATE INDEX idx_cliente_nombre ON cliente_empresarial (lower(nombre));
