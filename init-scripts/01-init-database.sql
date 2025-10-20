-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    avatar_url VARCHAR(500),
    status VARCHAR(20) DEFAULT 'OFFLINE',
    last_seen TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create conversations table
CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(20) NOT NULL DEFAULT 'PRIVATE', -- PRIVATE, GROUP
    created_by BIGINT REFERENCES users(id),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_conversations table (many-to-many)
CREATE TABLE IF NOT EXISTS user_conversations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    conversation_id BIGINT REFERENCES conversations(id),
    role VARCHAR(20) DEFAULT 'MEMBER', -- ADMIN, MEMBER
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP,
    is_active BOOLEAN DEFAULT true,
    UNIQUE(user_id, conversation_id)
);

-- Create messages table
CREATE TABLE IF NOT EXISTS messages (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT REFERENCES conversations(id),
    sender_id BIGINT REFERENCES users(id),
    content TEXT,
    message_type VARCHAR(20) DEFAULT 'TEXT', -- TEXT, IMAGE, FILE, SYSTEM
    file_url VARCHAR(500),
    file_name VARCHAR(255),
    file_size BIGINT,
    reply_to BIGINT REFERENCES messages(id),
    is_edited BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);
CREATE INDEX IF NOT EXISTS idx_messages_sender ON messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at);
CREATE INDEX IF NOT EXISTS idx_user_conversations_user ON user_conversations(user_id);
CREATE INDEX IF NOT EXISTS idx_user_conversations_conversation ON user_conversations(conversation_id);
CREATE INDEX IF NOT EXISTS idx_conversations_created_by ON conversations(created_by);

-- Insert sample data for testing
INSERT INTO users (username, email, password_hash, first_name, last_name, status) VALUES
('admin', 'admin@chatapp.com', '$2a$10$example_hash', 'Admin', 'User', 'ONLINE'),
('testuser', 'test@chatapp.com', '$2a$10$example_hash', 'Test', 'User', 'OFFLINE')
ON CONFLICT (email) DO NOTHING;
