import express from "express";

import * as db from "./src/config/db/initialData.js";
import UserRoutes from "./src/modules/user/routes/UserRoutes.js";
import tracing from "./src/config/tracing.js";

const app = express();
const env = process.env;
const PORT = env.PORT || 8080;
const CONTAINER_ENV = "container";

app.use(express.json());

app.get('/api/status', (req,res) => {
    return res.status(200).json({
        service: 'auth-api',
        status:  'up',
        httpStatus: 200
    })
})

startApplication();

function startApplication() {
    if(env.NODE_ENV !== CONTAINER_ENV) {
        db.createInitialDate();
    }
}

app.get("/api/initial-data", (req,res) => {
    db.createInitialDate();
    return res.json({ message: "Data created" })
})

app.use(tracing);
app.use(UserRoutes);

app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`);
})