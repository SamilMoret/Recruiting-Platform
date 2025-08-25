const fs = require("fs");
const path = require("path");
const User = require("../models/User");

// @desc Update user profile(name, avatar, company, details)
exports.updateProfile = async (req, res) => {
  const {
    name,
    email,
    avatar,
    resume,
    companyName,
    companyDescription,
    companyLogo,
  } = req.body;

  try {
    const user = await User.findById(req.user._id);
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    user.name = name || user.name;
    user.avatar = avatar || user.avatar;
    user.resume = resume || user.resume;

    // if employer, allow updating company info
    if (user.role === "employer") {
      user.companyName = companyName || user.companyName;
      user.companyDescription = companyDescription || user.companyDescription;
      user.companyLogo = companyLogo || user.companyLogo;
    }
    await user.save();

    res.json({
      _id: user._id,
      name: user.name,
      avatar: user.avatar,
      role: user.role,
      companyName: user.companyName,
      companyDescription: user.companyDescription,
      companyLogo: user.companyLogo,
      resume: user.resume || "",
    });
  } catch (error) {
    res.status(500).json({ message: "problem is here" });
  }
};

// @desc Delete resume file (Jobseeker only)
exports.deleteResume = async (req, res) => {
  try {
    const { resumeUrl } = req.body;

    //extract file name from the url
    const fileName = resumeUrl?.split("/")?.pop();

    const user = await User.findById(req.user._id);
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }
    if (user.role !== "jobseeker") {
      return res
        .status(403)
        .json({ message: "Only jobseekers can delete their resume" });
    }

    //Construct the full file path
    const filePath = path.join(__dirname, "../uploads", fileName);

    // check if the file exists and then delete
    if (fs.existsSync(filePath)) {
      fs.unlinkSync(filePath); // delete the file
    }

    //set the user's resume field an empty string
    user.resume = "";

    await user.save();
    return res.status(200).json({ message: "Resume deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};

// @desc Get public profile
exports.getPublicProfile = async (req, res) => {
  try {
    const user = await User.findById(req.params.id).select("-password");

    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    res.json(user);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
};
