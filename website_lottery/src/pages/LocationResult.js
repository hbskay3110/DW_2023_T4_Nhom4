
import "../App.css";
import { useParams } from "react-router-dom";
import { Location } from "../components/Location";
import LotteryTable from "../components/LotteryTable";

export const LocationResult = () => {

    let { code, date } = useParams();
    const isValidDateFormat = (date) => {
        // Biểu thức chính quy cho định dạng ngày tháng "d-m-yyyy" hoặc "dd-mm-yyyy"
        const dateRegex = /^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[0-2])-\d{4}$/;

        // Kiểm tra chuỗi với biểu thức chính quy
        return dateRegex.test(date);
    }
    date = isValidDateFormat(date) ? date : "";

    return (
        <div className="App">
            <div className="container bg-light">
                <div className="row pt-4 pb-4">
                    <div className="col-md-3">
                        <Location></Location>
                    </div>
                    <div className="col-md-9">
                        <LotteryTable code={code} date={date ? date : ''}></LotteryTable>
                    </div>
                </div>
            </div>
        </div>
    );

}