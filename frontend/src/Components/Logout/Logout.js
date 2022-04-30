import Navbar from "../Navbar/Navbar";
import {Redirect} from "../Router/Router";
import {removeSessionObject} from "../../utils/session";

const Logout = () => {
  removeSessionObject("user");
  Navbar();
  Redirect("/");
};

export default Logout;
