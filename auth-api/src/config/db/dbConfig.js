import { Sequelize } from "sequelize";
import * as db from "../constants/secret.js"

const sequelize = new Sequelize(db.DB_NAME,db.DB_USER,db.DB_PASSWORD, {
    host: db.DB_HOST,
    dialect: "postgres",
    port: db.DB_PORT,
    quoteIdentifiers: false,
    define: {
        syncOnAssociation: true,
        timestamps: false,
        underscored: true,
        underscoredAll: true,
        freezeTableName: true
    },
    pool: {
        acquire: 180000,
    }
});

sequelize.authenticate().then(() => {
    console.info("Connection has been stablished")
}).catch((err) => {
    console.error("Unable to connect to the database.");
    console.error(err);
});

export default sequelize;