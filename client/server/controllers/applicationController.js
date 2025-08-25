const Application = require("../models/Application");
const Job = require("../models/job");

//@desc  Apply to a job
exports.applyToJob = async (req, res) => {
  try {
    if (req.user.role !== "jobseeker") {
      return res
        .status(403)
        .json({ message: "Only jobseeker can apply to jobs" });
    }
    const existing = await Application.findOne({
      job: req.params.jobId,
      applicant: req.user.id,
    });

    if (existing) {
      return res
        .status(400)
        .json({ message: "You have already applied to this job" });
    }

    const application = await Application.create({
      job: req.params.jobId,
      applicant: req.user._id,
      resume: req.user.resume, //assuming resume is stored in user profile
    });

    res.status(201).json(application);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

// @desc Get all applicants for a job(employer)
exports.getMyApplications = async (req, res) => {
  try {
    const apps = await Application.find({ applicant: req.user._id }).populate(
      "job",
      "title company location type"
    );
    res.json(apps);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

// @desc get logged-in user's applications
exports.getApplicantsForJob = async (req, res) => {
  try {
    const apps = await Application.find({ applicant: req.user._id })
      .populate("job", "title company location type")
      .sort({ createdAt: -1 });
    res.json(apps);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

// @desc   get applicantion by id (jobseeker of Employer)
exports.getApplicationById = async (req, res) => {
  try {
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

//@ desc Update application status (employer)
exports.updateStatus = async (req, res) => {
  try {
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};
