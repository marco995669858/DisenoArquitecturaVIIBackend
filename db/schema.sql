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
-- GTID state at the beginning of the backup 
--

SET @@GLOBAL.GTID_PURGED=/*!80000 '+'*/ '27bdcb26-3097-11f1-ba24-e8bfe175558e:1-401';

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
INSERT INTO `contrato` (`id_contrato`, `id_unidad`, `id_inquilino`, `fecha_inicio`, `fecha_fin`, `monto_alquiler`, `ruta_contrato_digital`, `estado`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,4,2,'2026-09-01','2027-08-31',950.00,'','VIGENTE','ELIMINADO',1,'2026-09-01 22:23:41','0:0:0:0:0:0:0:1',1,'2026-09-01 22:40:48','0:0:0:0:0:0:0:1',1,'2026-09-01 22:44:00','0:0:0:0:0:0:0:1'),(2,5,3,'2026-09-01','2027-08-31',500.00,NULL,'VIGENTE','ELIMINADO',1,'2026-09-01 22:45:29','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-01 22:45:37','0:0:0:0:0:0:0:1'),(3,10,4,'2026-09-01','2026-12-18',670.00,'','VIGENTE','ELIMINADO',1,'2026-09-01 23:01:04','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-01 23:01:07','0:0:0:0:0:0:0:1'),(4,10,5,'2026-09-01','2026-12-31',670.00,'','VIGENTE','ACTIVO',1,'2026-09-01 23:01:23','0:0:0:0:0:0:0:1',1,'2026-09-27 02:12:52','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(5,13,4,'2026-06-01','2026-09-20',500.00,NULL,'VENCIDO','ELIMINADO',1,'2026-09-27 00:54:06','0:0:0:0:0:0:0:1',1,'2026-09-27 00:54:06','0:0:0:0:0:0:0:1',1,'2026-09-27 00:54:28','0:0:0:0:0:0:0:1'),(6,13,4,'2026-09-21','2026-10-10',520.00,NULL,'VIGENTE','ELIMINADO',1,'2026-09-27 00:54:18','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:54:28','0:0:0:0:0:0:0:1'),(8,15,4,'2026-09-27','2026-10-15',670.00,'','VIGENTE','ACTIVO',1,'2026-09-27 02:12:08','0:0:0:0:0:0:0:1',1,'2026-09-27 21:38:02','127.0.0.1',NULL,NULL,NULL),(9,18,4,'2026-09-28','2026-12-31',500.00,'','VIGENTE','ACTIVO',1,'2026-09-28 00:40:11','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL);
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
INSERT INTO `detalle_liquidacion` VALUES (1,1,'Alquiler','ALQUILER',950.00,NULL,NULL,'ACTIVO',1,'2026-09-26 23:38:39',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,2,'Alquiler','ALQUILER',670.00,NULL,NULL,'ACTIVO',1,'2026-09-26 23:38:39',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(4,1,'Consumo de luz','LUZ',45.05,NULL,NULL,'ACTIVO',1,'2026-09-26 23:38:39',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(5,2,'Consumo de luz','LUZ',50.00,NULL,NULL,'ACTIVO',1,'2026-09-26 23:38:39',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(7,1,'Consumo de agua','AGUA',30.00,NULL,NULL,'ACTIVO',1,'2026-09-26 23:38:39',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(8,2,'Consumo de agua','AGUA',10.00,NULL,NULL,'ACTIVO',1,'2026-09-26 23:38:39',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(10,2,'Otros conceptos','OTRO',100.00,NULL,NULL,'ACTIVO',1,'2026-09-26 23:38:39',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(11,3,'Alquiler 2026-10','ALQUILER',670.00,NULL,NULL,'ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(12,3,'Consumo de luz 2026-10','LUZ',450.00,3,NULL,'ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(13,3,'Reparacion de grifo (danio del inquilino)','MANTENIMIENTO',80.00,NULL,4,'ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(14,4,'Alquiler 2026-09','ALQUILER',670.00,NULL,NULL,'ACTIVO',1,'2026-09-27 02:26:58','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL);
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
INSERT INTO `inquilino` (`id_inquilino`, `tipo_documento`, `numero_documento`, `nombre_completo`, `telefono`, `email`, `direccion`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,'DNI','12345678','Rosa Medina','999999999','rosa@test.com',NULL,'ELIMINADO',1,'2026-09-01 22:23:32','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-01 22:39:59','0:0:0:0:0:0:0:1'),(2,'DNI','75058989','Marco Antonio Muñoz Melendez','929369150','marcoamm.0117@gmail.com','','ELIMINADO',2,'2026-09-01 22:37:05','0:0:0:0:0:0:0:1',2,'2026-09-01 22:37:09','0:0:0:0:0:0:0:1',1,'2026-09-01 22:40:53','0:0:0:0:0:0:0:1'),(3,'DNI','99999999','Prueba Integridad',NULL,NULL,NULL,'ELIMINADO',1,'2026-09-01 22:45:29','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-01 22:45:37','0:0:0:0:0:0:0:1'),(4,'DNI','75058989','Marco Antonio Muñoz Melendez','929369150','marcoamm.0117@gmail.com','','ACTIVO',1,'2026-09-01 23:00:42','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(5,'DNI','75058981','Maria Jesus Melendes Herrera','999999999','marcos99566898@gmail.com','','ACTIVO',1,'2026-09-27 01:56:19','0:0:0:0:0:0:0:1',1,'2026-09-27 02:03:23','0:0:0:0:0:0:0:1',NULL,NULL,NULL);
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
INSERT INTO `lectura_medidor` (`id_lectura`, `id_medidor`, `periodo`, `lectura_anterior`, `lectura_actual`, `fecha_lectura`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,1,'2026-09',120.00,165.00,'2026-09-01','ACTIVO',1,'2026-09-01 22:23:50','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(3,4,'2026-09',3.00,33.00,'2026-09-02','ELIMINADO',1,'2026-09-02 14:43:22','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:16:41','0:0:0:0:0:0:0:1'),(4,1,'2026-11',100.00,150.00,'2026-11-01','ELIMINADO',1,'2026-09-27 00:04:47','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:05:04','0:0:0:0:0:0:0:1'),(5,2,'2026-09',200.00,500.00,'2026-09-30','ELIMINADO',1,'2026-09-27 00:14:11','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:14:41','0:0:0:0:0:0:0:1'),(6,2,'2026-09',200.00,500.00,'2026-09-30','ACTIVO',1,'2026-09-27 00:16:53','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(7,1,'2026-10',165.00,210.00,'2026-10-30','ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',1,'2026-09-27 02:20:14','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(8,2,'2026-10',500.00,800.00,'2026-10-30','ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(9,5,'2026-09',50.00,50.00,'2026-09-30','ACTIVO',1,'2026-09-27 02:16:32','0:0:0:0:0:0:0:1',1,'2026-09-27 02:19:58','0:0:0:0:0:0:0:1',NULL,NULL,NULL);
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
INSERT INTO `liquidacion` (`id_liquidacion`, `id_contrato`, `periodo`, `monto_alquiler`, `monto_luz`, `monto_agua`, `monto_otros`, `fecha_emision`, `fecha_vencimiento`, `estado`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,1,'2026-09',950.00,45.05,30.00,0.00,'2026-09-01','2026-10-15','PAGADO','ACTIVO',1,'2026-09-01 22:23:50','0:0:0:0:0:0:0:1',NULL,'2026-09-26 23:38:39',NULL,NULL,NULL,NULL),(2,4,'2026-09',670.00,50.00,10.00,100.00,'2026-09-02','2026-12-31','PAGADO','ACTIVO',1,'2026-09-02 14:44:24','0:0:0:0:0:0:0:1',1,'2026-09-27 02:26:56','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(3,4,'2026-10',670.00,450.00,0.00,80.00,'2026-09-27','2026-10-07','PAGADO','ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',1,'2026-09-27 02:34:27','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(4,8,'2026-09',670.00,0.00,0.00,0.00,'2026-09-27','2026-09-20','VENCIDO','ACTIVO',1,'2026-09-27 02:26:58','0:0:0:0:0:0:0:1',1,'2026-09-27 21:38:02','127.0.0.1',NULL,NULL,NULL);
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
INSERT INTO `mantenimiento` VALUES (1,NULL,NULL,'Pintado de fachada','2026-09-01','AMBOS','Pinturas XYZ',800.00,0.00,800.00,0,'ELIMINADO',1,'2026-09-01 22:24:05','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 01:32:41','127.0.0.1'),(2,3,NULL,'Cambio de tuberia','2026-09-02','MATERIAL','Ferreteria ABC',150.00,0.00,150.00,0,'ACTIVO',1,'2026-09-01 22:24:20','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(3,NULL,NULL,'Prueba desglose','2026-09-27','AMBOS',NULL,300.00,200.00,500.00,0,'ELIMINADO',1,'2026-09-27 00:04:47','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:05:04','0:0:0:0:0:0:0:1'),(4,10,NULL,'Reparacion de grifo (danio del inquilino)','2026-10-05','MANO_DE_OBRA',NULL,0.00,80.00,80.00,1,'ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL);
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
INSERT INTO `medidor` (`id_medidor`, `id_unidad`, `tipo_servicio`, `tipo_medidor`, `numero_serie`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,10,'LUZ','INDIVIDUAL','LUZ-301','ACTIVO',1,'2026-09-01 22:23:41','0:0:0:0:0:0:0:1',1,'2026-09-01 23:03:12','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(2,NULL,'LUZ','GENERAL','LUZ-GEN-01','ACTIVO',1,'2026-09-01 22:23:41','0:0:0:0:0:0:0:1',1,'2026-09-01 23:02:50','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(4,10,'LUZ','INDIVIDUAL','LUZ-3021','ELIMINADO',1,'2026-09-02 14:42:10','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:16:42','0:0:0:0:0:0:0:1'),(5,15,'LUZ','INDIVIDUAL','LUZ-TD-PS-2-001','ACTIVO',1,'2026-09-27 02:15:43','0:0:0:0:0:0:0:1',1,'2026-09-27 02:15:52','0:0:0:0:0:0:0:1',NULL,NULL,NULL);
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
INSERT INTO `pago` VALUES (1,1,'2026-09-05',1025.05,'TRANSFERENCIA','TR-001','','ACTIVO',1,'2026-09-01 22:24:05','0:0:0:0:0:0:0:1',1,'2026-09-01 23:05:54','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(4,2,'2026-09-26',300.00,'EFECTIVO','PRUEBA-1',NULL,'ELIMINADO',1,'2026-09-26 23:52:36','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-26 23:52:58','0:0:0:0:0:0:0:1'),(5,2,'2026-09-26',530.00,'TRANSFERENCIA','PRUEBA-2',NULL,'ELIMINADO',1,'2026-09-26 23:52:48','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-26 23:52:48','0:0:0:0:0:0:0:1'),(6,3,'2026-09-27',1200.00,'EFECTIVO','GM-1','asdasd','ACTIVO',1,'2026-09-27 02:34:06','0:0:0:0:0:0:0:1',1,'2026-09-27 02:34:27','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(7,4,'2026-09-27',500.00,'EFECTIVO','TD-001','DASD','ACTIVO',1,'2026-09-27 10:28:49','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL);
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
INSERT INTO `parametro_sistema` (`id_parametro`, `clave`, `valor`, `descripcion`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,'DIAS_ALERTA_VENCIMIENTO','30','Dias de anticipacion para alertar el vencimiento de un contrato (RN-03)','ACTIVO',1,'2026-09-26 23:29:27',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(2,'CRITERIO_PRORRATEO','CONSUMO','Criterio de reparto del excedente de areas comunes: CONSUMO o AREA (S-04)','ACTIVO',1,'2026-09-26 23:29:27',NULL,NULL,NULL,NULL,NULL,NULL,NULL),(3,'PRUEBA_TEMP','1','temporal','ELIMINADO',1,'2026-09-27 00:04:30','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:05:04','0:0:0:0:0:0:0:1'),(5,'DIAS_VENCIMIENTO_LIQUIDACION','10','Dias entre la emision y el vencimiento de una liquidacion mensual','ACTIVO',1,'2026-09-27 00:23:40','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL);
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
INSERT INTO `periodo_facturacion` VALUES (1,'2026-09','LIQUIDADO',1,'2026-09-26 23:29:41'),(2,'2026-10','LIQUIDADO',1,'2026-09-27 00:04:30'),(3,'2026-11','EN_PROGRESO',1,'2026-09-27 00:04:47');
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
INSERT INTO `prorrateo` (`id_prorrateo`, `id_unidad`, `periodo`, `tipo_servicio`, `id_recibo`, `consumo_individual`, `consumo_comun_asignado`, `costo_unitario`, `monto_calculado`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,10,'2026-09','LUZ',2,30.00,90.00,1.5000,180.00,'ELIMINADO',1,'2026-09-01 22:23:50','0:0:0:0:0:0:0:1',1,'2026-09-27 00:14:11','0:0:0:0:0:0:0:1',1,'2026-09-27 00:14:40','0:0:0:0:0:0:0:1'),(2,10,'2026-09','LUZ',3,45.00,255.00,1.5000,450.00,'ACTIVO',1,'2026-09-27 00:16:53','0:0:0:0:0:0:0:1',1,'2026-09-27 22:26:01','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(3,10,'2026-10','LUZ',4,45.00,255.00,1.5000,450.00,'ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',1,'2026-09-27 00:24:54','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(4,15,'2026-09','LUZ',3,0.00,0.00,1.5000,0.00,'ACTIVO',1,'2026-09-27 02:23:38','0:0:0:0:0:0:0:1',1,'2026-09-27 22:26:01','0:0:0:0:0:0:0:1',NULL,NULL,NULL);
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
INSERT INTO `recibo_servicio` (`id_recibo`, `periodo`, `tipo_servicio`, `monto_total`, `fecha_emision`, `fecha_vencimiento`, `ruta_documento`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,'2026-10','LUZ',500.00,'2026-10-01',NULL,NULL,'ELIMINADO',1,'2026-09-27 00:04:30','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:05:04','0:0:0:0:0:0:0:1'),(2,'2026-09','LUZ',450.00,'2026-09-28',NULL,NULL,'ELIMINADO',1,'2026-09-27 00:14:11','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-27 00:14:41','0:0:0:0:0:0:0:1'),(3,'2026-09','LUZ',450.00,'2026-09-28',NULL,NULL,'ACTIVO',1,'2026-09-27 00:16:53','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(4,'2026-10','LUZ',450.00,'2026-10-28',NULL,NULL,'ACTIVO',1,'2026-09-27 00:24:24','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL);
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
INSERT INTO `refresh_token` VALUES (2,1,'b9d71f1e-4e84-424d-a383-0827efd8f235','2026-09-02 06:23:24'),(3,1,'338e7f8c-7ffd-47f5-832a-c0b830f4b0eb','2026-09-02 06:23:32'),(4,1,'802f9226-e9cf-458c-84a5-c007bd756676','2026-09-02 06:23:41'),(5,1,'d72a9ec5-ee50-454c-be0d-bbf0376c376c','2026-09-02 06:23:41'),(6,1,'eaf50287-bde0-4446-8600-f8469ac4e3e0','2026-09-02 06:23:50'),(7,1,'0f6225ab-c5fd-4e20-91ca-d1725187bc2a','2026-09-02 06:24:06'),(8,1,'8f119f5a-2186-4d02-a143-1965cbb3c938','2026-09-02 06:24:20'),(9,2,'b6d39e34-b2bf-4802-b749-281d1f5ab3c0','2026-09-02 06:28:27'),(10,3,'4fcbac2b-2eff-467b-a88c-a58c3aede73b','2026-09-02 06:28:27'),(11,2,'d4bb659d-6664-4d26-aeb1-2374c974dcaa','2026-09-02 06:29:03'),(12,3,'caeef01a-550a-4926-9e3b-21b42872609a','2026-09-02 06:29:29'),(13,1,'14380117-cd99-4809-a48e-3217c70f8286','2026-09-02 06:33:11'),(14,2,'d7fe2439-e659-4c71-9692-090c58a15ad3','2026-09-02 06:34:53'),(15,1,'c7624f0f-1d5d-4a98-b414-ac6ce85d63e7','2026-09-02 06:39:33'),(16,1,'20f00dfb-84d1-46ea-a3cf-088afad3f69e','2026-09-02 06:43:50'),(17,1,'f934e6e0-28f6-45c9-9f85-1c39ee3a060f','2026-09-02 06:45:29'),(18,1,'7428a50e-4680-4962-9fe1-490ed7155de0','2026-09-02 06:45:37'),(19,1,'b8a9e208-ad4d-4ac5-93a7-54320b54b3d9','2026-09-02 06:53:01'),(20,1,'18c392f0-a107-4ada-aee4-abe26f7c402a','2026-09-02 07:05:01'),(21,1,'706e8a7c-d94d-4afb-a368-e8dcaf99514f','2026-09-02 22:41:32'),(22,1,'327c8d84-3696-4eaf-b62b-d816cb2b5fdd','2026-09-15 04:18:21'),(23,1,'151a5cb3-2fee-4364-a927-ba03f44c0a7d','2026-09-27 07:52:25'),(24,1,'e40d9f80-88b4-4e4a-b2d6-83a30e2ab787','2026-09-27 08:04:30'),(25,1,'90c95b1b-eaad-4b6a-afeb-d37e9f2d92ef','2026-09-27 08:23:40'),(26,1,'549b30ee-5225-4839-81ab-d9366f236ca7','2026-09-27 08:45:22'),(27,4,'277e5c97-06ec-4ec2-a48e-05845c7428d6','2026-09-27 08:45:43'),(28,4,'03005d37-f48f-4cdf-8c1d-d6289e4ad6da','2026-09-27 08:45:44'),(29,1,'787a60cc-c4f1-4fa7-b868-ff9523e78670','2026-09-27 08:47:09'),(30,1,'5876d471-68f5-4942-90e2-796619fe66c7','2026-09-27 08:48:41'),(31,1,'6e69dbad-cd52-4aa3-bcd9-d4d75414c774','2026-09-27 08:53:39'),(32,1,'442ff00c-2400-405d-84ec-8144b0b10d55','2026-09-27 09:34:40'),(33,2,'f6197738-f8a0-4cb0-9ff5-abd0f31100d5','2026-09-27 09:36:33'),(34,2,'233a7e1c-cfa9-4106-a3a2-a39ced440a35','2026-09-27 10:38:27'),(35,2,'0d440c88-970f-40e2-8d37-784c9879c06c','2026-09-27 10:54:45'),(36,2,'4ffe54a7-e80f-4c69-bfb5-e3921f8efab4','2026-09-27 11:06:56'),(37,2,'ad780301-3c6d-4dc8-b1e9-05b21cb6cc09','2026-09-27 11:14:41'),(38,2,'8c75de9f-9098-4ec1-80a7-6e7196eeb565','2026-09-27 11:15:10'),(39,2,'2f7a9255-aa40-4e6b-bd8c-c46992ceb009','2026-09-27 11:16:05'),(40,2,'c95a2f8e-1439-43e2-9986-c906061b7b4d','2026-09-27 11:16:21'),(41,2,'0c8fa063-a580-4659-afb1-c47712c46838','2026-09-27 11:21:19'),(42,2,'f1c75adf-f481-4f67-98d4-31a6d4df57ab','2026-09-27 11:24:44'),(43,1,'2ef2ab1b-7807-458d-8f4d-9ca7c137fdc6','2026-09-27 18:06:22'),(44,1,'beafc83c-b6c1-420a-a04d-4e8919c3dafb','2026-09-27 18:07:03'),(45,1,'dcf225ba-86c1-4738-b6ae-5b14b63a09b2','2026-09-27 18:09:12'),(46,1,'00b1ccd6-b3bd-4803-a1c8-c12f6272cc5c','2026-09-27 18:16:37'),(47,1,'33a4482f-79d1-49d5-88a4-a0794f0eab1f','2026-09-27 18:18:17'),(48,1,'ee38adcf-ac41-44f7-991f-84b6e5f69e05','2026-09-28 05:17:49'),(49,3,'7ea71369-2d02-4d94-bc58-c1e1b0f054a9','2026-09-28 06:20:13'),(50,5,'c1b7913a-fbe5-4211-b994-bb4418f34626','2026-09-28 07:08:49'),(51,1,'52046217-f1b3-4400-81d0-316fc18886b6','2026-09-28 07:27:40'),(52,1,'008c0ad7-32ec-40da-8859-6f43049ba5ec','2026-09-28 07:30:03'),(53,1,'006d4032-5277-4069-afee-c6af729f3182','2026-09-28 07:32:21');
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
INSERT INTO `unidad` (`id_unidad`, `piso`, `numero_unidad`, `area_m2`, `caracteristicas`, `estado`, `estado_registro`, `usuario_creacion`, `fecha_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (3,3,'301',65.50,'2 dorm','DISPONIBLE','ELIMINADO',1,'2026-09-01 22:23:32','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-01 22:24:06','0:0:0:0:0:0:0:1'),(4,2,'GM',2.00,'Un cuarto amplio','DISPONIBLE','ELIMINADO',1,'2026-09-01 22:40:26','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-01 22:42:46','0:0:0:0:0:0:0:1'),(5,9,'901',60.00,NULL,'DISPONIBLE','ELIMINADO',1,'2026-09-01 22:45:29','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-01 22:45:37','0:0:0:0:0:0:0:1'),(10,1,'GM',40.00,'','OCUPADO','ACTIVO',1,'2026-09-01 22:53:01','0:0:0:0:0:0:0:1',1,'2026-09-27 10:10:19','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(13,3,'em',40.00,'Prueba','DISPONIBLE','ACTIVO',1,'2026-09-01 22:56:22','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(15,2,'TD',40.00,'Prueba','OCUPADO','ACTIVO',1,'2026-09-27 01:54:01','0:0:0:0:0:0:0:1',1,'2026-09-27 02:12:08','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(16,2,'TD-1',40.00,'Prueba caracteristicas','DISPONIBLE','ACTIVO',1,'2026-09-27 10:20:08','0:0:0:0:0:0:0:1',NULL,NULL,NULL,NULL,NULL,NULL),(17,1,'CUARTO 1',40.00,'Esto es una prueba','DISPONIBLE','ELIMINADO',1,'2026-09-28 00:38:37','0:0:0:0:0:0:0:1',NULL,NULL,NULL,1,'2026-09-28 00:38:46','0:0:0:0:0:0:0:1'),(18,1,'CUARTO 1',40.00,'Prueba','OCUPADO','ACTIVO',1,'2026-09-28 00:38:57','0:0:0:0:0:0:0:1',1,'2026-09-28 00:40:11','0:0:0:0:0:0:0:1',NULL,NULL,NULL);
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
INSERT INTO `usuario` (`id_usuario`, `id_rol`, `nombre_usuario`, `password_hash`, `nombre_completo`, `email`, `estado`, `mfa_habilitado`, `mfa_secret`, `mfa_confirmado`, `fecha_creacion`, `estado_registro`, `usuario_creacion`, `ip_creacion`, `usuario_actualizacion`, `fecha_actualizacion`, `ip_actualizacion`, `usuario_eliminacion`, `fecha_eliminacion`, `ip_eliminacion`) VALUES (1,1,'admin','$2a$10$uT7TfwgtEZbpwY5nm0O/l.gUiPxjnDjyEZAO3Tn4.U8E/NzoLsOw6','Administrador General','admin@milanoasociados.com','ACTIVO',1,'OLOAPSJCKHUAM4Y3R2WM4Z3ZQKD5UKEF',1,'2026-09-01 21:34:14','ACTIVO',NULL,NULL,1,'2026-09-27 23:30:03','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(2,2,'cobranzas','$2a$10$zv/Hb.ybx9fVE3dBR6PXJe7iuKDRWfAUM0TBdQsuDLiW.OY.sRC1m','Encargado de Cobranzas','cobranzas@milanoasociados.com','ACTIVO',1,'PHQ4EFZLCZWW2YX3BM5XDRLISEZWHAX2',1,'2026-09-01 22:28:20','ACTIVO',NULL,NULL,2,'2026-09-27 03:24:44','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(3,3,'mantenimiento','$2a$10$nB5C.7zBcb9RC5Wgwz5iZOPmy9yB/1C5A6k8KQHWxz4Jn04gbsN.i','Encargado de Mantenimiento','mantenimiento@milanoasociados.com','ACTIVO',1,'2VGEM5HSKRBLAQ6DR53NDFD46HFG7YVX',1,'2026-09-01 22:28:20','ACTIVO',NULL,NULL,3,'2026-09-27 22:20:12','0:0:0:0:0:0:0:1',NULL,NULL,NULL),(4,3,'prueba.qa','$2a$10$Y.ZHK0Sn5tPKCCqph0spj.xGAPCEtCA5T39NY7Z2.qEFRYjb.reXC','Usuario QA Editado','qa@dataverse.test','INACTIVO',1,NULL,0,'2026-09-27 00:45:22','ELIMINADO',1,'0:0:0:0:0:0:0:1',1,'2026-09-27 00:47:09','0:0:0:0:0:0:0:1',1,'2026-09-27 00:48:41','0:0:0:0:0:0:0:1'),(5,5,'consulta','$2a$10$DauAP83S.l5N7VYPlqIyI.NAsMqaEd5XGrf6m8Ef.8SiwDIwczbaO','Marco Antonio Muñoz Melendez','oti125@oefa.gob.pe','ACTIVO',1,'4XSNJIL6MSXWKL7HLV474DYI4Y5PBRVM',1,'2026-09-27 23:08:04','ACTIVO',1,'0:0:0:0:0:0:0:1',5,'2026-09-27 23:08:49','0:0:0:0:0:0:0:1',NULL,NULL,NULL);
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
