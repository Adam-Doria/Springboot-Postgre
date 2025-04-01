-- Chat Application Database Schema

-- Users table
CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  display_name VARCHAR(100),
  profile_image_url VARCHAR(255),
  is_online BOOLEAN DEFAULT FALSE,
  last_seen_at TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Chat rooms table
CREATE TABLE chat_rooms (
  id SERIAL PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  is_public BOOLEAN DEFAULT TRUE,
  created_by INTEGER NOT NULL REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Private conversations table (between two users)
CREATE TABLE private_conversations (
  id SERIAL PRIMARY KEY,
  user1_id INTEGER NOT NULL REFERENCES users(id),
  user2_id INTEGER NOT NULL REFERENCES users(id),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT different_users CHECK (user1_id <> user2_id),
  CONSTRAINT unique_conversation UNIQUE (user1_id, user2_id)
);

-- Chat room memberships table (N:M relationship between users and chat_rooms)
CREATE TABLE chat_room_memberships (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(id),
  chat_room_id INTEGER NOT NULL REFERENCES chat_rooms(id),
  joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  last_read_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT unique_membership UNIQUE (user_id, chat_room_id)
);

-- Messages table
CREATE TABLE messages (
  id SERIAL PRIMARY KEY,
  content TEXT NOT NULL,
  sender_id INTEGER NOT NULL REFERENCES users(id),
  chat_room_id INTEGER REFERENCES chat_rooms(id),
  private_conversation_id INTEGER REFERENCES private_conversations(id),
  is_edited BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT message_destination CHECK (
    (chat_room_id IS NULL AND private_conversation_id IS NOT NULL) OR
    (chat_room_id IS NOT NULL AND private_conversation_id IS NULL)
  )
);

-- Chat roles table (user roles in chat rooms)
CREATE TABLE chat_roles (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(id),
  chat_room_id INTEGER NOT NULL REFERENCES chat_rooms(id),
  role_name VARCHAR(50) NOT NULL, -- 'admin', 'moderator', 'member', etc.
  assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  assigned_by INTEGER REFERENCES users(id),
  CONSTRAINT unique_user_role UNIQUE (user_id, chat_room_id)
);

-- Blocked users in chat rooms
CREATE TABLE blocked_users (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL REFERENCES users(id),
  chat_room_id INTEGER NOT NULL REFERENCES chat_rooms(id),
  blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  blocked_by INTEGER NOT NULL REFERENCES users(id),
  reason TEXT,
  CONSTRAINT unique_blocked_user UNIQUE (user_id, chat_room_id)
);

-- Indexes for performance
CREATE INDEX idx_messages_chat_room ON messages(chat_room_id);
CREATE INDEX idx_messages_private_conversation ON messages(private_conversation_id);
CREATE INDEX idx_messages_sender ON messages(sender_id);
CREATE INDEX idx_chat_roles_user ON chat_roles(user_id);
CREATE INDEX idx_chat_roles_chat_room ON chat_roles(chat_room_id);
CREATE INDEX idx_chat_room_memberships_user ON chat_room_memberships(user_id);
CREATE INDEX idx_chat_room_memberships_chat_room ON chat_room_memberships(chat_room_id);
CREATE INDEX idx_blocked_users_user ON blocked_users(user_id);
CREATE INDEX idx_blocked_users_chat_room ON blocked_users(chat_room_id); 