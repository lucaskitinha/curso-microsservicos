import express from "express";

import * as db from "./src/config/db/initialData.js";
import UserRoutes from "./src/modules/user/routes/UserRoutes.js";

const app = express();
const env = process.env;
const PORT = env.PORT || 8080;

db.createInitialDate();

app.use(express.json());

app.get('/api/status', (req,res) => {
    return res.status(200).json({
        service: 'auth-api',
        status:  'up',
        httpStatus: 200
    })
})

app.use(UserRoutes);

app.listen(PORT, () => {
    console.info(`Server started successfully at port ${PORT}`);
})