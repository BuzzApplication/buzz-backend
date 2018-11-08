CREATE TABLE `User` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `guid` varchar(36) NOT NULL,
  `alias` varchar(20) DEFAULT NULL,
  `status` enum('ACTIVE', 'SUSPENDED') CHARACTER SET utf8 NOT NULL DEFAULT 'ACTIVE',
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Authentication` (
  `guid` varchar(36) NOT NULL,
  `email` varchar(1024) DEFAULT NULL,
  `password` varchar(1024) DEFAULT NULL,
  `verificationCode` varchar(4) DEFAULT NULL,
  `status` enum('VERIFIED', 'UNVERIFIED') CHARACTER SET utf8 NOT NULL DEFAULT 'UNVERIFIED',
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Company` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(1024) DEFAULT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;

CREATE TABLE `UserEmail` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `email` varchar(512) NOT NULL,
  `companyId` int(11) unsigned NOT NULL,
  `userId` int(11) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `userId` (`userId`),
  KEY `companyId` (`companyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `CompanyEmail` (
  `email` varchar(512) NOT NULL,
  `companyId` int(11) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`email`, `companyId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Buzz` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `text` text NOT NULL,
  `companyId` int(11) unsigned NOT NULL,
  `alias` varchar(20) DEFAULT NULL,
  `userEmailId` int(11) unsigned NOT NULL,
  `likesCount` int(11) unsigned NOT NULL,
  `commentsCount` int(11) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `companyId` (`companyId`),
  KEY `userEmailId` (`userEmailId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BuzzLike` (
  `userId` int(11) unsigned NOT NULL,
  `buzzId` int(11) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`, `buzzId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `BuzzFavorite` (
  `userId` int(11) unsigned NOT NULL,
  `buzzId` int(11) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`, `buzzId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Comment` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `buzzId` int(11) unsigned NOT NULL,
  `text` text NOT NULL,
  `alias` varchar(20) DEFAULT NULL,
  `userEmailId` int(11) unsigned NOT NULL,
  `likesCount` int(11) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `buzzId` (`buzzId`),
  KEY `userEmailId` (`userEmailId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `CommentLike` (
  `userId` int(11) unsigned NOT NULL,
  `commentId` int(11) unsigned NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`userId`, `commentId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ReportCategory` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `category` varchar(512) NOT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `Report` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `userId` int(11) unsigned NOT NULL,
  `type` enum('BUZZ', 'COMMENT') CHARACTER SET utf8 NOT NULL DEFAULT 'BUZZ',
  `itemId` int(11) unsigned NOT NULL,
  `reportCategoryId` int(11) unsigned NOT NULL,
  `comments` text DEFAULT NULL,
  `created` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `lastModified` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `reportCategoryId` (`reportCategoryId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO `ReportCategory` (`category`)
VALUES
	('Bullying'),
	('Inappropriate Content'),
	('Discrimination'),
	('Sexual Harassment'),
	('Impersonation'),
	('Spam'),
	('Other');

INSERT INTO `Authentication` (`guid`, `email`, `password`, `created`, `lastModified`)
VALUES
	('f771f429-d987-4ce4-9087-1468f0a64070', 'johnDoe@buzz.com', 'johnDoe', '2018-10-24 06:31:19', '2018-10-24 06:31:19');

INSERT INTO `User` (`id`, `guid`, `alias`, `status`, `created`, `lastModified`)
VALUES
	(1, 'f771f429-d987-4ce4-9087-1468f0a64070', 'johnDoe', 'ACTIVE', '2018-10-24 05:59:27', '2018-10-24 05:59:27');

INSERT INTO `UserEmail` (`id`, `email`, `companyId`, `userId`, `created`, `lastModified`)
VALUES
	(1, 'johnDoe@buzz.com', 2, 1, '2018-10-24 06:00:13', '2018-10-24 06:00:13');

INSERT INTO `Company` (`id`, `name`, `created`, `lastModified`)
VALUES
	(1, 'Everyone', '2018-10-24 05:59:36', '2018-10-24 05:59:36'),
	(2, 'Buzz', '2018-10-24 05:59:36', '2018-10-24 05:59:36');

INSERT INTO `CompanyEmail` (`email`, `companyId`, `created`, `lastModified`)
VALUES
	('@buzz.com', 1, '2018-10-24 05:59:52', '2018-10-24 05:59:57');

