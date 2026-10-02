CREATE DATABASE  IF NOT EXISTS `dataverse_rentals` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `dataverse_rentals`;
-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: localhost    Database: dataverse_rentals
-- ------------------------------------------------------
-- Server version	9.6.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
SET @MYSQLDUMP_TEMP_LOG_BIN = @@SESSION.SQL_LOG_BIN;
SET @@SESSION.SQL_LOG_BIN= 0;

--
-- Table structure for table `contrato`
--

DROP TABLE IF EXISTS `contrato`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contrato` (
  `id_contrato` int NOT NULL AUTO_INCREMENT,
  `id_unidad` int NOT NULL,
  `id_inquilino` int NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `monto_alquiler` decimal(10,2) NOT NULL,
  `ruta_contrato_digital` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('VIGENTE','VENCIDO','RENOVADO','FINALIZADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'VIGENTE',
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `unidad_vigente_activa` int GENERATED ALWAYS AS ((case when ((`estado` = _utf8mb4'VIGENTE') and (`estado_registro` = _utf8mb4'ACTIVO')) then `id_unidad` else NULL end)) STORED,
  PRIMARY KEY (`id_contrato`),
  UNIQUE KEY `uq_contrato_unidad_vigente` (`unidad_vigente_activa`),
  KEY `fk_contrato_unidad` (`id_unidad`),
  KEY `fk_contrato_inquilino` (`id_inquilino`),
  KEY `fk_contrato_usuario_creacion` (`usuario_creacion`),
  KEY `fk_contrato_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_contrato_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_contrato_inquilino` FOREIGN KEY (`id_inquilino`) REFERENCES `inquilino` (`id_inquilino`),
  CONSTRAINT `fk_contrato_unidad` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id_unidad`),
  CONSTRAINT `fk_contrato_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_contrato_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_contrato_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `chk_contrato_fechas` CHECK ((`fecha_fin` > `fecha_inicio`)),
  CONSTRAINT `chk_contrato_monto` CHECK ((`monto_alquiler` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contrato`
--

