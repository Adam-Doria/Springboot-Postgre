-- Insertion de données dans la table users
INSERT INTO users (id, username, email, password, display_name, profile_image_url, is_online, last_seen_at, created_at, updated_at)
VALUES
    (1, 'alice', 'alice@example.com', 'password1', 'Alice', 'https://example.com/alice.png', true, NOW(), NOW(), NOW()),
    (2, 'bob', 'bob@example.com', 'password2', 'Bob', 'https://example.com/bob.png', true, NOW(), NOW(), NOW()),
    (3, 'charlie', 'charlie@example.com', 'password3', 'Charlie', 'https://example.com/charlie.png', false, NOW(), NOW(), NOW()),
    (4, 'david', 'david@example.com', 'password4', 'David', 'https://example.com/david.png', false, NOW(), NOW(), NOW()),
    (5, 'eve', 'eve@example.com', 'password5', 'Eve', 'https://example.com/eve.png', true, NOW(), NOW(), NOW()),
    (6, 'frank', 'frank@example.com', 'password6', 'Frank', 'https://example.com/frank.png', false, NOW(), NOW(), NOW()),
    (7, 'grace', 'grace@example.com', 'password7', 'Grace', 'https://example.com/grace.png', true, NOW(), NOW(), NOW()),
    (8, 'heidi', 'heidi@example.com', 'password8', 'Heidi', 'https://example.com/heidi.png', false, NOW(), NOW(), NOW()),
    (9, 'ivan', 'ivan@example.com', 'password9', 'Ivan', 'https://example.com/ivan.png', true, NOW(), NOW(), NOW()),
    (10, 'judy', 'judy@example.com', 'password10', 'Judy', 'https://example.com/judy.png', true, NOW(), NOW(), NOW());

-- Insertion de données dans la table chat_rooms
INSERT INTO chat_rooms (id, name, description, admin_id, created_at, updated_at)
VALUES
    (1, 'Général', 'Salon de discussion général', 1, NOW(), NOW()),
    (2, 'Tech', 'Discussions sur la technologie', 2, NOW(), NOW()),
    (3, 'Jeux', 'Discussions sur les jeux vidéo', 3, NOW(), NOW()),
    (4, 'Musique', 'Discussions sur la musique', 4, NOW(), NOW()),
    (5, 'Films', 'Discussions sur les films', 5, NOW(), NOW());

-- Insertion de données dans la table private_conversations
INSERT INTO private_conversations (id, user1_id, user2_id, created_at, updated_at)
VALUES
    (1, 1, 2, NOW(), NOW()),
    (2, 3, 4, NOW(), NOW()),
    (3, 5, 6, NOW(), NOW()),
    (4, 7, 8, NOW(), NOW()),
    (5, 9, 10, NOW(), NOW());

-- Insertion de données dans la table messages

-- Messages dans le salon "Général" (chat_room_id = 1)
INSERT INTO messages (id, content, sender_id, recipient_id, chat_room_id, private_conversation_id, is_edited, is_read, created_at, updated_at)
VALUES
    (1, 'Bonjour à tous !', 1, NULL, 1, NULL, false, false, NOW(), NOW()),
    (2, 'Salut Alice !', 2, NULL, 1, NULL, false, false, NOW(), NOW()),
    (3, 'Hello, comment ça va ?', 3, NULL, 1, NULL, false, false, NOW(), NOW()),
    (4, 'Très bien, merci.', 4, NULL, 1, NULL, false, false, NOW(), NOW()),
    (5, 'Bienvenue dans le salon Général.', 5, NULL, 1, NULL, false, false, NOW(), NOW());

-- Messages dans le salon "Tech" (chat_room_id = 2)
INSERT INTO messages (id, content, sender_id, recipient_id, chat_room_id, private_conversation_id, is_edited, is_read, created_at, updated_at)
VALUES
    (6, 'Quelqu’un a testé le dernier smartphone ?', 2, NULL, 2, NULL, false, false, NOW(), NOW()),
    (7, 'Oui, il est vraiment top !', 3, NULL, 2, NULL, false, false, NOW(), NOW()),
    (8, 'J’hésite encore à l’acheter.', 4, NULL, 2, NULL, false, false, NOW(), NOW());

-- Messages dans le salon "Jeux" (chat_room_id = 3)
INSERT INTO messages (id, content, sender_id, recipient_id, chat_room_id, private_conversation_id, is_edited, is_read, created_at, updated_at)
VALUES
    (9, 'Qui est partant pour une session de jeu ce soir ?', 5, NULL, 3, NULL, false, false, NOW(), NOW()),
    (10, 'Je suis chaud !', 6, NULL, 3, NULL, false, false, NOW(), NOW()),
    (11, 'Comptez sur moi.', 7, NULL, 3, NULL, false, false, NOW(), NOW());

-- Messages pour les conversations privées

-- Conversation 1 entre Alice (1) et Bob (2)
INSERT INTO messages (id, content, sender_id, recipient_id, chat_room_id, private_conversation_id, is_edited, is_read, created_at, updated_at)
VALUES
    (12, 'Salut Bob, comment ça va ?', 1, 2, NULL, 1, false, false, NOW(), NOW()),
    (13, 'Ça va bien, merci. Et toi ?', 2, 1, NULL, 1, false, false, NOW(), NOW());

-- Conversation 2 entre Charlie (3) et David (4)
INSERT INTO messages (id, content, sender_id, recipient_id, chat_room_id, private_conversation_id, is_edited, is_read, created_at, updated_at)
VALUES
    (14, 'Salut David, on se retrouve pour un café ?', 3, 4, NULL, 2, false, false, NOW(), NOW()),
    (15, 'Bonne idée, on se tient au courant.', 4, 3, NULL, 2, false, false, NOW(), NOW());

-- Conversation 3 entre Eve (5) et Frank (6)
INSERT INTO messages (id, content, sender_id, recipient_id, chat_room_id, private_conversation_id, is_edited, is_read, created_at, updated_at)
VALUES
    (16, 'Frank, tu as vu la dernière news ?', 5, 6, NULL, 3, false, false, NOW(), NOW()),
    (17, 'Oui, c’était surprenant !', 6, 5, NULL, 3, false, false, NOW(), NOW());

-- Conversation 4 entre Grace (7) et Heidi (8)
INSERT INTO messages (id, content, sender_id, recipient_id, chat_room_id, private_conversation_id, is_edited, is_read, created_at, updated_at)
VALUES
    (18, 'Heidi, ça te dit de sortir ce weekend ?', 7, 8, NULL, 4, false, false, NOW(), NOW()),
    (19, 'Avec plaisir, on se cale ça !', 8, 7, NULL, 4, false, false, NOW(), NOW());

-- Conversation 5 entre Ivan (9) et Judy (10)
INSERT INTO messages (id, content, sender_id, recipient_id, chat_room_id, private_conversation_id, is_edited, is_read, created_at, updated_at)
VALUES
    (20, 'Judy, tu as un moment pour discuter ?', 9, 10, NULL, 5, false, false, NOW(), NOW()),
    (21, 'Oui, dis-moi tout.', 10, 9, NULL, 5, false, false, NOW(), NOW());
