import express from "express";

import { connectMongoDb } from "./src/config/db/mongoDbConfig.js";
import { createInitialData } from "./src/config/db/initialData.js";
import checkToken from "./src/config/auth/checkToken.js";
import { connectRabbitMq } from './src/config/rabbitmq/rabbitConfig.js';
import tracing from "./src/config/tracing.js";

import { sendMessageToProductStockUpdateQueue } from "./src/modules/product/rabbitmq/productStockUpdateSender.js"
import orderRoutes from "./src/modules/sales/routes/OrderRoutes.js"

const app = express();
const env = process.env;
const PORT = env.PORT || 8082;
const CONTAINER_ENV = "container";
const THREE_MINUTES = 180000;

startApplication();

function startApplication() {

    if (CONTAINER_ENV === env.NODE_ENV) {
        console.info("Waiting for RabbitMq and MongoDB containers to start...");
        setInterval(async () => {
            connectMongoDb();
            connectRabbitMq();
        }, THREE_MINUTES);
    } else {
      connectMongoDb();
      createInitialData();
      connectRabbitMq();
    }
}

app.use(express.json());

app.get("/api/initial-data", (req,res) => {
    db.createInitialData();
    return res.json({ message: "Data created" })
})

app.use(tracing);
app.use(checkToken);
app.use(orderRoutes);

app.get("/teste", (req, res) => {
    try {
        sendMessageToProductStockUpdateQueue([
            {
                productId: 1000,
                quantity: 3,
            },
            {
                productId: 1001,
                quantity: 2,
            },
            {
                productId: 1002,
                quantity: 1,
            },
        ])
        return res.status(200).json({ status: 200 });
    } catch (err) {
        console.log(err);
        return res.status(500).json({ error: true });
    }
});

app.get('/api/status', (req,res) => {
    return res.status(200).json({
        service: 'sales-api',
        status:  'up',
        httpStatus: 200
    })
})

app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`);
});