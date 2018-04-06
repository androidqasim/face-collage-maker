CREATE TABLE StoreItem (
	id	TEXT,
	name	TEXT,
	type	TEXT,
	thumbnail	TEXT,
	selected_thumbnail	TEXT,
	url	TEXT,
	signature	TEXT,
	last_modified	INTEGER,
	status	TEXT
);
CREATE TABLE Shade (
	id	INTEGER PRIMARY KEY AUTOINCREMENT,
	name	TEXT,
	thumbnail	TEXT,
	selected_thumbnail	INTEGER,
	foreground	TEXT,
	type	TEXT,
	package_id	NUMERIC,
	last_modified	TEXT,
	status	TEXT
);
INSERT INTO Shade VALUES (1,'Frame 1','assets://images/frame_ic_1.png',NULL,'assets://images/frame_fg_1.png','frame',0,'2015-10-15 21:16:12','active');
INSERT INTO Shade VALUES (2,'Frame 2','assets://images/frame_ic_2.png',NULL,'assets://images/frame_fg_2.png','frame',0,'2015-10-15 21:16:12','active');
INSERT INTO Shade VALUES (3,'Frame 3','assets://images/frame_ic_3.png',NULL,'assets://images/frame_fg_3.png','frame',0,'2015-10-15 21:16:12','active');
INSERT INTO Shade VALUES (4,'Frame 4','assets://images/frame_ic_4.png',NULL,'assets://images/frame_fg_4.png','frame',0,'2015-10-15 21:16:12','active');
INSERT INTO Shade VALUES (5,'Frame 5','assets://images/frame_ic_5.png',NULL,'assets://images/frame_fg_5.png','frame',0,'2015-10-15 21:16:12','active');
INSERT INTO Shade VALUES (6,'Frame 6','assets://images/frame_ic_6.png',NULL,'assets://images/frame_fg_6.png','frame',0,'2015-10-15 21:16:12','active');
INSERT INTO Shade VALUES (7,'Frame 7','assets://images/frame_ic_7.png',NULL,'assets://images/frame_fg_7.png','frame',0,'2015-10-15 21:16:12','active');
INSERT INTO Shade VALUES (8,'Frame 8','assets://images/frame_ic_8.png',NULL,'assets://images/frame_fg_8.png','frame',0,'2015-10-15 21:16:12','active');
INSERT INTO Shade VALUES (9,'Frame 9','assets://images/frame_ic_9.png',NULL,'assets://images/frame_fg_9.png','frame',0,'2015-10-15 21:16:12','active');
CREATE TABLE ItemPackage (
	id	INTEGER PRIMARY KEY AUTOINCREMENT,
	name	TEXT NOT NULL,
	thumbnail	TEXT,
	selected_thumbnail	TEXT,
	type	TEXT NOT NULL,
	folder	TEXT,
	id_str	TEXT,
	last_modified	TEXT,
	status	TEXT
);
CREATE TABLE ImageTemplate (
	id	INTEGER PRIMARY KEY AUTOINCREMENT,
	name	TEXT,
	thumbnail	TEXT,
	selected_thumbnail	TEXT,
	preview	TEXT,
	template	TEXT,
	child	TEXT,
	package_id	NUMERIC,
	last_modified	TEXT,
	status	TEXT
);
CREATE TABLE Filter (
	id	INTEGER PRIMARY KEY AUTOINCREMENT,
	name	TEXT,
	thumbnail	TEXT,
	selected_thumbnail	TEXT,
	cmd	TEXT NOT NULL,
	package_id	NUMERIC,
	last_modified	TEXT,
	status	TEXT
);
INSERT INTO Filter VALUES (1,'Normal','assets://images/filter_ic_normal.png',NULL,'',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (2,'Sexy Lips','assets://images/filter_ic_sexy_lips.png',NULL,'H',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (3,'Autumn','assets://images/filter_ic_autumn.png',NULL,'S',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (4,'Fresh','assets://images/filter_ic_fresh.png',NULL,'Sat 1.9',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (5,'Slight','assets://images/filter_ic_slight.png',NULL,'M',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (6,'Decoration','assets://images/filter_ic_decoration.png',NULL,'V 0.5 0.5 0.2 0.5 0.2 0.3 0.75',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (7,'Light','assets://images/filter_ic_light.png',NULL,'O 0.9',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (8,'Sunrise','assets://images/filter_ic_sunrise.png',NULL,'T assets://images/filter_lomo.acv',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (9,'Red','assets://images/filter_ic_red.png',NULL,'C 0.1 0.3 0.6 0.2 0.1 0.1 0.5 0.1 0.1 0.1 0.5 0 0 0 0 1',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (10,'Gloaming','assets://images/filter_ic_gloaming.png',NULL,'C 0.1 0.3 0.2 0.2 0.1 0.1 0.8 0.1 0.1 0.1 0.5 0 0 0 0 1',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (11,'Fading','assets://images/filter_ic_fading.png',NULL,'G',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (12,'Time','assets://images/filter_ic_time.png',NULL,'L1 assets://images/filter_map_time.png,
V 0.5 0.5 0 0 0 0.1 0.9',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (13,'Amat','assets://images/filter_ic_amat.png',NULL,'L2 assets://images/filter_lookup_amatorka.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (14,'Etikate','assets://images/filter_ic_etikate.png',NULL,'L2 assets://images/filter_lookup_miss_etikate.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (15,'Elegance','assets://images/filter_ic_elegance.png',NULL,'L2 assets://images/filter_lookup_soft_elegance_1.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (16,'Eleg','assets://images/filter_ic_eleg.png',NULL,'L2 assets://images/filter_lookup_soft_elegance_2.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (17,'Quite','assets://images/filter_ic_quite.png',NULL,'L1 assets://images/filter_map_quite.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (18,'Leopard','assets://images/filter_ic_leopard.png',NULL,'L1 assets://images/filter_map_leopard.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (19,'Chinaberry','assets://images/filter_ic_chinaberry.png',NULL,'L1 assets://images/filter_map_chinaberry.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (20,'Dye','assets://images/filter_ic_dye.png',NULL,'L1 assets://images/filter_map_dye.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Filter VALUES (21,'Airy','assets://images/filter_ic_airy.png',NULL,'L1 assets://images/filter_map_airy.png',0,'2015-10-15 21:16:12','active');
CREATE TABLE Crop (
	id	INTEGER PRIMARY KEY AUTOINCREMENT,
	name	TEXT,
	thumbnail	TEXT,
	selected_thumbnail	TEXT,
	foreground	TEXT,
	background	TEXT,
	package_id	NUMERIC,
	last_modified	TEXT,
	status	TEXT
);
INSERT INTO Crop VALUES (1,'Balloon','assets://images/crop_ic_balloon.png','assets://images/crop_ic_balloon_selected.png','assets://images/crop_balloon.png','assets://images/crop_balloon_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (2,'Heart','assets://images/crop_ic_heart.png','assets://images/crop_ic_heart_selected.png','assets://images/crop_heart.png','assets://images/crop_heart_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (3,'Cog','assets://images/crop_ic_cog.png','assets://images/crop_ic_cog_selected.png','assets://images/crop_cog.png','assets://images/crop_cog_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (4,'Fat Star','assets://images/crop_ic_fat_star.png','assets://images/crop_ic_fat_star_selected.png','assets://images/crop_fat_star.png','assets://images/crop_fat_star_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (5,'Circle','assets://images/crop_ic_circle.png','assets://images/crop_ic_circle_selected.png','assets://images/crop_circle.png','assets://images/crop_circle_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (6,'Heart 2','assets://images/crop_ic_heart2.png','assets://images/crop_ic_heart2_selected.png','assets://images/crop_heart2.png','assets://images/crop_heart2_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (7,'Hexagon','assets://images/crop_ic_hexagon.png','assets://images/crop_ic_hexagon_selected.png','assets://images/crop_hexagon.png','assets://images/crop_hexagon_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (8,'Leopard','assets://images/crop_ic_leopard.png','assets://images/crop_ic_leopard_selected.png','assets://images/crop_leopard.png','assets://images/crop_leopard_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (9,'Octagon','assets://images/crop_ic_octagon.png','assets://images/crop_ic_octagon_selected.png','assets://images/crop_octagon.png','assets://images/crop_octagon_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (10,'Omsg','assets://images/crop_ic_ovan_message.png','assets://images/crop_ic_ovan_message_selected.png','assets://images/crop_ovan_message.png','assets://images/crop_ovan_message_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (11,'Pentagon','assets://images/crop_ic_pentagon.png','assets://images/crop_ic_pentagon_selected.png','assets://images/crop_pentagon.png','assets://images/crop_pentagon_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (12,'Rmsg','assets://images/crop_ic_rectangle_message.png','assets://images/crop_ic_rectangle_message_selected.png','assets://images/crop_rectangle_message.png','assets://images/crop_rectangle_message_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (13,'Square','assets://images/crop_ic_square.png','assets://images/crop_ic_square_selected.png','assets://images/crop_square.png','assets://images/crop_square_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (14,'Star','assets://images/crop_ic_star.png','assets://images/crop_ic_star_selected.png','assets://images/crop_star.png','assets://images/crop_star_bg.png',0,'2015-10-15 21:16:12','active');
INSERT INTO Crop VALUES (15,'Vase','assets://images/crop_ic_vase.png','assets://images/crop_ic_vase_selected.png','assets://images/crop_vase.png','assets://images/crop_vase_bg.png',0,'2015-10-15 21:16:12','active');
