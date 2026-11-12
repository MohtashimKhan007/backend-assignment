CREATE TABLE github_repositories (
                                     id BIGINT PRIMARY KEY,
                                     name VARCHAR(255),
                                     description TEXT,
                                     owner_name VARCHAR(255),
                                     language VARCHAR(100),
                                     stars INTEGER,
                                     forks INTEGER,
                                     last_updated TIMESTAMP
);