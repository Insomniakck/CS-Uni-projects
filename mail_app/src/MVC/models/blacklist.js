const client = require('../blacklistClient.js');
const { sendToBlacklistServer } = require('../blacklistClient.js')

module.exports = {
    addURL: (url) => sendToBlacklistServer('POST', url),
    checkURL: (url) => sendToBlacklistServer('GET', url),
    deleteURL: (url) => sendToBlacklistServer('DELETE', url)
};
