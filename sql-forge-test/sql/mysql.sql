DROP TABLE IF EXISTS `student`;
CREATE TABLE `student` (
                           `id` int unsigned NOT NULL AUTO_INCREMENT,
                           `name` varchar(100) NOT NULL,
                           `sex` varchar(5) NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;

INSERT INTO `test`.`student` (`id`, `name`, `sex`) VALUES (1, '小明', '男');
INSERT INTO `test`.`student` (`id`, `name`, `sex`) VALUES (2, '小红', '女');