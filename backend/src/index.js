require('dotenv').config();
const express = require('express');
// Must be required before any route files are required: it patches Express so a
// rejected promise inside an `async (req, res) => {...}` handler is forwarded to the
// error middleware below instead of becoming an unhandled rejection that crashes the
// whole process (this actually happened during development when Postgres blipped).
require('express-async-errors');
const cors = require('cors');

const PLACEHOLDER_SECRET = 'change-this-secret-in-production';
if (process.env.NODE_ENV === 'production' && (!process.env.JWT_SECRET || process.env.JWT_SECRET === PLACEHOLDER_SECRET)) {
  console.error('Refusing to start: set a real JWT_SECRET (not the .env.example placeholder) before running in production.');
  process.exit(1);
}

const { router: authRouter } = require('./routes/auth');
const workOrdersRouter = require('./routes/workOrders');
const techniciansRouter = require('./routes/technicians');
const inventoryRouter = require('./routes/inventory');
const dashboardRouter = require('./routes/dashboard');
const customersRouter = require('./routes/customers');
const reportsRouter = require('./routes/reports');

const app = express();
app.use(cors());
app.use(express.json());

app.get('/health', (req, res) => res.json({ ok: true }));

app.use('/api/auth', authRouter);
app.use('/api/work-orders', workOrdersRouter);
app.use('/api/technicians', techniciansRouter);
app.use('/api/inventory', inventoryRouter);
app.use('/api/dashboard', dashboardRouter);
app.use('/api/customers', customersRouter);
app.use('/api/reports', reportsRouter);

app.use((err, req, res, next) => {
  console.error(err);
  res.status(500).json({ error: 'Internal server error' });
});

const port = process.env.PORT || 3000;
app.listen(port, () => console.log(`API listening on :${port}`));
