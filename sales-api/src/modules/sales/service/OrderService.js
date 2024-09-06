import OrderRepository from "../repository/OrderRepository.js";
import { sendMessageToProductStockUpdateQueue } from "../../product/rabbitmq/productStockUpdateSender.js";
import * as httpStatus from "../../../config/constants/httpStatus.js";
import { PENDING, ACCEPTED, REJECTED } from "../status/OrderStatus.js";
import OrderException from "../exception/OrderException.js";

class OrderService {
    async createOrder(req) {
        try {
            let { orderData } = req.body;
            this.validateOrderData(orderData);
            const { authUser } = req;

            let order = {
                status: PENDING,
                user: authUser,
                createdAt: new Date(),
                updatedAt: new Date(),
                products: orderData
            };
            await this.validateProductStock(order);
            let createOrder = await OrderRepository.save(order);
            sendMessageToProductStockUpdateQueue(order.products);
            return {
                status: httpStatus.SUCCESS,
                createOrder,
            };
        } catch (error) {
            return {
                status: error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: error.message,
            };
        }
    }

    async updateOrder(orderMessage) {
        try{
            const order = JSON.parse(orderMessage)
            if(order.salesId && order.status) {
                let existingOrder = await OrderRepository.findById(order.salesId);
                if(existingOrder && order.status !== existingOrder.status) {
                    existingOrder.status = order.status;
                    await OrderRepository.save(existingOrder);
                }
            } else {
                console.warn("The order message was not complete");
            }
            
        } catch (err) {
            console.error("Could not parse order Message from queue");
            console.error(err.message);
        }
    }

    validateOrderData(data) {
        if(!data || !data.products) {
            throw new OrderException( httpStatus.BAD_REQUEST, 'The products must be informed.')
        }
    }

    async validateProductStock(order) {
        let stockIsOut = true;
            
        if(stockIsOut) {
            throw new OrderException(httpStatus.BAD_REQUEST, 'The stock is out for the product');
        }
    }
}

export default new OrderService();