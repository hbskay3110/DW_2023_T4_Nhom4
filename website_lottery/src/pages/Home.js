
import "../App.css";
import { Location } from "../components/Location";
import LotteryTable from "../components/LotteryTable";

export const Home = () => {
  const onChange = (date) => {
    // console.log(date.toString());
  };

  return (
    <div className="App">
      <div className="container bg-light">
        <div className="row pt-4 pb-4">
          <div className="col-md-3">
            <Location></Location>
          </div>
          <div className="col-md-9">
            <LotteryTable></LotteryTable>
          </div>
        </div>
      </div>
    </div>
  );
};
