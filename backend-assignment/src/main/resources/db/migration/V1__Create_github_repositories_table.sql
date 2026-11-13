CREATE TABLE IF NOT EXISTS github_repositories (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    description TEXT,
    owner_name VARCHAR(255),
    language VARCHAR(100),
    stars INTEGER,
    forks INTEGER,
    last_updated TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_github_repositories_language ON github_repositories(language);
CREATE INDEX IF NOT EXISTS idx_github_repositories_stars ON github_repositories(stars);
CREATE INDEX IF NOT EXISTS idx_github_repositories_forks ON github_repositories(forks);
CREATE INDEX IF NOT EXISTS idx_github_repositories_last_updated ON github_repositories(last_updated);