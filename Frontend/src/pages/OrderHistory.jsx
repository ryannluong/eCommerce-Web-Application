import React from "react";
import {useUser} from "hook/User";
import styled from "styled-components";
import {orderList} from "backend/idm";
import {useNavigate, useParams} from "react-router-dom";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const CenterDiv = styled.div`
  display: flex;
  justify-content: space-around;
  align-items: center;
  padding: 5px;
`

const OrderHistory = () => {
    const {
        accessToken, setAccessToken,
        refreshToken, setRefreshToken
    } = useUser();

    const navigate = useNavigate();
    const [history, setHistory] = React.useState({sales: []});

    const getHistory = () => {
        const payload = {
            accessToken: accessToken
        };

        orderList(payload).then(res => setHistory(res.data))
    }

    React.useEffect(() => {
        getHistory();
    }, [])

    return (
        <StyledDiv>
            <CenterDiv>
                <h1>
                    Order History
                </h1>
            </CenterDiv>
            <div>
                <table className="salesList" style={{gap: 25}}>
                    {history.sales &&
                        <tbody>
                            <tr>
                                <th> Order ID </th>
                                <th> Total </th>
                                <th> Order Date </th>
                            </tr>

                            {history.sales && history.sales.map(sale => (
                                <tr key={sale.saleId}>
                                    <a href={`/order/detail/${sale.saleId}`} title={sale.saleId}>
                                        <td> {sale.saleId} </td>
                                    </a>
                                    <td> ${(sale.total * 100).toFixed(2)} </td>
                                    <td> {sale.orderDate} </td>
                                </tr>
                            ))
                            }
                        </tbody>
                    }
                </table>
                <CenterDiv>
                    {!history.sales &&
                        <>
                            <h2 style={{textAlign: "center"}}>
                                No orders to display
                            </h2>
                        </>
                    }

                </CenterDiv>
            </div>
        </StyledDiv>
    )
}

export default OrderHistory;