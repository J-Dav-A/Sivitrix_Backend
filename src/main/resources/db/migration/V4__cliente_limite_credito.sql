-- F-05: cupo maximo de credito que se le aprueba a cada cliente empresarial.
ALTER TABLE cliente_empresarial
    ADD COLUMN limite_credito NUMERIC(14, 2) NOT NULL DEFAULT 0;