LOCK TABLES `contrato` WRITE;
/*!40000 ALTER TABLE `contrato` DISABLE KEYS */;
/*!40000 ALTER TABLE `contrato` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_liquidacion`
--

DROP TABLE IF EXISTS `detalle_liquidacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_liquidacion` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_liquidacion` int NOT NULL,
  `concepto` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_concepto` enum('ALQUILER','LUZ','AGUA','MANTENIMIENTO','OTRO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'OTRO',
  `monto` decimal(10,2) NOT NULL,
  `id_prorrateo` int DEFAULT NULL,
  `id_mantenimiento` int DEFAULT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `fk_detalle_liquidacion` (`id_liquidacion`),
  KEY `fk_detalle_usuario_creacion` (`usuario_creacion`),
  KEY `fk_detalle_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_detalle_usuario_eliminacion` (`usuario_eliminacion`),
  KEY `idx_detalle_prorrateo` (`id_prorrateo`),
  KEY `idx_detalle_mantenimiento` (`id_mantenimiento`),
  CONSTRAINT `fk_detalle_liquidacion` FOREIGN KEY (`id_liquidacion`) REFERENCES `liquidacion` (`id_liquidacion`) ON DELETE CASCADE,
  CONSTRAINT `fk_detalle_mantenimiento` FOREIGN KEY (`id_mantenimiento`) REFERENCES `mantenimiento` (`id_mantenimiento`),
  CONSTRAINT `fk_detalle_prorrateo` FOREIGN KEY (`id_prorrateo`) REFERENCES `prorrateo` (`id_prorrateo`),
  CONSTRAINT `fk_detalle_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_detalle_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_detalle_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_liquidacion`
--

LOCK TABLES `detalle_liquidacion` WRITE;
/*!40000 ALTER TABLE `detalle_liquidacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalle_liquidacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inquilino`
--

DROP TABLE IF EXISTS `inquilino`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inquilino` (
  `id_inquilino` int NOT NULL AUTO_INCREMENT,
  `tipo_documento` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_documento` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_completo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `documento_activo` varchar(41) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then concat(`tipo_documento`,_utf8mb4':',`numero_documento`) else NULL end)) STORED,
  PRIMARY KEY (`id_inquilino`),
  UNIQUE KEY `uq_inquilino_documento_activo` (`documento_activo`),
  KEY `fk_inquilino_usuario_creacion` (`usuario_creacion`),
  KEY `fk_inquilino_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_inquilino_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_inquilino_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_inquilino_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_inquilino_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inquilino`
--

LOCK TABLES `inquilino` WRITE;
/*!40000 ALTER TABLE `inquilino` DISABLE KEYS */;
/*!40000 ALTER TABLE `inquilino` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lectura_medidor`
--

DROP TABLE IF EXISTS `lectura_medidor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lectura_medidor` (
  `id_lectura` int NOT NULL AUTO_INCREMENT,
  `id_medidor` int NOT NULL,
  `periodo` char(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `lectura_anterior` decimal(10,2) NOT NULL,
  `lectura_actual` decimal(10,2) NOT NULL,
  `fecha_lectura` date NOT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `medidor_periodo_activo` varchar(20) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then concat(`id_medidor`,_utf8mb4':',`periodo`) else NULL end)) STORED,
  PRIMARY KEY (`id_lectura`),
  UNIQUE KEY `uq_lectura_periodo_activo` (`medidor_periodo_activo`),
  KEY `fk_lectura_usuario_creacion` (`usuario_creacion`),
  KEY `fk_lectura_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_lectura_usuario_eliminacion` (`usuario_eliminacion`),
  KEY `idx_lectura_medidor` (`id_medidor`),
  KEY `fk_lectura_periodo` (`periodo`),
  CONSTRAINT `fk_lectura_medidor` FOREIGN KEY (`id_medidor`) REFERENCES `medidor` (`id_medidor`),
  CONSTRAINT `fk_lectura_periodo` FOREIGN KEY (`periodo`) REFERENCES `periodo_facturacion` (`periodo`),
  CONSTRAINT `fk_lectura_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_lectura_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_lectura_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `chk_lectura_valor` CHECK ((`lectura_actual` >= `lectura_anterior`))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lectura_medidor`
--

LOCK TABLES `lectura_medidor` WRITE;
/*!40000 ALTER TABLE `lectura_medidor` DISABLE KEYS */;
/*!40000 ALTER TABLE `lectura_medidor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `liquidacion`
--

DROP TABLE IF EXISTS `liquidacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `liquidacion` (
  `id_liquidacion` int NOT NULL AUTO_INCREMENT,
  `id_contrato` int NOT NULL,
  `periodo` char(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `monto_alquiler` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monto_luz` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monto_agua` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monto_otros` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monto_total` decimal(10,2) GENERATED ALWAYS AS ((((`monto_alquiler` + `monto_luz`) + `monto_agua`) + `monto_otros`)) STORED,
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `estado` enum('PENDIENTE','PARCIAL','PAGADO','VENCIDO','BORRADOR') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDIENTE',
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `contrato_periodo_activo` varchar(20) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then concat(`id_contrato`,_utf8mb4':',`periodo`) else NULL end)) STORED,
  PRIMARY KEY (`id_liquidacion`),
  UNIQUE KEY `uq_liquidacion_periodo_activo` (`contrato_periodo_activo`),
  KEY `fk_liquidacion_usuario_creacion` (`usuario_creacion`),
  KEY `fk_liquidacion_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_liquidacion_usuario_eliminacion` (`usuario_eliminacion`),
  KEY `idx_liquidacion_contrato` (`id_contrato`),
  KEY `fk_liquidacion_periodo` (`periodo`),
  CONSTRAINT `fk_liquidacion_contrato` FOREIGN KEY (`id_contrato`) REFERENCES `contrato` (`id_contrato`),
  CONSTRAINT `fk_liquidacion_periodo` FOREIGN KEY (`periodo`) REFERENCES `periodo_facturacion` (`periodo`),
  CONSTRAINT `fk_liquidacion_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_liquidacion_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_liquidacion_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `liquidacion`
--

LOCK TABLES `liquidacion` WRITE;
/*!40000 ALTER TABLE `liquidacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `liquidacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mantenimiento`
--

DROP TABLE IF EXISTS `mantenimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `mantenimiento` (
  `id_mantenimiento` int NOT NULL AUTO_INCREMENT,
  `id_unidad` int DEFAULT NULL,
  `ubicacion` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `descripcion` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_mantenimiento` date NOT NULL,
  `tipo_gasto` enum('MATERIAL','MANO_DE_OBRA','AMBOS') COLLATE utf8mb4_unicode_ci NOT NULL,
  `proveedor` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `monto_materiales` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monto_mano_obra` decimal(10,2) NOT NULL DEFAULT '0.00',
  `monto` decimal(10,2) NOT NULL,
  `imputable_inquilino` tinyint(1) NOT NULL DEFAULT '0',
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_mantenimiento`),
  KEY `fk_mantenimiento_unidad` (`id_unidad`),
  KEY `fk_mantenimiento_usuario_creacion` (`usuario_creacion`),
  KEY `fk_mantenimiento_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_mantenimiento_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_mantenimiento_unidad` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id_unidad`),
  CONSTRAINT `fk_mantenimiento_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_mantenimiento_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_mantenimiento_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `chk_mantenimiento_desglose` CHECK ((`monto` = (`monto_materiales` + `monto_mano_obra`))),
  CONSTRAINT `chk_mantenimiento_monto` CHECK ((`monto` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mantenimiento`
--

LOCK TABLES `mantenimiento` WRITE;
/*!40000 ALTER TABLE `mantenimiento` DISABLE KEYS */;
/*!40000 ALTER TABLE `mantenimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medidor`
--

DROP TABLE IF EXISTS `medidor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medidor` (
  `id_medidor` int NOT NULL AUTO_INCREMENT,
  `id_unidad` int DEFAULT NULL,
  `tipo_servicio` enum('LUZ','AGUA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_medidor` enum('INDIVIDUAL','GENERAL') COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_serie` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_serie_activo` varchar(50) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then `numero_serie` else NULL end)) STORED,
  PRIMARY KEY (`id_medidor`),
  UNIQUE KEY `uq_medidor_serie_activo` (`numero_serie_activo`),
  KEY `fk_medidor_unidad` (`id_unidad`),
  KEY `fk_medidor_usuario_creacion` (`usuario_creacion`),
  KEY `fk_medidor_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_medidor_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_medidor_unidad` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id_unidad`),
  CONSTRAINT `fk_medidor_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_medidor_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_medidor_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `chk_medidor_tipo` CHECK ((((`tipo_medidor` = _utf8mb4'GENERAL') and (`id_unidad` is null)) or ((`tipo_medidor` = _utf8mb4'INDIVIDUAL') and (`id_unidad` is not null))))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medidor`
--

LOCK TABLES `medidor` WRITE;
/*!40000 ALTER TABLE `medidor` DISABLE KEYS */;
/*!40000 ALTER TABLE `medidor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `modulo_sistema`
--

DROP TABLE IF EXISTS `modulo_sistema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `modulo_sistema` (
  `id_modulo` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `orden` tinyint NOT NULL,
  PRIMARY KEY (`id_modulo`),
  UNIQUE KEY `codigo` (`codigo`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modulo_sistema`
--

LOCK TABLES `modulo_sistema` WRITE;
/*!40000 ALTER TABLE `modulo_sistema` DISABLE KEYS */;
INSERT INTO `modulo_sistema` VALUES (1,'UNIDADES','Unidades',1),(2,'INQUILINOS','Inquilinos',2),(3,'CONTRATOS','Contratos',3),(4,'MEDIDORES','Medidores',4),(5,'LECTURAS','Lecturas',5),(6,'RECIBOS','Recibos de servicios',6),(7,'PRORRATEO','Prorrateo',7),(8,'LIQUIDACIONES','Liquidaciones',8),(9,'PAGOS','Pagos',9),(10,'MANTENIMIENTO','Mantenimiento',10),(11,'USUARIOS','Usuarios',11),(12,'ROLES','Roles',12),(13,'PARAMETROS','Par├ímetros',13),(14,'PERIODOS','Periodos',14),(15,'AUDITORIA','Auditor├¡a',15);
/*!40000 ALTER TABLE `modulo_sistema` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pago`
--

DROP TABLE IF EXISTS `pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pago` (
  `id_pago` int NOT NULL AUTO_INCREMENT,
  `id_liquidacion` int NOT NULL,
  `fecha_pago` date NOT NULL,
  `monto_pagado` decimal(10,2) NOT NULL,
  `metodo_pago` enum('EFECTIVO','TRANSFERENCIA','TARJETA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `numero_voucher` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `ruta_evidencia` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id_pago`),
  KEY `fk_pago_liquidacion` (`id_liquidacion`),
  KEY `fk_pago_usuario_creacion` (`usuario_creacion`),
  KEY `fk_pago_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_pago_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_pago_liquidacion` FOREIGN KEY (`id_liquidacion`) REFERENCES `liquidacion` (`id_liquidacion`),
  CONSTRAINT `fk_pago_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_pago_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_pago_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `chk_pago_evidencia` CHECK (((`numero_voucher` is not null) or (`ruta_evidencia` is not null))),
  CONSTRAINT `chk_pago_monto` CHECK ((`monto_pagado` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pago`
--

LOCK TABLES `pago` WRITE;
/*!40000 ALTER TABLE `pago` DISABLE KEYS */;
/*!40000 ALTER TABLE `pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parametro_sistema`
--

DROP TABLE IF EXISTS `parametro_sistema`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `parametro_sistema` (
  `id_parametro` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `valor` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `descripcion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `clave_activa` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then `clave` else NULL end)) STORED,
  PRIMARY KEY (`id_parametro`),
  UNIQUE KEY `uq_parametro_clave_activa` (`clave_activa`),
  KEY `fk_parametro_usuario_creacion` (`usuario_creacion`),
  KEY `fk_parametro_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_parametro_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_parametro_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_parametro_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_parametro_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parametro_sistema`
--

LOCK TABLES `parametro_sistema` WRITE;
/*!40000 ALTER TABLE `parametro_sistema` DISABLE KEYS */;
INSERT INTO `parametro_sistema` (`id_parametro`, `clave`, `valor`, `descripcion`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,'DIAS_ALERTA_VENCIMIENTO','30','Dias de anticipacion para alertar el vencimiento de un contrato (RN-03)','ACTIVO',1,'2026-09-26 23:29:27',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'CRITERIO_PRORRATEO','CONSUMO','Criterio de reparto del excedente de areas comunes: CONSUMO o AREA (S-04)','ACTIVO',1,'2026-09-26 23:29:27',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'DIAS_VENCIMIENTO_LIQUIDACION','10','Dias entre la emision y el vencimiento de una liquidacion mensual','ACTIVO',1,'2026-09-27 00:23:40','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `parametro_sistema` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `periodo_facturacion`
--

DROP TABLE IF EXISTS `periodo_facturacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `periodo_facturacion` (
  `id_periodo` int NOT NULL AUTO_INCREMENT,
  `periodo` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado` enum('EN_PROGRESO','COMPLETO','LIQUIDADO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'EN_PROGRESO',
  `usuario_creacion` int DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_periodo`),
  UNIQUE KEY `uq_periodo` (`periodo`),
  KEY `fk_periodo_usuario_creacion` (`usuario_creacion`),
  CONSTRAINT `fk_periodo_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `periodo_facturacion`
--

LOCK TABLES `periodo_facturacion` WRITE;
/*!40000 ALTER TABLE `periodo_facturacion` DISABLE KEYS */;
/*!40000 ALTER TABLE `periodo_facturacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prorrateo`
--

DROP TABLE IF EXISTS `prorrateo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prorrateo` (
  `id_prorrateo` int NOT NULL AUTO_INCREMENT,
  `id_unidad` int NOT NULL,
  `periodo` char(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_servicio` enum('LUZ','AGUA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_recibo` int DEFAULT NULL,
  `consumo_individual` decimal(10,2) NOT NULL,
  `consumo_comun_asignado` decimal(10,2) NOT NULL DEFAULT '0.00',
  `costo_unitario` decimal(10,4) NOT NULL,
  `monto_calculado` decimal(10,2) NOT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `prorrateo_key_activo` varchar(30) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then concat(`id_unidad`,_utf8mb4':',`periodo`,_utf8mb4':',`tipo_servicio`) else NULL end)) STORED,
  PRIMARY KEY (`id_prorrateo`),
  UNIQUE KEY `uq_prorrateo_activo` (`prorrateo_key_activo`),
  KEY `fk_prorrateo_usuario_creacion` (`usuario_creacion`),
  KEY `fk_prorrateo_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_prorrateo_usuario_eliminacion` (`usuario_eliminacion`),
  KEY `idx_prorrateo_unidad` (`id_unidad`),
  KEY `fk_prorrateo_periodo` (`periodo`),
  KEY `idx_prorrateo_recibo` (`id_recibo`),
  CONSTRAINT `fk_prorrateo_periodo` FOREIGN KEY (`periodo`) REFERENCES `periodo_facturacion` (`periodo`),
  CONSTRAINT `fk_prorrateo_recibo` FOREIGN KEY (`id_recibo`) REFERENCES `recibo_servicio` (`id_recibo`),
  CONSTRAINT `fk_prorrateo_unidad` FOREIGN KEY (`id_unidad`) REFERENCES `unidad` (`id_unidad`),
  CONSTRAINT `fk_prorrateo_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_prorrateo_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_prorrateo_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prorrateo`
--

LOCK TABLES `prorrateo` WRITE;
/*!40000 ALTER TABLE `prorrateo` DISABLE KEYS */;
/*!40000 ALTER TABLE `prorrateo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recibo_servicio`
--

DROP TABLE IF EXISTS `recibo_servicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recibo_servicio` (
  `id_recibo` int NOT NULL AUTO_INCREMENT,
  `periodo` char(7) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `tipo_servicio` enum('LUZ','AGUA') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `monto_total` decimal(10,2) NOT NULL,
  `fecha_emision` date NOT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `ruta_documento` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `recibo_periodo_servicio_activo` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then concat(`periodo`,_utf8mb4':',`tipo_servicio`) else NULL end)) STORED,
  PRIMARY KEY (`id_recibo`),
  UNIQUE KEY `uq_recibo_periodo_servicio_activo` (`recibo_periodo_servicio_activo`),
  KEY `idx_recibo_periodo` (`periodo`),
  KEY `fk_recibo_usuario_creacion` (`usuario_creacion`),
  KEY `fk_recibo_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_recibo_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_recibo_periodo` FOREIGN KEY (`periodo`) REFERENCES `periodo_facturacion` (`periodo`),
  CONSTRAINT `fk_recibo_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_recibo_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_recibo_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `chk_recibo_monto` CHECK ((`monto_total` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recibo_servicio`
--

LOCK TABLES `recibo_servicio` WRITE;
/*!40000 ALTER TABLE `recibo_servicio` DISABLE KEYS */;
/*!40000 ALTER TABLE `recibo_servicio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `refresh_token`
--

DROP TABLE IF EXISTS `refresh_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `refresh_token` (
  `id_token_refresco` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `fecha_expiracion` datetime NOT NULL,
  PRIMARY KEY (`id_token_refresco`),
  UNIQUE KEY `token` (`token`),
  KEY `fk_refresh_usuario` (`id_usuario`),
  CONSTRAINT `fk_refresh_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=54 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `refresh_token`
--

LOCK TABLES `refresh_token` WRITE;
/*!40000 ALTER TABLE `refresh_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `refresh_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol`
--

DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre_activo` varchar(50) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then `nombre` else NULL end)) STORED,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `uq_rol_nombre_activo` (`nombre_activo`),
  KEY `fk_rol_usuario_creacion` (`usuario_creacion`),
  KEY `fk_rol_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_rol_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_rol_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_rol_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_rol_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol`
--

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` (`id_rol`, `nombre`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,'ADMINISTRADOR','ACTIVO',NULL,'2026-09-01 21:34:14',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'COBRANZAS','ACTIVO',NULL,'2026-09-01 21:34:14',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'MANTENIMIENTO','ACTIVO',NULL,'2026-09-01 21:34:14',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,'GERENCIA','ACTIVO',NULL,'2026-09-01 21:34:14',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,'GERENCIA_CONSULTA','ACTIVO',1,'2026-09-27 23:07:35','0:0:0:0:0:0:0:1',1,'2026-09-28 00:32:46','0:0:0:0:0:0:0:1',NULL,NULL,NULL);
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rol_permiso`
--

DROP TABLE IF EXISTS `rol_permiso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol_permiso` (
  `id_rol_permiso` int NOT NULL AUTO_INCREMENT,
  `id_rol` int NOT NULL,
  `id_modulo` int NOT NULL,
  `nivel_acceso` enum('LECTURA','ESCRITURA') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ESCRITURA',
  PRIMARY KEY (`id_rol_permiso`),
  UNIQUE KEY `uq_rol_modulo` (`id_rol`,`id_modulo`),
  KEY `fk_rolpermiso_modulo` (`id_modulo`),
  CONSTRAINT `fk_rolpermiso_modulo` FOREIGN KEY (`id_modulo`) REFERENCES `modulo_sistema` (`id_modulo`) ON DELETE CASCADE,
  CONSTRAINT `fk_rolpermiso_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rol_permiso`
--

LOCK TABLES `rol_permiso` WRITE;
/*!40000 ALTER TABLE `rol_permiso` DISABLE KEYS */;
INSERT INTO `rol_permiso` VALUES (1,1,15,'ESCRITURA'),(2,1,3,'ESCRITURA'),(3,1,2,'ESCRITURA'),(4,1,5,'ESCRITURA'),(5,1,8,'ESCRITURA'),(6,1,10,'ESCRITURA'),(7,1,4,'ESCRITURA'),(8,1,9,'ESCRITURA'),(9,1,13,'ESCRITURA'),(10,1,14,'ESCRITURA'),(11,1,7,'ESCRITURA'),(12,1,6,'ESCRITURA'),(13,1,12,'ESCRITURA'),(14,1,1,'ESCRITURA'),(15,1,11,'ESCRITURA'),(16,2,3,'ESCRITURA'),(17,2,2,'ESCRITURA'),(18,2,8,'ESCRITURA'),(19,2,9,'ESCRITURA'),(23,3,10,'ESCRITURA'),(24,3,1,'ESCRITURA'),(26,5,1,'LECTURA'),(27,5,2,'LECTURA'),(28,5,3,'LECTURA'),(29,5,4,'LECTURA'),(30,5,5,'LECTURA'),(31,5,6,'LECTURA'),(32,5,7,'LECTURA'),(33,5,8,'LECTURA'),(34,5,9,'LECTURA'),(35,5,10,'LECTURA'),(36,5,11,'LECTURA'),(37,5,12,'LECTURA'),(38,5,13,'LECTURA'),(39,5,14,'LECTURA'),(40,5,15,'LECTURA');
/*!40000 ALTER TABLE `rol_permiso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `unidad`
--

DROP TABLE IF EXISTS `unidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `unidad` (
  `id_unidad` int NOT NULL AUTO_INCREMENT,
  `piso` tinyint NOT NULL,
  `numero_unidad` varchar(10) COLLATE utf8mb4_unicode_ci NOT NULL,
  `area_m2` decimal(7,2) NOT NULL,
  `caracteristicas` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('DISPONIBLE','OCUPADO','MANTENIMIENTO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'DISPONIBLE',
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `numero_unidad_activo` varchar(10) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then `numero_unidad` else NULL end)) STORED,
  PRIMARY KEY (`id_unidad`),
  UNIQUE KEY `uq_unidad_numero_activo` (`numero_unidad_activo`),
  KEY `fk_unidad_usuario_creacion` (`usuario_creacion`),
  KEY `fk_unidad_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_unidad_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_unidad_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_unidad_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_unidad_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `chk_unidad_area` CHECK (((`estado_registro` = _cp850'ELIMINADO') or (`area_m2` >= 40))),
  CONSTRAINT `chk_unidad_piso` CHECK ((`piso` between 1 and 10))
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `unidad`
--

LOCK TABLES `unidad` WRITE;
/*!40000 ALTER TABLE `unidad` DISABLE KEYS */;
/*!40000 ALTER TABLE `unidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `id_rol` int NOT NULL,
  `nombre_usuario` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nombre_completo` varchar(150) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(150) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `mfa_habilitado` tinyint(1) NOT NULL DEFAULT '1',
  `mfa_secret` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `mfa_confirmado` tinyint(1) NOT NULL DEFAULT '0',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado_registro` enum('ACTIVO','ELIMINADO') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVO',
  `usuario_creacion` int DEFAULT NULL,
  `ip_creacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_actualizacion` int DEFAULT NULL,
  `fecha_actualizacion` datetime DEFAULT NULL,
  `ip_actualizacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `usuario_eliminacion` int DEFAULT NULL,
  `fecha_eliminacion` datetime DEFAULT NULL,
  `ip_eliminacion` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `nombre_usuario_activo` varchar(50) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then `nombre_usuario` else NULL end)) STORED,
  `email_activo` varchar(150) COLLATE utf8mb4_unicode_ci GENERATED ALWAYS AS ((case when (`estado_registro` = _utf8mb4'ACTIVO') then `email` else NULL end)) STORED,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uq_usuario_nombre_activo` (`nombre_usuario_activo`),
  UNIQUE KEY `uq_usuario_email_activo` (`email_activo`),
  KEY `fk_usuario_rol` (`id_rol`),
  KEY `fk_usuario_usuario_creacion` (`usuario_creacion`),
  KEY `fk_usuario_usuario_actualizacion` (`usuario_actualizacion`),
  KEY `fk_usuario_usuario_eliminacion` (`usuario_eliminacion`),
  CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`),
  CONSTRAINT `fk_usuario_usuario_actualizacion` FOREIGN KEY (`usuario_actualizacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_usuario_usuario_creacion` FOREIGN KEY (`usuario_creacion`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_usuario_usuario_eliminacion` FOREIGN KEY (`usuario_eliminacion`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` (`id_usuario`, `id_rol`, `nombre_usuario`, `password_hash`, `nombre_completo`, `email`, `estado`, `mfa_habilitado`, `mfa_secret`, `mfa_confirmado`, `fecha_creacion`, `estado_registro`, `usuario_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,1,'admin','$2b$10$dFSjXLIi2JxjBAn75pqK9ed.BCXDZW1u0fH0HTnT.83sl8NdrWHJq','Administrador General','admin@dataverse.test','ACTIVO',1,NULL,0,'2026-09-01 21:34:14','ACTIVO',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,2,'cobranzas','$2b$10$XluJN6dwIN4iGZeEILKgieOOHiKv4O2Z1BuZMsMAYpfYh6OA15xS.','Encargado de Cobranzas','cobranzas@dataverse.test','ACTIVO',1,NULL,0,'2026-09-01 22:28:20','ACTIVO',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,3,'mantenimiento','$2b$10$wXDyvIOONPgUdY77XdAZn.csP9Du1gCArqJOX6h72VS9a5NqNyzeO','Encargado de Mantenimiento','mantenimiento@dataverse.test','ACTIVO',1,NULL,0,'2026-09-01 22:28:20','ACTIVO',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,5,'consulta','$2b$10$NzAUYFxduT9t.1L7/EZEze3VCg0yCynsqbSEWDS7/SpAmJ2y7hzBm','Usuario de Consulta (solo lectura)','consulta@dataverse.test','ACTIVO',1,NULL,0,'2026-09-27 23:08:04','ACTIVO',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `v_estado_deuda_inquilino`
--

DROP TABLE IF EXISTS `v_estado_deuda_inquilino`;
/*!50001 DROP VIEW IF EXISTS `v_estado_deuda_inquilino`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_estado_deuda_inquilino` AS SELECT 
 1 AS `id_inquilino`,
 1 AS `deuda_total`,
 1 AS `liquidaciones_con_saldo`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `v_saldo_liquidacion`
--

DROP TABLE IF EXISTS `v_saldo_liquidacion`;
/*!50001 DROP VIEW IF EXISTS `v_saldo_liquidacion`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `v_saldo_liquidacion` AS SELECT 
 1 AS `id_liquidacion`,
 1 AS `id_contrato`,
 1 AS `id_unidad`,
 1 AS `id_inquilino`,
 1 AS `periodo`,
 1 AS `monto_total`,
 1 AS `total_pagado`,
 1 AS `saldo_pendiente`*/;
SET character_set_client = @saved_cs_client;

--
-- Final view structure for view `v_estado_deuda_inquilino`
--

/*!50001 DROP VIEW IF EXISTS `v_estado_deuda_inquilino`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_estado_deuda_inquilino` AS select `v_saldo_liquidacion`.`id_inquilino` AS `id_inquilino`,sum(`v_saldo_liquidacion`.`saldo_pendiente`) AS `deuda_total`,sum((case when (`v_saldo_liquidacion`.`saldo_pendiente` > 0) then 1 else 0 end)) AS `liquidaciones_con_saldo` from `v_saldo_liquidacion` group by `v_saldo_liquidacion`.`id_inquilino` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `v_saldo_liquidacion`
--

/*!50001 DROP VIEW IF EXISTS `v_saldo_liquidacion`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `v_saldo_liquidacion` AS select `l`.`id_liquidacion` AS `id_liquidacion`,`l`.`id_contrato` AS `id_contrato`,`c`.`id_unidad` AS `id_unidad`,`c`.`id_inquilino` AS `id_inquilino`,`l`.`periodo` AS `periodo`,`l`.`monto_total` AS `monto_total`,coalesce(sum(`p`.`monto_pagado`),0) AS `total_pagado`,(`l`.`monto_total` - coalesce(sum(`p`.`monto_pagado`),0)) AS `saldo_pendiente` from ((`liquidacion` `l` join `contrato` `c` on((`c`.`id_contrato` = `l`.`id_contrato`))) left join `pago` `p` on(((`p`.`id_liquidacion` = `l`.`id_liquidacion`) and (`p`.`estado_registro` = 'ACTIVO')))) where ((`l`.`estado_registro` = 'ACTIVO') and (`l`.`estado` <> 'BORRADOR')) group by `l`.`id_liquidacion`,`l`.`id_contrato`,`c`.`id_unidad`,`c`.`id_inquilino`,`l`.`periodo`,`l`.`monto_total` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
SET @@SESSION.SQL_LOG_BIN = @MYSQLDUMP_TEMP_LOG_BIN;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-28  0:49:57
