const express = require('express');
const path = require('path');

const app = express();
const PORT = 3000;

app.use('/libs/bootstrap', express.static(path.join(__dirname, 'node_modules/bootstrap/dist')));
app.use('/libs/jquery', express.static(path.join(__dirname, 'node_modules/jquery/dist')));
app.use('/libs/angular', express.static(path.join(__dirname, 'node_modules/angular')));
app.use('/libs/angular-route', express.static(path.join(__dirname, 'node_modules/angular-route')));
app.use('/', express.static(path.join(__dirname, 'public')));

app.get('/', (req, res) => {
  res.sendFile(path.join(__dirname, 'public/index.html'));
});

app.listen(PORT, () => {
  console.log(`Frontend corriendo en http://192.168.12.1:${PORT}`);
});
