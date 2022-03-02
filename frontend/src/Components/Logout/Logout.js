import Navbar from "../Navbar/Navbar";
import {Redirect} from "../Router/Router";
import {removeSessionObject} from "../../utils/session";

const Logout = () => {
  console.log("Logout");
  removeSessionObject("user");
  Navbar();
  Redirect("/Login");
};

export default Logout;
