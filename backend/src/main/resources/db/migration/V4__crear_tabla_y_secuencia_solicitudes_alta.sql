-- V4__crear_tabla_y_secuencia_solicitudes_alta.sql
-- Creación de secuencia, tabla SOLICITUDES_ALTA y datos iniciales para la Clase 8

CREATE SEQUENCE SOLICITUDES_ALTA_SEQ
    START WITH 4
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

CREATE TABLE SOLICITUDES_ALTA (
    ID               NUMBER(19)      NOT NULL,
    NOMBRE           VARCHAR2(100)   NOT NULL,
    EMAIL            VARCHAR2(100)   NOT NULL,
    TELEFONO         VARCHAR2(30)    NOT NULL,
    ESTADO           VARCHAR2(20)    NOT NULL,
    FECHA_CREACION   DATE            NOT NULL,
    FECHA_RESOLUCION DATE,
    CLIENTE_ID       NUMBER(19),
    CONSTRAINT PK_SOLICITUDES_ALTA PRIMARY KEY (ID),
    CONSTRAINT CK_SOLICITUDES_ESTADO CHECK (ESTADO IN ('PENDIENTE', 'APROBADA', 'RECHAZADA')),
    CONSTRAINT FK_SOLICITUDES_CLIENTE FOREIGN KEY (CLIENTE_ID) REFERENCES CLIENTES (ID)
);

-- Solicitud A (ID 1): para demostrar inconsistencia inicial (antipatrón sin transacción conjunta)
INSERT INTO SOLICITUDES_ALTA (ID, NOMBRE, EMAIL, TELEFONO, ESTADO, FECHA_CREACION, FECHA_RESOLUCION, CLIENTE_ID)
VALUES (1, 'Marcos Paz', 'marcos.paz@example.com', '11-4000-2001', 'PENDIENTE', DATE '2025-02-01', NULL, NULL);

-- Solicitud B (ID 2): reservada para demostrar rollback tras la refactorización en vivo
INSERT INTO SOLICITUDES_ALTA (ID, NOMBRE, EMAIL, TELEFONO, ESTADO, FECHA_CREACION, FECHA_RESOLUCION, CLIENTE_ID)
VALUES (2, 'Valentina Torres', 'valentina.torres@example.com', '11-4000-2002', 'PENDIENTE', DATE '2025-02-02', NULL, NULL);

-- Solicitud C (ID 3): reservada para demostrar commit exitoso
INSERT INTO SOLICITUDES_ALTA (ID, NOMBRE, EMAIL, TELEFONO, ESTADO, FECHA_CREACION, FECHA_RESOLUCION, CLIENTE_ID)
VALUES (3, 'Lucía Ramos', 'lucia.ramos@example.com', '11-4000-2003', 'PENDIENTE', DATE '2025-02-03', NULL, NULL);
