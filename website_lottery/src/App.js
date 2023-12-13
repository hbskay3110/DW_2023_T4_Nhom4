import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { Home } from "./pages/Home";
import { LocationResult } from "./pages/LocationResult";
import { useEffect } from "react";
import { fetchRegionsData } from "./utils/Const";

function App() {
  useEffect(() => {
    fetchRegionsData();
  }, []); // Empty dependency array ensures the effect runs only once on mount

  return (
    <Router>
      <Routes>
        <Route index path="/" element={<Home />} />
        {/* Add a dynamic route for the location code */}
        <Route path="/lottery/:code" element={<LocationResult />} />
        <Route path="/lottery/:code/:date" element={<LocationResult />} />
      </Routes>
    </Router>
  );
}

export default App;
