import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "../css/style.css";
import { API_PREFIX, fetchRegionsData, regionsData } from "../utils/Const";

export const Location = () => {
  const [locationData, setLocationData] = useState([]);
  const [expandedRegions, setExpandedRegions] = useState([]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch regionsData if it's not available
        if (!regionsData.length) {
          await fetchRegionsData();
        }

        // Continue with your existing fetch logic here
        const response = await fetch(`${API_PREFIX}/regions`);
        const data = await response.json();
        // Map data to the desired structure
        const modifiedData = data.data.map(region => {
          const matchingRegion = regionsData.find(entry => entry.name === region.region);
          return {
            id: region.id,
            region: region.region,
            code: matchingRegion ? matchingRegion.code : '',
            listLocations: region.listLocations.map(location => {
              const matchingLocation = regionsData.find(entry => entry.name === location);
              return {
                code: matchingLocation ? matchingLocation.code : '', // Use the code from regionsData
                name: location
              };
            })
          };
        });
        // Sắp xếp mảng theo id trước khi set vào state
        const sortedData = modifiedData.sort((a, b) => a.id - b.id);
        setLocationData(sortedData);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchData();
  }, []);

  const handleExpandToggle = (regionId) => {
    if (expandedRegions.includes(regionId)) {
      setExpandedRegions((prevExpanded) =>
        prevExpanded.filter((id) => id !== regionId)
      );
    } else {
      setExpandedRegions((prevExpanded) => [...prevExpanded, regionId]);
    }
  };
  if (!regionsData) {
    return (
      <div className="loading-overlay">
        <div className="loading-spinner display-3">
          <i className="fas fa-spinner fa-spin"></i>
        </div>
      </div>
    );
  }

  return (
    <>
      <div>
        {locationData.map((region) => (
          <div key={region.id}>
            <ul className="list-group">
              <li
                className="list-group-item active text-uppercase"
                aria-current="true"
              >
                <Link
                  to={`/lottery/${region.code}`}
                  className="text-white"
                >{`Xổ số ${region.region}`}</Link>
              </li>
              {region.listLocations
                .slice(
                  0,
                  expandedRegions.includes(region.id)
                    ? region.listLocations.length
                    : 8
                )
                .map((location, index) => (
                  <li
                    key={index}
                    className="list-group-item text-left text-muted"
                  >
                    <i className="fa fa-angle-double-right mr-2"></i>
                    <Link
                      to={`/lottery/${location.code}`}
                      className="text-muted"
                    >{`Xổ số ${location.name}`}</Link>
                  </li>
                ))}
            </ul>
            {region.listLocations.length > 6 && (
              <button
                onClick={() => handleExpandToggle(region.id)}
                className="btn white text-capitalize btn-block"
              >
                {expandedRegions.includes(region.id) ? "Thu nhỏ" : "Xem thêm"}
              </button>
            )}
          </div>
        ))}
      </div>
    </>
  );
};
