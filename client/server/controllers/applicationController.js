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

// @desc Get my applications
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
// @desc  get all applicants for a job(employer)
exports.getApplicantsForJob = async (req, res) => {
  try {
    const job = await Job.findById(req.params.jobId);

    if (!job || job.company.toString() !== req.user._id.toString()) {
      return res.status(403).json({ message: "Not authorized" });
    }

    const applications = await Application.find({ job: req.params.jobId })
      .populate("job", "title location category type")
      .populate("applicant", "name email avatar resume");
    res.json(applications);
  } catch (err) {
    res.status(500).json({ message: err.message });
  }
};

// @desc get logged-in user's applications
// Duplicate removed. Use getMyApplications for logged-in user's applications.

// @desc   get applicantion by id (jobseeker of Employer)
exports.getApplicationById = async (req, res) => {
  try {
    const app = await Application.findById(req.params.id)
      .populate("job", "title")
      .populate("applicant", "name email avatar resume");
    if (!app) {
      return res
        .status(404)
        .json({ message: "Application not found", id: req.params.id });
    }
    const isOwner =
      (app.applicant &&
        app.applicant._id &&
        app.applicant._id.toString() === req.user._id.toString()) ||
      (app.job &&
        app.job.company &&
        app.job.company.toString() === req.user._id.toString());

    if (!isOwner) {
      return res
        .status(403)
        .json({ message: "Not authorized to view this application" });
    }
    res.json(app);
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
