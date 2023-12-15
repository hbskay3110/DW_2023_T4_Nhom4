import { useEffect, useState } from "react";

export const TableData = (props) => {
    const [lotteryData, setLotteryData] = useState(props.data);
    useEffect(() => {
        setLotteryData(props.data);
    }, [props.data]);
    return (
        <table className="table table-bordered bg-white mb-0 ">
            {lotteryData.length > 1 && (
                <thead>
                    <tr>
                        <th className="font-weight-bold grey lighten-1 h5">Giải</th>
                        {lotteryData.map((location) => (
                            <th
                                className="font-weight-bold grey lighten-1 h5"
                                key={location.id}
                            >
                                {location.location}
                            </th>
                        ))}
                    </tr>
                </thead>
            )}
            <tbody>
                {lotteryData[0]?.prizes.map((prize, rowIndex) => (
                    <tr key={rowIndex}>
                        <td className="font-weight-bold pr-1 pl-1 text-center align-middle h5">
                            {prize.prizeName}
                        </td>
                        {lotteryData.map((location) => (
                            <td key={location.id}>
                                {(() => {
                                    let foundIndex = -1;
                                    const foundPrize = location.prizes.find(
                                        (prizeItem, i) => {
                                            if (prizeItem.prizeName === prize.prizeName) {
                                                foundIndex = i;
                                                return true;
                                            }
                                            return false;
                                        }
                                    );

                                    return foundPrize?.result.map((prizeItem, index) => (

                                        <div
                                            className={`${(foundIndex === 8 || foundIndex === 0) &&
                                                "text-danger"
                                                } pl-1 pr-1 font-weight-bold h5`}
                                            key={index}
                                        >
                                            {prizeItem}
                                        </div>
                                    ));
                                })()}
                            </td>
                        ))}
                    </tr>
                ))}
            </tbody>
        </table>
    )
}