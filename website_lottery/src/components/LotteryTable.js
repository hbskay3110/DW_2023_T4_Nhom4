import React, { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/style.css";
import DatePicker from "sassy-datepicker";
import "../css/sassy-datepicker/dist/style.css";
import { TableData } from "./TableData"
import { API_PREFIX } from "../utils/Const";
const LotteryTable = (props) => {
  const [lotteryData, setLotteryData] = useState();
  const [selectedDate, setSelectedDate] = useState(null);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const buttonRef = useRef(null);
  const navigate = useNavigate();
  let link = props.code || "mien_nam";
  let date = props.date || '';
  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch(`${API_PREFIX}/lottery/${link}/${date}`);
        const data = await response.json();
        setLotteryData(data);
      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchData();
    // setLink(props.code ? props.code : "MN")
  }, [props.code, date]);
  useEffect(() => {
    // Update logic when data changes
  }, [lotteryData]);
  useEffect(() => {
    // Update logic when data changes
  },);
  const handleToggleDatePicker = () => {
    setShowDatePicker(!showDatePicker);
  };

  const calculateDatePickerPosition = () => {
    if (buttonRef.current) {
      const buttonRect = buttonRef.current.getBoundingClientRect();

      return {
        top: buttonRect.bottom - 25,
        left: buttonRect.left - 440,
      };
    }
    return {};
  };

  const convertDateFormat = (inputDate) => {
    // Chia ngày tháng năm từ định dạng "dd/mm/yyyy"
    const parts = inputDate.split('/');

    // Tạo đối tượng ngày từ các phần tử trích xuất
    const dateObject = new Date(parts[2], parts[1] - 1, parts[0]);

    // Lấy các thành phần ngày, tháng, năm từ đối tượng ngày
    const day = dateObject.getDate();
    const month = dateObject.getMonth() + 1; // Tháng bắt đầu từ 0
    const year = dateObject.getFullYear();

    // Tạo định dạng "dd-mm-yyyy"
    const formattedDate = `${day}-${month}-${year}`;

    return formattedDate;
  }
  const handleDateChange = (date) => {
    setSelectedDate(date);
    date &&
      navigate(`/lottery/${link}/${convertDateFormat(date.toLocaleDateString())}`);
    date && setShowDatePicker(false);


  };
  // alert(selectedDate && convertDateFormat(selectedDate.toLocaleDateString()));

  if (!lotteryData || lotteryData.length === 0) {
    return (
      <div className="loading-overlay">
        <div className="loading-spinner display-3">
          <i className="fas fa-spinner fa-spin"></i>
        </div>
      </div>
    );
  }

  return (
    <div className="row">
      <div className="col font-weight-bold">
        <div>
          <ul className="d-flex justify-content-between p-0 white-text flex-wrap">
            <li className="list-group-item pr-5 pl-5 pt-4 pb-4 blue darken-1 ">
              XSMN
            </li>
            <li className="list-group-item pr-5 pl-5 pt-4 pb-4 blue darken-1 ">
              XSMB
            </li>
            <li className="list-group-item pr-5 pl-5 pt-4 pb-4 blue darken-1 ">
              XSMT
            </li>
            <li className="list-group-item pr-5 pl-5 pt-4 pb-4 blue darken-1 ">
              Thống kê
            </li>
            <li className="list-group-item pr-5 pl-5 pt-4 pb-4 blue darken-1 ">
              Tiện ích
            </li>
          </ul>
        </div>
        <div className="bg-white">
          <button
            type="button"
            className="btn btn-outline-blue-grey text-capitalize rounded"
          >
            Chọn giải<i className="fas fa-caret-down ml-1"></i>
          </button>
          <button
            type="button"
            className="btn btn-outline-blue-grey text-capitalize rounded"
          >
            Hôm qua
          </button>
          <button
            type="button"
            className="btn btn-outline-primary text-capitalize rounded"
          >
            Hôm nay
          </button>
          <button
            type="button"
            className={`btn ${showDatePicker ? "btn-outline-primary" : "btn-outline-blue-grey"
              } text-capitalize rounded`}
            ref={buttonRef}
            onClick={handleToggleDatePicker}
          >
            Chọn ngày<i className="fas fa-caret-down ml-1"></i>
          </button>
          {showDatePicker && (
            <div
              className="position-absolute"
              style={calculateDatePickerPosition()}
            >
              <DatePicker />
            </div>
          )}
          {showDatePicker && (
            <div
              className="position-absolute"
              style={calculateDatePickerPosition()}
            >
              <DatePicker onChange={handleDateChange} />
            </div>
          )}
        </div>
        <div className=" pt-2 pb-2 blue darken-1 text-center white-text font-size-12">
          Xổ số mở thưởng ngày {lotteryData[0].date || "hôm nay"}
        </div>
        <div className="pt-2 pb-2 grey lighten-1 text-center">
          Kết quả Xổ số {link.includes('mien') ? lotteryData[0].region : lotteryData[0].location} {lotteryData[0].weekday ? lotteryData[0].weekday : "hôm nay"}{" "}
          {lotteryData[0].date ? lotteryData[0].date : ''}
        </div>
        <TableData data={lotteryData} />
        <div className="text-muted d-flex justify-content-start align-items-center white-text blue darken-1 mt-0 p-2">
          <i className="fas fa-dot-circle mr-1"></i> Đầy đủ{" "}
          <i className="fas fa-circle ml-2 mr-1"></i> 2 Số{" "}
          <i className="fas fa-circle ml-2 mr-1"></i> 3 Số
        </div>
      </div>
    </div>
  );
};

export default LotteryTable;
