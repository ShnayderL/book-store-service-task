ALTER TABLE EMPLOYEES ADD CONSTRAINT unique_employee_email UNIQUE (email);
INSERT INTO EMPLOYEES (BIRTH_DATE, EMAIL, NAME, PASSWORD, PHONE)
VALUES
('1990-05-15', 'john.doe@email.com', 'John Doe', '$2b$12$l5GNHoUmrl1Wtw2.bfTVYeEVrcIVgp2MHVCG7WPpApHhYZSQ.FRM6', '555-123-4567'),
('1985-09-20', 'jane.smith@email.com', 'Jane Smith', '$2b$12$pDM53DzngktDcnthJBqLxugL0a/Ghmq2sZXa1cSrIE9NiHUWmq1G2', '555-987-6543'),
('1978-03-08', 'bob.jones@email.com', 'Bob Jones', '$2b$12$P2TLww3jCKJYhDdjGeEaEOM8yKrkWF5ZemiuuFPfC3mNPM7NIDfyC', '555-321-6789'),
('1982-11-25', 'alice.white@email.com', 'Alice White', '$2b$12$MzZAD7ZyS7jyrQe8ynrokOtxc9X95vyP/IYiEf1CXoQu83AgX4X7C', '555-876-5432'),
('1995-07-12', 'mike.wilson@email.com', 'Mike Wilson', '$2b$12$sKGY6IZwExLB1eKSRhPvaei52UwrxN/lZxV868qBeh1fGvaofOUaq', '555-234-5678'),
('1989-01-30', 'sara.brown@email.com', 'Sara Brown', '$2b$12$wqvOmZ8IYthmdCx6j4dVYuSTjbbddXEUVHQMWrVTodBrLO38c1Q2m', '555-876-5433'),
('1975-06-18', 'tom.jenkins@email.com', 'Tom Jenkins', '$2b$12$rdav4RJ9dbylc2irT4NdIOKaie8JGtuPAaLgpw2kuHlOd8Sk6r0x.', '555-345-6789'),
('1987-12-04', 'lisa.taylor@email.com', 'Lisa Taylor', '$2b$12$3CrVHLHiKGV.EItUj0RDWOmi8bhc87/OPjsEYMZbzLAVOINo5ldoe', '555-789-0123'),
('1992-08-22', 'david.wright@email.com', 'David Wright', '$2b$12$4Soq4DBJM/3q1.tkDr6U6e3b7wKkeRSYZQ770Y7Fm4rRA/wloAw9m', '555-456-7890'),
('1980-04-10', 'emily.harris@email.com', 'Emily Harris', '$2b$12$lGsXhHok/A7oniad0qvGTOfrVhiAd0H6P4YzAXX4/1vMx0o1YllRO', '555-098-7654');

ALTER TABLE CLIENTS ADD CONSTRAINT unique_client_email UNIQUE (email);
INSERT INTO CLIENTS (BALANCE, EMAIL, NAME, PASSWORD)
VALUES
(1000.00, 'client1@example.com', 'Medelyn Wright', '$2b$12$MuvV48IKkXwc3vBPfVdsPefs6X8TqXqW2h6dLuibmtDJkm5inR7AK'),
(1500.50, 'client2@example.com', 'Landon Phillips', '$2b$12$BQdM4v/PNGVO2bPyq9v31u44icXNpLro0uD3d/dg80xOVCfKBRFXW'),
(800.75, 'client3@example.com', 'Harmony Mason', '$2b$12$Ycj8NKSJO.1f8i/eJnCvq.ytLnIajnfADwIQ8L4dMrs0No/isqipi'),
(1200.25, 'client4@example.com', 'Archer Harper', '$2b$12$M7aKuGo.Dkm1q62K.Eek8OeqMMSRs.sp3BxhqU2qS0xDFWitj50sW'),
(900.80, 'client5@example.com', 'Kira Jacobs', '$2b$12$jcL0Vy7AjckpU2xBUcsVReltVnJ7Bh9AuYcscTj1rPFqVycsiVoMm'),
(1100.60, 'client6@example.com', 'Maximus Kelly', '$2b$12$zPd.79vZ82BTg3ywoa1BNOpkfGyu47Jns8BYMY32TGLssmaM5OIgW'),
(1300.45, 'client7@example.com', 'Sierra Mitchell', '$2b$12$DLChGXvzpTypSj7dc0fueOZgvFTHzCKkTGXJX.MOrV4VyjEduwr0.'),
(950.30, 'client8@example.com', 'Quinton Saunders', '$2b$12$r9K.dQdPEToZzFvXHXNaUead8edIvWpF1lKIx.RbFam/eDIpzINdK'),
(1050.90, 'client9@example.com', 'Amina Clarke', '$2b$12$Mg36SaCL4H2OuFU42Q.XKufzYL7V2Rv9t3ow3iZ9V/7.FLErj2MBq'),
(880.20, 'client10@example.com', 'Bryson Chavez', '$2b$12$a9kednInFVAxzXhvUUq97OVoKXqM74AjXxWJONe2rAF1CyHt1zMlu');


INSERT INTO BOOKS (name, genre, age_group, price, publication_year, author, number_of_pages, characteristics,description, language)
VALUES ('The Hidden Treasure', 'Adventure', 'ADULT', 24.99, '2018-05-15', 'Emily White', 400, 'Mysterious journey','An enthralling adventure of discovery', 'ENGLISH'),
       ('Echoes of Eternity', 'Fantasy', 'TEEN', 16.50, '2011-01-15', 'Daniel Black', 350, 'Magical realms', 'A spellbinding tale of magic and destiny', 'ENGLISH'),
       ('Whispers in the Shadows', 'Mystery', 'ADULT', 29.95, '2018-08-11', 'Sophia Green', 450, 'Intriguing suspense','A gripping mystery that keeps you guessing', 'ENGLISH'),
       ('The Starlight Sonata', 'Romance', 'ADULT', 21.75, '2011-05-15', 'Michael Rose', 320, 'Heartwarming love story','A beautiful journey of love and passion', 'ENGLISH'),
       ('Beyond the Horizon', 'Science Fiction', 'CHILD', 18.99, '2004-05-15', 'Alex Carter', 280,'Interstellar adventure', 'An epic sci-fi adventure beyond the stars', 'ENGLISH'),
       ('Dancing with Shadows', 'Thriller', 'ADULT', 26.50, '2015-05-15', 'Olivia Smith', 380, 'Suspenseful twists','A thrilling tale of danger and intrigue', 'ENGLISH'),
       ('Voices in the Wind', 'Historical Fiction', 'ADULT', 32.00, '2017-05-15', 'William Turner', 500,'Rich historical setting', 'A compelling journey through time', 'ENGLISH'),
       ('Serenade of Souls', 'Fantasy', 'TEEN', 15.99, '2013-05-15', 'Isabella Reed', 330, 'Enchanting realms','A magical fantasy filled with wonder', 'ENGLISH'),
       ('Silent Whispers', 'Mystery', 'ADULT', 27.50, '2021-05-15', 'Benjamin Hall', 420, 'Intricate detective work','A mystery that keeps you on the edge', 'ENGLISH'),
       ('Whirlwind Romance', 'Romance', 'OTHER', 23.25, '2022-05-15', 'Emma Turner', 360, 'Passionate love affair','A romance that sweeps you off your feet', 'ENGLISH');
