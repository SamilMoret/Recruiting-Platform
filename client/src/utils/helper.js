//validation functions
export const validateEmail = (email) => {
  if (!email) return "Email is required";
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) return "Please enter a valid email address";
  return "";
};

export const validatePassword = (password) => {
  if (!password) return "Password is required";
  if (password.length < 8) return "Password must be at least 8 characters long";
  if (!/[A-Z]/.test(password))
    return "Password must contain at least one uppercase letter";
  if (!/[a-z]/.test(password))
    return "Password must contain at least one lowercase letter";
  if (!/[0-9]/.test(password))
    return "Password must contain at least one number";
  if (!/[!@#$%^&*]/.test(password))
    return "Password must contain at least one special character";
  return "";
};

export const validateAvatar = (file) => {
  if (!file) return ""; //Avatar is optional

  const allowedTypes = ["image/jpeg", "image/png", "image/jpg"];
  if (!allowedTypes.includes(file.type))
    return "Avatar must be a JPG or PNG file";

  const maxSize = 10 * 1024 * 1024;
  if (file.size > maxSize) return "Avatar must be less than 10MB";

  return "";
};

export const getInitials = (name) => {
  return name
    .split(" ")
    .map((word) => word.chart(0))
    .join("")
    .toUpperCase()
    .slice(0, 2);
};
