-- Usuarios del sistema (login). El administrador principal se crea al arrancar y no se puede eliminar.
CREATE TABLE usuario (
                         id        BIGSERIAL PRIMARY KEY,
                         username  VARCHAR(30)  NOT NULL UNIQUE,
                         password  VARCHAR(100) NOT NULL,
                         rol       VARCHAR(20)  NOT NULL,
                         principal BOOLEAN      NOT NULL DEFAULT FALSE,
                         creado_en TIMESTAMP    NOT NULL DEFAULT now()
);