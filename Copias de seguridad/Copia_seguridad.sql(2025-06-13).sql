-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: qtfood2
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (12,'Entradas'),(13,'Sopas'),(14,'Acompañamientos'),(15,'Pescado'),(16,'Ternera'),(17,'Cerdo'),(18,'Pollo'),(19,'Bebidas'),(20,'Postres'),(22,'Oferta');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_pedido`
--

DROP TABLE IF EXISTS `detalle_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `detalle_pedido` (
  `id_detalle` int(11) NOT NULL AUTO_INCREMENT,
  `id_pedido` int(11) NOT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `cantidad` int(11) NOT NULL CHECK (`cantidad` > 0),
  `precio_unitario` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `id_pedido` (`id_pedido`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `detalle_pedido_ibfk_1` FOREIGN KEY (`id_pedido`) REFERENCES `pedidos` (`id_pedido`) ON DELETE CASCADE,
  CONSTRAINT `detalle_pedido_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=46 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_pedido`
--

LOCK TABLES `detalle_pedido` WRITE;
/*!40000 ALTER TABLE `detalle_pedido` DISABLE KEYS */;
INSERT INTO `detalle_pedido` VALUES (1,1,1,2,15.50),(2,1,3,1,10.75),(3,1,NULL,3,20.00),(4,1,5,5,5.99),(17,9,NULL,1,20.00),(18,9,3,3,10.75);
/*!40000 ALTER TABLE `detalle_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `oferta`
--

DROP TABLE IF EXISTS `oferta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oferta` (
  `id_oferta` int(11) NOT NULL AUTO_INCREMENT,
  `id_producto` int(11) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `descuento` decimal(10,2) DEFAULT 0.00,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `estado` enum('activo','inactivo') NOT NULL DEFAULT 'activo',
  PRIMARY KEY (`id_oferta`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `oferta_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `oferta`
--

LOCK TABLES `oferta` WRITE;
/*!40000 ALTER TABLE `oferta` DISABLE KEYS */;
INSERT INTO `oferta` VALUES (1,5,'Descuento por temporada',20.00,'2025-05-11','2025-06-26','activo');
/*!40000 ALTER TABLE `oferta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos`
--

DROP TABLE IF EXISTS `pedidos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedidos` (
  `id_pedido` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `fecha` datetime DEFAULT NULL,
  `total` decimal(10,2) NOT NULL,
  `metodo_pago` varchar(50) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `estado` enum('pendiente','enviado','entregado','cancelado') DEFAULT NULL,
  PRIMARY KEY (`id_pedido`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `pedidos_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos`
--

LOCK TABLES `pedidos` WRITE;
/*!40000 ALTER TABLE `pedidos` DISABLE KEYS */;
INSERT INTO `pedidos` VALUES (1,1,'2025-04-30 01:03:13',150.00,'tarjeta','Calle Ficticia 123, Ciudad, País','cancelado'),(9,2,'2025-05-23 12:06:10',32.25,'Efectivo','Avenida Cordoba 2','enviado');
/*!40000 ALTER TABLE `pedidos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `productos` (
  `id_producto` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio` decimal(10,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `imagen_url` varchar(255) DEFAULT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  `estado` enum('ACTIVO','INACTIVO') DEFAULT 'INACTIVO',
  PRIMARY KEY (`id_producto`),
  KEY `id_categoria` (`id_categoria`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'Ensalada China ','Es un plato fresco y ligero que suele servirse como entrada en restaurantes chinos, especialmente fuera de China. Generalmente incluye brotes de soja, zanahoria rallada, col, jamón cocido en tiras, algas y huevo hilado, todo aderezado con una salsa agridulce o vinagreta de soja y sésamo. No es una receta tradicional china, sino una adaptación popularizada en occidente.',3.80,100,'http://localhost/QTFood/imagenes/ensalada-china-receta.jpg',12,'ACTIVO'),(2,'Ensaladas de gambas','Gambas cocidas con vegetales y aderezo de soja, sésamo y lima.',4.30,100,'http://localhost/QTFood/imagenes/ensalada-de-gambas.jpg',12,'ACTIVO'),(3,'Rollitos de primavera','Fritos y crujientes, rellenos de verduras, carne o mariscos, envueltos en pasta de trigo.',2.00,100,'http://localhost/QTFood/imagenes/1366_2000.jpg',12,'ACTIVO'),(5,'Patatas Fritas con salsa ',' Papas crujientes servidas con salsa agridulce o picante al estilo oriental.',3.30,100,'http://localhost/QTFood/imagenes/salsas-para-patatas-fritas.jpg',12,'ACTIVO'),(20,'Arroz Firto Tres Delicias','Arroz salteado con jamón, gambas, huevo y guisantes.',4.50,100,'http://localhost/QTFood/imagenes/foto-arroz-frito-tres-delicias-receta-scaled.jpg',14,'ACTIVO'),(21,'Sopa de pollo con champiñones',' Caldo ligero con pollo desmenuzado y champiñones, aromatizado con jengibre y soja.',3.30,100,'http://localhost/QTFood/imagenes/champis.jpg',13,'ACTIVO'),(22,'Sopa de maiz','Sopa suave con granos de maíz, huevo batido y a veces pollo, en caldo ligero.',3.30,100,'http://localhost/QTFood/imagenes/spicycornsoup_thumbnail.jpg',13,'ACTIVO'),(23,'Arroz Frito con Cerdo o Pollo',' Arroz salteado con carne, verduras, huevo y salsa de soja.',4.50,100,'http://localhost/QTFood/imagenes/maxresdefault.jpg',14,'ACTIVO'),(24,'Tallarines Fritos con Cerdo',' Fideos salteados con tiras de cerdo, verduras y salsa de soja.',5.10,100,'http://localhost/QTFood/imagenes/images.jpg',14,'ACTIVO'),(25,'Arroz Blanco','Arroz simple cocido al vapor, base neutra para acompañar platos.',2.80,100,'http://localhost/QTFood/imagenes/receta-facil-y-rapida-de-arroz-blanco-920-520.png',14,'ACTIVO'),(26,'Langostinos Fritos ',' Langostinos crujientes rebozados y fritos hasta dorar.',8.00,100,'http://localhost/QTFood/imagenes/langostinos-rebozados.jpg',15,'ACTIVO'),(27,'Sultido de Sushi ','Variedad de piezas de sushi con diferentes pescados, mariscos y arroz.',20.50,20,'http://localhost/QTFood/imagenes/degustacion.jpg',15,'ACTIVO'),(28,'Gambas al Ajillo',' Gambas salteadas en aceite con ajo y guindilla, muy sabrosas y aromáticas.',8.00,100,'http://localhost/QTFood/imagenes/gambas-al-ajillo.jpg',15,'ACTIVO'),(29,'Ternera con salsa curry ','Tiras de ternera cocinadas en salsa cremosa y especiada de curry.',7.50,100,'http://localhost/QTFood/imagenes/receta-ternera-al-curry.jpg',16,'ACTIVO'),(30,'Cerdo con Salsa Agridulce ','Trozos de cerdo fritos y cubiertos con salsa dulce y ligeramente ácida.',6.10,100,'http://localhost/QTFood/imagenes/cerdo-agridulce-salsa-soja.jpg',17,'ACTIVO'),(31,'Pollo con Salsa Picante ',' Trozos de pollo salteados en salsa con sabor fuerte y picante.',6.80,100,'http://localhost/QTFood/imagenes/pollo-en-salsa-picante.jpg',18,'ACTIVO'),(32,'Coca-Cola',' ',2.00,100,'http://localhost/QTFood/imagenes/coca-cola-clsica.jpg',19,'ACTIVO'),(33,'Nestle',' ',2.00,100,'http://localhost/QTFood/imagenes/nestea.jpg',19,'ACTIVO'),(34,'Agua',' ',1.00,1000,'',19,'ACTIVO'),(35,'Dulce de loto','Pasta dulce hecha con semillas de loto, usada en pasteles o bollos.',2.50,50,'http://localhost/QTFood/imagenes/Mooncake_with_double_yolk_and_lotus_seed_paste.jpg',20,'ACTIVO'),(36,'Pastel de Luna','Pastel tradicional relleno de pasta dulce o yema de huevo.',2.50,50,'http://localhost/QTFood/imagenes/mooncake-relleno-sesamo-25-1200x800.jpg',20,'ACTIVO'),(37,'Fanta de Naranja',' ',2.00,100,'http://localhost/QTFood/imagenes/603168.jpg',19,'ACTIVO');
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reserva`
--

DROP TABLE IF EXISTS `reserva`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `reserva` (
  `id_reserva` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `fecha` datetime NOT NULL,
  `n_personas` int(11) NOT NULL CHECK (`n_personas` > 0),
  `estado` enum('Confirmado','Pendiente','Rechazado','Finalizado') NOT NULL DEFAULT 'Pendiente',
  `telefono` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  PRIMARY KEY (`id_reserva`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `reserva_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reserva`
--

LOCK TABLES `reserva` WRITE;
/*!40000 ALTER TABLE `reserva` DISABLE KEYS */;
INSERT INTO `reserva` VALUES (1,2,'2025-05-12 00:00:00',4,'Confirmado','+34 123 456 789','Juan Pérez'),(9,2,'2025-05-28 22:21:00',4,'Rechazado','633345032','daniel');
/*!40000 ALTER TABLE `reserva` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Juan Pérez','juan.perez@email.com','contraseña_segura123'),(2,'liang','lzqiu05@gmail.com','$2y$10$RQ1t7YIpbKZXy4SlkE1Zj.I26jVX8zRYaddrNL/MUobiCsHGk1FHC'),(3,'Juan','juang@gmail.com','$2y$10$n8GLVCstWMpZGAuLNvSKlui/ubkIB/6VTCYY6/GMpPxB7BHJioImK');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-13  0:08:01
