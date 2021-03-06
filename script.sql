-- MySQL Script generated by MySQL Workbench
-- Wed Sep 12 08:06:27 2018
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema cognitive-service
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cognitive-service
-- ---------------------------cognitive-service--------------------------
CREATE SCHEMA IF NOT EXISTS `cognitive-service` DEFAULT CHARACTER SET utf8 ;
USE `cognitive-service` ;

-- -----------------------------------------------------
-- Table `cognitive-service`.`cliente`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cognitive-service`.`cliente` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `RG` INT NOT NULL,
  `CPF` INT NOT NULL,
  `nome` VARCHAR(120) NOT NULL,
  `telefone` INT NOT NULL,
  `email` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cognitive-service`.`face`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cognitive-service`.`face` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `faceId` VARCHAR(45) NOT NULL,
  `cliente_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_face_cliente1_idx` (`cliente_id` ASC),
  CONSTRAINT `fk_face_cliente1`
    FOREIGN KEY (`cliente_id`)
    REFERENCES `cognitive-service`.`cliente` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cognitive-service`.`endereco`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cognitive-service`.`endereco` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cep` INT NOT NULL,
  `logradouro` VARCHAR(45) NOT NULL,
  `endereco` VARCHAR(150) NOT NULL,
  `numero` INT NOT NULL,
  `bairro` VARCHAR(45) NOT NULL,
  `cidade` VARCHAR(45) NOT NULL,
  `estado` VARCHAR(2) NOT NULL,
  `pais` VARCHAR(45) NOT NULL,
  `cliente_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_endereco_cliente_idx` (`cliente_id` ASC),
  CONSTRAINT `fk_endereco_cliente`
    FOREIGN KEY (`cliente_id`)
    REFERENCES `cognitive-service`.`cliente` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
