SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `dbmysql05` DEFAULT CHARACTER SET utf8 ;
USE `dbmysql05` ;

-- -----------------------------------------------------
-- Table `dbmysql05`.`invocations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`invocations` (
  `invokeCode` TINYINT NOT NULL,
  `invokeDate` DATETIME NOT NULL,
  PRIMARY KEY (`invokeCode`, `invokeDate`))
ENGINE = InnoDB	-- used InnoDB because this should have more writes than selects
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`person`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`person` (
  `idPerson` INT NOT NULL AUTO_INCREMENT,
  `personName` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`idPerson`),
  INDEX `personName_idx` USING BTREE (`personName` ASC))
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`actor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`actor` (
  `idPerson` INT NOT NULL,
  PRIMARY KEY (`idPerson`),
  CONSTRAINT `idPersonActor`
    FOREIGN KEY (`idPerson`)
    REFERENCES `dbmysql05`.`person` (`idPerson`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`director`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`director` (
  `idPerson` INT NOT NULL,
  PRIMARY KEY (`idPerson`),
  CONSTRAINT `idPersonDirector`
    FOREIGN KEY (`idPerson`)
    REFERENCES `dbmysql05`.`person` (`idPerson`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`language`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`language` (
  `idLanguage` INT NOT NULL AUTO_INCREMENT,
  `LanguageName` CHAR(20) NOT NULL,
  PRIMARY KEY (`idLanguage`))
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`movie` (
  `idMovie` INT NOT NULL AUTO_INCREMENT,
  `idLanguage` INT NULL DEFAULT NULL,
  `idDirector` INT NULL DEFAULT NULL,
  `movieName` VARCHAR(128) NOT NULL,
  `year` INT NULL DEFAULT NULL,
  `wiki` TEXT NULL DEFAULT NULL,
  `duration` INT NULL DEFAULT NULL,
  `plot` TEXT NULL DEFAULT NULL,
  PRIMARY KEY (`idMovie`),
  INDEX `idLanguage_idx` (`idLanguage` ASC),
  INDEX `idDirector_idx` (`idDirector` ASC),
  INDEX `movieName` (`movieName` (128)),
  CONSTRAINT `idDirector`
    FOREIGN KEY (`idDirector`)
    REFERENCES `dbmysql05`.`director` (`idPerson`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `idLanguage`
    FOREIGN KEY (`idLanguage`)
    REFERENCES `dbmysql05`.`language` (`idLanguage`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`users` (
  `idUsers` INT NOT NULL AUTO_INCREMENT,
  `userName` CHAR(10) NOT NULL,
  `hashPassword` INT NOT NULL,
  PRIMARY KEY (`idUsers`))
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`genre`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`genre` (
  `idGenre` INT NOT NULL AUTO_INCREMENT,
  `genreName` CHAR(20) NOT NULL,
  PRIMARY KEY (`idGenre`))
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`tag` (
  `idTag` INT NOT NULL AUTO_INCREMENT,
  `tagName` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`idtag`),
  INDEX `tagName_idx` USING BTREE (`tagName` ASC))
ENGINE = MyISAM
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`actor_movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`actor_movie` (
  `idMovie` INT NOT NULL AUTO_INCREMENT,
  `idActor` INT NOT NULL,
  INDEX `idMovie_idx` (`idMovie` ASC),
  INDEX `idActor_idx` (`idActor` ASC),
  PRIMARY KEY (`idMovie`, `idActor`),
  CONSTRAINT `idActor`
    FOREIGN KEY (`idActor`)
    REFERENCES `dbmysql05`.`actor` (`idPerson`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `idMovie`
    FOREIGN KEY (`idMovie`)
    REFERENCES `dbmysql05`.`movie` (`idMovie`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`friend_relation`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`friend_relation` (
  `friend1` INT NOT NULL,
  `friend2` INT NOT NULL,
  `friendshipDate` DATETIME NOT NULL,
  PRIMARY KEY (`friend1`, `friend2`),
  CONSTRAINT `friend1_fk`
    FOREIGN KEY (`friend1`)
    REFERENCES `dbmysql05`.`users` (`idUsers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `friend2_fk`
    FOREIGN KEY (`friend2`)
    REFERENCES `dbmysql05`.`users` (`idUsers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`genre_movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`genre_movie` (
  `idMovie` INT NOT NULL,
  `idGenre` INT NOT NULL,
  PRIMARY KEY (`idMovie`, `idGenre`),
  INDEX `idGenre_idx` (`idGenre` ASC),
  INDEX `idMovie_idx` (`idMovie` ASC),
  CONSTRAINT `idGenre`
    FOREIGN KEY (`idGenre`)
    REFERENCES `dbmysql05`.`genre` (`idGenre`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `idMovie1`
    FOREIGN KEY (`idMovie`)
    REFERENCES `dbmysql05`.`movie` (`idMovie`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`movie_tag`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`movie_tag` (
  `idMovie` INT NOT NULL,
  `idTag` INT NOT NULL,
  PRIMARY KEY (`idmovie`, `idtag`),
  INDEX `fk_tag_idx` (`idtag` ASC),
  CONSTRAINT `fk_movie`
    FOREIGN KEY (`idMovie`)
    REFERENCES `dbmysql05`.`movie` (`idMovie`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_tag`
    FOREIGN KEY (`idTag`)
    REFERENCES `dbmysql05`.`tag` (`idTag`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`user_prefence`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`user_prefence` (
  `idUser` INT NOT NULL,
  `idTag` INT NOT NULL,
  `tag_user_rate` INT NOT NULL,
  PRIMARY KEY (`idUser`, `idTag`),
  INDEX `tag_id_fk_user_prefences_idx` (`idTag` ASC),
  CONSTRAINT `tag_id_fk_user_prefences`
    FOREIGN KEY (`idTag`)
    REFERENCES `dbmysql05`.`tag` (`idtag`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_id_fk_user_prefences`
    FOREIGN KEY (`idUser`)
    REFERENCES `dbmysql05`.`users` (`idUsers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`user_rank`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`user_rank` (
  `idUser` INT NOT NULL,
  `idMovie` INT NOT NULL,
  `rank` INT NULL DEFAULT NULL,
  `rankDate` DATETIME NOT NULL,
  PRIMARY KEY (`idUser`, `idMovie`),
  INDEX `movie_fk_idx` (`idMovie` ASC),
  CONSTRAINT `movie_fk`
    FOREIGN KEY (`idMovie`)
    REFERENCES `dbmysql05`.`movie` (`idMovie`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_fk`
    FOREIGN KEY (`idUser`)
    REFERENCES `dbmysql05`.`users` (`idUsers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

-- -----------------------------------------------------
-- Table `dbmysql05`.`user_tag_movie`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `dbmysql05`.`user_tag_movie` (
  `idUser` INT NOT NULL,
  `idTag` INT NOT NULL,
  `idMovie` INT NOT NULL,
  `rate` INT NOT NULL,
  `reteDate` DATETIME NOT NULL,
  PRIMARY KEY (`idUser`, `idTag`, `idMovie`),
  INDEX `movie_fk_user_tags_movie_rating_idx` (`idMovie` ASC),
  INDEX `tag_fk_user_tags_movie_rating_idx` (`idTag` ASC),
  CONSTRAINT `movie_fk_user_tags_movie_rating`
    FOREIGN KEY (`idMovie`)
    REFERENCES `dbmysql05`.`movie` (`idMovie`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `tag_fk_user_tags_movie_rating`
    FOREIGN KEY (`idTag`)
    REFERENCES `dbmysql05`.`tag` (`idtag`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `user_fk_user_tags_movie_rating`
    FOREIGN KEY (`idUser`)
    REFERENCES `dbmysql05`.`users` (`idUsers`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
