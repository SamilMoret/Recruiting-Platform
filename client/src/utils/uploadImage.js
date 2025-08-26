import { API_PATHS } from "./apiPaths";
import axiosInstance from "./axiosInstance";

export const uploadImage = async (imageFile) => {
  const formData = new FormData();
  formData.append("image", imageFile);

  try {
    const response = await axiosInstance.post(
      API_PATHS.IMAGE.UPLOAD_IMAGE,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data", //Set header for fle upload
        },
      }
    );
    return response.data; //return response data
  } catch (error) {
    console.error("Error uploading image:", error);
    throw error; // Rethrow the error for further handling
  }
};

export default uploadImage;
