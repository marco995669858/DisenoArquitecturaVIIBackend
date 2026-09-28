-- Dataverse Rentals — esquema relacional (MySQL 8+/9.x)
-- Derivado de RF-01..RF-09 del documento del proyecto (Documentos/Trabajo_Final_Dataverse_Rentals...)
--
-- Auditoría: toda tabla de negocio tiene estado_registro (ACTIVO/ELIMINADO -> borrado lógico)
-- y el trío usuario/fecha/ip para creación, actualización y eliminación. usuario_* referencia a
-- usuario(id_usuario) y lo completa el backend a partir del usuario autenticado (JWT) y la IP del
-- request — nunca lo envía el cliente. Se excluye refresh_token: es una tabla técnica de tokens
-- efímeros, no una entidad de negocio.

CREATE DATABASE IF NOT EXISTS dataverse_rentals
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE dataverse_rentals;

-- RNF-01: roles y usuarios del sistema
-- usuario_creacion es NULLABLE aquí porque rol/usuario se siembran antes de que exista cualquier usuario (bootstrap).
-- nombre no lleva UNIQUE directo: un rol borrado lógicamente no debe bloquear el nombre para
-- siempre (ver nombre_activo, columna generada que solo "cuenta" filas con estado_registro = 'ACTIVO').
CREATE TABLE rol (
  id_rol      INT AUTO_INCREMENT PRIMARY KEY,
  nombre      VARCHAR(50) NOT NULL,
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  nombre_activo VARCHAR(50) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN nombre ELSE NULL END) STORED,
  UNIQUE KEY uq_rol_nombre_activo (nombre_activo)
) ENGINE=InnoDB;

CREATE TABLE usuario (
  id_usuario      INT AUTO_INCREMENT PRIMARY KEY,
  id_rol          INT NOT NULL,
  nombre_usuario  VARCHAR(50) NOT NULL,
  password_hash   VARCHAR(255) NOT NULL,
  nombre_completo VARCHAR(150) NOT NULL,
  email           VARCHAR(150),
  estado          ENUM('ACTIVO','INACTIVO') NOT NULL DEFAULT 'ACTIVO',
  fecha_creacion  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NULL,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  nombre_usuario_activo VARCHAR(50) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN nombre_usuario ELSE NULL END) STORED,
  email_activo VARCHAR(150) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN email ELSE NULL END) STORED,
  UNIQUE KEY uq_usuario_nombre_activo (nombre_usuario_activo),
  UNIQUE KEY uq_usuario_email_activo (email_activo),
  CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol),
  CONSTRAINT fk_usuario_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_usuario_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_usuario_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- fk_rol_* se agregan después de crear "usuario" porque rol.usuario_creacion la referencia
ALTER TABLE rol
  ADD CONSTRAINT fk_rol_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  ADD CONSTRAINT fk_rol_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  ADD CONSTRAINT fk_rol_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario);

-- Tokens de refresco: tabla técnica, sin auditoría de negocio (se crea/borra en cada rotación).
CREATE TABLE refresh_token (
  id_token_refresco INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario        INT NOT NULL,
  token             VARCHAR(255) NOT NULL UNIQUE,
  fecha_expiracion  DATETIME NOT NULL,
  CONSTRAINT fk_refresh_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
) ENGINE=InnoDB;

-- RF-01: unidades del edificio
CREATE TABLE unidad (
  id_unidad       INT AUTO_INCREMENT PRIMARY KEY,
  piso            TINYINT NOT NULL,
  numero_unidad   VARCHAR(10) NOT NULL,
  area_m2         DECIMAL(7,2) NOT NULL,
  caracteristicas VARCHAR(255),
  estado          ENUM('DISPONIBLE','OCUPADO','MANTENIMIENTO') NOT NULL DEFAULT 'DISPONIBLE',
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  numero_unidad_activo VARCHAR(10) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN numero_unidad ELSE NULL END) STORED,
  UNIQUE KEY uq_unidad_numero_activo (numero_unidad_activo),
  CONSTRAINT chk_unidad_piso CHECK (piso BETWEEN 1 AND 10),
  CONSTRAINT chk_unidad_area CHECK (area_m2 > 0),
  CONSTRAINT fk_unidad_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_unidad_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_unidad_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-02: inquilinos
