CREATE TABLE IF NOT EXISTS `attacks` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `a_territory` varchar(150) COLLATE utf8_bin NOT NULL DEFAULT '',
  `d_territory` varchar(150) COLLATE utf8_bin NOT NULL DEFAULT '',
  `a_roll` char(3) COLLATE utf8_bin NOT NULL,
  `transfer` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`gid`,`a_territory`,`d_territory`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `chat` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `uid` int(11) NOT NULL DEFAULT '0',
  `time` int(11) NOT NULL DEFAULT '0',
  `message` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`gid`,`uid`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `combatlog` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `type` smallint(6) NOT NULL DEFAULT '0',
  `time` int(11) NOT NULL DEFAULT '0',
  `uid` int(11) NOT NULL DEFAULT '0',
  `num` int(11) DEFAULT NULL,
  `char_three` char(3) COLLATE utf8_bin DEFAULT NULL,
  `d_roll` char(2) COLLATE utf8_bin DEFAULT NULL,
  `units` int(11) DEFAULT NULL,
  `territory` varchar(150) COLLATE utf8_bin DEFAULT NULL,
  `d_territory` varchar(150) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`gid`,`type`,`time`,`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `games` (
  `gid` int(11) NOT NULL AUTO_INCREMENT,
  `map` int(11) NOT NULL,
  `name` varchar(255) COLLATE utf8_bin NOT NULL,
  `uid` int(11) DEFAULT NULL,
  `turn` int(11) NOT NULL DEFAULT '0',
  `time` int(11) NOT NULL,
  `state` smallint(6) NOT NULL DEFAULT '0',
  `units` int(11) NOT NULL DEFAULT '0',
  `conquered` smallint(6) NOT NULL DEFAULT '0',
  `password` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`gid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 ;

CREATE TABLE IF NOT EXISTS `gamesjoined` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `uid` int(11) NOT NULL DEFAULT '0',
  `color` char(6) COLLATE utf8_bin NOT NULL,
  `autoroll` smallint(6) NOT NULL DEFAULT '1',
  `mission` int(11) NOT NULL DEFAULT '0',
  `m_uid` int(11) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  `c_art` smallint(6) NOT NULL DEFAULT '0',
  `c_cav` smallint(6) NOT NULL DEFAULT '0',
  `c_inf` smallint(6) NOT NULL DEFAULT '0',
  `c_jok` smallint(6) NOT NULL DEFAULT '0',
  PRIMARY KEY (`gid`,`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `territories` (
  `gid` int(11) NOT NULL DEFAULT '0',
  `territory` varchar(150) COLLATE utf8_bin NOT NULL DEFAULT '',
  `uid` int(11) DEFAULT NULL,
  `units` int(11) NOT NULL,
  PRIMARY KEY (`gid`,`territory`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE IF NOT EXISTS `users` (
  `uid` int(11) NOT NULL DEFAULT '0',
  `wins` int(11) NOT NULL DEFAULT '0',
  `losses` int(11) NOT NULL DEFAULT '0',
  `score` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

ALTER TABLE `attacks`
  ADD CONSTRAINT `attacks_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `games` (`gid`) ON DELETE CASCADE;

ALTER TABLE `combatlog`
  ADD CONSTRAINT `combatlog_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `games` (`gid`) ON DELETE CASCADE;

ALTER TABLE `gamesjoined`
  ADD CONSTRAINT `gamesjoined_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `games` (`gid`) ON DELETE CASCADE;

ALTER TABLE `territories`
  ADD CONSTRAINT `territories_ibfk_1` FOREIGN KEY (`gid`) REFERENCES `games` (`gid`) ON DELETE CASCADE;
