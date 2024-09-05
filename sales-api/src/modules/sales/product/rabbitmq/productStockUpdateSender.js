import amqp from "amqplib/callback_api.js"

import { RABBIT_MQ_URL } from "../../../../config/constants/secrets.js";
import * as rabbitConstants from "../../../../config/rabbitmq/queue.js";

export function sendMessageToProductStockUpdateQueue(message) {
    amqp.connect(RABBIT_MQ_URL, (error, connection) => {
        if (error) {
            throw error;
        }
        console.info("Listening to Sales Confirmation Queue...");
        connection.createChannel((error, channel) => {
            if (error) {
                throw error;
            }
            let jsonStringMessage = JSON.stringify(message);
            console.info(`Sending message to product update stock: ${jsonStringMessage}`);
            channel.publish(
                rabbitConstants.PRODUCT_TOPIC ,
                rabbitConstants.PRODUCT_STOCK_UPDATE_ROUTING_KEY,
                Buffer.from(jsonStringMessage)
            );
            console.info("Message was sent successfully!")
        });
    });
}