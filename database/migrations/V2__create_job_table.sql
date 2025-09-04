CREATE TABLE Job (
  id INT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(255) NOT NULL,
  description TEXT NOT NULL,
  requirements TEXT NOT NULL,
  location VARCHAR(255),
  category VARCHAR(255),
  type ENUM('Full-Time', 'Part-Time', 'Contract', 'Remote', 'Internship') DEFAULT 'Full-Time',
  salaryMin INT,
  salaryMax INT,
  isClosed BOOLEAN DEFAULT FALSE,
  companyId INT,
  createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
  updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (companyId) REFERENCES User(id)
);