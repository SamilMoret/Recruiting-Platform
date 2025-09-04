CREATE TABLE Application (
  id INT AUTO_INCREMENT PRIMARY KEY,
  resume VARCHAR(255),
  status ENUM('Applied', 'In Review', 'Rejected', 'Accepted') DEFAULT 'Applied',
  jobId INT,
  applicantId INT,
  createdAt DATETIME DEFAULT CURRENT_TIMESTAMP,
  updatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (jobId) REFERENCES Job(id),
  FOREIGN KEY (applicantId) REFERENCES User(id)
);