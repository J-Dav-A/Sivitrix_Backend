CREATE TABLE proveedor (
    id                  BIGSERIAL PRIMARY KEY,
    nit                 VARCHAR(20)     NOT NULL UNIQUE,
    nombre_empresa      VARCHAR(150)    NOT NULL,
    nombre_contacto     VARCHAR(150)    NOT NULL,
    telefono            VARCHAR(20)     NOT NULL,
    email               VARCHAR(150)    NOT NULL,
    fecha_ultimo_pedido TIMESTAMP,
    creado_en           TIMESTAMP       NOT NULL DEFAULT now(),
    actualizado_en      TIMESTAMP       NOT NULL DEFAULT now()
);

-- SWR-33: tabla intermedia para la relacion muchos-a-muchos
-- proveedor <-> producto (un proveedor suministra varios productos,
-- un producto puede venir de varios proveedores).
CREATE TABLE proveedor_producto (
    proveedor_id BIGINT NOT NULL REFERENCES proveedor(id) ON DELETE CASCADE,
    producto_id  BIGINT NOT NULL REFERENCES producto(id)  ON DELETE CASCADE,
    PRIMARY KEY (proveedor_id, producto_id)
);

CREATE INDEX idx_proveedor_nombre ON proveedor (lower(nombre_empresa));