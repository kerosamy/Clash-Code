import { Outlet, useNavigate, useParams, useLocation } from "react-router-dom";
import TopActionButtons from "./components/TopActionButtons";
import SubmitProblemIcon from "../src/assets/icons/subimt.svg";
import ProblemStatmentIcon from "../src/assets/icons/problem-statement.svg";
import { useState, useEffect } from "react";

export default function ProblemLayout() {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  // Set button based on current URL
  const [selected, setSelected] = useState("Problem-Statment");

  useEffect(() => {
    if (location.pathname.endsWith("/submit")) setSelected("Submit-Solution");
    else setSelected("Problem-Statment");
  }, [location.pathname]);

  const buttons = [
    { 
      icon: <img src={ProblemStatmentIcon} className="w-5 h-5"/>, 
      label: "Problem Statement", 
      value: "Problem-Statment", 
      onClick: () => navigate(`/problem/${id}`)
    },
    { 
      icon: <img src={SubmitProblemIcon} className="w-5 h-5"/>, 
      label: "Submit Solution", 
      value: "Submit-Solution", 
      onClick: () => navigate(`/problem/${id}/submit`)
    },
  ];

  return (
    <div>
      <TopActionButtons selectedValue={selected} buttons={buttons} />
      <Outlet />
    </div>
  );
}
