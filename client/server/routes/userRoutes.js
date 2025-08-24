const express = require("express");
const {
  updateProfile,
  deleteResume,
  getPublicProfile,
} = require("../controllers/userController");
const { protect } = require("../middlewares/authMiddleware");

const router = express.Router();

//protected routes
router.route("/profile").put(updateProfile);
router.route("/resume").delete(deleteResume);

//public route
router.route("/:id").get(getPublicProfile);

module.exports = router;
