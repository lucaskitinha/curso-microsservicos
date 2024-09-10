import OrderRepository from "../repository/OrderRepository.js";
import { sendMessageToProductStockUpdateQueue } from "../../product/rabbitmq/productStockUpdateSender.js";
import * as httpStatus from "../../../config/constants/httpStatus.js";
import { PENDING, ACCEPTED, REJECTED } from "../status/OrderStatus.js";
import OrderException from "../exception/OrderException.js";
import ProductClient from "../../product/client/ProductClient.js";

class OrderService {
    async createOrder(req) {
        try {
            let orderData  = req.body;
            const { transactionid, serviceid } = req.headers;
            console.info(`Request to POST new order with data ${JSON.stringify(orderData)} | [transactionID: ${transactionid} | serviceID: ${serviceid}]`);

            this.validateOrderData(orderData);
            const { authUser } = req;
            const { authorization } = req.headers;

            let order = this.createInitialOrderData(orderData, authUser, transactionid, serviceid);
            await this.validateProductStock(order, authorization, transactionid);
            let createOrder = await OrderRepository.save(order);
            this.sendMessage(createOrder, transactionid);
            
            let response =  {
                status: httpStatus.SUCCESS,
                createOrder,
            };
            
            console.info(`Response to POST new order with data ${JSON.stringify(createOrder)} | [transactionID: ${transactionid} | serviceID: ${serviceid}]`);
            return response

        } catch (error) {
            return {
                status: error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: error.message,
            };
        }
    }

    createInitialOrderData(orderData, authUser, transactionid, serviceid) {
        return {
            status: PENDING,
            user: authUser,
            createdAt: new Date(),
            updatedAt: new Date(),
            transactionid,
            serviceid,
            products: orderData.products,
        };
    }

    async updateOrder(orderMessage) {
        try{
            const order = JSON.parse(orderMessage)
            if(order.salesId && order.status) {
                let existingOrder = await OrderRepository.findById(order.salesId);
                if(existingOrder && order.status !== existingOrder.status) {
                    existingOrder.status = order.status;
                    existingOrder.updatedAt = new Date();
                    await OrderRepository.save(existingOrder);
                }
            } else {
                console.warn(`The order message was not complete. TransactionID: ${orderMessage.transactionid}`);
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

    async validateProductStock(order, token, transactionid) {
        let stockIsOk = await ProductClient.checkProductStock(order, token, transactionid);
            
        if(!stockIsOk) {
            throw new OrderException(httpStatus.BAD_REQUEST, 'The stock is out for the product');
        }
    }

    sendMessage(createdOrder, transactionid) {
        const message = {
            salesId: createdOrder.id,
            products: createdOrder.products,
            transactionid,
        }
        sendMessageToProductStockUpdateQueue(message);
    }

    async findById(req) {
        try {
            const { id } = req.params;
            const { transactionid, serviceid } = req.headers;
            console.info(`Request to GET order by ID: ${id} | [transactionID: ${transactionid} | serviceID: ${serviceid}]`);
            this.validateInformedId(id);
            const existingOrder = await OrderRepository.findById(id);
            if(!existingOrder) {
                throw new OrderException(httpStatus.BAD_REQUEST, "The order was not informed");
            } 
            let response = {
                status: httpStatus.SUCCESS,
                existingOrder,
            };
            console.info(`Response to GET order by ID: ${id} with data ${JSON.stringify(response)} | [transactionID: ${transactionid} | serviceID: ${serviceid}]`);
            return response;
        } catch (error) {
            return {
                status: error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: error.message,
            };
        }
    }

    async findAll() {
        try {
            const { transactionid, serviceid } = req.headers;
            console.info(`Request to GET all orders | [transactionID: ${transactionid} | serviceID: ${serviceid}]`);
            const orders = await OrderRepository.findAll();
            if(!orders) {
                throw new OrderException(httpStatus.BAD_REQUEST, "No orders were found");
            } 
            let response = {
                status: httpStatus.SUCCESS,
                salesId: orders.map(order => {
                    return order.id;
                }),
            };
            
            console.info(`Response to GET all orders with data ${JSON.stringify(response)} | [transactionID: ${transactionid} | serviceID: ${serviceid}]`);
            return response;
        } catch (error) {
            return {
                status: error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: error.message,
            };
        }
    }

    async findByProductId(req) {
        try {

            const { productId } = req.params;
            const { transactionid, serviceid } = req.headers;
            console.info(`Request to GET orders by productID: ${productId} with data ${JSON.stringify(response)} | [transactionID: ${transactionid} | serviceID: ${serviceid}]`);
            this.validateInformedProductId(productId);
            const orders = await OrderRepository.findByProductId(productId);
            if(!orders) {
                throw new OrderException(httpStatus.BAD_REQUEST, "No orders were found");
            } 
            let response = {
                status: httpStatus.SUCCESS,
                salesId: orders.map(order => {
                    return order.id;
                }),
            };
            console.info(`Response to GET orders by productID: ${productId} | [transactionID: ${transactionid} | serviceID: ${serviceid}]`);
            return response;
        } catch (error) {
            return {
                status: error.status ? error.status : httpStatus.INTERNAL_SERVER_ERROR,
                message: error.message,
            };
        }
    }

    validateInformedId(id) {
        if(!id) {
            throw new OrderException(httpStatus.BAD_REQUEST, "The order ID must be informed.")
        }
    }

    validateInformedProductId(productId) {
        if(!productId) {
            throw new OrderException(httpStatus.BAD_REQUEST, "The order's productId must be informed.")
        }
    }  
}

export default new OrderService();