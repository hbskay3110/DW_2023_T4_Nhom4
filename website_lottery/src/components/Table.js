import React from "react";

const Table = () => {
  // Tạo một mảng 2 chiều đại diện cho dữ liệu bảng (10 dòng x 34 cột)
  const tableData = Array.from({ length: 10 }, (_, rowIndex) => {
    // Mỗi dòng sẽ là một mảng chứa giá trị của từng ô
    return Array.from(
      { length: 34 },
      (_, colIndex) => `Giải ${colIndex + 1} - Tỉnh ${rowIndex + 1}`
    );
  });

  return (
    <table border="1">
      <thead>
        <tr>
          <th>Giải</th>
          {[...Array(10)].map((_, index) => (
            <th key={index}>Tỉnh {index + 1}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {tableData.map((row, rowIndex) => (
          <tr key={rowIndex}>
            {row.map((cell, colIndex) => (
              <td key={colIndex}>{cell}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default Table;
