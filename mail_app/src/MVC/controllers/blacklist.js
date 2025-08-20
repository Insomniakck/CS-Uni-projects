const blacklist = require('../models/blacklist');

exports.addURL = async (req, res) => {
    const { url } = req.body;
    if (!url) {
        return res.status(400).json({ error: 'Missing URL in request body.' });
    }

    try {
        const response = await blacklist.addURL(url);
        respondFromStatusLine(res, response);
    } catch (err) {
        res.status(500).json({ error: err.toString() });
    }
};

exports.deleteURL = async (req, res) => {
    const encodedURL = req.params.urlID;
    if (!encodedURL) {
        return res.status(400).json({ error: 'Missing URL in request parameter.' });
    }

    let url;
    try {
        url = decodeURIComponent(encodedURL);
    } catch (e) {
        res.status(400).json({ error: 'Invalid URL encoding' });
    }

    try {
        const response = await blacklist.deleteURL(url);
        respondFromStatusLine(res, response);
    } catch (err) {
        res.status(500).json({ error: err.toString() });
    }
};

// helper function to parse and extract status code and text from the returned string
function respondFromStatusLine(res, responseLine) {
    const [statusCodeStr, ...statusParts] = responseLine.split(' ');
    const statusCode = parseInt(statusCodeStr, 10);
    const statusText = statusParts.join(' ') || 'OK';

    if (statusCode === 204) {
        res.status(204).end();
    } 
    if (statusCode === 404) {
        res.status(statusCode).send(`${statusCode} ${statusText}`);

    } else {
        res.status(statusCode).send(statusText);
    }
}

