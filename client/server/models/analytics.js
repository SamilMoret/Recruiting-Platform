const mongoose = require;

const analyticsSchema = new mongoose.Schema(
  {
    job: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    totalJobsPosted: {
      type: Number,
      default: 0,
    },
    totalApplicationsReceived: {
      type: Number,
      default: 0,
    },
    totalHired: {
      type: Number,
      default: 0,
    },
  },
  { timestamps: true }
);

module.exports = mongoose.model("Analytics", analyticsSchema);
