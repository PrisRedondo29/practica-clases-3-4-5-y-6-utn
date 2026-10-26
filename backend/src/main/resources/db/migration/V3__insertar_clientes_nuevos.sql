-- V3__insertar_nuevos_clientes.sql
-- Carga de nuevos clientes para la Clase 7

INSERT INTO CLIENTES (ID, NOMBRE, ESTADO, EMAIL, TELEFONO, FECHA_ALTA)
VALUES (CLIENTES_SEQ.NEXTVAL, 'Florencia', 'ACTIVO', 'florencia@example.com', '11-4000-1006', DATE '2024-06-12');

INSERT INTO CLIENTES (ID, NOMBRE, ESTADO, EMAIL, TELEFONO, FECHA_ALTA)
VALUES (CLIENTES_SEQ.NEXTVAL, 'Gabriel', 'ACTIVO', 'gabriel@example.com', '11-4000-1007', DATE '2024-07-18');