CREATE TABLE IF NOT EXISTS profile.profile (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID,
    first_name VARCHAR(255),
    city VARCHAR(255),
    email VARCHAR(255),
    active_in_search BOOLEAN DEFAULT FALSE,
    date_of_birhday TIMESTAMP,
    created_at TIMESTAMP,
    gender SMALLINT,
    search_gender SMALLINT,
    search_age_min SMALLINT,
    search_age_max SMALLINT,
    geo_point JSONB,
    is_student BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS profile.profile_photo_links (
    profile_id BIGINT REFERENCES profile.profile(id) ON DELETE CASCADE,
    photo_links VARCHAR(512),
    PRIMARY KEY (profile_id, photo_links)
);

CREATE TABLE IF NOT EXISTS profile.student (
    id BIGSERIAL PRIMARY KEY,
    profile_id BIGINT REFERENCES profile.profile(id) ON DELETE CASCADE,
    university VARCHAR(255),
    faculty VARCHAR(255),
    specialization VARCHAR(255),
    course INTEGER,
    search_university VARCHAR(255),
    search_faculty VARCHAR(255),
    student_id_card_hash VARCHAR(255)
);