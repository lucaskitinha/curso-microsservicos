import amqp from 'amqplib/callback_api.js';

import * as rabbitConstants from "./queue.js";
import { RABBIT_MQ_URL } from '../constants/secrets.js';

const HALF_SECOND = 500;

export async function connectRabbitMq(params) {
    amqp.connect(RABBIT_MQ_URL, (error, connection) => {
        if (error) {
            throw error;
        }
        createQueue(connection, 
            rabbitConstants.PRODUCT_STOCK_UPDATE_QUEUE,
            rabbitConstants.PRODUCT_STOCK_UPDATE_ROUTING_KEY,
            rabbitConstants.PRODUCT_TOPIC)
        createQueue(connection, 
            rabbitConstants.SALES_CONFIRMATION_QUEUE,
            rabbitConstants.SALES_CONFIRMATION_ROUTING_KEY,
            rabbitConstants.PRODUCT_TOPIC)
        setTimeout(function () {
            connection.close();
        }, HALF_SECOND)
    });

    function createQueue(connection, queue, routingKey, topic) {
        connection.createChannel((error, channel) => {
            if (error) {
                throw error;
            }
            channel.assertExchange(topic,"topic", { durable: true });
            channel.assertQueue(queue, { durable: true });
            channel.bindQueue(queue, topic, routingKey);
        });
    }
    
}