CREATE TABLE inquilino (
  id_inquilino     INT AUTO_INCREMENT PRIMARY KEY,
  tipo_documento   VARCHAR(20) NOT NULL,
  numero_documento VARCHAR(20) NOT NULL,
  nombre_completo  VARCHAR(150) NOT NULL,
  telefono         VARCHAR(20),
  email            VARCHAR(150),
  direccion        VARCHAR(255),
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  documento_activo VARCHAR(41) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN CONCAT(tipo_documento, ':', numero_documento) ELSE NULL END) STORED,
  UNIQUE KEY uq_inquilino_documento_activo (documento_activo),
  CONSTRAINT fk_inquilino_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_inquilino_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_inquilino_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-02 / RF-03: contratos
CREATE TABLE contrato (
  id_contrato           INT AUTO_INCREMENT PRIMARY KEY,
  id_unidad             INT NOT NULL,
  id_inquilino          INT NOT NULL,
  fecha_inicio          DATE NOT NULL,
  fecha_fin             DATE NOT NULL,
  monto_alquiler        DECIMAL(10,2) NOT NULL,
  ruta_contrato_digital VARCHAR(255),
  estado                ENUM('VIGENTE','VENCIDO','RENOVADO','FINALIZADO') NOT NULL DEFAULT 'VIGENTE',
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  CONSTRAINT fk_contrato_unidad FOREIGN KEY (id_unidad) REFERENCES unidad(id_unidad),
  CONSTRAINT fk_contrato_inquilino FOREIGN KEY (id_inquilino) REFERENCES inquilino(id_inquilino),
  CONSTRAINT chk_contrato_fechas CHECK (fecha_fin > fecha_inicio),
  CONSTRAINT chk_contrato_monto CHECK (monto_alquiler > 0),
  CONSTRAINT fk_contrato_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_contrato_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_contrato_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-04: medidores. id_unidad NULL = medidor general (áreas comunes del edificio)
CREATE TABLE medidor (
  id_medidor    INT AUTO_INCREMENT PRIMARY KEY,
  id_unidad     INT NULL,
  tipo_servicio ENUM('LUZ','AGUA') NOT NULL,
  tipo_medidor  ENUM('INDIVIDUAL','GENERAL') NOT NULL,
  numero_serie  VARCHAR(50) NOT NULL,
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  numero_serie_activo VARCHAR(50) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN numero_serie ELSE NULL END) STORED,
  UNIQUE KEY uq_medidor_serie_activo (numero_serie_activo),
  CONSTRAINT fk_medidor_unidad FOREIGN KEY (id_unidad) REFERENCES unidad(id_unidad),
  CONSTRAINT chk_medidor_tipo CHECK (
    (tipo_medidor = 'GENERAL' AND id_unidad IS NULL) OR
    (tipo_medidor = 'INDIVIDUAL' AND id_unidad IS NOT NULL)
  ),
  CONSTRAINT fk_medidor_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_medidor_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_medidor_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-04: lecturas mensuales (usuario_creacion = quien registró la lectura)
CREATE TABLE lectura_medidor (
  id_lectura        INT AUTO_INCREMENT PRIMARY KEY,
  id_medidor        INT NOT NULL,
  periodo           CHAR(7) NOT NULL,           -- 'YYYY-MM'
  lectura_anterior  DECIMAL(10,2) NOT NULL,
  lectura_actual    DECIMAL(10,2) NOT NULL,
  fecha_lectura     DATE NOT NULL,
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  medidor_periodo_activo VARCHAR(20) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN CONCAT(id_medidor, ':', periodo) ELSE NULL END) STORED,
  UNIQUE KEY uq_lectura_periodo_activo (medidor_periodo_activo),
  CONSTRAINT fk_lectura_medidor FOREIGN KEY (id_medidor) REFERENCES medidor(id_medidor),
  CONSTRAINT chk_lectura_valor CHECK (lectura_actual >= lectura_anterior),
  CONSTRAINT fk_lectura_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_lectura_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_lectura_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-05: prorrateo calculado por unidad, periodo y servicio
CREATE TABLE prorrateo (
  id_prorrateo            INT AUTO_INCREMENT PRIMARY KEY,
  id_unidad               INT NOT NULL,
  periodo                 CHAR(7) NOT NULL,
  tipo_servicio           ENUM('LUZ','AGUA') NOT NULL,
  consumo_individual      DECIMAL(10,2) NOT NULL,
  consumo_comun_asignado  DECIMAL(10,2) NOT NULL DEFAULT 0,
  costo_unitario          DECIMAL(10,4) NOT NULL,
  monto_calculado         DECIMAL(10,2) NOT NULL,
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  prorrateo_key_activo VARCHAR(30) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN CONCAT(id_unidad, ':', periodo, ':', tipo_servicio) ELSE NULL END) STORED,
  UNIQUE KEY uq_prorrateo_activo (prorrateo_key_activo),
  CONSTRAINT fk_prorrateo_unidad FOREIGN KEY (id_unidad) REFERENCES unidad(id_unidad),
  CONSTRAINT fk_prorrateo_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_prorrateo_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_prorrateo_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-06: liquidación mensual por contrato
CREATE TABLE liquidacion (
  id_liquidacion    INT AUTO_INCREMENT PRIMARY KEY,
  id_contrato       INT NOT NULL,
  periodo           CHAR(7) NOT NULL,
  monto_alquiler    DECIMAL(10,2) NOT NULL DEFAULT 0,
  monto_luz         DECIMAL(10,2) NOT NULL DEFAULT 0,
  monto_agua        DECIMAL(10,2) NOT NULL DEFAULT 0,
  monto_otros       DECIMAL(10,2) NOT NULL DEFAULT 0,
  monto_total       DECIMAL(10,2) GENERATED ALWAYS AS
                       (monto_alquiler + monto_luz + monto_agua + monto_otros) STORED,
  fecha_emision      DATE NOT NULL,
  fecha_vencimiento  DATE NOT NULL,
  estado             ENUM('PENDIENTE','PARCIAL','PAGADO','VENCIDO') NOT NULL DEFAULT 'PENDIENTE',
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  contrato_periodo_activo VARCHAR(20) GENERATED ALWAYS AS
    (CASE WHEN estado_registro = 'ACTIVO' THEN CONCAT(id_contrato, ':', periodo) ELSE NULL END) STORED,
  UNIQUE KEY uq_liquidacion_periodo_activo (contrato_periodo_activo),
  CONSTRAINT fk_liquidacion_contrato FOREIGN KEY (id_contrato) REFERENCES contrato(id_contrato),
  CONSTRAINT fk_liquidacion_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_liquidacion_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_liquidacion_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-06: conceptos variables de una liquidación (multas, cargos extra, etc.)
CREATE TABLE detalle_liquidacion (
  id_detalle      INT AUTO_INCREMENT PRIMARY KEY,
  id_liquidacion  INT NOT NULL,
  concepto        VARCHAR(150) NOT NULL,
  monto           DECIMAL(10,2) NOT NULL,
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  CONSTRAINT fk_detalle_liquidacion FOREIGN KEY (id_liquidacion) REFERENCES liquidacion(id_liquidacion) ON DELETE CASCADE,
  CONSTRAINT fk_detalle_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_detalle_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_detalle_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-07: cobranza contra una liquidación (usuario_creacion = quien registró el pago)
CREATE TABLE pago (
  id_pago          INT AUTO_INCREMENT PRIMARY KEY,
  id_liquidacion   INT NOT NULL,
  fecha_pago       DATE NOT NULL,
  monto_pagado     DECIMAL(10,2) NOT NULL,
  metodo_pago      ENUM('EFECTIVO','TRANSFERENCIA','TARJETA') NOT NULL,
  numero_voucher   VARCHAR(50),
  ruta_evidencia   VARCHAR(255),
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  CONSTRAINT fk_pago_liquidacion FOREIGN KEY (id_liquidacion) REFERENCES liquidacion(id_liquidacion),
  CONSTRAINT chk_pago_monto CHECK (monto_pagado > 0),
  CONSTRAINT fk_pago_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_pago_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_pago_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RF-08: mantenimiento. id_unidad NULL = gasto de áreas comunes; usuario_creacion = quien lo registró
CREATE TABLE mantenimiento (
  id_mantenimiento    INT AUTO_INCREMENT PRIMARY KEY,
  id_unidad           INT NULL,
  descripcion         VARCHAR(255) NOT NULL,
  fecha_mantenimiento DATE NOT NULL,
  tipo_gasto          ENUM('MATERIAL','MANO_DE_OBRA','AMBOS') NOT NULL,
  proveedor           VARCHAR(150),
  monto               DECIMAL(10,2) NOT NULL,
  estado_registro       ENUM('ACTIVO','ELIMINADO') NOT NULL DEFAULT 'ACTIVO',
  usuario_creacion      INT NOT NULL,
  fecha_creacion        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ip_creacion           VARCHAR(45) NULL,
  usuario_actualizacion INT NULL,
  fecha_actualizacion   DATETIME NULL,
  ip_actualizacion      VARCHAR(45) NULL,
  usuario_eliminacion   INT NULL,
  fecha_eliminacion     DATETIME NULL,
  ip_eliminacion        VARCHAR(45) NULL,
  CONSTRAINT fk_mantenimiento_unidad FOREIGN KEY (id_unidad) REFERENCES unidad(id_unidad),
  CONSTRAINT chk_mantenimiento_monto CHECK (monto > 0),
  CONSTRAINT fk_mantenimiento_usuario_creacion FOREIGN KEY (usuario_creacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_mantenimiento_usuario_actualizacion FOREIGN KEY (usuario_actualizacion) REFERENCES usuario(id_usuario),
  CONSTRAINT fk_mantenimiento_usuario_eliminacion FOREIGN KEY (usuario_eliminacion) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- RNF-01: catalogo de modulos del sistema (uno por cada pantalla protegida por
-- rol en el frontend) y que permisos tiene cada rol sobre ellos. Antes esto
-- vivia hardcodeado en el codigo Angular (arreglos de ROLE_X por ruta): un rol
-- nuevo creado desde la pantalla Roles no encajaba en ningun arreglo y
-- terminaba sin acceso a nada mas que el Dashboard (que no tiene restriccion).
-- Con esta tabla, el propio formulario de Roles decide que modulos ve cada rol.
-- Dashboard no esta en el catalogo a proposito: es la unica pantalla que
-- cualquier usuario autenticado ve siempre, sin permiso que activar.
CREATE TABLE modulo_sistema (
  id_modulo INT AUTO_INCREMENT PRIMARY KEY,
  codigo    VARCHAR(30) NOT NULL UNIQUE,
  nombre    VARCHAR(50) NOT NULL,
  orden     TINYINT NOT NULL
) ENGINE=InnoDB;

-- Tabla tecnica de configuracion (igual que refresh_token): se reemplaza por
-- completo cada vez que se guarda un rol, no acumula historial de negocio.
-- nivel_acceso: RNF-01 exige "control de acceso a funciones segun rol", no
-- solo a pantallas -- un rol como "Consulta" (p.ej. para una auditoria
-- externa) necesita ver un modulo sin poder crear/editar/eliminar en el. Se
-- aplica en ambos lados: el frontend oculta los botones de escritura y el
-- backend los rechaza de verdad (ver PermisoAccessFilter), para que no sea
-- una restriccion solo cosmetica.
CREATE TABLE rol_permiso (
  id_rol_permiso INT AUTO_INCREMENT PRIMARY KEY,
  id_rol         INT NOT NULL,
  id_modulo      INT NOT NULL,
  nivel_acceso   ENUM('LECTURA','ESCRITURA') NOT NULL DEFAULT 'ESCRITURA',
  UNIQUE KEY uq_rol_modulo (id_rol, id_modulo),
  CONSTRAINT fk_rolpermiso_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol) ON DELETE CASCADE,
  CONSTRAINT fk_rolpermiso_modulo FOREIGN KEY (id_modulo) REFERENCES modulo_sistema(id_modulo) ON DELETE CASCADE
) ENGINE=InnoDB;

INSERT INTO modulo_sistema (codigo, nombre, orden) VALUES
  ('UNIDADES', 'Unidades', 1),
  ('INQUILINOS', 'Inquilinos', 2),
  ('CONTRATOS', 'Contratos', 3),
  ('MEDIDORES', 'Medidores', 4),
  ('LECTURAS', 'Lecturas', 5),
  ('RECIBOS', 'Recibos de servicios', 6),
  ('PRORRATEO', 'Prorrateo', 7),
  ('LIQUIDACIONES', 'Liquidaciones', 8),
  ('PAGOS', 'Pagos', 9),
  ('MANTENIMIENTO', 'Mantenimiento', 10),
  ('USUARIOS', 'Usuarios', 11),
  ('ROLES', 'Roles', 12),
  ('PARAMETROS', 'Parámetros', 13),
  ('PERIODOS', 'Periodos', 14),
  ('AUDITORIA', 'Auditoría', 15);

-- Datos de referencia (roles del sistema, RNF-01)
INSERT INTO rol (nombre) VALUES
  ('ADMINISTRADOR'),
  ('COBRANZAS'),
  ('MANTENIMIENTO');

-- Permisos por defecto de los 3 roles semilla: reproducen exactamente lo que
-- antes estaba fijo en el codigo (app.routes.ts / app.menu.ts), para que la
-- migracion no le quite acceso a nadie que ya lo tenia.
INSERT INTO rol_permiso (id_rol, id_modulo)
SELECT r.id_rol, m.id_modulo FROM rol r CROSS JOIN modulo_sistema m
WHERE r.nombre = 'ADMINISTRADOR';

INSERT INTO rol_permiso (id_rol, id_modulo)
SELECT r.id_rol, m.id_modulo FROM rol r JOIN modulo_sistema m ON m.codigo IN ('INQUILINOS','CONTRATOS','LIQUIDACIONES','PAGOS')
WHERE r.nombre = 'COBRANZAS';

INSERT INTO rol_permiso (id_rol, id_modulo)
SELECT r.id_rol, m.id_modulo FROM rol r JOIN modulo_sistema m ON m.codigo IN ('UNIDADES','MANTENIMIENTO')
WHERE r.nombre = 'MANTENIMIENTO';

-- Usuarios semilla para poder iniciar sesión de inmediato, uno por rol:
--   admin / Admin123!            (ADMINISTRADOR)
--   cobranzas / Cobranza123!     (COBRANZAS)
--   mantenimiento / Mantenimiento123!  (MANTENIMIENTO)
INSERT INTO usuario (id_rol, nombre_usuario, password_hash, nombre_completo, email, estado)
SELECT (SELECT id_rol FROM rol WHERE nombre = 'ADMINISTRADOR'),
       'admin', '$2a$10$uT7TfwgtEZbpwY5nm0O/l.gUiPxjnDjyEZAO3Tn4.U8E/NzoLsOw6',
       'Administrador General', 'admin@milanoasociados.com', 'ACTIVO'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE nombre_usuario = 'admin');

INSERT INTO usuario (id_rol, nombre_usuario, password_hash, nombre_completo, email, estado)
SELECT (SELECT id_rol FROM rol WHERE nombre = 'COBRANZAS'),
       'cobranzas', '$2a$10$zv/Hb.ybx9fVE3dBR6PXJe7iuKDRWfAUM0TBdQsuDLiW.OY.sRC1m',
       'Encargado de Cobranzas', 'cobranzas@milanoasociados.com', 'ACTIVO'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE nombre_usuario = 'cobranzas');

INSERT INTO usuario (id_rol, nombre_usuario, password_hash, nombre_completo, email, estado)
SELECT (SELECT id_rol FROM rol WHERE nombre = 'MANTENIMIENTO'),
       'mantenimiento', '$2a$10$nB5C.7zBcb9RC5Wgwz5iZOPmy9yB/1C5A6k8KQHWxz4Jn04gbsN.i',
       'Encargado de Mantenimiento', 'mantenimiento@milanoasociados.com', 'ACTIVO'
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE nombre_usuario = 'mantenimiento');
