import { AXIOS_INSTANCE } from "./Axios";


export const user_login_api = async(requestBody) => {
  console.log("user_login_api post (", requestBody, ")");
  return await AXIOS_INSTANCE.post("/user/login", requestBody)
  .then(response => response)
  .catch(error => {
    throw error;
  });
}

export const user_user_api = async(requestBody) => {
  console.log("user_user_api get");
  return await AXIOS_INSTANCE.get("/user/user")
  .then(response => response)
  .catch(error => {
    throw error;
  });
}